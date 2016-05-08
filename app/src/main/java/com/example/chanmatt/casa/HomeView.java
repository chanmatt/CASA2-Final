package com.example.chanmatt.casa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;

import com.belkin.wemo.localsdk.WeMoDevice;
import com.belkin.wemo.localsdk.WeMoSDKContext;
import com.belkin.wemo.localsdk.WeMoSDKContext.NotificationListener;
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

/**
 * Created by chanmatt on 2/19/16.
 */
public class HomeView extends View {

    Context context;
    boolean selected1;
    boolean selected2;
    boolean selected3;
    boolean selected4;
    ArrayList<String> switches;
    PHHueSDK phHueSDK;

    private WeMoSDKContext mWeMoSDKContext = null;

    public HomeView(Context c) {
        super(c);
        context = c;
        start();
    }

    public HomeView(Context c, AttributeSet a) {
        super(c, a);
        context = c;
        start();
    }

    public void start() {
        selected1=false;
        selected2=false;
        selected3=false;
        selected4=false;

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

        //Header (Time and Location)
        t.setColor(Color.WHITE);
        t.setTextSize(90);
        canvas.drawText("7:23", centerx, 125, t);
        t.setTextSize(30);
        canvas.drawText("matt's room", centerx, 165, t);

        //Switch Buttons


        p.setStyle(Paint.Style.FILL);
        t.setTextSize(100);

        if (!selected1) {
            p.setColor(Color.argb(30,255,255,255));
        } else {
            p.setColor(Color.argb(100,0,191,255));
        }
        canvas.drawRect(50, 225, width-50, 450, p);
        canvas.drawText("all lights", centerx, 375, t);

        if (!selected2) {
            p.setColor(Color.argb(30,255,255,255));
        } else {
            p.setColor(Color.argb(100,0,191,255));
        }
        canvas.drawRect(50, 500, width-50, 725, p);
        canvas.drawText("ceiling", centerx, 650, t);

        if (!selected3) {
            p.setColor(Color.argb(30,255,255,255));
        } else {
            p.setColor(Color.argb(100,0,191,255));
        }
        canvas.drawRect(50, 775, width-50, 1000, p);
        canvas.drawText("ambient", centerx, 925, t);

        t.setTextSize(25);

        Drawable d = getResources().getDrawable(R.drawable.alarm);
        d.setBounds(width/2+25, 1075, width/2 + 100, 1150);
        d.draw(canvas);
        canvas.drawText("7:00a", width/2 + 60, 1180, t);

        Drawable e = getResources().getDrawable(R.drawable.music);
        e.setBounds(width/2+150, 1075, width/2 + 225, 1150);
        e.draw(canvas);
        canvas.drawText("paused", width/2 + 185, 1180, t);

        Drawable f  = getResources().getDrawable(R.drawable.winter);
        f.setBounds(width/2-100, 1075, width/2 - 25, 1150);
        f.draw(canvas);
        if (selected4) {
            canvas.drawText("cooling", width / 2 - 60, 1180, t);
        } else {
            canvas.drawText("off", width / 2 - 60, 1180, t);
        }

        Drawable g  = getResources().getDrawable(R.drawable.electricity);
        g.setBounds(width/2-225, 1075, width/2 - 150, 1150);
        g.draw(canvas);
        canvas.drawText("74wh", width/2 - 185, 1180, t);

        //Devices
    }

    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {

            int height = this.getHeight();
            int width = this.getWidth();
            int x = (int) e.getX();
            int y = (int) e.getY();

            if (225<y && y<450) {
                this.playSoundEffect(android.view.SoundEffectConstants.CLICK);

                if (!selected1) {
                    turnOnHue();
                } else {
                    turnOffHue();
                }

                setStateByID(selected1, "uuid:Socket-1_0-221311K0101F94");
                setStateByID(selected1, "uuid:Socket-1_0-221429K01000F7");
                setStateByID(selected1, "uuid:Socket-1_0-221438K01005FB");
                setStateByID(selected1, "uuid:Socket-1_0-221233K0100B81");
                selected1 = !selected1;
                selected2 = selected1;
                selected3 = selected1;

            } else if (500<y && y<725) {
                this.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                if (!selected2) {
                    turnOnHue();
                } else {
                    turnOffHue();
                }
                selected2 = !selected2;
            } else if (775<y && y<1000) {
                this.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                setStateByID(selected3, "uuid:Socket-1_0-221311K0101F94");
                setStateByID(selected3, "uuid:Socket-1_0-221429K01000F7");
                setStateByID(selected3, "uuid:Socket-1_0-221438K01005FB");
                setStateByID(selected3, "uuid:Socket-1_0-221233K0100B81");
                selected3 = !selected3;
            } else if (y>1000 && x<this.getWidth()/2) {
                setStateByID(selected4, "uuid:Insight-1_0-221413K1200502");
                selected4 = !selected4;
            } else if (y<225) {
                Intent intent = new Intent();
                intent.setClass(context.getApplicationContext(), DetailActivity.class);
                context.startActivity(intent);
                //mWeMoSDKContext.refreshListOfWeMoDevicesOnLAN();
            }
            invalidate();
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
