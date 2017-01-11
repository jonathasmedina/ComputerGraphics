package com.example.jonathas.computgraf;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Jonathas on 03/12/2016.
 *
 * Esta classe alimenta a cena, os shaders e renderiza os elementos
 * Utiliza VBO - Vertex Buffer Object e IBO - Index Buffer Object
 *
 *
 */

public class ActOpenGLESRenderizadorVBOTex extends Activity implements GLSurfaceView.Renderer{

    private boolean POSSUI_TEXTURAS;

    String vertexShader, fragmentShader;

    private  FloatBuffer mVertexDataBuffer;
    private ShortBuffer mIndicesBuffer;

    float[] positionData;
    float[] texturesDataF;
    float[] normaisF;
    int[] indicesTriangulosF;
    short[] indicesTriangulosS;

    float zoom = 1.0f; //variável global que será alterada com o evento de toque

    //dados ator
    ArrayList<Float> verticesData;
    ArrayList<Float> texturesData;
    ArrayList<Float> materialData;
    ArrayList<Float> materialData_Ka  = new ArrayList<>();
    ArrayList<Float> materialData_Kd = new ArrayList<>();
    ArrayList<Float> materialData_Ks = new ArrayList<>();
    ArrayList<Float> materialData_Ns = new ArrayList<>();
    ArrayList<Float> materialData_Tr = new ArrayList<>();
    ArrayList<Float> normais;
    ArrayList<Integer> normaisVertices;
    ArrayList<Float> cores;
    ArrayList<Integer> indicesTriangulos;

    //dados camera
    ArrayList<Float> positionCam;
    ArrayList<Float> dop;
    ArrayList<Float> vup;
    int angleView;

    //dados luz
    ArrayList<Float> positionLight;
    ArrayList<Float> colorLight;

    float eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ, lightX, lightY, lightZ;

    //variáveis globais que serão alteradas com o evento de toque
    public float mDeltaX;
    public float mDeltaY;

    //matrizes: rotação atual, acumulada, e uma matriz temporária
    private final float[] mCurrentRotation = new float[16];
    private float[] mTemporaryMatrix = new float[16];
    private float[] mTempMatrix = new float[16];
    private final float[] mAccumulatedRotation = new float[16];

    private  final Context mContexto;

    //handle da compilação e criação dos shaders
    private int mPerVertexProgramHandle;

    int indexCount;
    private VBO_IBO_Render vbo_ibo_render;

    //matriz modelo
    private float[] mModelMatrix = new float[16];

    /**
     * Matriz de Vista (Câmera). Transforma "world space" em "eye space".
     * Sua posição muda em relação ao "eye";
     */
    private float[] mViewMatrix = new float[16];

    /* Matriz de Projeção. Utilizada para projetar a cena em um ViewPort 2d*/
    private float[] mProjectionMatrix = new float[16];

    /* Armazena a matriz combinada - model, view, projection. É passada para o shader*/
    private float[] mMVPMatrix = new float[16];

    /**
     * Cópia da matriz modelo, para a posição da luz.
     */
    private float[] mLightModelMatrix = new float[16];

    /** Handles que serão passados nas transformações das matrizes */
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mLightPosHandle;

    /** Handle para a informação da posição na matriz modelo. */
    private int mPositionHandle;

    /** Handle com a informação das cores */
    private int mColorHandle;
    private int mColorLightHandle;

    /** Handle com a informação das normais */
    private int mNormalHandle;

    //...Texturas//
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;

    private final int mBytesPerFloat = 4;
    private static final int mBytesPerShort = 2;

    int k;

    /** Tamanho dos dados dos elementos. */
    private final int mPositionDataSize = 3;
    private final int mTextureCoordinateDataSize = 3;

    /** Tamanho dos dados de cor nos elementos. RGBA */
    private final int mColorDataSize = 4;

    /** Tamanho dos dados de normal. */
    private final int mNormalDataSize = 3;

