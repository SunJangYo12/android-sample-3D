/*

* BAB 11
* Adding Terain
*
*/

package com.gles.GameTerain;

import android.util.Log;
import static android.opengl.GLSurfaceView.Renderer;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glValidateProgram;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLUtils.texImage2D;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.setRotateEulerM;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.content.Context;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gles.R;

/*
Sekarang kita memiliki latar belakang yang bagus dengan beberapa
awan badai dicakrawala, sudah saatnya kita untuk mulai menambahkan
beberapa zat ke dunia kita. dalam bab ini kami akan belajar cara menggunakan
peta ketinggian untuk menambahkan bebrapa medan di scene(tempat kejadian).
ketiak kami melakukan ini kami akan membahas area baru di OpenGL dan
belajar cara menggunakan depth(kedalaman) buffer untuk mencegah overdraw
dan kami juga akan belajar cara menyimpan vertex dan index data langsung
pada GPU untuk kinerja yang lebih baik.
    highmap(peta tinggi) adalah cara mudah untuk menambahkan medan ke
adegan dan mereka dapat dengan mudah dihasilkan atau diedit menggunakan
program paint(cat) biasa. depth buffer itu sendiri adalah bagian mendasar
dari OpenGL dan itu membantu kita dengan mudah membuat adegan yang lebih
komplex tanpa terlalu mengkhawatirkan tentang bagaimana adegan itu disatukan.
inilah rencara game kami:
    1. pertama kita akan melihat cara membuat highmap dan membuatnya kedalam
       aplikasi kita menggunakan object buffer vertex dan buffer index.
    2. kami kemudian akan melihat tampilan pertama kami di pemusnahan dan
       depth buffer, dua teknik untuk menyumbat object tersembunyi.

highmap hanyalah peta ketinggian dua dimensi seperti peta topgrafi yang
dapat anda temukan di atlas. cara sederhana untuk membuat highmap adalah
dengan menggunakan gambar skala abu-abu dengan area cahaya yang mewakili
dataran tinggi dan area gelap yang memiliki dataran rendah. karena ini
hanya gambar kita dapat menggambar highmap kita sendiri menggunakan program
paint biasa dan akan berakhir seperti dipage:234. anda dapat mengunduh highmap
dari terain(medan) yang nyata online seperti National elevation dataset.
*/

