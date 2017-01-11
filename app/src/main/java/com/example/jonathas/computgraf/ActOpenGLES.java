package com.example.jonathas.computgraf;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Jonathas on 03/12/2016.
 * Recebe os dados, testa compatibilidade com OpenGLES, inicia renderizador
 */

public class ActOpenGLES extends Activity{

    /** Mantém a referência para nosso GLSurfaceView */
    private ActOpenGLESView mGLSurfaceView;

    private ActOpenGLESRenderizadorVBOTex mRenderer;
//    private ActOpenGLESRenderizadorVBO mRenderer;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //recebe os dados do Bundle
        Cena cena = ((ObjectWrapperForBinder)getIntent().getExtras().getBinder("cena")).getData();

        super.onCreate(savedInstanceState);

        //seta a tela com um elemento do tipo ActOpenGLESView
        setContentView(R.layout.act);

        //conecta ao elemento ActOpenGLESView do XML
        mGLSurfaceView = (ActOpenGLESView) findViewById(R.id.gl_surface_view);

        //Verifica compatibilidade com OpenGLES
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2)
        {
            //Solicita um contexto compatível com a OpenGL ES 2.0
            mGLSurfaceView.setEGLContextClientVersion(2);

            //Recupera dados gerais do display, como tamanho, densidade, dimensionamento da fonte (size, density, font scaling)
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            //Inicializa nosso renderizador, passando os dados da cena
            mRenderer = new ActOpenGLESRenderizadorVBOTex(cena, getApplicationContext());
//            mRenderer = new ActOpenGLESRenderizadorVBO(cena, getApplicationContext());

            //Seta o renderizador para nosso renderizador, já iniciado
            mGLSurfaceView.setRenderer(mRenderer, displayMetrics.density);
        }
        else
        {
            //implementação de outra versão suportada da OpenGL ES...
            return;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}