    //Luz centralizada na origem
    private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};

    //Posição corrente da luz em coordenadas globais - após transformação via matriz modelo
    private final float[] mLightPosInWorldSpace = new float[4];

    //Posição corrente da luz em coordenadas locais - após transformação via matriz modelo
    private final float[] mLightPosInEyeSpace = new float[4];

    /** Handle para o ponto de luz */
    private int mPointProgramHandle;

    //handle para a textura mipmap
    private int mGrassDataHandle;

    //todos os dados do vértice
    final float[] mVertexData;

    private int STRIDE = (mPositionDataSize + mColorDataSize + mNormalDataSize + mTextureCoordinateDataSize)
            * mBytesPerFloat;

    private final int STRIDETEX = (mPositionDataSize + mNormalDataSize + mTextureCoordinateDataSize)
            * mBytesPerFloat;

    int floatsPerVertex = (mPositionDataSize+mColorDataSize+mNormalDataSize+mTextureCoordinateDataSize);
    int floatsPerVertexTex = (mPositionDataSize+mNormalDataSize+mTextureCoordinateDataSize);

    //construtor
    public ActOpenGLESRenderizadorVBOTex(Cena cena, final Context contexto) {

        mContexto = contexto;

        Cena i = cena;
        //recuperando camera, luz, ator
        ObjCamera objCamera = i.getObjCamera();
        ObjLight objLight = i.getObjLight();
        ObjActor objActor = i.getObjActor();

        //camera
        positionCam = objCamera.getPosition();
        dop = objCamera.getDop();
        vup = objCamera.getVup();
        angleView = objCamera.getAngle_view();

        //luz
        positionLight = objLight.getPosition();
        colorLight = objLight.getColor();

        //ator
        verticesData = objActor.getVertices();
        texturesData = objActor.getTextures();
        materialData = objActor.getMaterial();

        materialData_Ka.add(materialData.get(0));
        materialData_Ka.add(materialData.get(1));
        materialData_Ka.add(materialData.get(2));
        materialData_Ka.add(materialData.get(3));
        materialData_Kd.add(materialData.get(4));
        materialData_Kd.add(materialData.get(5));
        materialData_Kd.add(materialData.get(6));
        materialData_Kd.add(materialData.get(7));
        materialData_Ks.add(materialData.get(8));
        materialData_Ks.add(materialData.get(9));
        materialData_Ks.add(materialData.get(10));
        materialData_Ks.add(materialData.get(11));
        materialData_Ns.add(materialData.get(12));
        materialData_Tr.add(materialData.get(13));

        if (texturesData.size() > 0) { //se tem textura, não cria com o tamanho para as cores
            POSSUI_TEXTURAS = true;
            mVertexData = new float[objActor.getNumberOfVertices()*floatsPerVertexTex];

            STRIDE = (mPositionDataSize + mNormalDataSize + mTextureCoordinateDataSize)
                    * mBytesPerFloat;

        } else{ //se não tem textura, cria com tamanho extra para cores
            mVertexData = new float[objActor.getNumberOfVertices()*floatsPerVertex];
            POSSUI_TEXTURAS = false;
        }

        //arraylist de int contendo os índices dos triangulos
        indicesTriangulos = objActor.getTrianglesV();

        //arraylist de float contendo as normais
        //as normais estão sendo calculadas caso este valor não seja informado
        normais = objActor.getNormals();

        normaisVertices = objActor.getTrianglesVN(); //os índices das normais para cada vértice

        //arraylist de float contendo as cores
        cores = objActor.getColors();

        //todos os dados dos vértices neste vetor

        //converte o ArrayList<Float> em float[] - posicoes dos vertices
        positionData = new float[verticesData.size()];
        k = 0;

        for (Float f : verticesData){
            positionData[k++] = (f != null ? f : Float.NaN);
        }

        //converte o ArrayList<Float> em float[] - normais
        normaisF = new float[normais.size()*3];
        k = 0;

        for (Float j : normais){
            normaisF[k++] = (j != null ? j : Float.NaN);
        }

        //converte o ArrayList<Int> em int[] - vertices dos triangulos
        indicesTriangulosF = new int[indicesTriangulos.size()];
        k = 0;

        for (Integer j : indicesTriangulos){
            indicesTriangulosF[k++] = (j != null ?  j : 10000);

        }

        //... e de float para short...
        indicesTriangulosS = new short[indicesTriangulosF.length];
        k = 0;

        for (float j : indicesTriangulosF){
            indicesTriangulosS[k++] = (short) j;

        }

        //dados com as cores. 4 informações para cada vértice
        final float[] colorData = new float[verticesData.size()*4]; //cada vértice tem rgba, por isso *4
        int ka = 0;

        if(cores.size() == 0){//sem cor vinda do obj = definindo cor arbitrária
            for (int j = 0; j < (colorData.length); j++) {
                /*colorData[j++] = 1.0f;
                colorData[j++] = 0.627f;
                colorData[j++] = 0.478f;
                colorData[j] = 1.0f;*/
                colorData[j++] = 0.0f;
                colorData[j++] = 0.027f;
                colorData[j++] = 0.078f;
                colorData[j] = 1.0f;
            }
        }
        else{ //cor vinda do obj
            k = 0;
            for (float j : cores){
                colorData[k++] = j;

            }
        }

        //se coordenadas das texturas informadas no obj
        if (POSSUI_TEXTURAS){
            texturesDataF = new float[texturesData.size()*3];
            k = 0;
            if (texturesData.size() > 0) {
                for (float j : texturesData) {
                    texturesDataF[k++] = j;
                }
            }
        }

        //populando o array mVertexData com os dados dos vértices
        indexCount = indicesTriangulosF.length; //contador utilizado no "drawElements"

        //variáveis de controle por vértice, para os vetores de posição, textura, normais, cores
        int wpos = 0;
        int wtex = 0;
        int wnor = 0;
        int wcor = 0;
        int w, j = 0;

        float x, y, z;

        for (w=0; w< objActor.getNumberOfVertices(); w++){ //percorrendo todos os vértices...
            x = positionData[wpos++];
            y = positionData[wpos++];
            z = positionData[wpos++];

            mVertexData[j++] = x;
            mVertexData[j++] = y;
            mVertexData[j++] = z;

            if (normais.size()==0) {//se não vierem no obj, calcular normais utilizando produto interno
                final float[] planeVectorX = {1f, 0f, x};
                final float[] planeVectorY = {0f, 1f, y};
                final float[] normalVector = {
                        (planeVectorX[1] * planeVectorY[2]) - (planeVectorX[2] * planeVectorY[1]),
                        (planeVectorX[2] * planeVectorY[0]) - (planeVectorX[0] * planeVectorY[2]),
                        (planeVectorX[0] * planeVectorY[1]) - (planeVectorX[1] * planeVectorY[0])};

                // normalizando as normais
                final float length = Matrix.length(normalVector[0], normalVector[1], normalVector[2]);

                mVertexData[j++] = normalVector[0] / length;
                mVertexData[j++] = normalVector[1] / length;
                mVertexData[j++] = normalVector[2] / length;
            }
            else { // se vetor de normais não está nulo, popular com os dados do obj
                mVertexData[j++] = normaisF[wnor++];
                mVertexData[j++] = normaisF[wnor++];
                mVertexData[j++] = normaisF[wnor++];
            }

            //rgba, somente se não houver textura
            if (!POSSUI_TEXTURAS) {
                mVertexData[j++] = colorData[wcor++];
                mVertexData[j++] = colorData[wcor++];
                mVertexData[j++] = colorData[wcor++];
                mVertexData[j++] = colorData[wcor++];
            }

            //texturas
            if (POSSUI_TEXTURAS) {
                mVertexData[j++] = texturesDataF[wtex++];
                mVertexData[j++] = texturesDataF[wtex++];
                mVertexData[j++] = texturesDataF[wtex++];
            } else {
                mVertexData[j++] = 0;
                mVertexData[j++] = 0;
                mVertexData[j++] = 0;
            }
        }

        //        -- sem VBO --
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
        //
        //        mCubeTextureCoordinatesForPlaneBuffer = ByteBuffer.allocateDirect(textureCoordinateDataForPlane.length * mBytesPerFloat)
        //                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        //        mCubeTextureCoordinatesForPlaneBuffer.put(textureCoordinateDataForPlane).position(0);

        // Inicializa os buffers. - client-side buffers, não precisa mais do array de float depois
        //VBO - Vertex Buffer Object - todos os dados do vértice estão neste buffer
        mVertexDataBuffer = ByteBuffer.allocateDirect(mVertexData.length * mBytesPerFloat).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexDataBuffer.put(mVertexData).position(0);

        //buffer de indices tem que ser short - deve estar separado do VBO!
        mIndicesBuffer = ByteBuffer.allocateDirect(indicesTriangulosS.length * mBytesPerShort)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndicesBuffer.put(indicesTriangulosS).position(0);
    }

    // Chamada quando a superfície é criada ou tem seu tamanho alterado
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        vbo_ibo_render = new VBO_IBO_Render();

        // Set the background clear color to black.
        // GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //branco
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //cinza
        //GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        //Não desenha faces ocultas (back faces).
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        //Teste de profundidade
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            //alterar modos de iluminação...não implementado
            /*glUnused.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambient, 0);
            glUnused.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mf_ambientMaterial, 0);
            glUnused.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specular, 0);
            glUnused.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, Ns, 0);*/

        //Posição do olho, na frenteatrás da origem.
        eyeX =  positionCam.get(0);
        eyeY =  positionCam.get(1);
        eyeZ =  positionCam.get(2) - 0.5f;
            if (eyeZ == 0.0) //recuo da câmera - eixo z cresce para fora da tela. limite: 6
                eyeZ = 0.0f;

        //para onde olhar
            lookX  = dop.get(0);
            lookY  = dop.get(1);
            lookZ  = dop.get(2) - 50.0f;

        // Seta o VUP
