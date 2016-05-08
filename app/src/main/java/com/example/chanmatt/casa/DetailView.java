package com.example.chanmatt.casa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.belkin.wemo.localsdk.WeMoDevice;
import com.belkin.wemo.localsdk.WeMoSDKContext;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chanmatt on 2/19/16.
 */
public class DetailView extends View {

    Context context;
    boolean selected1;
    boolean selected2;
    boolean selected3;
    boolean selected4;
    ArrayList<String> switches;
    PHHueSDK phHueSDK;
    Timer theTimer;
    int timerCount;

    int slider;

    private WeMoSDKContext mWeMoSDKContext = null;

    public DetailView(Context c) {
        super(c);
        context = c;
        start();
    }

    public DetailView(Context c, AttributeSet a) {
        super(c, a);
        context = c;
        start();
    }

    public void start() {
        slider = 20;

        selected1=false;
        selected2=false;
        selected3=false;
        selected4=false;

        timerCount = 0;

        mWeMoSDKContext = new WeMoSDKContext(context);
        //mWeMoSDKContext.addNotificationListener(this);

        mWeMoSDKContext.refreshListOfWeMoDevicesOnLAN();

        phHueSDK = PHHueSDK.getInstance();
        phHueSDK.setAppName("Casa");     // e.g. phHueSDK.setAppName("QuickStartApp");
        phHueSDK.setDeviceName(android.os.Build.MODEL);  // e.g. If you are programming for Android: phHueSDK.setDeviceName(android.os.Build.MODEL);

    }

