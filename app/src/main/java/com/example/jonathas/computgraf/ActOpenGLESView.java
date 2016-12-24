package com.example.jonathas.computgraf;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Matrix;
import android.graphics.PointF;
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
    private float m_OldDist;
    private float m_NewDist;
    float m_ScaleOld = 0.0F;
    private float m_LastScale;
    private float m_CurrentFOV;
    private float m_StartFOV=1;

    final PointF m_CurrentTouchPoint = new PointF();
    PointF m_MidPoint = new PointF();
    PointF m_LastTouchPoint = new PointF();
    static int m_Gesture = 0;
    static int DRAG_GESTURE = 1;
    static int PINCH_GESTURE = 2;
    static int m_DragFlag = 0;
    static int m_GestureMode;


    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    PointF mid = new PointF();
    float oldDist = 1f;
    int pointerCount = 0;
    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    public ActOpenGLESView(Context context) {
        super(context);
    }

    public ActOpenGLESView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
         float m_Scale;



     /*   boolean retval = true;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
             m_Gesture = DRAG;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                m_Gesture = NONE;
                m_LastTouchPoint.x = event.getX();
                m_LastTouchPoint.y = event.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                m_OldDist = spacing(event);

                midPoint(m_MidPoint, event);
                m_Gesture = ZOOM;
                m_LastTouchPoint.x = m_MidPoint.x;
                m_LastTouchPoint.y = m_MidPoint.y;
                m_CurrentTouchPoint.x = m_MidPoint.x;
                m_CurrentTouchPoint.y = m_MidPoint.y;
                break;

            case MotionEvent.ACTION_MOVE:
                if (m_Gesture == DRAG){
                    retval = handleDragGesture(event);
                }
                else if (m_Gesture == ZOOM) {
                    retval = handlePinchGesture(event);
            }
                break;


        }
*/        if (event != null) {
            float x = event.getX();
            float y = event.getY();
//

//            if (event.getAction() == MotionEvent.ACTION_MASK){
//                if(event.getAction() == MotionEvent.ACTION_MOVE){
//                    float deltaX = (x - mPreviousX) / mDensity / 2f;
//                    float deltaY = (y - mPreviousY) / mDensity / 2f;
//                    mRenderer.mDeltaX += deltaX;
//                    mRenderer.mDeltaY += deltaY;
//                }
//                if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
//                    float sx = event.getX(0) - event.getX(1);
//                    float sy = event.getY(0) - event.getY(1);
//                    m_OldDist = (float) Math.sqrt(sx * sx + sy * sy);
//                }
//
//            }

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
//                    float sx = event.getX(0) - event.getX(1);
//                    float sx = event.getX(0) - event.getX(1);
                    mode = ZOOM;
                    float sx = event.getX(0);
                    float sx2 = event.getX(1);
                    float sy = event.getY(0);
                    float sy2 = event.getY(1);

                    m_OldDist = (float) Math.sqrt(Math.pow(sx2-sx,2) + Math.pow(sy2-sy, 2));

                    if (oldDist > 5f) {
                        //matriz...
                        midPoint(mid, event);

                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        float deltaX = (x - mPreviousX) / mDensity / 2f;
                        float deltaY = (y - mPreviousY) / mDensity / 2f;
                        mRenderer.mDeltaX += deltaX;
                        mRenderer.mDeltaY += deltaY;
                    }
                    if (mode == ZOOM) {
                        float newsx = event.getX(0);
                        float newsx2 = event.getX(1);
                        float newsy = event.getY(0);
                        float newsy2 = event.getY(1);
                        m_NewDist = (float) Math.sqrt(Math.pow(newsx2-newsx,2) + Math.pow(newsy2-newsy, 2));


                        //acumula valores para o zoom
                        m_Scale = m_NewDist/m_OldDist;
                        if (m_Scale>m_ScaleOld)
                            mRenderer.zoom += m_Scale/50;
                        else
                            mRenderer.zoom -= m_Scale/35;

                        m_ScaleOld = m_Scale;
                    }
                    break;
            }




//            if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                if (mRenderer != null) {
//
//                    //essa ação colocar no evento de 2 dedos
////                    m_OldDist = (sx * sx + sy * sy);
////
////                midPoint(m_MidPoint, event);
//////                m_Gesture = ZOOM;
////                m_LastTouchPoint.x = m_MidPoint.x;
////                m_LastTouchPoint.y = m_MidPoint.y;
////                m_CurrentTouchPoint.x = m_MidPoint.x;
////                m_CurrentTouchPoint.y = m_MidPoint.y;
//                    if (event.getAction() == MotionEvent.ACTION_PO) {
//
//
//                    }
////                handlePinchGesture(event);
//
//
//                }
//
//
//            }
//                if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
////                m_OldDist = spacing(event);
////
////                midPoint(m_MidPoint, event);
////                m_Gesture = ZOOM;
////                m_LastTouchPoint.x = m_MidPoint.x;
////                m_LastTouchPoint.y = m_MidPoint.y;
////                m_CurrentTouchPoint.x = m_MidPoint.x;
////                m_CurrentTouchPoint.y = m_MidPoint.y;
////                handlePinchGesture(event);
//
//
//            }



            mPreviousX = x;
            mPreviousY = y;

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    public boolean handleDragGesture(MotionEvent event){
        m_LastTouchPoint.x = m_CurrentTouchPoint.x;
        m_LastTouchPoint.y = m_CurrentTouchPoint.y;

        m_CurrentTouchPoint.x = event.getX();
        m_CurrentTouchPoint.y = event.getY();

        m_Gesture = DRAG_GESTURE;
        m_DragFlag = 1;

        return true;
    }

   /* public boolean handlePinchGesture(MotionEvent event) {
        float minFOV = 5.0f;
        float maxFOV = 100.0f;
//        float newDist = spacing(event);

//        m_Scale = m_OldDist/newDist;

        if (m_Scale > m_LastScale) {
            m_LastScale = m_Scale;
        } else if (m_Scale <= m_LastScale) {
            m_LastScale = m_Scale;
        }

        m_CurrentFOV = m_StartFOV * m_Scale;
        m_LastTouchPoint = m_MidPoint;
        m_GestureMode = PINCH_GESTURE;

//        if (m_CurrentFOV >= minFOV && m_CurrentFOV <= maxFOV) {


        return true;
//        }

//        else
//            return false;

    }*/

    // Hides superclass method.
    public void setRenderer(ActOpenGLESRenderizadorVBO renderer, float density)
    {
        mRenderer = renderer;
        mDensity = density;
        super.setRenderer(renderer);
    }



    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

}