//        if (vup != null) {
//            upX = vup.get(0);
//            upY = vup.get(1);
//            upZ = vup.get(2);
//        } else {
        upX = 0.0f;
        upY = 1.0f;
        upZ = 0.0f;
//        }

        //seta a Matriz de Vista 'mViewMatrix', que representa a posição da câmera
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        //Stringfy o Vertex Shader e o Fragment Shader
        if (POSSUI_TEXTURAS) {
            vertexShader = RawResourceReader.readTextFileFromRawResource(mContexto, R.raw.per_pixel_vertex_shader_tex);
            fragmentShader = RawResourceReader.readTextFileFromRawResource(mContexto, R.raw.per_pixel_fragment_shader_tex);
        } else {
            vertexShader = RawResourceReader.readTextFileFromRawResource(mContexto, R.raw.per_pixel_vertex_shader);
            fragmentShader = RawResourceReader.readTextFileFromRawResource(mContexto, R.raw.per_pixel_fragment_shader);
        }

        // Carrega os shaders.
        final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        String[] stringShader;
        if (POSSUI_TEXTURAS) { //caso possua, passo a informação para o shader
            stringShader = new String[] {"a_Position", "a_Normal", "a_TexCoordinate"};
        }
        else
            stringShader = new String[] {"a_Position",  "a_Color", "a_Normal"};

        //finalmente, criar e lincar o programa com os shaders. retorno=handle
        mPerVertexProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                stringShader);

        // Shaders para o ponto de luz.
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

        //novamente, agora para o ponto de luz
        final int pointVertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
        mPointProgramHandle = createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                new String[] {"a_Position"});

        //Tratando texturas: leitura figura - gerando mipmap
