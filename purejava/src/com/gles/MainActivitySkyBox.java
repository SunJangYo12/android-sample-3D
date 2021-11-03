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

import com.gles.GameSkyBox.GameSkyBox;

public class MainActivitySkyBox extends Activity 
{
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    private GameSkyBox gameRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);
        final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) 
        {
            glSurfaceView.setEGLContextClientVersion(2);            

            String[] aksi ={"0. Basic Skybox", "1. Touch kamera disekitar scene(tempat kejadian)"};
            AlertDialog.Builder builderIndex = new AlertDialog.Builder(this);
            builderIndex.setTitle("Select Tutorial");
            builderIndex.setItems(aksi, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    gameRenderer = new GameSkyBox(MainActivitySkyBox.this, item);
                    glSurfaceView.setRenderer(gameRenderer);
                    rendererSet = true;
                    setContentView(glSurfaceView);
                }
            });
            builderIndex.create().show();
            
        }
        else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0", Toast.LENGTH_LONG).show();
        }

        glSurfaceView.setOnTouchListener(new OnTouchListener() 
        {
            /*
            kode ini mendefinisikan listener sentuh yang akan mengukur seberapa
            jauh anda telah menyeret(drag) jari anda di antara setiap panggilan
            berturut-turut on onTouch(). ketika anda pertama kali menyentuh layar
            posisi sentuh saat ini akan direkam di previousX dan previousY. saat
            anda menyeret jari anda dilayar, anda akan mendapatkan banyak event drag
            dan setiap kali anda melakukanya anda akan terlebih dahulu mengambil
            perbedaan antara posisi baru dan posisi lama dan menyimpanyanya ke
            deltaX dan delayY kemudian andan akan memperbarui previousX dan previusY.
            delta akan diteruskan ke renderer partikel dengan memanggil handleDrag()
            */
            float previousX, previousY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) 
                    {
                        previousX = event.getX();
                        previousY = event.getY();
                    }
                    else if (event.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        final float deltaX = event.getX() - previousX;
                        final float deltaY = event.getY() - previousY;
                        previousX = event.getX();
                        previousY = event.getY();

                        if (rendererSet) {
                            glSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    gameRenderer.handleTouchDrag(deltaX, deltaY);
                                }
                            });
                        }
                    }
                    return true;
                }
                else {
                    return false;
                }
            }
        });
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
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}
