package com.gles;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.widget.Toast;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.gles.GameAirHoki7.GameAirHoki7;

public class MainActivityAirHoki7 extends Activity 
{
    private GLSurfaceView glSurfaveView;
    private boolean rendererSet = false;
    private GameAirHoki7 gameRenderer;

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
            gameRenderer = new GameAirHoki7(this);
            glSurfaveView.setRenderer(gameRenderer);
            rendererSet = true;
        }
        else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0", Toast.LENGTH_LONG).show();
        }

        /*
        di andrroid kita dapat mendengarkan pada acara sentuhan tampilan
        dengan memanggil setOnTouchListener(). ketika penguna menyentuh
        tampilan itu kami akan menerima panggila ke onTouch(). hal pertama
        yang kami lakukan adalah memeriksa apakah ada acara untuk ditangani
        diandroid peristiwa sentuhan akan berada dalam ruang koordinat tampilan
        sehingga sudut kiri atas tampilan akan memetakan ke (0,0) dan sudut
        kanan bawah akan memetakan ke dimensi tampilan. Misalnya jika pandangan
        kami adlah 480 pixel lebar 800 pixel maka sudut kanan bawah akan memetakan
        k (480, 800). kita perlu bekerja dengan koordnat perangkat yang dinormalisasi
        dalam renderer kita(lihat page:78 masalah rasio aspek) jadi kami mengkonversi
        koordinat acara sentuh kembal ke koordinat perangkat yang dinormalisai dengan
        membalikan sumbu y dan mengurangi setiap koordinat ke dalam kisaran [-1, 1]
        */
        glSurfaveView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    /*
                    konversi koordinat sentuh menjadi perangkat yang dinormalisasi
                    koordinat, ingatlah bahwa koordinat android terbalik
                    */
                    final float normalizedX = (event.getX() / (float)v.getWidth()) * 2 - 1; 
                    final float normalizedY = (event.getY() / (float)v.getHeight()) * 2 - 1; 
                        
                    /*
                    kami memeriksa untuk melihat apakah acara ini adalah initial press(press awal)
                    atau touch drag(acara seret), karena kami harus menangani setiap kasus secara
                    berbeda. pers awal sesuai dengan MotionEvent.ACTION_DOWN dan drag sesuai dengan
                    MotionEvent.ACTION_MOVE, penting untuk diingat bahwa ui android berjalan dalam
                    thread(utas) utama sedangkan surfaceView menjalankan OpenGL di thread terpisah
                    jadi kita perlu berkomunikasi diantara keduanya menggunakan teknik thread-save(thread aman)
                    kami menggunakan queueEvent() untuk mengirim panggilan ke thread OpenGL memenaggil
                    GameRenderer.handleTouchPress() untuk press dan GameRenderer.handleTouchDrag()
                    untuk drag. Kami menyelesaikan penanganan dengan mengembalikan true untuk memberi
                    tau android bahwa kami telah menkonsumsi acara sentuh. jika acara itu null maka
                    kita mengembalikan false.
                    */
                    if (event.getAction() == MotionEvent.ACTION_DOWN) 
                    {
                        glSurfaveView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                gameRenderer.handleTouchPress(normalizedX, normalizedY);
                            }
                        });
                    }
                    else if (event.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        glSurfaveView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                gameRenderer.handleTouchDrag(normalizedX, normalizedY);
                            }
                        });
                    }
                    return true;
                }
                else {
                    return false;
                }
            }
        });
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
}