public class GameTerain implements Renderer
{
    private int tutorial = 0;
    public static final int BYTES_PER_FLOAT = 4;
    public static final int BYTES_PER_SHORT = 2;
    private Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    //menambah matrix untuk heightmap
    private final float[] modelMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] viewMatrixForSky = new float[16];
    private final float[] tempMatrix = new float[16];

    private PartikelShaderProgram particlesProgram;
    private PartikelSystem particlesSystem;
    private PartikelShooter redParticleShooter;
    private PartikelShooter greenParticleShooter;
    private PartikelShooter blueParticleShooter;
    private long globalStartTime;
    private int texture;

    private SkyboxShaderProgram skyboxProgram;
    private Skybox skybox;
    private int skyboxTexture;
    private float xRotation, yRotation;

    private HeightMapShaderProgram heightmapProgram;
    private Heightmap heightMap;

    public GameTerain() {
    }
    public GameTerain(Context context, int tutorial) {
        this.context = context;
        this.tutorial = tutorial;
    }

    public GameTerain(Context context) 
    {
        this.context = context;
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        String skyBoxVertexShader = getShaderCode("skyVertex");
        String skyBoxFragmentShader = getShaderCode("skyFragmen");
        String partikelVertexShader = getShaderCode("parvertex");
        String partikelFragmentShader = getShaderCode("parfragmen");
        String heightmapVertexShader = getShaderCode("hmVertex");
        String heightmapFragmentShader = getShaderCode("hmFragmen");

        heightmapProgram = new HeightMapShaderProgram(heightmapVertexShader, heightmapFragmentShader, tutorial);
        heightMap = new Heightmap(((BitmapDrawable)context.getResources()
            .getDrawable(R.drawable.heightmap)).getBitmap());

        skyboxProgram = new SkyboxShaderProgram(skyBoxVertexShader, skyBoxFragmentShader, tutorial);
        skybox = new Skybox();
    
        particlesProgram = new PartikelShaderProgram(partikelVertexShader, partikelFragmentShader, tutorial);
        particlesSystem = new PartikelSystem(10000);
        globalStartTime = System.nanoTime();

        texture = TextureHelper.loadTexture(context, R.drawable.particle_texture);
        skyboxTexture = TextureHelper.loadCubeMap(context, new int[] {
            R.drawable.left, R.drawable.right,
            R.drawable.bottom, R.drawable.top,
            R.drawable.front, R.drawable.back
        });

        final Geometry.Vector particleDirection = new Geometry.Vector(0f, 0.5f, 0f);

        final float angelVarianceInDegrees = 5f;
        final float speedVariance = 1f;

        redParticleShooter = new PartikelShooter(
            new Geometry.Point(-1f, 0f, 0f),
            particleDirection,
            Color.rgb(255, 50, 5),
            angelVarianceInDegrees,
            speedVariance);

        greenParticleShooter = new PartikelShooter(
            new Geometry.Point(0f, 0f, 0f),
            particleDirection,
            Color.rgb(25, 255, 25),
            angelVarianceInDegrees,
            speedVariance);
        
        blueParticleShooter = new PartikelShooter(
            new Geometry.Point(1f, 0f, 0f),
            particleDirection,
            Color.rgb(5, 50, 255),
            angelVarianceInDegrees,
            speedVariance);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float)height, 1f, 100f);
        updateViewMatrixs();
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        glClear(GL_COLOR_BUFFER_BIT);

        drawHeightmap();
        drawSkybox();
        drawParticles();
    }

    /*
    setelah update drawSkybox() dan drawParticels() dengan mengganti
    referensi viewProjectionMatrix yang hilang dengan modelViewProjectionMatrix
    kami sekarang merawat rotasi kamera dan mendorong barang-barang ke dalam
    adegan dengan matrix tampilan jadi kami tidak perlu lagi menyalin/menempel
    kode peraturan matrix untuk setiap object. Dengan memeanggil setIdentityM(modelMatrix, 0)
    kami mengatur ulang matrix model ke matrix identitas yang tidak melakukan
    apa-apa jadi ketika kami mengalikan semua matrix bersama-sama di updateMvMatrix()
    hanya tampilan matrix dan proyeksi.
    */
    private void drawSkybox() {
        setIdentityM(modelMatrix, 0);
        updateMvpMatrixForskybox();
        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(viewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticle(particlesSystem, currentTime, 5);
        greenParticleShooter.addParticle(particlesSystem, currentTime, 5);
        blueParticleShooter.addParticle(particlesSystem, currentTime, 5);

        setIdentityM(modelMatrix, 0);
        updateMvpMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particlesProgram.useProgram();
        particlesProgram.setUniforms(viewProjectionMatrix, currentTime, texture);
        particlesSystem.bindData(particlesProgram);
        particlesSystem.draw();

        glDisable(GL_BLEND);
    }

    /*
    kali ini kita menggunakan modelMatrix untuk membuat heightmap 100 kali
    lebih lebar diarah x dan z serta 10 kali lebih tinggi ke arah y, karena
    kita tidak ingin gunung terlalu extrem. Tunggu bukankah ini mengacaukan
    interpolasi warna dishader kami, karena itu tergantung pada posisi v vertex?
    itu tidak akan karena dishader yang kami baca di posisi titik sebelum kami
    mengalikanya dengan matrix.
        kita perlu memperbarui matrix proyeksi untuk memberi kita cukup ruang
    jadi di onSurfaceChanged() ubah parameter terakhir ke perspektif() ke 100f
    ini akan mengatur proyeksi sehingga kita dapat menggambar barang hingga
    seratus unit jauh sebelum diclipped oleh far(jauh) plane.
    */
    private void drawHeightmap() {
        setIdentityM(modelMatrix, 0);
        scaleM(modelMatrix, 0, 100f, 10f, 100f);
        updateMvpMatrix();
        heightmapProgram.useProgram();
        heightmapProgram.setUniforms(modelViewProjectionMatrix);
        heightMap.bindData(heightmapProgram);
        heightMap.draw();
    }

    /*
    kita perlu membuat beberapa perubahan lagi untuk mengurangi jumlah
    salinan/tempel dengan kode matrix kita. kita dapat melakukan ini dengan
    menggunakan satu matrix untuk mewakili kamera untuk semua object kita
    dan matrix kedua untuk skybox yang represent hanya rotasi.
        dengan menggunakan kode ini, kita dapat menggunakan viewMatrix untuk
    menerapkan rotasi dan translate yang sama ke hightmap dan partikel dan
    kita dapat menggunakan viewMatrixForSkybox untuk menerapkan rotasi ke skybox.
    */
    private void updateViewMatrixs() {
        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        System.arraycopy(viewMatrix, 0, viewMatrixForSky, 0, viewMatrix.length);

        /*
        kami ingin translate untuk diterapkan pada matrix tampilan
        biasa tapi tidak untuk skybox 
        */
        translateM(viewMatrix, 0, 0, -1.5f, -5f);
    }

    /*
    kami juga membutuhkan beberapa metode pembantu baru untuk melipatgandakan
    matrix bersama-sama menjadi matrix proyeksi tampilan final, gabungan, tergantung
    pada apakah kita menggambar skybox atau menggambar sesuatu yang lain.
        kita perlu menggunakan matrix sementara untuk menahan hasil perantara
    karena metode ini akan mengacaukan matrix jika kita menggunakan matrix yang
    sama dengan destinasi dan sebagai operan.
    */
    private void updateMvpMatrix() {
        multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }
    private void updateMvpMatrixForskybox() {
        multiplyMM(tempMatrix, 0, viewMatrixForSky, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }

    private String getShaderCode(String options) {
        if (options.equals("parvertex")) {
            StringBuffer strbuffV = new StringBuffer();
            strbuffV.append("uniform mat4 u_Matrix;\n");
            strbuffV.append("uniform float u_Time;\n");
            strbuffV.append("attribute vec3 a_Position;\n");  
            strbuffV.append("attribute vec3 a_Color;\n");
            strbuffV.append("attribute vec3 a_DirectionVector;\n");
            strbuffV.append("attribute float a_ParticleStartTime;\n");
            strbuffV.append("varying vec3 v_Color;\n");
            strbuffV.append("varying float v_ElapsedTime;\n");
            strbuffV.append("void main()         \n");           
            strbuffV.append("{                   \n");                   
            strbuffV.append("    v_Color = a_Color;\n");
            strbuffV.append("    v_ElapsedTime = u_Time - a_ParticleStartTime;\n");    
            strbuffV.append("    vec3 currentPosition = a_Position + (a_DirectionVector * v_ElapsedTime);\n");
            strbuffV.append("    float grafityFactor = v_ElapsedTime * v_ElapsedTime / 8.0;\n");
            strbuffV.append("    currentPosition.y -= grafityFactor;\n");
            strbuffV.append("    gl_Position = u_Matrix * vec4(currentPosition, 1.0);\n");
            strbuffV.append("    gl_PointSize = 25.0;\n");
            strbuffV.append("}   ");
            return strbuffV.toString();
        }
        else if (options.equals("parfragmen")) {
            StringBuffer strbuff = new StringBuffer();
            strbuff.append("precision mediump float;    \n");
            strbuff.append("varying vec3 v_Color;       \n");
            strbuff.append("varying float v_ElapsedTime;\n");
            strbuff.append("uniform sampler2D u_TextureUnit;");
            strbuff.append("void main()                 \n");
            strbuff.append("{                           \n");
            strbuff.append("  float xDistance = 0.5 - gl_PointCoord.x;\n");
            strbuff.append("  float yDistance = 0.5 - gl_PointCoord.y;\n");
            strbuff.append("  float distanceFromCenter = sqrt(xDistance * xDistance + yDistance * yDistance);\n");
            strbuff.append("  if (distanceFromCenter > 0.5) {\n");
            strbuff.append("      discard;\n");
            strbuff.append("  } else {\n");
            strbuff.append("      gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0) * texture2D(u_TextureUnit, gl_PointCoord);\n");
            strbuff.append("  }");
            strbuff.append("}");
            return strbuff.toString();
        }
        else if (options.equals("skyVertex")) {
            StringBuffer strbuffS = new StringBuffer();
            strbuffS.append("uniform mat4 u_Matrix;\n");
            strbuffS.append("attribute vec3 a_Position;\n");  
            strbuffS.append("varying vec3 v_Position;\n");
            strbuffS.append("void main()         \n");           
            strbuffS.append("{                   \n");                   
            strbuffS.append("    v_Position   =  a_Position;\n");
            strbuffS.append("    v_Position.z = -v_Position.z;\n");
            strbuffS.append("    gl_Position = u_Matrix * vec4(a_Position, 1.0);\n");
            strbuffS.append("    gl_Position = gl_Position.xyww;\n");
            strbuffS.append("}   ");
            return strbuffS.toString();
        }
        else if (options.equals("skyFragmen")) {
            StringBuffer strbuffSF = new StringBuffer();
            strbuffSF.append("precision mediump float;    \n");
            strbuffSF.append("uniform samplerCube u_TextureUnit;");
            strbuffSF.append("varying vec3 v_Position;       \n");
            strbuffSF.append("void main()                 \n");
            strbuffSF.append("{                           \n");
            strbuffSF.append("   gl_FragColor = textureCube(u_TextureUnit, v_Position);\n");
            strbuffSF.append("}");
            return strbuffSF.toString();
        }
        else if (options.equals("hmVertex")) 
        {
            /*
            Shader vertex ini menggunakan fungsi shader baru mix() untuk
            menginterpolasi dengan lancar antara dua warna berbeda. kami
            mengatur heightmap kami sehingga ketinggianya antara 0 dan 1
            dan kami menggunakan tinggi ini sebagai rasio antara dua warna
            Heightmap akan tampak hijau didekat bagian bawah dan abu abu
            didekat bagian atas.
            */
            StringBuffer strbuffHMV = new StringBuffer();
            strbuffHMV.append("uniform mat4 u_Matrix;\n");
            strbuffHMV.append("attribute vec3 a_Position;\n");  
            strbuffHMV.append("varying vec3 v_Color;\n");
            strbuffHMV.append("void main()         \n");           
            strbuffHMV.append("{                   \n");                   
            strbuffHMV.append("    v_Color = mix(vec3(0.180, 0.467, 0.153),\n");  //a dark green
            strbuffHMV.append("                  vec3(0.660, 0.670, 0.680), a_Position.y);\n"); //a stony(bebatu) green
            strbuffHMV.append("    gl_Position = u_Matrix * vec4(a_Position, 1.0);\n");
            strbuffHMV.append("}   ");
            return strbuffHMV.toString();
        }
        else if (options.equals("hmFragmen")) {
            StringBuffer strbuffHMF = new StringBuffer();
            strbuffHMF.append("precision mediump float;    \n");
            strbuffHMF.append("varying vec3 v_Color;       \n");
            strbuffHMF.append("void main()                 \n");
            strbuffHMF.append("{                           \n");
            strbuffHMF.append("   gl_FragColor = vec4(v_Color, 1.0);\n");
            strbuffHMF.append("}");
            return strbuffHMF.toString();
        }
        return "";
    }
    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;
        
        if (yRotation < -90)
            yRotation = -90;
        else if(yRotation > 90)
            yRotation = 90;
    }

}

