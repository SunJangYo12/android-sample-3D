/*

* BAB 7
* Building simple Object
*
*/

package com.gles.GameAirHoki6;

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
import static android.opengl.GLES20.GL_LINEAR;
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
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLUtils.texImage2D;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setLookAtM;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
//import android.util.FloatMath; kelas ini kosong
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.gles.R;

/*
Kami telah datang jauh dengan proyek hoki kami, meja kami sekarang
pada sudut yang baik dan terlihat lebih baik sekarang setelah kami
memiliki texture. Namun mallet kami sebenarnya tidak terlihat seperti
mallet yang nyata karena kami sedang menggambar masing-masing sebagai titik.
biasanya anda membayangkan game hoki dengan titik kecil sebagai mallet?
banyak aplikasi menggabungkan bentuk-bentuk sederhana untuk membangun
object yang lebih rumit dan kami akan belajar bagaimana melakukannya
disini sehingga kami dapat membangun mallet yang lebih baik.
    kami juga melewatkan cara mudah untuk menggeser, memutar dan bergerak
ditempat kejadian. banyak aplikasi 3D mengimplementasikan ini dengan menggunakan
matrix tampilan, perubahan yang dilakukan pada matrix ini mempengaruhi seluruh
adegan, seolah-olah kita sedang melihat sesuatu dari kamera yang bergerak.
kami akan menambahkan matrix tampilan untuk memudahkan memutar dan bergerak.
setelah kami menyelesaikan tugas-tugas ini kami akan dapat memindahkan pemandangan
dengan satu baris kode, dan kami akan memiliki mallet yang mirip sesuatu yang 
sebenarnya dapa kami kami gunakan untuk memukul keping. Berbicara mengenai keping
kami belum memilikinya jadi kami akan menambahkanya juga.
    Untuk membangun mallet atau keping mari kita coba bayangkan bentuk pada tinhkat
yang lebih tinggi. Keping dapat mewakili sebagai silinder datar(page:142) mallet sedikit
lebih kompleks dan dapat diwakili sebagai dua silinder, satu diatas yang lain. untu mengetahui
cara membangun object-object ini di OpenGL mari kita coba bayangkan bagaimana kita
akan membangun ini dari kertas, pertama kita akan memotong lingkaran untuk bagian silinder.
kami kemudian mengambil selembar kertas yang datar, potong keukuran yang tepat dan
gulung menjadi tabung. Untuk membuat silinder kita kemudian bisa meletakan lingkaran
diatas tabung. Kami membutuhkan salah satu dari silinder ini untuk keping dan dua mallet
ternyata ini sebenarnya cukup mudah dilakukan di OpenGL. Untuk membangun lingkaran kita
dapat menggunakan TRIANGLE_FAN(kipas segitiga). kami juga dapat menggunakan TRIANGLE_FAN untuk
mewakili lingkaran, kita hanya perlu menggunakan lebih banyak segitiga dan mengatur vertices(simpul)
luar dalam bentuk lingkaran. Untuk membangun sisi silinder, kita dapat menggunakan konsep
yang dikenal sebagai strip segitiga yang memungkinkan kita mendifinisikan banyak segitiga tanpa
menduplikasi poin bersama berulang-ulang, tetapi alih-alih mengipasi lingkaran, strip segitiga
dibangun seperti gelagar jembatan, dengan segitiga ditata disamping satu sama lain.

        (1)----(3)-----(5)----(6)-----(7)
         |     /^     .^|     ^ |     ^|
         | 1  / | 3  /  | 5  /  | 7  / |
         |   /  |   /   |   /   |   /  |
         |  /   |  /    |  /    |  /   |
         | / 2  | / 4   | / 6   | /  8 |
         v/     v/      v/      v/     v
        (2)----(4)-----(5)-----(6)----(8)

Seperti dengan triangle_fan, tiga simpul pertama dari strip segitiga mendefinisikan
segitiga pertama. setiap vertex tambahan setelah itu mendefinisikin segitiga tambahan.
untuk membangun sisi silinder menggunakan strip segitiga, kita hanya perlu menggulung
strip di tabung dan memastikan bahwa dua vertices(simpul) berbaris dengan dua yang pertama.
*/


public class GameAirHoki6 implements Renderer
{
    public static final int BYTES_PER_FLOAT = 4;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private Context context;
    private Table table;
    private Mallet mallet;
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;
    private int texture;