    public void onDraw(Canvas canvas) {
        int height = this.getHeight();
        int width = this.getWidth();

        int size = 50;
        int ssize = 40;
        int spacing = 250;

        int centerx = width/2;

        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.BLACK);
        canvas.drawRect(0, 0, width, height, p);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5);
        p.setColor(Color.WHITE);
        Paint t = new Paint();
        t.setColor(Color.WHITE);
        t.setStyle(Paint.Style.FILL);
        t.setTextSize(50);
        t.setTextAlign(Paint.Align.CENTER);

        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.argb(20,255,255,255));
        //canvas.drawRect(50, 100, width-50, height-100, p);

        //Header (Time and Location)
        t.setColor(Color.WHITE);
        t.setTextSize(90);
        canvas.drawText("ceiling", centerx, 225, t);

        t.setTextSize(30);
        canvas.drawText("brightness", centerx, 275, t);

        p.setColor(Color.argb(60, 255, 255, 255));

        for (int x = 0; x<=500; x=x+25) {
            canvas.drawRect(250, 400+x, width - 250, 405+x, p);
        }

        int where = 500 - slider * 25;

        p.setColor(Color.argb(255, 255, 255, 150));
        canvas.drawRect(230, 395+where, width - 230, 410+where, p);

        for (int i = where; i<=500; i=i+25) {
            canvas.drawRect(250, 400+i, width - 250, 405+i, p);
        }

        //Devices
    }

    public void updateBrightness() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setOn(true);
            lightState.setBrightness((int)(slider*12.7));
            bridge.updateLightState(light, lightState, new PHLightListener() {
                @Override
                public void onReceivingLightDetails(PHLight phLight) {

                }

                @Override
                public void onReceivingLights(List<PHBridgeResource> list) {

                }

                @Override
                public void onSearchComplete() {

                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {

                }
            });
        }

    }

    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            //timerActive = false;
            updateBrightness();
            timerCount = 10;
        }

        if (e.getAction() == MotionEvent.ACTION_MOVE || e.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) e.getX();
            int y = (int) e.getY();


            if (370<=y && y<930) {
                slider = (500-(y-400))/25;
            }
            if (slider<0) {
                slider = 0;
            }
            if (slider>20) {
                slider = 20;
            }

            //System.out.println("Action Move");

            if (timerCount == 30) {
                System.out.println("update");
                timerCount = 0;
                updateBrightness();
            }
            timerCount++;

            invalidate();
        }

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) e.getX();
            int y = (int) e.getY();

            if (y<350 || 950<y) {
                ((DetailActivity)context).endActivity();

            }
        }

        return true;
    }

    public boolean setStateByID(boolean on, String ID) {
        WeMoDevice device = mWeMoSDKContext.getWeMoDeviceByUDN(ID);
        if (device != null) {
            System.out.println(device.getFriendlyName());

            //we can change the state of switches and insight devices only
            String type = device.getType();
            String state = device.getState().split("\\|")[0];

            if (type.equals(WeMoDevice.SWITCH)
                    || type.equals(WeMoDevice.LIGHT_SWITCH)
                    || type.equals(WeMoDevice.INSIGHT)) {


                String newState = WeMoDevice.WEMO_DEVICE_ON;

                if (on) {
                    newState = WeMoDevice.WEMO_DEVICE_OFF;
                    //Turn from on state to off state
                }

                mWeMoSDKContext.setDeviceState(newState, device.getUDN());
            }
            return true;
        } else {
            int count = 0;
            while (count < 5) {
                mWeMoSDKContext.refreshListOfWeMoDevicesOnLAN();
                device = mWeMoSDKContext.getWeMoDeviceByUDN(ID);
                if (device != null) {
                    System.out.println(device.getFriendlyName());

                    //we can change the state of switches and insight devices only
                    String type = device.getType();
                    String state = device.getState().split("\\|")[0];

                    if (type.equals(WeMoDevice.SWITCH)
                            || type.equals(WeMoDevice.LIGHT_SWITCH)
                            || type.equals(WeMoDevice.INSIGHT)) {


                        String newState = WeMoDevice.WEMO_DEVICE_ON;

                        if (on) {
                            newState = WeMoDevice.WEMO_DEVICE_OFF;
                            //Turn from on state to off state
                        }

                        mWeMoSDKContext.setDeviceState(newState, device.getUDN());
                    }
                    return true;
                }
                count++;

            }
        }
        return false;
    }

    public void turnOffHue() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light : allLights) {
            System.out.println(light.getName());
            PHLightState lightState = new PHLightState();
            lightState.setOn(false);
            bridge.updateLightState(light, lightState, new PHLightListener() {
                @Override
                public void onReceivingLightDetails(PHLight phLight) {

                }

                @Override
                public void onReceivingLights(List<PHBridgeResource> list) {

                }

                @Override
                public void onSearchComplete() {

                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {

                }
            });
        }
    }

    public void turnOnHue() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setOn(true);
            lightState.setBrightness(254);
            bridge.updateLightState(light, lightState, new PHLightListener() {
                @Override
                public void onReceivingLightDetails(PHLight phLight) {

                }

                @Override
                public void onReceivingLights(List<PHBridgeResource> list) {

                }

                @Override
                public void onSearchComplete() {

                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {

                }
            });
        }
    }

    /**switches = new ArrayList<String>();
     switches = mWeMoSDKContext.getListOfWeMoDevicesOnLAN();
     Log.i("test", "switches = " + switches.size());
     for (int i=0; i<switches.size(); i++) {
     System.out.println("Yo:");
     System.out.println(switches.get(i));
     WeMoDevice device = mWeMoSDKContext.getWeMoDeviceByUDN(switches.get(i));
     System.out.println(device.getFriendlyName());

     //we can change the state of switches and insight devices only
     String type = device.getType();
     String state = device.getState().split("\\|")[0];

     if (type.equals(WeMoDevice.SWITCH)
     || type.equals(WeMoDevice.LIGHT_SWITCH)
     || type.equals(WeMoDevice.INSIGHT)) {
     String newState = WeMoDevice.WEMO_DEVICE_ON;

     if (state.equals(WeMoDevice.WEMO_DEVICE_ON) || state.equals(WeMoDevice.WEMO_DEVICE_STAND_BY)) {
     newState = WeMoDevice.WEMO_DEVICE_OFF;
     //Turn from on state to off state
     } else {
     //Turn from off state to on state
     }

     mWeMoSDKContext.setDeviceState(newState, device.getUDN());
     }
     }**/
}