class MatrixHelper 
{
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f)
    {
        final float angleInRadians = (float)(yFovInDegrees * Math.PI / 180.0);
        final float a = (float)(1.0 / Math.tan(angleInRadians / 2.0));

        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;
        m[4] = 0f;
        m[5] = a;   //beda
        m[6] = 0f;
        m[7] = 0f;
        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;
        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;
    }
}

class TextureHelper
{
    public static int loadTexture(Context context, int resourceID)
    {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            Log.i("setsuna", "Could not generate a new OpenGL texture object.");
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceID, options);

        if (bitmap == null) {
            Log.i("setsuna", "Resource ID "+resourceID+ " could not be decoded.");
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        glGenerateMipmap(GL_TEXTURE_2D);

        bitmap.recycle();

        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }

    public static int loadCubeMap(Context context, int[] cubeResource) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            Log.i("setsuna", "Could not generate a new OpenGL texture object.");
            return 0;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap[] cubeBitmap = new Bitmap[6];

        //load image into bitmap array
        for (int i=0; i<6; i++) {
            cubeBitmap[i] = BitmapFactory.decodeResource(context.getResources(),
                                                         cubeResource[i], options);
            if (cubeBitmap[i] == null) {
                Log.i("setsuna", "Resource ID "+cubeResource[i]+" could not be decoded.");
                glDeleteTextures(1, textureObjectIds, 0);
                return 0;
            }
        }

        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmap[0], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmap[1], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmap[2], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmap[3], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmap[4], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmap[5], 0);

        glBindTexture(GL_TEXTURE_2D, 0);
        for (Bitmap bitmap : cubeBitmap)
            bitmap.recycle();

        return textureObjectIds[0];
    }
}