    /*
    mari kita lanjutkan dan tambahkan matrix tampilan dan kami
    juga akan bekerja di mallet baru kami dan keping pada saat yang sama
    kami pertama-tama akan menambahkan beberapa definisi matrix baru.
        kami akan menyimpan matrix tampilan kami di viewMatrix dan dua
    matrix lainya akan digunakan untuk menahan hasil perkalian matrix.
    */
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private Puck puck;

    public GameAirHoki6() {
    }

    public GameAirHoki6(Context context) 
    {
        this.context = context;
        
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        table = new Table();
        
        String textureVertexShaderCode = "attribute vec4 a_Position;  \n"+
                                         "attribute vec2 a_TextureCoordinates;     \n"+
                                         "varying vec2 v_TextureCoordinates;     \n"+
                                         "uniform mat4 u_Matrix;     \n"+

                                        "void main()                 \n"+
                                        "{                           \n"+
                                        "   v_TextureCoordinates = a_TextureCoordinates;\n"+
                                        "   gl_Position = u_Matrix * a_Position;"+
                                        "}";

        String textureFragmentShaderCode = "precision mediump float;    \n"+
                                           "uniform sampler2D u_TextureUnit; \n"+
                                           "varying vec2 v_TextureCoordinates;       \n"+
                                           "void main()                 \n"+
                                           "{                           \n"+
                                           "   gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);\n"+
                                           "}";

        //update object
        String vertexShaderCode = "attribute vec4 a_Position;  \n"+
                                    "uniform mat4 u_Matrix;     \n"+

                                    "void main()                 \n"+
                                    "{                           \n"+
                                    "   gl_Position = u_Matrix * a_Position;\n"+
                                    "}";

        String fragmentShaderCode = "precision mediump float;    \n"+
                                      "uniform vec4 u_Color;       \n"+
                                      "void main()                 \n"+
                                      "{                           \n"+
                                      "   gl_FragColor = u_Color;\n"+
                                      "}";

        /*
        bagian tersulit dari bab ini dilakukan. kami belajar cara membangun
        keping dan mallet dari bentuk geometri sederhana dan kami juga telah
        memperbarui shader kami untuk mencerminkan perubahan. yang tersisa
        adalah untuk mengintregasikan perubahan ke GameAirHoki6 renderer pada
        saat yang sama kami juga akan belajar cara menambahkan konsep kamera
        dengan menambahkan matrix tampilan.
        jadi kenapa kami ingin menambahkan matrix lain? ketika kami pertama kali
        memulai kali memulai proyek udara kami awalnya tidak menggunakan matrix
        apapun. kami pertama kali menambahkan matrix ortografis untuk menyesuaikan
        ratio aspek dan kemudian kami beralih ke mateix perspectif untuk mendapatkan
        proyeksi 3D. kami kemudian menambahkan matrix model untuk mulai memindahkan
        barang-barang. matrix tampilan hanya perpanjangan dari matrix model, ini
        digunakan untuk tujuan yang sama tetapi berlaku untuk setiap object adegan

        penjelasan sederhan dari matrix
            mari kita luangkan waktu sejenak untuk meninjau tiga jenis
        matrix yang akan kita gunakan untuk mendapatkan object ke layar
            1. matrix model
               matrix model digunakan untuk menempatkan object ke dalam koordinat
               ruang dunia. Misalnya kami mungkin memiliki model keping dan model
               mallet kami awalnya berpusat pada (0,0,0) tanpa matrix model, model
               kami akan terjebak disana, jika kami ingin memindahkanya, kami harus
               memeperbarui masing-masing vertices(simpul) sendiri. alih-alih melakukan
               itu kita dapat menggunakan matrix model dan mengubah vertice kita dengan
               mengalikanya dengan matrix. jika kita ingin memindahkan keping kita ke
               (5,5) kita hanya perlu menyiapkan matrix model yang akan melakukan ini
               untuk kita.
            2. view matrix (matrix tampilan)
               matrix view digunakan untuk alasan yang sama dengan matrix model, tetapi
               sama-sama mempengaruhi setiap object dalam adegan. Karena itu mempengaruhi
               segalanya, secara fungsional setara dengan kamera, pindahkan kamera disekitar
               dan anda akan melihat sesuatu dari sudut pandang yang berbeda.
               keuntungan menggunakan matrix terpisah adalah bahwa ia memungkinkan kami memacukan
               banyak transformasi menjadi satu matrix. sebagai contoh bayangkan kami ingin
               memutar adegan dan memindahkan jumlah tertentu ke kejauhan. salah satu cara kita
               bisa melakukan ini adalah dengan mengeluarkan putaran yang sama dan menerjemahkan
               panggilan untuk setiap object tunggal. sementara itu berfungsi, lebih mudah untuk
               hanya menyimpan transformasi ini menjadi matrix terpisah dan menerapkanya pada
               setiap object.
            3. projection matrix
               terakhir yaitu matrix proyeksi, matrix ini membantu menciptakan ilusi 3D dan bisanya
               hanya berubah setiap kali layar mengubah orientasi.
        mari kita tinjau bagaimana vertex(titik) ditransformasikan dari posisi semula kelayar:
            vertex-model
                ini adalah vertex(titik) dalam koordinat model, contohnya akan menjadi
                posisi yang terkandung didalam simpul(vertices) meja.
            vertex-world
                ini adalah simpul yang telah diposisikan didunia dengan model matrix
            vertex-eye
                ini adalah simpul relatif terhadap mata atau kamera. kami menggunakn
                matrix tampilan untuk memindahkan semua simpul didunia disekitar relatif
                terhadap posisi tampilan kami saat ini.
            vertex-clip
                ini adalah sismpul yang telah diproses dengan matrix proyeksi. Langkah
                selanjutnya adalah melakukan pembagian perspektif seperti penjelasa pada
                page 97.
            vertex-ndc
                ini adalah titik dalam koordinat perangkat yang dinormalisasi, setelah titik
                dikoordinat int, OpenGL akan memetakanya ke viewport dan anda akan dapat
                melihatnya dilayar anda.
        seperti inilah rantai itu:
            vertex-clip = ProjectionMatrix * vertex-eye
            vertex-clip = ProjectionMatrix * ViewMatrix * vertex-world
            vertex-clip = ProjectionMatrix * ViewMatrix * ModelMatrix
        kita perlu menerapkan setiap matrix dalam urutan ini
        untuk mendapatkan hasil yang tepat.

        */

        textureProgram = new TextureShaderProgram(textureVertexShaderCode, textureFragmentShaderCode);
        colorProgram = new ColorShaderProgram(vertexShaderCode, fragmentShaderCode);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
        
        /*
        radius dan tinggi untuk keping dan mallett diatur ke ukuran arbitary(sewanang-wenang)
        sehingga mereka terlihat proposiol dengan meja. setiap object akan dibuat dengan
        32 poin di sekitar lingkaran
        */
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float)height, 1f, 10f);

        /*
        kami mengature viewport dan kami mengatur matrix proyeksi
        kami sebut setLookAtM() untuk membuat jenis matrix tampilan khusu

        setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ)
            float[] rm : ini adalah array tujuan, panjang array ini harus setidaknya
                         enam belas elemen sehingga dapat menyimpan matrix tampilan.
            int rmOffese: setLookAtM() akan mulai menulis hasil pada offset ini menjadi rm
            float exeX, eyeY, eyeZ         : disinilah mata akan berada, segala sesuatu dalan adegan
                                             akan muncul seolah-olah kita melihatnya dari titik ini.
            float centerX, centerY, centerZ: disinilah mata akan melihat, posisi ini akan muncul di
                                             tengah-tengah adegan
            float upX, upY, upZ            : jika kita berbicara tentang mata anda maka disinilah kepala
                                             kepala anda menunjuk, upY 1 berarti kepala anda akan menunjuk lurus keatas

        kami menyebut setLookAtM() dengan mata (0, 1.2, 2.2) berarti mata anda akan menjadi 1,2 unit di atas plane X-Z
        dan 2,2 unit kembali. dengan kata lain segala sesuatu dalam adegan akan muncul 1,2 unot dibawah anda dan 2,2
        unit di depan anda. sebuah pusat (0,0,0) berarti anda akan melihat dibawah kearah asal didepan anda, dan naik
        (0,1,0) berarti bahwa kepala anda akan menunjuk lurus keatas dan adegan akan diputar ke kedua sisi.
        */

        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    
    /*
    kode ini sebagian besar sama dengn di proyek terakhir tetapi ada berbedaan utama
    perbedaan pertama adalah kita sebut positionTableInScene() dan positionObjectInscene()
    sebelum kita menarik benda-benda itu. kami juga memperbarui setUniforms() sebelum menggambar mallet
    dan kami telah menambahkan kode untuk menggambar keping, apakah anda memperhatikan bahwa
    kami menggambar dua mallet dengan data mallet yang sama? kita dapat menggunakan set simpul
    ang sama untuk menarik ratusan object jika kita ingin, yang harus kita lakukan adalah
    memperbarui matrix model sebelum menggambar setiap object.
    */
    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        //Clear rendering surface
        glClear(GL_COLOR_BUFFER_BIT);

        /*
        ini akan mencache hasil mengalikan proyeksi dan melihat matrix bersama-sama menjadi
        viewProjectionMatrix.
        */
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        //Draw the table
        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        //Draw the mallets
        positionObjectInScene(0f, mallet.height/2, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

        /*
        Draw the mallet 2
        perhatian bahwa kita tidak perlu mendefiniskan data object dua kali
        kita hanya menggambar mallet yang sama lagi tetapi dalam posisi yang
        berbeda dan dengan warna yang berbeda.
        */
        positionObjectInScene(0f, mallet.height/2, 0.4f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

        //Draw the puck(keping)
        positionObjectInScene(0f, puck.height/2, 0f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    /*
    table ini awalnya didefinisikan dalam ha koordinat X dan Y
    sehingga untuk membuatnya tetap rata di tengah kami memutarnya 90 derajat
    kembali di sekitar sumbu X, perhatikan bahwa tidak seperti pelajaran sebelumnya
    kami juga tidak menerjemahkan tabel ke kejauhan karena kami ingin menyimpan table 
    pada (0,0,0) dikoordinat dunia dan matrix tampilan sudah merawat meja anda terlihat
    langkah terakhir adalah menggabungkan semua matrix bersama-sama dengan mengalikan
    viewProjectionMatrix dan modelMetrix dan menyimpan hasil dalam modelViewProjectrionMatrix
    yang kemudian akan diteruskan ke program shader.
    */    
    private void positionTableInScene() {
        //table didefinisikan dalamm hal koordinat X dan Y
        //jadi kami memutarnya 90 derajat untuk berbaring
        //rata pada plane XZ
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }
    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    /*
    mallet dan keping sudah didefinisikan untuk berbaring rata pada plane X-Z jadi tidak perlu
    rotasi, kami menerjemahkanya berdasarkan parameter yang diteruskan sehingga mereka ditempatkan
    pada posisi yang tepat diatas table.
    */

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
}

class VertexArray {
    private final FloatBuffer floatBuffer;
    
    public VertexArray(float[] vertexData) {
        floatBuffer = ByteBuffer
            .allocateDirect(vertexData.length * GameAirHoki6.BYTES_PER_FLOAT)
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
}

class Table {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * GameAirHoki6.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
        //Order of coordinate: X, Y, S, T

        //triangle Fan
        0f,    0f,  0.5f, 0.5f,
     -0.5f, -0.8f,    0f, 0.9f,   
      0.5f, -0.8f,    0f, 0.9f,   
      0.5f,  0.8f,    0f, 0.1f,   
     -0.5f,  0.8f,    0f, 0.1f,   
     -0.5f, -0.8f,    0f, 0.9f   
    };

    private final VertexArray vertexArray;
    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            textureShaderProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            STRIDE);

        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            textureShaderProgram.getTextureCoordinatesAttributeLocation(),
            TEXTURE_COORDINATES_COMPONENT_COUNT,
            STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}

/*
perbarui shader
    kami juga perlu memperbarui shader warna. Kami mendefinisikan keping dan mallet
kami dengan posisi per vertex tetapi tidak dengan warna per vertex. sebaliknya
kita harus lulus dalam warna sebagai uniform. hal pertama yang akan kami kami lakukan
untuk membuat perubahan ini adalah menambahkan konstanta baru ke ShaderProgram
*/
abstract class ShaderProgram 
{

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

class TextureShaderProgram extends ShaderProgram{
    //Uniform location
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    //atribut locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(String textureVertexShaderCode, String textureFragmentShaderCode) 
    {
        super(textureVertexShaderCode, textureFragmentShaderCode);

        //mengambil lokasi uniform untuk shader program
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        //mengambil lokasi atribut untuk shader program
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] matrix, int textureId)
    {
        //pass matrix ke dalam program shader
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        //set the active texture unit to texture unit 0
        glActiveTexture(GL_TEXTURE0);

        //bind atau ikatkan texture ke unit ini
        glBindTexture(GL_TEXTURE_2D, textureId);

        //beri tau sampler uniform texture untuk menggunakan texture ini
        //dishader dengan mengatakanya untuk membaca dari unit texture 0
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}

class ColorShaderProgram extends ShaderProgram {
    //uniform locations
    private final int uMatrixLocation;

    //atribute locations
    private final int aPositionLocation;
    private final int aColorLocation;

    //update object
    private final int uColorLocation;

    public ColorShaderProgram(String vertexShaderCode, String fragmentShaderCode) {
        super(vertexShaderCode, fragmentShaderCode);

        //ambil lokasi uniform untuk program shader
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        
        //ambil lokasi atribut untuk program shader
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);

        uColorLocation = glGetUniformLocation(program, U_COLOR);
    }

    //update object
    public void setUniforms(float[] matrix, float r, float g, float b) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform4f(uColorLocation, r, g, b, 1f);
    }
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getColorAttributeLocation() {
        return aColorLocation;
    }
}


