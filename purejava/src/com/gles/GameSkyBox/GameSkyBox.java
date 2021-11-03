/*

* BAB 10
* Adding a SkyBox
*
*/

package com.gles.GameSkyBox;

import android.util.Log;
import static android.opengl.GLSurfaceView.Renderer;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_FLOAT;
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
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glBlendFunc;
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
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glBindTexture;
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
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.setRotateEulerM;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gles.R;

/*
Menambahkan SkyBox
    kita mulai dengan kekosongan gelap dan kemudian kami menambahkan
beberapa bentuk visual dengan menambahkan tiga air mancur partikel
ketempat kejadian. Namun dunia kita sebagian besar masih hitam dan hampa.
air mancur kami mengambang dalam gelap tanpa permukaan untuk menjaga
partikel jatuh ke jurang.
untuk memulai hal-hal kami akan belajar cara menambahkan latar belakang
ke adegan kami yang akan membantu memberikan rasa kontext visual. banyak
gam dan wallpape menggunakan kombinasi dar teknik seni dan 3D untuk menghasilkan
latar belakang mereka dan dalam bab ini kita akan belajar bagaimana mengatur
air mancur kita dengan latar belakang langit dengan mengunakan skybox yang
merupakan teknik yang dapat kita gunakan untuk melakukan 360-degre skybox
ini pertama kali terlihat dibanyak game populer di akhir 90 an dan masih
terus digunakan hingga kini. mereka juga banyak digunakan diluar game misal
panaroma 360 derajat yang dapat kita lihat saat menggunakan google street view.
mari kita lihat rencara permainan kami untuk bab ini:
    1. kami akan belajar cara mendefinisikan skybox menggunakan pemetaan kubus
       metode menjahit bersama enam texture berbeda menjadi kubus.
    2. kami juga akan mengambil tampilan pertama kami pada array index, cara
       mengurangi duplikasi vertex dengan menyimpan hanya simpul unik dalam
       array vertex dan menghubungkanya dengan merujuk pada simpul-simpul
       tersebut dengan index offset alih-alih menduplikasi data vertex.
       array index dapat mengurangi penggunaan memori dan meningkatkan
       kinerja ketika ada banyak penggunaan kembali vertex.
*/

public class GameSkyBox implements Renderer
{
    private int tutorial = 0;
    public static final int BYTES_PER_FLOAT = 4;
    private Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    private PartikelShaderProgram particlesProgram;
    private PartikelSystem particlesSystem;
    private PartikelShooter redParticleShooter;
    private PartikelShooter greenParticleShooter;
    private PartikelShooter blueParticleShooter;
    private long globalStartTime;
    private int texture;

    //Menambahkan Skybox ke adegan kami
    private SkyboxShaderProgram skyboxProgram;
    private Skybox skybox;
    private int skyboxTexture;

    //tutorial 1
    private float xRotation, yRotation;

    public GameSkyBox() {
    }
    public GameSkyBox(Context context, int tutorial) {
        this.context = context;
        this.tutorial = tutorial;
    }