class VertexArray {
    private final FloatBuffer floatBuffer;
    
    public VertexArray(float[] vertexData) {
        floatBuffer = ByteBuffer
            .allocateDirect(vertexData.length * GameTerain.BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride)
    {
        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, floatBuffer);
        glEnableVertexAttribArray(attributeLocation);

        floatBuffer.position(0);
    }

    public void updateBuffer(float[] vertexData, int start, int count) {
        floatBuffer.position(start);
        floatBuffer.put(vertexData, start, count);
        floatBuffer.position(0);
    }
}

/*
Creating Vertex dan Index buffer objects
    untuk memuat highmap kita akan menggunakan dua object OpenGL baru
object buffer vertex dan object buffer index. kedua object ini analog
dengan array vertex dan array index yang telah kami gunakan dalam bab
bab sebelumnya, kecuali bahwa driver grafis dapat memilih untuk menempatkanya
langsung dalam memori GPU. ini dapat menyebabkan kinerja yang lebih baik
untuk object yang tidak sering kita ubah begitu mereka telah dibuat
seperti pada highmap. Object buffer tidak terlalu cepat karena itu
kita menggunakan dua opsi.
*/
//Creating Vertex buffer Object
class VertexBuffer {
    private final int bufferId;

    public VertexBuffer(float[] vertexData) 
    {
        /*
        untuk mengirim data titik ke object buffer vertex pertama-tam
        kita membuat object buffer baru menggunakan glGenBuffers()
        metode ini mengambil array, jadi kami membuat array satu elemen
        baru untuk menyimpan id buffer baru. kami kemudian mengikat buffer
        dengan panggilan glBindBuffer() melewati GL_ARRAY_BUFFER untuk
        memberi tau OpenGL bahwa ini adalah buffer vertex.
            untuk menyalin data ke object buffer kami harus terlebih dahulu
        mentransfernya ke memori native seperti yang biasa kita lakukan dengan
        VertexArray. setelah ada disana kami dapat mentransfer data ke objek
        buffer dengan panggilan ke glBufferData().
            ketika kami selesai memuat data ke dalam buffer kami perlu memastikan
        bahwa kami mengikat buffer dengan memanggil glBindBuffer() dengan 0
        sebagai ID buffer, kalau tidak panggilan ke fungsi seperti glVertexAttribPointer()
        ditempat lain dalam kode kami tidak akan berfungsi dengan baik.
            
            glBufferData(int target, int size, Buffer data, int usage)

            - int target: ini harus GL_ARRAY_BUFFER untuk object buffer vertex
                          atau GL_ELEMENT_ARRAY_BUFFER untuk objec buffer index
            - int size  : ini adalah ukuran data dalam byte
            - Buffer data : ini harus menjadi object buffer yang dibuat dengan
                            allocationDirect()
            - int usage   : ini memberi tau OpenGL pola penggunaan yang diharapkan
                            untuk object buffer ini. berikut adalah pilihanya:
                - GL_STREAM_DRAW : object ini hanya dimodifikasi sekali dan hanya
                                   digunakan beberapa kali.
                - GL_STATIC_DRAW : object ini akan dimodifikasi sekali tetapi akan
                                   digunakan berkali-kali.
                - GL_DYNAMIC_DRAW : objet ini akan dimodifikasi dan digunakan berkali-kali
                            ini adalah petunjuk daripada kendala sehingga OpenGL
                            dapat melakukan optimasi pada akhirnya. kami menggunakan
                            GL_STATIC_DRAW disebagian besar kode.
        */

        //Alocate a buffer
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);

        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new vertex buffer objects.");
        }
        
        bufferId = buffers[0];

        //bind to the buffer
        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);

        //transfer data to native memory
        FloatBuffer vertexArray = ByteBuffer
            .allocateDirect(vertexData.length * GameTerain.BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData);

        vertexArray.position(0);

        //transfer data from native memory to the GPU buffer
        glBufferData(GL_ARRAY_BUFFER, vertexArray.capacity() * GameTerain.BYTES_PER_FLOAT, vertexArray, GL_STATIC_DRAW);
    
        //PENTING: unbind dari buffer saat kita selesai dengan itu.
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /*
    perbedaan utama disini adalah bahwa kita sekarang perlu mengikat
    buffer sebelum memanggil glVertexAttribPointer() dan kita menggunakan
    glVertexAttribPointer() yang sedikit berbeda yang mengambil int sebagai
    pengganti buffer sebagai parameter terakhir. Integer ini memberi tau OpenGL
    yang diimbangi dalam byte untuk atribut saat ini, ini bisa menjadi 0 untuk
    atribut pertama atau offset byte tertentu untuk atribut selanjutnya
    */
    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride)
    {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, dataOffset);
        glEnableVertexAttribArray(attributeLocation);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}