/*
Adding a Geometriy class
    kami sekarang memiliki ide bagus tentang apa yang perlu kita bangun keping dan mallet
untuk keping kita perlu satu kipas segitiga untuk bagian atas dan satu strip segitiga untuk samping
untuk mallet kita perlu dua kipas segitiga(triangle_fan) dan dua strip segitiga. Untuk membuatnya
lebih mudah untuk membangun benda-benda ini kita akan mendefinisikan kelas geometri untuk
menampung beberapa definisi bentuk dasar dan ObjectBuilder untuk melakukan bangunan yang sebenarnya
*/

class Geometry
{
    /*
    kami menambahkan kelas untuk mewakili titik dalam ruang 3D
    bersama dengan fungsi pembantu untuk menerjemahkan titik di
    sepanjang sumbu y.
    */
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
    }

    /*
    kami juga memiliki fungsi pembantu untuk mengukur jari-jari lingkaran 
    */
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

    /*
    sebuah silinder seperti lingkaran yang diperpanjang, jadi kami memiliki
    pusat, jari-jari, dan tinggi. anda mungkin telah memperhatikan bahwa kami
    telah mendefinisikan kelas geomatri kami sebagai immutable(abadi), setiap kami
    melakukan perubahan kami mengembalikan object baru. Ini membantu membuat kode
    lebih mudah untuk dipahami tetapi ketika anda memerlukan kinerja teratas anda
    mungkin ingin tetap dengan arrat floating-point sederhana dan memutasinya dengan
    fungsi statis
    */
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
}

