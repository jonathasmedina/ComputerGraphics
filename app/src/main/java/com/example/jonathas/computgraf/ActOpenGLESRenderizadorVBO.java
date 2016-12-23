package com.example.jonathas.computgraf;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.R.attr.left;
import static android.R.attr.right;

/**
 * Created by Jonathas on 03/12/2016.
 */

public class ActOpenGLESRenderizadorVBO extends Activity implements GLSurfaceView.Renderer{

    private  FloatBuffer mNormaisBuffer;
    private  FloatBuffer mCoresBuffer;
    private  FloatBuffer mPosicoesBuffer;
    private  FloatBuffer mVertexDataBuffer;
    private ShortBuffer mIndicesBuffer;

    float[] cubePositionData2;
    float[] normaisF;
    int[] indicesTriangulosF;
    short[] indicesTriangulosS;

    final float bottom = -1.0f;
    final float top = 1.0f;
    final float near = 1.0f;
    final float far = 10.0f;
    float left;
    float right;

    float zoom = 2.5f;
    static float m_FieldOfView = 30.0f;
    static int m_PinchFlag = 0;

    ArrayList<Float> verticesData;
    ArrayList<Float> normais;
    ArrayList<Integer> normaisVertices;
    ArrayList<Float> cores;
    ArrayList<Integer> indicesTriangulos;

//    public volatile float mDeltaX;
//    public volatile float mDeltaY;
    public float mDeltaX;
    public float mDeltaY;
    public float mDeltaZ;

    private final float[] mCurrentRotation = new float[16];
    private float[] mTemporaryMatrix = new float[16];
    private final float[] mAccumulatedRotation = new float[16];



    private  final Context mContexto;

    private int mPerVertexProgramHandle;

    private float[] mModelMatrix = new float[16];

    int indexCount;

    private VBO_IBO_Render vbo_ibo_render;

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];

    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    /**
     * Stores a copy of the model matrix specifically for the light position.
     */
    private float[] mLightModelMatrix = new float[16];

    private float[] mTempMatrix = new float[16];

    /** Store our model data in a float buffer. */
    private  FloatBuffer mTriangle1Vertices;
    private  FloatBuffer mTriangle2Vertices;
    private  FloatBuffer mTriangle3Vertices;

    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    private int mMVMatrixHandle;

    private int mLightPosHandle;

    private int programHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;

    private int mNormalHandle;

    private int indicesHandle;

    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    private static final int mBytesPerShort = 2;

    int k;


    /** How many elements per vertex. */
    private final int mStrideBytes = 7 * mBytesPerFloat;

    /** Offset of the position data. */
    private final int mPositionOffset = 0;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    /** Offset of the color data. */
    private final int mColorOffset = 3;

    /** Size of the color data in elements. */
    private final int mColorDataSize = 4;

    private final int mNormalDataSize = 3;

//    private static final int STRIDE = (mPositionDataSize + mNormalDataSize + COLOR_DATA_SIZE_IN_ELEMENTS)
//            * BYTES_PER_FLOAT;

    private final int indicesBufferDataSize = 3;

    /** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     *  we multiply this by our transformation matrices. */
    private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};

    /** Used to hold the current position of the light in world space (after transformation via model matrix). */
    private final float[] mLightPosInWorldSpace = new float[4];

    /** Used to hold the transformed position of the light in eye space (after transformation via modelview matrix) */
    private final float[] mLightPosInEyeSpace = new float[4];

    /** This is a handle to our light point program. */
    private int mPointProgramHandle;

    private final int STRIDE = (mPositionDataSize + mColorDataSize + mNormalDataSize)
            * mBytesPerFloat;

    int floatsPerVertex = (mPositionDataSize+mColorDataSize+mNormalDataSize);