/*
Creating index buffer object
*/
class IndexBuffer {
    private final int bufferId;

    public IndexBuffer(short[] vertexData) 
    {
        //Alocate a buffer
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);

        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new vertex buffer objects.");
        }
        
        bufferId = buffers[0];

        //bind to the buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0]);

        //transfer data to native memory
        ShortBuffer vertexArray = ByteBuffer
            .allocateDirect(vertexData.length * GameTerain.BYTES_PER_SHORT)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(vertexData);

        vertexArray.position(0);

        //transfer data from native memory to the GPU buffer
        glBufferData(GL_ARRAY_BUFFER, vertexArray.capacity() * GameTerain.BYTES_PER_SHORT, vertexArray, GL_STATIC_DRAW);
    
        //PENTING: unbind dari buffer saat kita selesai dengan itu.
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    /*
    kita perlu menggunakan ID buffer ketika kita menggunakanya
    untuk menggambar jadi tambahkan accessor untuk itu.
    */
    public int getBufferId() {
        return bufferId;
    }
}

abstract class ShaderProgram 
{

    //partikel konstanta
    protected static final String U_TIME = "u_Time";
    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

    //uniform konstanta
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    //atribut konstanta
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    //adding for object color
    protected static final String U_COLOR = "u_Color";

    protected final int program;
    protected ShaderProgram(String vertexShaderCode, String fragmentShaderCode) {

        program = buildProgram(vertexShaderCode, fragmentShaderCode);
    }

    public void useProgram() {
        glUseProgram(program);
    }

    private int compileShader(int type, String shader) 
    {
        final int shaderObjectId = glCreateShader(type);

        if (shaderObjectId == 0) {
            Log.i("setsuna", "Could not create new shader");
        }
        else {
            glShaderSource(shaderObjectId, shader);
            glCompileShader(shaderObjectId);

            final int[] compileStatus = new int[1];
            glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
        
            if (compileStatus[0] == 0)
            {
                glDeleteShader(shaderObjectId);
                Log.i("setsuna", "Compile of shader failed: "+shader);
            }
            else {
                Log.i("setsuna", "Results of compiling source: "+shader+"\n"+
                    glGetShaderInfoLog(shaderObjectId));
            }
        }
        return shaderObjectId;
    }

    private int buildProgram(String vertexShaderCode, String fragmentShaderSource) {
        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);

        final int programObjectId = glCreateProgram();

        if (programObjectId == 0) {
            Log.i("setsuna", "Could not create new program");
        }
        else {
            glAttachShader(programObjectId, vertexShader);
            glAttachShader(programObjectId, fragmentShader);

            glLinkProgram(programObjectId);

            final int[] linkStatus = new int[1];
            glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == 0) {
                glDeleteProgram(programObjectId);
                Log.i("setsuna", "Linking of program failed.");
            }
            else {
                glValidateProgram(programObjectId);

                final int[] validateStatus = new int[1];
                glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);

                if (validateStatus[0] == 0) {
                    Log.i("setsuna", "Results of validating program: "+validateStatus[0]
                        +"\nLog: "+glGetProgramInfoLog(programObjectId));
                }
            }
        }
        return programObjectId;
    }
}

class Geometry
{
    public static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public Point translateY(float distance) {
            return new Point(x, y + distance, z);
        }
        public Point translate(Vector vector) {
            return new Point(x + vector.x,
                             y + vector.y,
                             z + vector.z);
        }
    }

    public static class Circle {
        public final Point center;
        public final float radius;

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
        public Circle scale(float scale) {
            return new Circle(center, radius * scale);
        }
    }

    public static class Cylinder {
        public final Point center;
        public final float radius;
        public final float height;

        public Cylinder(Point center, float radius, float height) {
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }

    public static class Ray {
        public final Point point;
        public final Vector vector;

        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    public static class Vector {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length() {
            //return FloatMath.sqrt(x * x + y * y + z * z);
            return (float)Math.sqrt(x * x + y * y + z * z);
        }

        //https://en.wikipedia.org/wiki/Cross_product
        public Vector crossProduct(Vector other) {
            return new Vector(
                (y * other.z) - (z * other.y),
                (z * other.x) - (x * other.z),
                (x * other.y) - (y * other.x));
        }
        public float dotProduct(Vector other) {
            return x * other.x +
                   y * other.y +
                   z * other.z;
        }

        public Vector scale(float f) {
            return new Vector(x * f, y *f, z * f);
        }
    }

    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(to.x-from.x, to.y-from.y, to.z-from.z);
    }
   
    public static class Sphere {
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }
    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    public static float distanceBetween(Point point, Ray ray) {
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);

        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();

        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }

    public static class Plane {
        public final Point point;
        public final Vector normal;

        public Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;
        }
    }
   
    public static Point intersectionPoint(Ray ray, Plane plane) {
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);
        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal) /
                            ray.vector.dotProduct(plane.normal);

        Point intersectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
        return intersectionPoint;
    }
}