/*
Adding an Object Builder
    tidak ada yang terlalu mewah disini, kami telah mendefinisikan konstanta
untuk mewakili berapa banyak pelampung yang kami butuhkan untuk vertex array
untuk menahan simpul ini dan variable untuk melacak posisi dalam arges untuk
vertexs berikutnya. Konstruktor kami mendefinisikan array berdasarkan ukuran
yang diperlukan dalam simpul.
    kami akan segera mendefinisikan beberapa metode statis untuk menghasilkan
keping dan mallet. metode statis ini akan membuat instance ObjectBuilder baru
dengan ukuran yang tepat, memanggil metode instace pada ObjectBuilder untuk
menambah simpul ke vertexData, dan mengembalikan data yang dihasilkan kembali
ke pemanggil. berikut beberapa persyaratan untuk bagaimana membangun object kita
harus bekerja:
    1. pemanggil dapat memutuskan beberapa point yang seharusnya dimiliki object
       semakin banyak point, mallet akan terlihat lebih halus.
    2. object akan terkandung dalam satu array floating point. setelah object dibangun
       pemanggil akan memiliki satu array unyuk mengukat OpenGL dan satu perintah
       untuk menggambar object.
    3. Object akan dipusatkan pada posisi pemanggil yang ditentukan dan akan berbaring rata
       pada plane x-z. Dengan kata lain bagian atas object akan menunjuk lurus keatas.
*/
class ObjectBuilder {
    private static final int FLOATS_PER_VERTEX = 3;
    private final float[] vertexData;
    private int offset = 0;
    
