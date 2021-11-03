package com.gles;

import android.app.*;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.widget.Toast;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gles.GamePartikel.GamePartikel;

public class MainActivityPartikel extends Activity 
{
    private GLSurfaceView glSurfaveView;
    private boolean rendererSet = false;
    private GamePartikel gameRenderer;

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

            String[] aksi ={"0. Basic partikel", "1. Sebar partikel", "2. Add gravity",
                            "3. Mixing partikel with additive Blending", "4. Menyesuaikan penampilan titik",
                            "5. Menggambar setiap titik sebagai Sprite"};
            AlertDialog.Builder builderIndex = new AlertDialog.Builder(this);
            builderIndex.setTitle("Select Tutorial");
            builderIndex.setItems(aksi, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0){
                        gameRenderer = new GamePartikel(MainActivityPartikel.this);
                    }
                    else {
                        gameRenderer = new GamePartikel(MainActivityPartikel.this, item);
                    }
                    glSurfaveView.setRenderer(gameRenderer);
                    rendererSet = true;
                    setContentView(glSurfaveView);
                }
            });
            builderIndex.create().show();
            
        }
        else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0", Toast.LENGTH_LONG).show();
        }

       
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
}