//    public ActOpenGLESRenderizadorVBO(){
//
//    }

    public ActOpenGLESRenderizadorVBO(Cena cena, final Context contexto) {
        //dados...info vindo do objeto: MainActivity -> popula a pojo ObjJson, passa uma lista (caso mais de um ator) por
        // intent para Act -> recebe o objeto populado -> passa ele novamente na linha
        // mGLSurfaceView.setRenderer(new ActOpenGLESRenderizador(dados))para esta classe
        // Define points for equilateral triangles.

        mContexto = contexto;
        int offset = 0;
        //um único objeto com todos os dados da cena
//            Cena i = cena.get(0);
        Cena i = cena;
        //recuperando os dados e os objetos
        ObjCamera objCamera = i.getObjCamera();
        ObjLight objLight = i.getObjLight();
        ObjActor objActor = i.getObjActor();

        verticesData = objActor.getVertices();

        //arraylist de int contendo os índices dos triangulos
        indicesTriangulos = objActor.getTrianglesV();

        //arraylist de float contendo as normais
        //as normais estão sendo calculadas - o tamanho do vetor contendo as posições dos vértices não coincide com a quantidade de vértices informadas
        normais = objActor.getNormals(); //os valores ..

        normaisVertices = objActor.getTrianglesVN(); //os índices das normais para cada vértice

        //arraylist de float contendo as cores
        cores = objActor.getColors();

        final float[] mVertexData = new float[objActor.getNumberOfVertices()*floatsPerVertex]; //!

        //converte o ArrayList<Float> em float[] - posicoes dos vertices
        cubePositionData2 = new float[verticesData.size()];
        k = 0;

        for (Float f : verticesData){
//            mVertexData[offset++] = f;
            cubePositionData2[k++] = (f != null ? f : Float.NaN);
        }

        //converte o ArrayList<Float> em float[] - normais
        normaisF = new float[normais.size()*3];
        k = 0;


//        DecimalFormat df = new DecimalFormat();
//        df.setMaximumFractionDigits(2);

        for (Float j : normais){
//            mVertexData[offset++] = j;
//            normaisF[k++] = (j != null ? Float.parseFloat(df.format(j)) : Float.NaN);
            normaisF[k++] = (j != null ? j : Float.NaN);
        }



        //converte o ArrayList<Int> em int[] - vertices dos triangulos
        indicesTriangulosF = new int[indicesTriangulos.size()];
        k = 0;

        for (Integer j : indicesTriangulos){
            indicesTriangulosF[k++] = (j != null ?  j : 10000);

        }

        //converter de float para short...verificar necessidade
        indicesTriangulosS = new short[indicesTriangulosF.length];
        k = 0;

        for (float j : indicesTriangulosF){
            indicesTriangulosS[k++] = (short) j;

        }

            final float[] cubeColorData = new float[verticesData.size()*4]; //cada vértice tem rgba
            int ka = 0;

        //sem cor = cor preta pra tudo
        if(cores.size() == 0){
            for (int j = 0; j < (cubeColorData.length); j++) {
//                mVertexData[offset++] = 0.0f; //preto
                cubeColorData[j++] = 0.3f;
                cubeColorData[j++] = 0.4f;
                cubeColorData[j++] = 0.0f;
                cubeColorData[j] = 0.3f;
            }
        }
        else{
            k = 0;
            for (float j : cores){
//                mVertexData[offset++] = 0.0f; //branco
                cubeColorData[k++] = j;

            }
        }

        indexCount = indicesTriangulosF.length;

        int wpos = 0;
        int wnor = 0;
        int w, j = 0;
        float x, y, z;
        int cor = 0;
        for (w=0; w< objActor.getNumberOfVertices(); w++){ //?
            x = cubePositionData2[wpos++];
            y = cubePositionData2[wpos++];
            z = cubePositionData2[wpos++];

            mVertexData[j++] = x;
            mVertexData[j++] = y;
            mVertexData[j++] = z;

                     // Cálculo das normais utilizando produto interno.
        /*

                    final float[] planeVectorX = {1f, 0f, x};
                    final float[] planeVectorY = {0f, 1f, y};
                    final float[] normalVector = {
                            (planeVectorX[1] * planeVectorY[2]) - (planeVectorX[2] * planeVectorY[1]),
                            (planeVectorX[2] * planeVectorY[0]) - (planeVectorX[0] * planeVectorY[2]),
                            (planeVectorX[0] * planeVectorY[1]) - (planeVectorX[1] * planeVectorY[0])};

                    // Normalize the normal
                    final float length = Matrix.length(normalVector[0], normalVector[1], normalVector[2]);
        */

//            mVertexData[j++] = normalVector[0] / length;
//            mVertexData[j++] = normalVector[1] / length;
//            mVertexData[j++] = normalVector[2] / length;

            mVertexData[j++] = normaisF[wnor++];
            mVertexData[j++] = normaisF[wnor++];
            mVertexData[j++] = normaisF[wnor++];

            mVertexData[j++] = cubeColorData[cor++];
            mVertexData[j++] = cubeColorData[cor++];
            mVertexData[j++] = cubeColorData[cor++];
            mVertexData[j++] = cubeColorData[cor++];

        }

        //posições: cubePositionData2;
        //normais: normaisF;
        //indices: indicesTriangulosF;

        //http://www.learnopengles.com/android-lesson-seven-an-introduction-to-vertex-buffer-objects-vbos/
        // Initialize the buffers. - client-side buffers, não precisa mais do array de float depois
//        mPosicoesBuffer = ByteBuffer.allocateDirect(cubePositionData2.length * mBytesPerFloat)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        mPosicoesBuffer.put(cubePositionData2).position(0); //transferindo os dados
//
//        mCoresBuffer = ByteBuffer.allocateDirect(cubeColorData.length * mBytesPerFloat)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        mCoresBuffer.put(cubeColorData).position(0);
//
//        mNormaisBuffer = ByteBuffer.allocateDirect(normaisF.length * mBytesPerFloat)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        mNormaisBuffer.put(normaisF).position(0);

        mVertexDataBuffer = ByteBuffer.allocateDirect(mVertexData.length * mBytesPerFloat).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexDataBuffer.put(mVertexData).position(0);

        //buffer de indices tem que ser short
        mIndicesBuffer = ByteBuffer.allocateDirect(indicesTriangulosS.length * mBytesPerShort)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndicesBuffer.put(indicesTriangulosS).position(0);

    }
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        vbo_ibo_render = new VBO_IBO_Render();

        // Set the background clear color to black.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //branco