    /*
    kita juga memerlukan variable instan untuk menahan perintah draw
    yang dikumpulkan.
    */
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    /*
    Adding a Draw Command for the triangle fan
        kita juga harus memberi tau OpenGL cara menggambar bagian atas keping. karena
    keping dibanguna dari dua primitif, kipas segitiga untuk bagian atas dan side segitiga
    untuk samping, kita perlu cara untuk menggabungkan perintah ini bersama-sama sehingga
    nanti kita bisa memanggil keping. puck.draw() salah satu cara kita dapat melakukan ini
    adalah dengan menambahkan setiap perintah draw ke draw list.
    */
    static interface DrawCommand {
        void draw();
    }
    /*
    mengembalikan data yang dihasilkan
        untuk membuat createPuck() bekerja kita hanya perlu mendefinisikan
    metode build(). kami akan menggunakan ini untuk mengembalikan data yang
    dihasilkan di dalam object data.
    ini hanya kelas holder(pemegang) sehingga kami dapat mengembalikan data
    verex dan list draw dalam satu object.
    */
    static class GenerateData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GenerateData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    /*
    itu semua diatas yang kita butuhkan untuk createPuck() untuk bekerja
    mari kita ambil momen cepat untuk meninjau aliran:
        1. pertama kita sebut metode statis createPuck() dari luar kelas. metode
           ini membuat ObjectBuilder baru dengan ukuran array kanan untuk menampung
           semua data untuk keping. ini juga membuat list tampilan sehingga kita bisa
           menggambar keping nanti
        2. didalam createPuck() kami sebut appenCircle() dan appenOpenCylinder() untuk
           menghasilkan bagian atas dan sisi keping. setiap metode menambahkan datanya
           ke vertexData dan drawCommand ke drawList.
        3. terakhir kami menyebut build() untuk mengembalikan data yang diambil.
    */