class PartikelShaderProgram extends ShaderProgram {
    private int tutorial = 0;
    //uniform location
    private final int uMatrixLocation;
    private final int uTimeLocation;
    private int uTextureUnitLocation = 0;

    //Atributee location
    private final int aPositionLocation;
    private final int aColorLocation;
    private final int aDirectionVectorLocation;
    private final int aParticleStartTimeLocation;

    public PartikelShaderProgram(String partikelVertexShader, String partikelFragmentShader, int tutorial) {
        super(partikelVertexShader, partikelFragmentShader);

        //ambil lokasi uniform untuk program shader
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTimeLocation = glGetUniformLocation(program, U_TIME);

        this.tutorial = tutorial;
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        //ambil lokasi attribut untuk program shader
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aDirectionVectorLocation = glGetAttribLocation(program, A_DIRECTION_VECTOR);
        aParticleStartTimeLocation = glGetAttribLocation(program, A_PARTICLE_START_TIME);
    }

    public void setUniforms(float[] matrix, float elapsedTime, int textureID) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uTimeLocation, elapsedTime);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getColorAttributeLocation() {
        return aColorLocation;
    }
    public int getDirectionVectorAttributeLocation() {
        return aDirectionVectorLocation;
    }
    public int getParticleStartTimeAttributeLocation() {
        return aParticleStartTimeLocation;
    }
}

class PartikelSystem {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;

    private static final int TOTAL_COMPONENT_COUNT =
                    POSITION_COMPONENT_COUNT +
                    COLOR_COMPONENT_COUNT +
                    VECTOR_COMPONENT_COUNT +
                    PARTICLE_START_TIME_COMPONENT_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * new GameTerain().BYTES_PER_FLOAT;
    private final float[] particles;
    private final VertexArray vertexArray;
    private final int maxParticleCount;
    private int currentParticleCount;
    private int nextParticle;

    public PartikelSystem(int maxParticleCount) {
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        vertexArray = new VertexArray(particles);
        this.maxParticleCount = maxParticleCount;
    }

    public void addParticle(Geometry.Point position, int color, Geometry.Vector direction, float particleStartTime)
    {
        final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;
        int currentOffset = particleOffset;
        nextParticle++;

        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++;
        }
        if (nextParticle == maxParticleCount) {
            nextParticle = 0;
        }

        particles[currentOffset++] = position.x;
        particles[currentOffset++] = position.y;
        particles[currentOffset++] = position.z;

        particles[currentOffset++] = Color.red(color) / 255f;
        particles[currentOffset++] = Color.green(color) / 255f;
        particles[currentOffset++] = Color.blue(color) / 255f;

        particles[currentOffset++] = direction.x;
        particles[currentOffset++] = direction.y;
        particles[currentOffset++] = direction.z;

        particles[currentOffset++] = particleStartTime;

        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void bindData(PartikelShaderProgram particleProgram) {
        int dataOffset = 0;

        vertexArray.setVertexAttribPointer(dataOffset,
                                           particleProgram.getPositionAttributeLocation(),
                                           POSITION_COMPONENT_COUNT,
                                           STRIDE);
        dataOffset += POSITION_COMPONENT_COUNT;

        vertexArray.setVertexAttribPointer(dataOffset,
                                           particleProgram.getColorAttributeLocation(),
                                           COLOR_COMPONENT_COUNT,
                                           STRIDE);
        dataOffset += COLOR_COMPONENT_COUNT;

        vertexArray.setVertexAttribPointer(dataOffset,
                                           particleProgram.getDirectionVectorAttributeLocation(),
                                           VECTOR_COMPONENT_COUNT,
                                           STRIDE);
        dataOffset += VECTOR_COMPONENT_COUNT;

        vertexArray.setVertexAttribPointer(dataOffset,
                                           particleProgram.getParticleStartTimeAttributeLocation(),
                                           PARTICLE_START_TIME_COMPONENT_COUNT,
                                           STRIDE);

    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0, currentParticleCount);
    }
}

class PartikelShooter {
    private final Geometry.Point position;
    private final Geometry.Vector direction;
    private final int color;

    private final float angelVariance;
    private final float speedVariance;
    private final Random random = new Random();
    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];

    public PartikelShooter(Geometry.Point position, Geometry.Vector direction, int color,
                           float angelVarianceInDegrees, float speedVariance) {
        this.position = position;
        this.direction = direction;
        this.color = color;

        this.angelVariance = angelVarianceInDegrees;
        this.speedVariance = speedVariance;
        directionVector[0] = direction.x;
        directionVector[1] = direction.y;
        directionVector[2] = direction.z;
    }

    public void addParticle(PartikelSystem particleSystem, float currentTime, int count)
    {
        for (int i=0; i<count; i++) 
        {
            setRotateEulerM(rotationMatrix, 0,
                (random.nextFloat() - 0.5f) * angelVariance,
                (random.nextFloat() - 0.5f) * angelVariance,
                (random.nextFloat() - 0.5f) * angelVariance);

            multiplyMV(resultVector, 0, rotationMatrix, 0, directionVector, 0);

            float speedAdjustment = 1f + random.nextFloat() * speedVariance;
            Geometry.Vector thisDirection = new Geometry.Vector(
                resultVector[0] * speedAdjustment,
                resultVector[1] * speedAdjustment,
                resultVector[2] * speedAdjustment);
                
            particleSystem.addParticle(position, color, thisDirection, currentTime);
        }
    }
}