    public GameSkyBox(Context context) 
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

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float)height, 1f, 10f);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        glClear(GL_COLOR_BUFFER_BIT);

        /*
        kami tidak ingin translate diterapkan pada scene(adegan) ke skybox.
        jadi kita perlu menggunakan matrix berbeda untuk skybox dan untuk partikel.
        kita melakukan pembaruan matrix didalam drawParticles() dan kita
        memutar dan mati dalam metode itu sendiri. Kami melakukan ini karena
        kami tidak ingin blending dihidupkan ketika kami menggambar skybox.
        sekarang kita menaktifkan dan menonaktifkan blending hanya ketika
        kita menggambar partikel.
        */
        drawSkybox();
        drawParticles();
    }

    private void drawSkybox() {
        setIdentityM(viewMatrix, 0);
        if (tutorial >= 1) 
        {
            /*
            memutar matrix dengan rotasi y pertama dan putaran X kedua
            memberi anda rotasi "gaya FPS" dimana fps berdisi untuk penembak
            orang pertama sehingga memutar ke atas atau ke bawah selalu membawa
            anda ke arah kepala atau kaki anda, dan memutar kekiri atau kekanan
            selalu memutar anda. sekarang jika anda memberikan aplikasi ini lagi
            dan seret jari anda dilayar anda dapat memainkan kamera dan melihat
            bagian lain dari skybox.
            */
            rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
            rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        }
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
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

        setIdentityM(viewMatrix, 0);
        if (tutorial >= 1) {
            rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
            rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        }
        translateM(viewMatrix, 0, 0f, -1.5f, -5f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particlesProgram.useProgram();
        particlesProgram.setUniforms(viewProjectionMatrix, currentTime, texture);
        particlesSystem.bindData(particlesProgram);
        particlesSystem.draw();

        glDisable(GL_BLEND);
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

        /*
        Adding Skybox shaderProgram
            pertama kami meneruskan posisi titik ke fragmen shader seperti
        yang terlihat pada baris bertama didalam main() dan kemudian kami
        membalikan komponen Z pada baris berikutnya, ini memberi fragmen
        shader posisi yang akan diinterpolasi disetiap face kubus sehingga
        kita dapat menggunakan posisi ini untuk melihat bagian kanan dari
        texture skybox. komponen Z dibalik sehingga kita dapat menkonversi
        dari ruang koordinat tangan kanan dunia ke ruang koordinat kidal yang
        diharapkan skybox. jika kita melewati langkah ini skybox masih tetap
        bekerja tetapi texture akan muncul terbalik.
            setelah kami memproyeksikan posisi didalam koordinat klip dengan
        mengalikan a_Position dengan matrix, kami mengatur komponen Z sama
        dengan komponen W dengan kode berikut ini:
            
            gl_Position = gl_Position.xyww;
        
        ini adalah trik yang memastikan bahwa setiap bagian dari skybox akan
        berbaring diplane jauh dalam koordinat perangkat yang dinormalisasi
        dan dengan demikian dibelakang segalanya ditempat kejadian. Trik ini
        bekerja karena divisi persepektif membagi semuanya dengan W dan W dibagi
        dengan sendirinya akan sama dengan 1. setelah perspektif membelah Z akan
        berakhir pada plane jauh 1.
            trik ini mungkin nampak tidak perlu saat ini, karena jika kami ingin
        skybox muncul dibelakang segalanya kami hanya bisa menggambarnya terlebih
        dahulu dan kemudian menggambar yang lain diatasnya. ada alasan kinerja
        dibalik trik ini yang akan dibahas pada page:245

        */
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
        return "";
    }

    //tutorial 1
    /*
    metode ini akan mengambil jarak pengguna yang diseret di setiap
    arah dan menambahkanya ke xRotation dan yRotation yang mewakili
    rotasi dan derajat, kami tidak ingin sentuhan terlalu sensitif
    jadi kami mengurangi efeknya sebesar 16.
    dan kami tidak ingin memutar terlalu jauh keatas dan bawah jadi
    kami menjepit(clamp) rotasi Y antara +90 derajat dan -90 derajat
    */
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

/*
Creating a SkyBox
    Skybox adalah cara untuk mewakili panorama 3D yang dapat dilihat
di setiap arah tidak peduli dengan cara apapun anda mengubah kepala.
salah satu cara paling clasik untuk membuat skybox adalah dengan
menggunakan kubus disekitar pemirsa, dengan texture terperinci di
setiap wajah kubus, ini juga dikenal sebagai peta kubus. ketika kita
menggambar skybox kita hanya perlu memastikan bahwa itu muncul dibelakang
segalanya ditempat kejadian.
    ketika kita menggunakan skybox kita menggunakan kubus untuk mewakili
langit, jadi biasanya kita akan mengharapkan langit terlihat seperti kubus
raksasa dengna sendi yang terlihat antara setiap face(wajah). Namun ada
trik yang membuat tepi kubus menghilang, setiap face kubus adalah gambar
yang diambil dengan bidang pandang 90 derajat dan proyeksi aplanar, jenis
yang sama yang kami harapkan dari OpenGl itu sendiri. Ketika kita memposisikan
viewer(pemirsa) ditengah-tengah kubus dan menggunakan perspektif projection
dengan bidang penglihatan biasa semuanya berjalan tepat, tepi kubus menghilang
dan kita memiliki panorama yang mulus.
    Meskipun gambar itu sendiri harus dibuat dengan bidang visi 90 derajat
adegan OpenGL itu sendiri dapat menggunakan berbagai sudut seperti 45 derajat
atau 60 derajat. caranya akan bekerja dengan baik selama sudut yang berlebihan
tidak digunakan karena sifat bujursangkar dair perspektif OpenGL yang akan
menyebabkan peningkatan distorsi disekitar (edge)tepi. kita dapat melihat
contoh dari enam face texture peta kubus berbasis di samping satu sama lain
menunjukan bagaimana setiap face memadukan dengan pada page 218.
    untuk membuat skybox kami akan menyimpan setiap face kubus dalam textur
separte dan kemudian kami akan memberi tau OpenGL untuk menjahit texture ini
menjadi peta kubus dan menerapkanya pada kubus. ketika OpenGL membuat kubus
dengan interpolasi linier diaktifkan, banyak GPU akan benar-benar mencampur
texel antara face-face tetangga membuat (edge) tepi kubus seamles(mulus)
bahkan dukungan pemetaan kubus mulus dijamin pada OpenGL ES 3.0
*/

/*
Keuntungan dan kerugian teknik skybox
    sementara sebuah peta kubus dengan sendirinya berfungsi dengan baik untuk
skybox sederhana, ada beberapa kerugian yang datang dengan kesederhanaan.
efeknya hanya berfungsi dengan baik jika skybox diberikan dari pusat kubus
jadi apapun yang merupakan bagian dari skybox akan selalu tampak pada jarak
yang sama dari viewer(pemirsa) tidak peduli seberapa jauh mereka berpergian.
karena skybox biasanya terdiri dari texture prepended adegan juga harus statis
dengan awan yang tidak bergerak dan tergantung pada bidang penglihatan pemirsa
texture peta kubus mungkin perlu resolusi yang sangat tinggi untuk terlihat bagus
dilayar dan memakan banyak memori texture dan bandwidth.
banyak game dan aplikasi modern biasanya bekerja di sekitar batasan ini dengan
melengkapi teknik skybox tradisional dengan adegan 3D terpisah dengan elemen dan awan
dinamisya sendiri. adegan 3D yang terpisah ini masih akan diberikan dibelakang
segalanya, tetapi kamera juga dapat bergerak disekitar adegan dan elemen terpisah,
didalam adegan itu dapat dianimasikan memberi pemirsa ilusi berada di dunia 3D raksasa.
*/

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

    /*
    Loading Cube map ke OpenGL
        didalam resource res/drawable anda dapat menemukan contoh peta
    kubus yang terpisahkan menjadi enam texture, satu untuk setiap face kubus.
    ketika kami memanggil metode ini, kami akan melewati enam sumber gambar
    satu untuk setiap face(wajah) kubus. pesanan penting, jadi kami akan
    menggunakan gambar-gambar ini dalam urutan standar yang dapat kami
    dokumentasikan dalam komntar untuk metode ini. Untuk memulai hal-hal
    kami membuat satu object texture OpenGL sama seperti yang kami lakukan
    saat memuat texture biasa. kami juga memiliki 6 bitmap untuk sementara
    waktu, sementara data sumber daya gambar dalam memori dan mentransfernya
    ke OpenGL.
    */
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

        /*
        karena setiap texture map kubus akan selalu dilihat dari sudut
        pandang yang sama, maka teknik mipmaping(page:122) kurang diperlukan
        sehingga kita dapat menggunakan filter billinear biasa dan menghemat
        memori texture. jika kita tau bahwa resolusi peta kubus secara
        segnifikan lebih tinggi daripada resolusi perangkat maka kita juga
        bisa mengecilkan setiap texture sebelum mengunggahnya ke OpenGL.
        */
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    
        /*
        mengatikan setiap gambar dengan face yang sesuai dari peta kubus.
            ketika kami menyebut metode ini kami akan melewati face kubus
        dalam urutan ini: kiri/kanan, bawah/atas, depan/belakang. dan
        pemanggilan metode ini juga melewati dalam urutan yang sama.
        pertama kami memetakan texture kiri dan kanan ke face x yang
        negatif dan positif dan texture depan dan belakang ke Z positif,
        seolah-olah kami menggunakan sistem koordinat kidal.
            konvensi untuk peta kubus adalah dengan menggunakan sistem
        koordinat kidal ketika didalam kubus dan sistem koordinat tangan
        kanan ketika didalam kubus dan diluar kubus. ini akan penting
        ketika kita membuat skybox kita, jika kita mencampur konvensi
        skybox kita akan muncul terbalik disumbu Z.
            ketika berurusan dengan texture peta kubus besar, gaya memuat
        texture ini dapat memory-intensiv, jadi jika anda tau anda akan
        berjalan pada perangkat memory-contrained. satu alternatif adalah
        memuat satu bitmap pada satu waktu dan memuatnya ke face yang sesuai
        untuk setiap face berikutnya.
        */
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
            .allocateDirect(vertexData.length * GameSkyBox.BYTES_PER_FLOAT)
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

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * new GameSkyBox().BYTES_PER_FLOAT;
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

/*
Creating a Cube
    untuk langkah selanjutnya kami akan membuat object cube baru
untuk skybox kami dan kami juga akan membuat array index pertama kami.
sebuah kubus adalah kandidat yang baik untuk penindeksan karena hanya
memiliki 8 simpul unik dengan 3 komponen posisi per titik, kita akan
membutuhkan 24 float untuk menyimpan simpul(vertices) ini. katakanlah
kita memutuskan untuk menggambar kubus dengan 2 segitiga per face jadi
kita memiliki 12 segitiga semuanya. dengan 3 simpul per segitiga, jika
kita menggambar kubus dengan array vertex saja kita akan memerlukan
36 simpul atau 108 float dengan banyak data duplikasi.
    dengan array indeks kami tidak perlu mengulang semua titik data titik.
sebaliknya kita hanya perlu mengulangi index dan ini memungkinkan kita
untuk mengurangi ukuran keseluruhan data.
*/
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

        /*
        index array ini mangacu pada setiap titik dengan offset indexs.
        misalnya 0 mengacu pada simpul pertama dalam array vertex dan
        1 mengacu pada simpul kedua. dengan array index ini kami telah
        mengikat semua simpul ke dalam kelompok segitiga dengan dua segitiga
        per face kubus. mari kita lihat perbedaanya dengan memeriksa
        face depan kubus:

            1, 3, 0,
            0, 3, 2,
        
        sekarang mari kita lihat bagaimana kita akan mendefinisikan jika kita
        hanya menggunakan array vertex:
            
            1,  1,  1,   top-right near(dekat)
            1, -1,  1,   bottom-right near
           -1,  1,  1,   top-left near
           -1,  1,  1,   top-left near
            1, -1,  1,   bottom-right near
           -1, -1,  1,   bottom-left near
    
        dengan array index kita dapat merujuk ke setiap titik dengan posisi
        alih-alih mengulangi data titik yang sama berulang-ulang.
        */
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

    /*
    metode bindData(0) adalah metode standar dan semua yang kita lewatkan
    adalah kelas Skyboxshader untuk dikompilasi.
    untuk menggambar kubus kita sebut glDrawElements() yang memberi tau
    OpenGL untuk menggambar simpul yang kami ikat dalam bindData() dengan
    array index yang ditentukan oleh indices(index) dan menafsirkan indices sebagai
    unsigned byte. Dengan OpenGL ES 2 indices perlu dibayangkan
    unsigned byte (bilangan bilat 8 bit dengan kisaran 0-255) atau unsigned short
    (bilangan bilat 16 bit dengan kisaran 0-65535)
        sebelumnya kami telah mendefinisikan array index kami sebagai bytebuffer
    jadi kami memberi tau OpenGL untuk menafsirkan data ini sebagai stream(aliran)
    unsigned byte. byte java sebenarnya adalah actualy signed, artinya berkisar dari
    -128 hingga 127, tetapi ini tidak akan menjadi masalah selama kita berpegang
    pada bagian positif dari rentang itu.
    */
    public void bindData(SkyboxShaderProgram skyboxProgram) {
        vertexArray.setVertexAttribPointer(0,
                                           skyboxProgram.getPositionAttributeLocation(),
                                           POSITION_COMPONENT_COUNT, 0);
    }
    public void draw() {
        glDrawElements(GL_TRIANGLES, 35, GL_UNSIGNED_BYTE, indexArray);
    }

    /*
    Konversi antara tipe data signed dan unsigned data
        ketika kami bekerja dengan unsigned byte(byte tidak ditandatangani)
    kami dapat menyimpan nomor apapun dari 0 hingga 255 namun java biasanya
    hanya membiarkan kami menyimpan nomor dikisaran -128 hingga 127, dan 255
    tidak sesuai dengan jangkauan tersebut sementara kami tidak dapat menulis
    kode yang mengatakan byte b = 255, ada cara untuk mengakali java untuk
    menafsirkan angka sebagai signed byte.
        mari kita lihat nilai biner untuk 255, dalam biner kita akan mewakili
    angka ini sebagai 111 111 111 kita hanya perlu menemukan cara memberi tau
    java untuk memasukan bit ini ke dalam byte. ternyata kita tidak dapat melakukakan
    ini jika kita menggunakan bit masking java untuk menutupi delapan bit terakhir
    dari angka 255 sebagai berikut:

        byte b = (byte)(255 & 0xff);

    ini bekerja karena java akan menafsirkan angka literal 255 sebagai bilangan
    bulat 32bit yang cukup besar untuk memegang angka 255. Namum setelah kami
    menetapkan 255 ke byte, java akan benar-benar melihat byte ini sebagai -1
    java akan menganggap bilangan ini byte signed dan itu akan menafsirkan nilai
    menggunakan komplemen dua. Namum OpenGL atau ditempat lain akan mengharapkan
    unsigned dan melihat byte sebagai 255.
    untuk membaca nilai kembali di java, kami tidak bisa membacanya langsung karena
    java melihatnya sebagai -1 sebaliknya kita perlu menggunakan tipe data java yang
    lebih besar untuk menahan hasil. Misalnya kita dapat melakukan konversi
    dengan kode sebagai berikut:

        short s = (short)(b & 0xff);

    kita tidak dapat menggunakan s = b; karena java akan men-signed(menandatangani)
    extensi dan short kita masih akan -1. Dengan memberi tau java untuk menutupi
    delapan bit terakhir, kami secara implisit mengkonversi ke bilangan bulat dan
    kemudian java akan menafsirkan delapan bit terakhir dari integer itu sebagai 255
    short anda kemudian akan diatur ke 255 seperti yang kami harapkan.
    http://en.wikipedia.org/wiki/Two's_complement

    */
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

        //kami mengikat texture dengan GL_TEXTURE_CUBE_MAP
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    
}