    /*
    Building a mallet with two cylinder
        sekarang kita dapat menggunakan apa yang telah kita pelajari untuk membangun
    mallet. mallet dapat dibangun dari dua silinder, jadi mari membangun mallet hampir
    seperti membangun dua keping dengan ukuran yang berbeda. Kami akan mendefinisikan
    mallet dengan cara tertentu seperti pada gambar(page:152).
    tinggi pegangan akan sekitar 75 persen dari keseluruhan tinggi dan ketinggian pangkalan
    akan 25 persen dari ketinggian keseluruhan. kita juga dapat mengatakan bahwa lebar handles
    adalah sekitar sepertiga dari keseluruhan lebar. dengan definisi-definisi ini, kita akan dapat
    menghitung dimana menempatkan dua silinder yang membentuk mallet.
    saat menulis definisi-definisi ini, terkadang membantu untuk mengambil selembar kertas
    dan menggambar object dan kemudian plot dimana semuanya relatif terhadap pusat dan sisi benda
    untuk membuat mallet kita mencari tau posisi y untuk setiap bagian atas silinder serta posisi
    tengah untuk setiap silinder.
    kami membuat ObjectBuilder baru dari ukuran yang tepat, dan kemudian kami menghasilkan base mallet
    kode ini sangat mirip dengan apa yang kami lakukan di createPuck().
        ini saja untuk ObjectBuilder kami! kita sekarang dapat menghasilkan keping dan mallet, dan
    ketika kita ingin menggambar mereka yang perlu kita lakukan adalah mengikat data vertex ke
    OpenGL dan memanggil object.draw();
    */
    static GenerateData createMallet(Geometry.Point center, float radius, float height, int numPoints)
    {
        int size = sizeOfCircleInVertices(numPoints) * 2 +
                   sizeOfOpenClylinderInVertices(numPoints) * 2;

        ObjectBuilder builder = new ObjectBuilder(size);

        //first generate the mallet base
        float baseHeight = height * 0.25f;
        Geometry.Circle baseCircle = new Geometry.Circle(center.translateY(-baseHeight), radius);
        Geometry.Cylinder baseCyliner = new Geometry.Cylinder(baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight);
        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(baseCyliner, numPoints);

        //generate the handle
        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;

        Geometry.Circle handleCircle = new Geometry.Circle(center.translateY(height * 0.5f), handleRadius);
        Geometry.Cylinder handleCylinder = new Geometry.Cylinder(handleCircle.center.translateY(-handleHeight / 2f), handleRadius, handleHeight);
        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCylinder(handleCylinder, numPoints);

        return builder.build();       
    }
    /*
    Membangun keping dengan silinder
        kita sekarang dapat membuat metoode statis untuk menghasilkan keping
    hal pertama yanf kita lakukan adalah mencari tau berapa banyak simpul yang
    kita butuhkan untuk mewakili keping, dan kemudian kita instantiate ObectBuilder
    baru dengan ukuran itu. Keping dibangun dari satu bagian atas silinder (setara dengan lingkaran)
    dan satu sisi silinder sehingga ukuran total dalam simpul akan sama dengan
    ukuran sizeOfCircleInVertices(numPoints) + sizeOfOpenClylinderInVertices(numPoints).
        kami kemudian menghitung dimana bagian atas keping harus memanggil appendCircle()
    untuk membuatnya. Kami juga menghasilkan sisi keping dengan memanggil appendOpenCylinder()
    dan kemudian kami mengembalikan data yang dihasilkan dengan mengembalikan hasil build().
    mengapa kita memindahkan bagian atas keping oleh puck.height/2f ? coba lihat gambar(page:147)
    kepingnya berpusat pada tengah secara vertical di tengah-tengah, jadi tidak apa-apa untuk
    menempatkan sisi silinder disana. Namun atas silinder perlu ditempatkan dibagian atas keping.
    untuk melakukan itu, kita memindahkanya setengah dari tinggi keping keseluruhan.
    */
    static GenerateData createPuck(Geometry.Cylinder puck, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) +
                   sizeOfOpenClylinderInVertices(numPoints);