//        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //cinza
//        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Position the eye behind the origin.
        final float eyeX =  0.0f;
        final float eyeY =  0.0f;
        final float eyeZ =  2.0f; //distante - eixo z cresce para fora da tela. limite: 6

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        final String vertexShader = RawResourceReader.readTextFileFromRawResource(mContexto, R.raw.per_pixel_vertex_shader);

        final String fragmentShader =RawResourceReader.readTextFileFromRawResource(mContexto, R.raw.per_pixel_fragment_shader);

        // Load in the vertex shader.
        final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        mPerVertexProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {"a_Position",  "a_Color", "a_Normal", "a_Indices"});

        // Define a simple shader program for our point.
        final String pointVertexShader =
                        "uniform mat4 u_MVPMatrix;      \n"
                        +	"attribute vec4 a_Position;     \n"
                        + "void main()                    \n"
                        + "{                              \n"
                        + "   gl_Position = u_MVPMatrix   \n"
                        + "               * a_Position;   \n"
                        + "   gl_PointSize = 15.0;         \n"
                        + "}                              \n";

        final String pointFragmentShader =
                        "precision mediump float;       \n"
                        + "void main()                    \n"
                        + "{                              \n"
                        + "   gl_FragColor = vec4(1.0,    \n"
                        + "   1.0, 1.0, 1.0);             \n"
                        + "}                              \n";

        final int pointVertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
        mPointProgramHandle = createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                new String[] {"a_Position"});

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(mAccumulatedRotation, 0);

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        left = -ratio;
        right = ratio;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);


        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mPerVertexProgramHandle);

        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_LightPos");
        mPositionHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Normal");
        mColorHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Color");