class Skybox {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private final VertexArray vertexArray;
    private final ByteBuffer indexArray;

    public Skybox()
    {
        //Create a unit cube
        vertexArray = new VertexArray(new float[] {
            -1,  1,  1,  // (0) Top-left near(dekat)
             1,  1,  1,  // (1) Top-right near(dekat)
            -1, -1,  1,  // (2) bottom-left near(dekat)
             1, -1,  1,  // (3) bottom-right near(dekat)
            -1,  1, -1,  // (4) Top-left far(jauh)
             1,  1, -1,  // (5) Top-right far(jauh)
            -1, -1, -1,  // (6) bottom-left far(jauh)
             1, -1, -1,  // (7) bottm-right far(jauh)
        });
        indexArray = ByteBuffer.allocateDirect(6 * 6).put(new byte[] {
            // depan
            1, 3, 0,
            0, 3, 2,
            //belakang
            4, 6, 5,
            5, 6, 7,
            //kiri
            0, 2, 4,
            4, 2, 6,
            //kanan
            5, 7, 1,
            1, 7, 3,
            //atas
            5, 1, 4,
            4, 1, 0,
            //bawah
            6, 2, 7,
            7, 2, 3
        });
        indexArray.position(0);
    }

    public void bindData(SkyboxShaderProgram skyboxProgram) {
        vertexArray.setVertexAttribPointer(0,
                                           skyboxProgram.getPositionAttributeLocation(),
                                           POSITION_COMPONENT_COUNT, 0);
    }
    public void draw() {
        glDrawElements(GL_TRIANGLES, 35, GL_UNSIGNED_BYTE, indexArray);
    }
}

class SkyboxShaderProgram extends ShaderProgram {
    private int tutorial = 0;
    //uniform location
    private final int uMatrixLocation;
    private int uTextureUnitLocation = 0;

    //Atributee location
    private final int aPositionLocation;

    public SkyboxShaderProgram(String skyBoxVertexShader, String skyBoxFragmentShader, int tutorial) {
        super(skyBoxVertexShader, skyBoxFragmentShader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    public void setUniforms(float[] matrix, int textureID) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glActiveTexture(GL_TEXTURE0);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}

class HeightMapShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int aPositionLocation;

    public HeightMapShaderProgram(String vertex, String fragment, int tutorial) {
        super(vertex, fragment);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }
    public void setUniforms(float[] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}

/*
Loading the Height Map
    untuk memuat heightmap ke OpenGL kita perlu memuat dalam data gambar
dan mengubahnya menjadi satu set simpul, satu untuk setiap pixel. setiap
simpul akan memiliki posisi berdasarkan posisinya pada gambar dan tinggi
berdasarkan cerah pixel. setelah kami memiliki semua simpul dimuat, kami
akan menggunakan buffer index untuk mengkelompokanya ke segitiga yang dapat
kita gambar dengan OpenGL.
*/
//Menghasilkan data vertex
class Heightmap {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private final int width;
    private final int height;
    private final int numElements;
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    public Heightmap(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        if (width * height > 65536) {
            throw new RuntimeException("heightmap is too large for the index buffer.");
        }
        numElements = calculateNumElements();
        vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));
        indexBuffer = new IndexBuffer(createIndexData());
    }

    /*
    untuk membaca secara efisien dalam semua data bitmap pertama-tama kami
    mengestrak semua pixel dengan panggilan getPixels() kemudian kami
    mendaur ulang bitmap karena kami tidak perlu menyimpanya. karena ada
    satu titik per pixel kami membuat array baru untuk simpul dengan lebar
    dan tinggi yang sama dengan bitmap.
        untuk menghasilkan setiap simpul heightmap kami pertama-tama menghitung
    posisi vertex(titik), heightmap akan lebar 1 unit disetiap arah dan dipusatkan
    pada x-z dari (0,0) jadi dengan loop ini sudut kiri atas bitmap akan memetakan
    ke (-0,5, -0,5) dan sudut kanan bawah akan memetakan ke (0,5, 0,5). kami berasumsi
    bahwa gambar adalah skala abu-abu jadi kami membaca komponen merah dari pixel
    dan membaginya dengan 255 untuk mendapatkan ketinggian. nilai pixel 0 akan
    sesuai dengan ketinggian 0 dan nilai pixel 255 akan sesuai dengan ketinggian 1
    setelah kami menghitung posisi dan tinggi kami dapat menulis vertex baru ke
    array.
        sebelum kita pindah mari kita lihat loop ini lebih dekat. mengapa kita
    membaca perbaris dan memindai setiap kolom dari kiri ke kanan? kenapa tidak
    membaca kolom bitmap dengan kolom? alasanya kita membaca data baris demi baris
    karena bitmap ditata seara berurutan dalam memori, dan CPU jauh lebih baik
    pada caching dan memindahkan data disekitar ketika mereka dapat melakukanya
    secara berurutan. Penting juga untuk mencatat cara kami mengakses pixel. ketika
    kami mengestrak pixel menggunakan getPixels() andaroi memberi kami array satu dimensi
    lalu bagaimana kita tau dimana membaca di pixel? kami dapat menghitung tempat
    yang tepat dengan rumus beikut:
        pixelOffset = currentRow * height + currentColumn
    dengan menggunakan rumus ini kita dapat mengunakan dua loop untuk dibaca
    dalam array satu dimensi seolah-olah itu bitmap dua dimensi.
    */
    private float[] loadBitmapData(Bitmap bitmap) {
        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        final float[] heightmapVertices = new float[width * height * POSITION_COMPONENT_COUNT];
        int offset = 0;

        //konvert pixel bitmap ke data heightmap
        for(int row=0; row<height; row++) {
            for (int col=0; col<width; col++) 
            {
                final float xPosition = ((float)col / (float)(width-1)) - 0.5f;
            
                final float yPosition = (float)Color.red(pixels[(row * height) + col]) / (float)255;

                final float zPosition = ((float)row / (float)(height-1)) - 0.5f;

                heightmapVertices[offset++] = xPosition;
                heightmapVertices[offset++] = yPosition;
                heightmapVertices[offset++] = zPosition;

            }
        }
        return heightmapVertices;
    }