        ObjectBuilder builder = new ObjectBuilder(size);

        Geometry.Circle puckTop = new Geometry.Circle(puck.center.translateY(puck.height / 2f), puck.radius);
        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCylinder(puck, numPoints);

        return builder.build();
    }

    /*
    metode untuk menghitung ukuran atas silinder di vertices(simpul)
        atas silinder adalah lingkaran yang dibangun dari kipas segitiga
    ini memiliki satu titik ditengah, satu titik untuk setiap titik disekitar
    lingkaran dan vertex pertama disekitar lingkaran diulang dua kali
    sehingga kita dapat menutup lingkaran.
    */
    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    /*
    metode untuk menghitung ukuran sisi silinder dalam vertice(simpul)
        sisi silinder adalah persegi panjang yang digulung dibangun dari strip
    segitiga, dengan dua simpul untuk setiap titik disekitar lingkaran dan dengan
    dua simpul pertama diulang dua kali sehingga kita dapa menutup tabung.
    */
    private static int sizeOfOpenClylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    /*
    membangun lingkaran dengan kipas segitiga
        langkah selanjutnya adalah menulis kode untuk membangun bagian atas keping menggunakan
    kipas segitiga. kami akan menulis data ke VertexData dan kami akan menggunakan offset untuk
    melacak dimana kami sedang menulis dalam array.
    */
    private void appendCircle(Geometry.Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        // Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        /*
        kipas di sekitar titik tengah <= digunakan karena kami ingin
        menghasilkan titik pada sudut awal dua kali untuk menyelesaikan kipas
        lihat gambar(page:148).
        untuk membangun kipas segitiga kami pertama-tama mendefinisikan vertex
        tengah di circle.center dan kemudian kami mengipasi titik tengah, berhati-hati
        untuk mengulangi titik pertama disekitar lingkaran dua kali. kami kemudian
        mnggunakan fungsi trigometri dan konsep unit lingkaran untuk menghasilkan poin.
        untuk menghasilkan poin disekitar lingkaran, pertama-tama kita membutuhkan loop
        yang akan berkisar diseluruh lingkaran dari 0 hingga 360 derajat atau 0 hingga
        2 kali PI dalam radian. Untuk menemukan posisi x suatu titik disekitar lingkaran
        kami menyebut cos(angel) dan untuk menemukan posisi z kami memanggil sin(angel)
        karena lingkaran kami akan berbaring rata pada plane x-z, komponen y dari unit
        lingkaran maps ke unit posisi y.
        */
        for (int i=0; i<=numPoints; i++) {
            float angleInRadians = ((float)i / (float)numPoints) *
                                   ((float)Math.PI * 2f);

            vertexData[offset++] = circle.center.x +
                                   circle.radius * (float)Math.cos(angleInRadians);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = circle.center.z +
                                   circle.radius * (float)Math.sin(angleInRadians);
        }

        /*
        karena kami hanya menggunakan satu array untuk object, kita perlu
        memberi tau OpenGL offset simpul kanan untuk setiap perintah draw.
        kami menghitung offset dan panjang dan menyimpanya ke startVertex
        dan numVertices.
        dengan kode ini kami membuat kelas inner baru yang memanggil glDrawArrays()
        dan kami menambahkan kelas dalam ke list. untuk menggambar keping nanti
        kita hanya perlu mengeksekusi setiap metoder draw dalam list.
        */
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    /*
    Building a Cylinder side with a triangle strip
        sama seperti sebelumnya kami mencari tau vertexs awal dan jumlah
    vertices(simpul) sehingga kita dapat menggunakanya dalam perintah draw.
    kami juga mencari tau dimana keping harus mulai dan berakhir. posisi
    seperti gambar(page:149).
    kami menggunakan matematika yang seperti sebelumnya untuk menghasilkan
    dua vertices(simpul) untuk setiap titik disekitar lingkaran, satu untuk
    bagian atas silinder dan satu untuk bagian bawah. kami mengulangi posisi
    dua poin pertama sehingga kami dapat menutup silinder.
    kami menggunakan GL_TRIANGLE_STRIP untuk memberi tau OpenGL untuk
    menggambar strip segitiga.
    */
    private void appendOpenCylinder(Geometry.Cylinder cylinder, int numPoints)
    {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenClylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);
    
        for (int i=0; i<=numPoints; i++) {
            float angleInRadians = ((float)i / (float)numPoints) *
                                   ((float)Math.PI * 2f);

            float xPosition = cylinder.center.x +
                              cylinder.radius * (float)Math.cos(angleInRadians);

            float zPosition = cylinder.center.z +
                              cylinder.radius * (float)Math.sin(angleInRadians);

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }
    private GenerateData build() {
        return new GenerateData(vertexData, drawList);
    }
    
}