//        indicesHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Indices");

// Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        // Desenha ator
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -3.0f); //func
//        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.8f, -3.5f); //teste

                //rotaciona em x
//              Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.0f);
                //rotaciona em y
        //        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
                //rotaciona em z
        //        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        mDeltaX = 0.0f;
        mDeltaY = 0.0f;


        //truncando min e max
//        if(zoom <= 1.0f)
//            zoom = 1.0f;
//        if(zoom >= 6.0f)
//            zoom = 6.0f;
//        if (zoom == 0) {
//            zoom = 2.0f;
//        }
//            zoom = 0.0f;

        Matrix.scaleM(mModelMatrix, 0, zoom, zoom, zoom);
//                    zoom = 1.0f;



        // Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);

        // Rotate the cube taking the overall rotation into account.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);



      /*  Matrix.multiplyMM(mTempMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // Pass in the modelview matrix.
        //comentar?
//        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);
*/

        //desenha
        vbo_ibo_render.render();
        //luz
        GLES20.glUseProgram(mPointProgramHandle); // mPointProgramHandle
        drawLight();
    }

    private void drawLight()
    {
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(mPointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(mPointProgramHandle, "a_Position");

        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mTempMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mTempMatrix, 0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

    }



    private int compileShader(final int shaderType, final String shaderSource)
    {
        int shaderHandle = GLES20.glCreateShader(shaderType);

        if (shaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shaderSource);

            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                Log.e("cg", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0)
        {
            throw new RuntimeException("Error creating shader.");
        }

        return shaderHandle;
    }


    private int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes)
    {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            if (attributes != null)
            {
//                final int size = attributes.length;
//                for (int i = 16; i < 20; i++)
//                {
//                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
//                }
                //para o handle geral
                //{"a_Position",  "a_Color", "a_Normal", "a_Indices"});
                //https://www.opengl.org/sdk/docs/tutorials/ClockworkCoders/attributes.php ¬¬

                try {
                    final int size = attributes.length;
                    for (int i = 0; i < size; i++)
                    {
                        GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                Log.e("cg", "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }


    class VBO_IBO_Render{

        final int[] vbo = new int[1];
        final int[] ibo = new int[1];

        VBO_IBO_Render() {
             GLES20.glGenBuffers(1, vbo, 0);
             GLES20.glGenBuffers(1, ibo, 0);

             GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
             GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexDataBuffer.capacity()
                     * mBytesPerFloat, mVertexDataBuffer, GLES20.GL_STATIC_DRAW);

            //ibo = índices
             GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
             GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndicesBuffer.capacity()
                     * mBytesPerShort, mIndicesBuffer, GLES20.GL_STATIC_DRAW);

             GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
             GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        void render() {

            if (vbo[0] > 0 && ibo[0] > 0) {
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

                // Bind Attributes
                GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                        STRIDE, 0);
                GLES20.glEnableVertexAttribArray(mPositionHandle);

                GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false,
                        STRIDE, mNormalDataSize * mBytesPerFloat);
                GLES20.glEnableVertexAttribArray(mNormalHandle);

                GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                        STRIDE, (mPositionDataSize + mNormalDataSize) * mBytesPerFloat);
                GLES20.glEnableVertexAttribArray(mColorHandle);

                // Draw
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);

//               // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
                // (which currently contains model * view).
                Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

                // Pass in the modelview matrix.
                GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

                // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
                // (which now contains model * view * projection).
                Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
                System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

                // Pass in the combined matrix.
                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

                // Pass in the light position in eye space.
                GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

                GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);
//            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);

                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
            }
        }

        void release() {
            if (vbo[0] > 0) {
                GLES20.glDeleteBuffers(vbo.length, vbo, 0);
                vbo[0] = 0;
            }

            if (ibo[0] > 0) {
                GLES20.glDeleteBuffers(ibo.length, ibo, 0);
                ibo[0] = 0;
            }
        }
    }

}
