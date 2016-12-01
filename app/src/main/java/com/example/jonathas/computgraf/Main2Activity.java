package com.example.jonathas.computgraf;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Main2Activity extends Activity {

    //mantém a referência para o GLSurfaceView
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);

        //verifica se o sistema suporta a OpenGL ES 2.0
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2)
        {
            // Solicita um contexto compatível com a OpenGL ES 2.0
            mGLSurfaceView.setEGLContextClientVersion(2);

            //Seta o renderizador para nosso o renderizador
            mGLSurfaceView.setRenderer(new Renderizador());
        }
        else //não suporta
            return;

        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onResume() {
        super.onPostResume();
        mGLSurfaceView.onResume();
    }
}