//        mGrassDataHandle = TextureHelper.loadTexture(mContexto, R.drawable.farmhouse_texture);
            mGrassDataHandle = TextureHelper.loadTexture(mContexto, R.drawable.wood_floor_by_gnrbishop);
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGrassDataHandle);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Inicializa a matriz de rotação acumulada
        Matrix.setIdentityM(mAccumulatedRotation, 0);
    }

    //chamado para desenhar o frame corrente
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        // Seta a OpenGL viewport com o mesmo tamanho da superfície
        GLES20.glViewport(0, 0, width, height);

        //Cria uma nova Matriz de Projeção. Altura não muda; largura varia conforme razão de aspecto
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 1000.0f;
        //seta a Matriz de Projeção...
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

        //para câmera perspectiva
        //Matrix.perspectiveM(mProjectionMatrix, 0, angleView, ratio, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

            /*//Rotacionar objeto
            long time = SystemClock.uptimeMillis() % 10000L;
            long slowTime = SystemClock.uptimeMillis() % 100000L;
            float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
            float slowAngleInDegrees = (360.0f / 100000.0f) * ((int) slowTime);*/

        GLES20.glUseProgram(mPerVertexProgramHandle);

        //  Handles dos shaders
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Normal");
        mColorLightHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_ColorLight");
        if (POSSUI_TEXTURAS) {
            mColorHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Color");
            mTextureCoordinateHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_TexCoordinate");
        }

        // Calculando a posição da luz - transformações na matriz modelo
        Matrix.setIdentityM(mLightModelMatrix, 0);

        if (positionLight != null) {
            lightX = positionLight.get(0);
            lightY = positionLight.get(1);
            lightZ = positionLight.get(2);
        } else {
            lightX = 0.0f;
            lightY = 0.0f;
            lightZ = -2.7f;
        }

        //Trabalhar com os dados informados: Matrix.translateM(mLightModelMatrix, 0, lightX, lightY, lightZ);
        //fixa:
        Matrix.translateM(mLightModelMatrix, 0, 0, 0, -3.7f);

        //multiplica a matriz mLightPosInModelSpace pela mLightModelMatrix e armazena na mLightPosInWorldSpace
            //para converter coordenadas no espaço de câmera em globais
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        // multiplica mLightPosInWorldSpace pela mViewMatrix e armazena na mLightPosInEyeSpace
            //para converter coordenadas globais em locais
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        // Desenha ator
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -25.0f); //translação -z

        //Rotacionar objeto
            //rotaciona em x
            //Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.0f);
            //rotaciona em y
            //        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
            //rotaciona em z
            //        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);

        // Setar uma matriz com a rotação corrente - mDeltaX e mDeltaY são alterados pelos eventos de toque
        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        mDeltaX = 0.0f;
        mDeltaY = 0.0f;

        //aplicando zoom - valores vindos de ActOpenGLESView.java pelos eventos de toque
        Matrix.scaleM(mModelMatrix, 0, zoom, zoom, zoom);

        // Multiplica a matriz de rotação acumulada pela corrente e seta uma matriz temporária
        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
        //copia a matriz mTemporaryMatrix para a mAccumulatedRotation
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);

        //..para rotacionar o objeto levando em conta a o valor total
        // multiplica a matriz de rotação acumulada pela matriz modelo e armazena na temporária
        Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotation, 0);
        // copia a temp para a modelo
        System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);

        //desenha o ator
        vbo_ibo_render.render();

        //desenha a luz
        GLES20.glUseProgram(mPointProgramHandle);
        drawLight();
    }

    private void drawLight()
    {
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(mPointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(mPointProgramHandle, "a_Position");

        //Posição...
        GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);

        // Buffer Object não utilizado aqui, desabiliza vertex array para este atributo
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        // Transformações de matrizes
        // multiplica a mLightModelMatrix pela mViewMatrix e armazena na mTempMatrix
        Matrix.multiplyMM(mTempMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
        // multiplica a mTempMatrix pela mProjectionMatrix e armazena na mMVPMatrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mTempMatrix, 0);
        //passa para o atributo do shader a matriz transformada
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Desenha o ponto.
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
            //criando um buffer para os dados dos vértices e um para os índices
            GLES20.glGenBuffers(1, vbo, 0);
            GLES20.glGenBuffers(1, ibo, 0);

            //vbo = dados dos vértices...
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexDataBuffer.capacity()
                    * mBytesPerFloat, mVertexDataBuffer, GLES20.GL_STATIC_DRAW);

            //ibo = índices
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndicesBuffer.capacity()
                    * mBytesPerShort, mIndicesBuffer, GLES20.GL_STATIC_DRAW);

            //libera os buffers
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        void render() {
            if (vbo[0] > 0 && ibo[0] > 0) {
                //bind do vbo
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

                if (POSSUI_TEXTURAS) {
                    STRIDE = STRIDETEX;
                }

                // Bind atributos
                GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                        STRIDE, 0);
                GLES20.glEnableVertexAttribArray(mPositionHandle);

                GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false,
                        STRIDE, mNormalDataSize * mBytesPerFloat);
                GLES20.glEnableVertexAttribArray(mNormalHandle);

                if (POSSUI_TEXTURAS) {
                    GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                            STRIDE, (mPositionDataSize + mNormalDataSize) * mBytesPerFloat);
                    GLES20.glEnableVertexAttribArray(mColorHandle);
                }


                //bind dos índices
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);

                //passos e transformações finais para desenhar...

                //multiplica a matriz modelo pela matriz de vista e armazena na matriz mMVPMatrix
                Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

                // Passa a matriz mMVPMatrix (model*view) para o Handle
                GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

                // Multiplica a matriz Modelo-Vista pela matriz de projeção e armazena o resultado em uma matriz temporária
                Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
                //copia a temporária para a matriz mMVPMatrix
                System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);
                // Passa a matriz combinada (model*view*projection)
                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

                // Passa a informação da luz em posição local
                GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

                //..finalmente, desenha:
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);
                //com wireframe
//                GLES20.glDrawElements(GLES20.GL_LINE_STRIP, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);

                //libera os buffers
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
            }
        }
    }

}
