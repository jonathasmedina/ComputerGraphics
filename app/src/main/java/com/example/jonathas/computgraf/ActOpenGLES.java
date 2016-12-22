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
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Jonathas on 03/12/2016.
 */

public class ActOpenGLES extends Activity{
    /** Hold a reference to our GLSurfaceView */
    private ActOpenGLESView mGLSurfaceView;

    private ActOpenGLESRenderizadorVBO mRenderer;
    // Offsets for touch events
    private float mPreviousX;
    private float mPreviousY;
    private float mDensity;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
//        String c = "teste";
//        Intent intent = getIntent();
        //String d = (String) intent.getSerializableExtra("obj");
        //ArrayList<ObjJson> dados2 = (ArrayList<ObjJson>) intent.getSerializableExtra("obj");

        Cena cena = ((ObjectWrapperForBinder)getIntent().getExtras().getBinder("cena")).getData();


//        ArrayList<Cena> cena = (ArrayList<Cena>) intent.getSerializableExtra("cena");
//        String c = "teste";
       // Bundle dados = getIntent().getExtras();
        //ArrayList<ObjJson> dados2 =  dados.getParcelableArrayList("obj");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.act);

        mGLSurfaceView = (ActOpenGLESView) findViewById(R.id.gl_surface_view);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2)
        {
            // Request an OpenGL ES 2.0 compatible context.
            mGLSurfaceView.setEGLContextClientVersion(2);

            final DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            // Set the renderer to our demo renderer, defined below.
            mRenderer = new ActOpenGLESRenderizadorVBO(cena, getApplicationContext());
//            mGLSurfaceView.setRenderer(new ActOpenGLESRenderizadorVBO(cena, getApplicationContext()));
            mGLSurfaceView.setRenderer(mRenderer, displayMetrics.density);
        }
        else
        {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }
    }

    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        mGLSurfaceView.onPause();
    }

}
