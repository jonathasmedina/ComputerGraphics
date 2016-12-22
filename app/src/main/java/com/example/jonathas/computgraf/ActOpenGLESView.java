package com.example.jonathas.computgraf;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;

/**
 * Created by Jonathas on 03/12/2016.
 */

public class ActOpenGLESView extends GLSurfaceView{

    private ActOpenGLESRenderizadorVBO mRenderer;

    // Offsets for touch events
    private float mPreviousX;
    private float mPreviousY;
    private float mDensity;

    public ActOpenGLESView(Context context) {
        super(context);
    }

    public ActOpenGLESView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event != null)
        {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                if (mRenderer != null)
                {
                    float deltaX = (x - mPreviousX) / mDensity / 2f;
                    float deltaY = (y - mPreviousY) / mDensity / 2f;

                    mRenderer.mDeltaX += deltaX;
                    mRenderer.mDeltaY += deltaY;
                }
            }
//            if (event.getAction() == MotionEvent.ACTION_POINTER_INDEX_MASK) { //dois dedos? ver multigesture
//            http://www.vogella.com/tutorials/AndroidTouch/article.html#multi-touch

            if (event.getAction() == MotionEvent.ACTION_DOWN) { //primeiro dedo
                if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) { //dois dedos? ver multigesture
                    float distance = fingerDist(event);
                    float newDist = fingerDist(event);
                    float d = distance / newDist;
                    mRenderer.zoom(d);
                    distance = newDist;
                }
            }




                mPreviousX = x;
            mPreviousY = y;

            return true;
        }
        else
        {
            return super.onTouchEvent(event);
        }
    }

    // Hides superclass method.
    public void setRenderer(ActOpenGLESRenderizadorVBO renderer, float density)
    {
        mRenderer = renderer;
        mDensity = density;
        super.setRenderer(renderer);
    }


    protected final float fingerDist(MotionEvent event){
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


}
