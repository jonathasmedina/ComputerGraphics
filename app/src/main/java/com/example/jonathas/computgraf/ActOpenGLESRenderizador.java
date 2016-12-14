package com.example.jonathas.computgraf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Jonathas on 03/12/2016.
 */

public class ActOpenGLESRenderizador extends Activity implements GLSurfaceView.Renderer{

    private final FloatBuffer mNormais;
    private final FloatBuffer mCores;
    private final FloatBuffer mPosicoes;

    final float[] cubePositionData2;
    final float[] indicesTriangulosF;
    final short[] indicesTriangulosS;

    final ArrayList<Float> triangle1VerticesData2;
    final ArrayList<Integer> indicesTriangulos;

    // Intent intentVindo = getIntent();
   // ArrayList<ObjJson> dados = (ArrayList<ObjJson>) intentVindo.getSerializableExtra("obj");


    private final Context mContexto;

    private float[] mModelMatrix = new float[16];

    private ShortBuffer indicesBuffer;
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

    private int indicesBufferHandle;

    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

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

    /**
     * Initialize the model data.
     */

   /* public ActOpenGLESRenderizador(){

    }*/

    public ActOpenGLESRenderizador(ArrayList<ObjJson> dados, final Context contexto) {
        //dados...info vindo do objeto: MainActivity -> popula a pojo ObjJson, passa uma lista (caso mais de um ator) por
        // intent para Act -> recebe o objeto populado -> passa ele novamente na linha
        // mGLSurfaceView.setRenderer(new ActOpenGLESRenderizador(dados))para esta classe
        // Define points for equilateral triangles.

        mContexto = contexto;

        //um único objeto com todos os dados
        ObjJson i = dados.get(0);
        triangle1VerticesData2 = i.getVertices();

        //arraylist de int contendo os índices dos triangulos
        indicesTriangulos = i.getTriangles();


        //converte o ArrayList<Float> em float[] - posicoes dos vertices
        cubePositionData2 = new float[triangle1VerticesData2.size()];
        k = 0;

        for (Float f : triangle1VerticesData2){
            cubePositionData2[k++] = (f != null ? f : Float.NaN);

        }

        //converte o ArrayList<Float> em float[] - vertices dos triangulos
                    indicesTriangulosF = new float[indicesTriangulos.size()];
                    k = 0;

                    for (Float j : indicesTriangulosF){
                        indicesTriangulosF[k++] = (j != null ? j : Float.NaN);

                    }


        //converter de float para short...
        indicesTriangulosS = new short[indicesTriangulosF.length];
        k = 0;

        for (float j : indicesTriangulosF){
            indicesTriangulosS[k++] = (short) j;

        }
//        indicesTriangulosS = (short) indicesTriangulosF;


                   /* //procedimento double<->float e normalizar - verificar a necessidade
                    //convertendo os dados para double para executar a função normalizadora
                    Double[] cubePositionData2D = new Double[cubePositionData2.length];
                    //alimentando o double com os dados float
                    for (int g = 0 ; g < cubePositionData2.length; g++)
                    {
                        cubePositionData2D[g] = (double) cubePositionData2[g];
                    }

                    //normaliza os valores e retorne para um vetor Double
                    cubePositionData2D = normalizar(cubePositionData2D);
                    //copia os valores de volta para o vetor float
                    for (int g = 0 ; g < cubePositionData2.length; g++)
                    {
                        cubePositionData2[g] = Float.parseFloat(String.valueOf(cubePositionData2D[g]));
                    }*/

        // Define points for a cube.
        // X, Y, Z
        final float[] cubePositionData =
                {
                        // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                        // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                        // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                        // usually represent the backside of an object and aren't visible anyways.

                        // Front face
                        -3.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,

                        // Right face
                        1.0f, 1.0f, 1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        1.0f, -1.0f, -1.0f,
                        1.0f, 1.0f, -1.0f,

                        // Back face
                        1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, -1.0f,
                        1.0f, -1.0f, -1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, -1.0f,

                        // Left face
                        -1.0f, 1.0f, -1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                        -1.0f, -1.0f, 1.0f,
                        -1.0f, 1.0f, 1.0f,

                        // Top face
                        -1.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,
                        -1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, 1.0f,
                        1.0f, 1.0f, -1.0f,

                        // Bottom face
                        1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                        1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, 1.0f,
                        -1.0f, -1.0f, -1.0f,
                };

        // R, G, B, A
        final float[] cubeColorData =
                {
                        // Front face (red)
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,

                        // Right face (green)
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,

                        // Back face (blue)
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        // Left face (yellow)
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,

                        // Top face (cyan)
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,

                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,

                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,

                        // Bottom face (magenta)
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                        1.0f, 1.0f, 0.0f, 0.0f,
                };

        // X, Y, Z
        // The normal is used in light calculations and is a vector which points
        // orthogonal to the plane of the surface. For a cube model, the normals
        // should be orthogonal to the points of each face.
        final float[] cubeNormalData =
                {
                        // Front face
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        // Right face
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        // Left face
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        // Top face
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,

                        // Bottom face
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f
                };


        // Initialize the buffers.
        mPosicoes = ByteBuffer.allocateDirect(cubePositionData2.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPosicoes.put(cubePositionData2).position(0);

        mCores = ByteBuffer.allocateDirect(cubeColorData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCores.put(cubeColorData).position(0);

        mNormais = ByteBuffer.allocateDirect(cubeNormalData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mNormais.put(cubeNormalData).position(0);

        indicesBuffer = ByteBuffer.allocateDirect(cubeNormalData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indicesTriangulosS).position(0);


    }
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        // Set the background clear color to black.
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Position the eye behind the origin.
        final float eyeX =  0.0f;
        final float eyeY =  0.0f;
        final float eyeZ = 2.5f;

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
//                "uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.
//                        + "uniform mat4 u_MVMatrix;       \n"		// A constant representing the combined model/view matrix.
//                        + "uniform vec3 u_LightPos;       \n"	    // The position of the light in eye space.
//
//                        + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
//                        + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.
//                        + "attribute vec3 a_Normal;       \n"		// Per-vertex normal information we will pass in.
//
//                        + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.
//
//                        + "void main()                    \n" 	// The entry point for our vertex shader.
//                        + "{                              \n"
//                        // Transform the vertex into eye space.
//                        + "   vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);              \n"
//                        // Transform the normal's orientation into eye space.
//                        + "   vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));     \n"
//                        // Will be used for attenuation.
//                        + "   float distance = length(u_LightPos - modelViewVertex);             \n"
//                        // Get a lighting direction vector from the light to the vertex.
//                        + "   vec3 lightVector = normalize(u_LightPos - modelViewVertex);        \n"
//                        // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
//                        // pointing in the same direction then it will get max illumination.
//                        + "   float diffuse = max(dot(modelViewNormal, lightVector), 0.1);       \n"
//                        // Attenuate the light based on distance.
//                        + "   diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));  \n"
//                        // Multiply the color by the illumination level. It will be interpolated across the triangle.
//                        + "   v_Color = a_Color * diffuse;                                       \n"
//                        // gl_Position is a special variable used to store the final position.
//                        // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
//                        + "   gl_Position = u_MVPMatrix * a_Position;                            \n"
//                        + "}                                                                     \n";

        final String fragmentShader =RawResourceReader.readTextFileFromRawResource(mContexto, R.raw.per_pixel_fragment_shader);
//                "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
//                        // precision in the fragment shader.
//                        + "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the
//                        // triangle per fragment.
//                        + "void main()                    \n"		// The entry point for our fragment shader.
//                        + "{                              \n"
//                        + "   gl_FragColor = v_Color;     \n"		// Pass the color directly through the pipeline.
//                        + "}                              \n";

        // Load in the vertex shader.
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if (vertexShaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);

            // Compile the shader.
            GLES20.glCompileShader(vertexShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        if (vertexShaderHandle == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }

        // Load in the fragment shader shader.
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if (fragmentShaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);

            // Compile the shader.
            GLES20.glCompileShader(fragmentShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }

        if (fragmentShaderHandle == 0)
        {
            throw new RuntimeException("Error creating fragment shader.");
        }

        // Create a program object and store the handle to it.
        programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");
            GLES20.glBindAttribLocation(programHandle, 2, "a_Normal");
            GLES20.glBindAttribLocation(programHandle, 3, "indices");

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        // Set program handles. These will later be used to pass in values to the program.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(programHandle, "a_Normal");
        indicesBufferHandle = GLES20.glGetAttribLocation(programHandle, "indices");

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

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
        GLES20.glUseProgram(programHandle);

        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(programHandle, "u_LightPos");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(programHandle, "a_Normal");
        indicesBufferHandle = GLES20.glGetAttribLocation(programHandle, "indices");


// Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);
        //Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        //Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        // Draw some cubes.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -8.0f);
        //rotaciona em x
//        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.0f);

        //rotaciona em y
//        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);

        //rotaciona em z
//        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);

        drawCube();

        GLES20.glUseProgram(programHandle);
        drawLight();
    }


    private void drawCube()
    {
        // Pass in the position information
        mPosicoes.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                0, mPosicoes);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        mCores.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                0, mCores);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // Pass in the normal information
        mNormais.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false,
                0, mNormais);

        GLES20.glEnableVertexAttribArray(mNormalHandle);

        //inf vertices - triang
        indicesBuffer.position(0);
        GLES20.glVertexAttribPointer(indicesBufferHandle, indicesBufferDataSize, GLES20.GL_UNSIGNED_SHORT, false,
                0, indicesBuffer);

        GLES20.glEnableVertexAttribArray(indicesBufferHandle);


        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Pass in the light position in eye space.
        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 48);
        //esse
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 48);

//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 48);

        //ESSE draw elements precisa do buffer com os indices
//       GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, k, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
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
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

    }



    public Double[] normalizar(Double x[]){
        int i = 0;
        Double y[] = new Double[x.length];
        for (Double f : x){
            if (f<0)
                y[i++] = (f/Math.pow(f*f, 0.5));
            else if (f==0)
                y[i++] = 0.0;
            else
                y[i++] = f/Math.pow(f*f, 0.5);
        }
        return y;
    }
}