    /*
    cara kerjanya adalah untuk setiap kelompok 4 simpul di hightmap kami
    menghasilkan 2 segitiga, 3 index untuk setiap segitiga, untuk total 6 index
    kita dapat menghitung berapa banyak kelompok yang kita butuhkan dengan
    mengalikan (width-1) dengan (height-1) dan kemudian kita hanya mengalikan
    dengan 2 segitiga per kelompok dan 3 elemen per segitiga untuk mendapatkan
    jumlah elemen total. Misalnya hightmap 3x3 akan memiliki (3-1) * (3-1) = 2*2=4
    grup. dengan dua segitiga per kelompok dan 3 elemen per segitiga, itu total
    24 elemen.
    */
    private int calculateNumElements() {
        return (width - 1) * (height - 1) * 2 * 3;
    }

    /*
    Metode ini menciptakan array short dengan ukuran yang diperlukan
    kemudian ia melipat melalui baris dan kolom, membuat segitiga untuk
    setiap kelompok empat simpul. kami bahkan tidak memerlukan data pixel
    yang sebenarnya untuk melakukan ini, yang kita butuhkan hanyalah lebar
    dan tingginya. kami pertama kali belajar tentang index kembali pada page:221
    dan kode ini mengikuti pola yang sama.
        sesuatu yang menarik terjadi jika anda mencoba menyimpan nilai index
    lebih besar dari 32.767 untuk short akan menyebabkan angka membungkus
    kedalam nilai negatif. Namun karena pelengkap dua angka negatif ini akan
    memiliki nilai yang tepat ketika OpenGl membacanya dalam nilai yang unsigned
    selama kita tidak memiliki elemen lebih dari 65.536 untuk index kita akan
    baik-baik saja.
        Ada beberapa hal yang harus diperhatikan saat menggunakan benda buffer
    secara teknis android mendukung OpenGL ES dari android 2.2 tetapi sayangya
    binding ini rusak dan vertex dan index beffer tidak dapat digunakan dari java
    tanpa menulis binding antarmuka native java(JNI). berita baiknya adalah bahwa
    binding ini diperbaiki dalam rilis android gingerbread dan pada saat penulis
    saat ini hanya 9 persen dari pasar masih di froyo jadi masalah ini tidak
    sebesar kesepakatan yang dulu. Sama seperti dengan byteBuffer java, menggunakan
    oject buffer OpenGL yang tidak benar dapat menyebabkan crash native yang sulit
    didebug. jika aplikasi anda tiba-tiba menghilang dan anda melihat sesuatu seperti
    Segmentation Fault di log android itu ide yang baik untuk memeriksa ulang
    semua panggilan Android yang melibatkan buffer terutama panggilan ke glVertexAttribPointer()
    */
    private short[] createIndexData() {
        final short[] indexData = new short[numElements];
        int offset = 0;

        for (int row=0; row<height-1; row++) {
            for (int col=0; col<width-1; col++) 
            {
                short topLeftIndexNum = (short)(row * width + col); 
                short topRightIndexNum = (short)(row * width + col + 1); 
                short bottomLeftIndexNum = (short)((row + 1) * width + col);
                short bottomRightIndexNum = (short)((row + 1) * width + col + 1);
            
                //Write out dua segitiga
                indexData[offset++] = topLeftIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = topRightIndexNum;

                indexData[offset++] = topRightIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = bottomRightIndexNum;
            }
        }
        return indexData;
    }

    /*
    kami menggunakan bindData() untuk memberi tau OpenGL dimana mendapatkan
    data saat kami memanggil draw(). di metode draw() kami memberi tau OpenGL
    untuk menggambar data menggunakan buffer indexs. panggilan ini menggunakan
    glDrawElements() yang sedikit berbeda daripada yang kami gunakan dibab
    sebelumnya seperti glVertexAttribPointer(), parameter terakhir diubah dari
    referensi buffer ke int offset yang digunakan untuk memberi tau OpenGL dimana
    index untuk mulai membaca. seperti biasanya kami mengikat buffer sebelum
    digunakan dan melepaskan sesudahnya
    */

    public void bindData(HeightMapShaderProgram heightmapProgram) {
        vertexBuffer.setVertexAttribPointer(0,
                                            heightmapProgram.getPositionAttributeLocation(),
                                            POSITION_COMPONENT_COUNT, 0);
    }
    public void draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}