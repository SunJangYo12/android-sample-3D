package com.gles;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.widget.Toast;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gles.GameAirHoki6.GameAirHoki6;

public class MainActivity extends Activity 
{
    private GLSurfaceView glSurfaveView;
    private boolean rendererSet = false;
    private FirstRenderer gameRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaveView = new GLSurfaceView(this);
        final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) 
        {
            glSurfaveView.setEGLContextClientVersion(2);
            gameRenderer = new FirstRenderer();
            glSurfaveView.setRenderer(gameRenderer);
            rendererSet = true;
        }
        else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0", Toast.LENGTH_LONG).show();
        }

        setContentView(glSurfaveView);
    }

    /*
    Sangat penting untuk memiliki metode dibawah ini sehingga aplikasi
    dapat menjeda dengan benar dan melanjutkan thread rendering background
    serta melepaskan dan memperbarui kontext opengl. Jika tidak aplikasi
    dapat crash atau terbunuh oleh android.
    */
    
    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaveView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaveView.onResume();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, 1, 1, "GameAirHoki");
        menu.add(Menu.FIRST, 2, 1, "Partikel");
        menu.add(Menu.FIRST, 3, 1, "SkyBox");
        menu.add(Menu.FIRST, 4, 1, "Terain");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            startActivity(new Intent(this, MainActivityAirHoki7.class));
        }
        if (item.getItemId() == 2) {
            startActivity(new Intent(this, MainActivityPartikel.class));
        }
        if (item.getItemId() == 3) {
            startActivity(new Intent(this, MainActivitySkyBox.class));
        }
        if (item.getItemId() == 4) {
            startActivity(new Intent(this, MainActivityTerain.class));
        }
        return true;
    }
}