/*
Sekarang kita memiliki pembangun object(ObjectBuilder), kita perlu memperbarui
kelas mallet kita karena kita tidak lagi menggambarnya sebagai titik. kita juga
perlu menambahkan kelas Keping(puck) baru. Mari kita mulai dengan keping.
ketika Puck(keping) baru dibuat, itu akan menghasilkan data object, menyimpan
simpul dalam buffer asli dengan vertexArray dan menyimpan list draw di drawList.
*/

class Puck {
    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius, height;
    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        ObjectBuilder.GenerateData generateData = ObjectBuilder.createPuck(
                                              new Geometry.Cylinder(
                                              new Geometry.Point(0f, 0f, 0f), radius, height),
                                              numPointsAroundPuck);

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generateData.vertexData);
        drawList = generateData.drawList;
    }

    /*
    metode ini mengikuti pola yang sama dengan yang kami ikuti dengan meja dan mallet.
    ia mengikat data titik ke atribut yang ditentukan oleh program shader. metode kedua
    draw() baru saja melewati list tampilan yang dibuat oleh ObjectBuilder ObjectBuilder.createPuck().
    */
    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0, 
                                           colorProgram.getPositionAttributeLocation(), 
                                           POSITION_COMPONENT_COUNT, 
                                           0);
    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}

class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius;
    public final float height;
    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Mallet(float radius, float height, int numPointsAroundMallet)
    {
        ObjectBuilder.GenerateData generateData = ObjectBuilder.createMallet(
                                              new Geometry.Point(0f, 0f, 0f),
                                              radius,
                                              height,
                                              numPointsAroundMallet);
        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generateData.vertexData);
        drawList = generateData.drawList;
    }
    public void bindData(ColorShaderProgram colorProgram)
    {
        vertexArray.setVertexAttribPointer(0,
                                           colorProgram.getPositionAttributeLocation(),
                                           POSITION_COMPONENT_COUNT, 0);
    }
    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
