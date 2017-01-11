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

    private ActOpenGLESRenderizadorVBOTex mRenderer;
//    private ActOpenGLESRenderizadorVBO mRenderer;

    // Offsets para eventos de touch
    private float mPreviousX;
    private float mPreviousY;
    private float mDensity;
    private float m_OldDist;
    private float m_NewDist;
    float m_ScaleOld = 0.0F;

    PointF mid = new PointF();
    float oldDist = 1f;
    // 3 estados - nenhum, arrastar e zoom
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

        if (event != null) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: //toque do primeiro dedo na tela
                    mode = DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_UP: //retirada do dedo
                    mode = NONE;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN: //toque do segundo dedo na tela
                    mode = ZOOM;

                    //recupera posições (x,y) dos dois dedos
                    float sx = event.getX(0);
                    float sx2 = event.getX(1);
                    float sy = event.getY(0);
                    float sy2 = event.getY(1);

                    //calcula distância entre dois dedos (fórmula de dist entre 2 pontos)
                    m_OldDist = (float) Math.sqrt(Math.pow(sx2-sx,2) + Math.pow(sy2-sy, 2));

                    if (oldDist > 5f) {
                        midPoint(mid, event);

                    }
                    break;

                case MotionEvent.ACTION_MOVE: //movendo o dedo na tela
                    if (mode == DRAG) { //se modo DRAG, alterar valores x,y no renderer.
                                        // variáveis globais que serão passadas em uma matriz de rotação
                        float deltaX = (x - mPreviousX) / mDensity / 2f;
                        float deltaY = (y - mPreviousY) / mDensity / 2f;
                        mRenderer.mDeltaX += deltaX;
                        mRenderer.mDeltaY += deltaY;
                    }
                    if (mode == ZOOM) { //se zoom -> recupera novas posições dos dedos, calcula distância,
                                        // altera variável global zoom que será passada na matriz de escala
                        float newsx = event.getX(0);
                        float newsx2 = event.getX(1);
                        float newsy = event.getY(0);
                        float newsy2 = event.getY(1);
                        m_NewDist = (float) Math.sqrt(Math.pow(newsx2-newsx,2) + Math.pow(newsy2-newsy, 2));

                        //acumula valores para o zoom
                        m_Scale = m_NewDist/m_OldDist;
                        if (m_Scale>m_ScaleOld)
                            mRenderer.zoom += m_Scale/50; //valor arbitrário da progressão da escala
                        else
                            mRenderer.zoom -= m_Scale/35; //valor arbitrário da progressão da escala

                        m_ScaleOld = m_Scale;
                    }
                    break;
            }

            //valor anterior recebe valor atual..
            mPreviousX = x;
            mPreviousY = y;

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    // Oculta o método da superclasse. utilizado em ACTOpenGLES
    public void setRenderer(ActOpenGLESRenderizadorVBOTex renderer, float density)
//    public void setRenderer(ActOpenGLESRenderizadorVBO renderer, float density)
    {
        mRenderer = renderer;
        mDensity = density;
        super.setRenderer(renderer);
    }

    /** Calcula o ponto médio entre dois dedos */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

}