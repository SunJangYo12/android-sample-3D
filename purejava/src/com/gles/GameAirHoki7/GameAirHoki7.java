/*

* BAB 8
* Adding touch interacting Air Hockey
*
* kendala touch kebalik, harusnya mallet biru
* yang disentuh malah merah untuk menggerakan mallet biru
*/

package com.gles.GameAirHoki7;

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
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.invertM;
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
Interaksi pengguna yang baik melalui dukungan sentuh adalah landasan
banyak game dan aplikasi, ini dapat memberi pengguna rasa game dengan
sesuatu yang nyata, bahkan jika mereka hanya melihat pixel layar.
beberapa game seluler menjadi sangat populer hanya karena mereka datang
dengan paradigma sentuhan baru, game tertentu yang melibatkan burung
terlintas dalam pikiran.
kami sekarang memiliki mallet yang terlihat lebih baik tetapi tidak akan
menyenangkan jika kami benar-benar dapat mengunakanya, dalam bab ini kita
mulai membuat program kita lebih interaktif dengan menambahkan dukungan sentuh
kami akan belajar cara menambahkan tes persimpangan 3D dan deteksi tabrakan
sehingga kami dapat menggambil mallet kami dan menyeretnya disekitar layar
inilah rencana game kami untuk bab ini:
    1. kami akan mulai dengan menambahkan interaktive sentuk ke game kami
       kami akan membahas matematika dan pipa yang diperlukan untuk menyelesaikan
       ini.
    2. kami kemudian akan belajar cara membuat mallet kami berinteraksi dengan keping
       dan tetap dalam batas.
ketika kami selesai dalam bab ini kami dapat menyerang keping dengan mallet dan
menontonya memantau disekitar meja.

*/

public class GameAirHoki7 implements Renderer
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
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private Puck puck;

    /*
    sekarang kita memiliki area layar yang disentuh dalam koordinasi
    perangkat yang dinormalisasi, kita perlu menentukan apakah area
    yang disentuh mengandung mallet. kita perlu melakukan tes persimpangan
    operasi yang sangat penting saat bekerja dengan game dan aplikasi 3D.
    inilah hal yang perlu kita perhatikan:
      1. pertama-tama kita perlu mengkonversi layar 2D berkoordinasi kembali
         ke ruang 3D dan melihat apa yang kita sentuh. kami akan melakukan ini
         dengan mencoret titik yang disentuh menjadi sinar yang mencakup adegan 3D
         dari sudut pandang kami.
      2. kita kemudian perlu memeriksa untuk mleihat apakah sinar ini berpotongan
         dengan mallet. untuk membuat segalanya lebih mudah kami akan berpura-pura
         bahwa mallet sebenarnya adalah bola yang membatasi sekitar ukuran yang sama
         dan kemudian kami akan menguji bola itu. mari kita membuat dua variable anggota baru
    kami akan menggunakan malletpressed untuk melacak apakah mallet saat ini ditekan atau
    tidak. kami juga akan menyimpan posisi mallet di blueMalletPosition. kita juga perlu
    menginisialisasi ini ke nilai default.
    */
    private boolean malletPressed = false;
    private Geometry.Point blueMalletPosition;

    /*
    Memperluas titik 2D menjadi garis 3D
      biasanya ketika kami memproyeksikan adegan 3D ke layar 2D kami
    menggunakan proyeksi perspektif dan divide perspektif untuk mengubah
    simpil kami menjadi koordinat perangkat yang dinormalisasi (lihat page:97)
    sekarang kita ingin pergi ke arah lain, kita memiliki koordinat perangkat
    yang dinormalisasi dari titik yang disentuh dan kami ingin mencari tau
    dimana didunia 3D yang menyentuh titik sesuai. untuk mengonvensi titik yang
    disentuh menjadi sinar 3D kami pada dasarnya perlu membatalkan proyeksi perspektid
    dan perbedaan perspektif. kami saat ini telah menyentuh koordinat X dan Y
    tetapi kami tidak tau seberapa dekat atau jauh titik yang disentuh seharusnya.
    untuk membatasi ambiguitas kami akan memetakan titik yang disentuh ke garis
    dalam ruang 3D, ujung dekat garis akan memetakan ke ujung frustum. untuk melakukan
    konversi ini kita akan memerlukan matrix terbalik yang akan membatalkan efek dari
    tampilan dan matrix proteksi. mari kita tambahkan definisi berikut ke list definisi
    matrix.
    */
    private final float[] invertedViewProjectionMatrix = new float[16];


    /*
    Adding Collision Detection
        sekarang setelah anda memiliki kesempatan untuk bersenang-senang dan
    menyeret mallet disekitar, anda mungkin memperhatikan masalah pertama kami
    mallet dapat keluar dari batas. di bagian ini kami akan menambahkan beberapa
    tabrakan dasar kami juga akan menambahkan beeberapa fisika dasar untuk membiarkan
    kami menampar keping disekitar meja.
    */

    //menjaga pemain mallet dalam batas
    private final float leftBound = -0.5f;
    private final float rightBound =  0.5f;
    private final float farBound = -0.8f;
    private final float nearBound =  0.8f;

    /*
    Menambahkan Velocity(kecepatan) dan Direction(arah)
        sekarang kita dapat menambahkan beberapa kode untuk memukul keping
    dengan mallet. untuk mendapatkan gambaram tentang bagaimana keping harus
    berinteraksi kita perlu menjawab beberapa pertanyaan:
        1. seberapa cepat perlu?
        2. kearah mana mallet bergerah?
    kita dapat menjawab pertanyaan-pertanyaan ini, kita perlu melacak bagaimana
    mallet bergerak seiring waktu. hal pertama yang akan kita lakukan adalah
    menambahkan variable anggota baru yang disebut previousBlueMallet.
    */
    private Geometry.Point previousBlueMalletPosition;

    /*
    sekarang adalah menyimpan posisi untuk keping
    serta kecepatan dan arah
    */
    private Geometry.Point puckPosition;
    private Geometry.Vector puckVector;


    public GameAirHoki7() {
    }

    public GameAirHoki7(Context context) 
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

        textureProgram = new TextureShaderProgram(textureVertexShaderCode, textureFragmentShaderCode);
        colorProgram = new ColorShaderProgram(vertexShaderCode, fragmentShaderCode);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
        
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);

        blueMalletPosition = new Geometry.Point(0f, mallet.height/2, 0.4f);
    
        //kami menggunakan vector untuk menyimpan kecepatan dan arah keping
        puckPosition = new Geometry.Point(0f, puck.height/2f, 0f);
        puckVector = new Geometry.Vector(0f, 0f, 0f);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float)height, 1f, 10f);
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        //Clear rendering surface
        glClear(GL_COLOR_BUFFER_BIT);

        //membuat keping bergerak pada setiap bingkai
        puckPosition = puckPosition.translate(puckVector);

        /*
        Menambahkan refleksi terhadap batas
            sekarang kita memiliki masalah lain, keping kita bergerak
        tetapi seperti yang anda lihat keping diluar batas, itu terus
        berjalan dan pergi.
        kami pertama-tama memeriksa apakah kepingnya sudah terlalu jauh
        kekiri atau kekanan. Jika sudah maka kita membalikan arahnya dengan
        membalik komponen x dari vector. kami kemudian memeriksa apakah keping
        telah melewati tepi meja dekat atau jauh. dalam hal ini kami membalikan
        arahnya dengan membalik komponen z dari vector. jangan bingung dengan
        pemeriksaan z - semakin jauh sesuatu adalah semakin kecil z karena z
        negatif menunjuk ke kejauhan.
            akhirnya kami membawa puck kembali dengan batas-batas meja dengan
        menjepitnya ke batas meja. jika kita mencobanya keping kita sekarang harus
        memantul didalam meja alih-alih menembus ketepi tapi keping belum bisa melambat.
        */
        if (puckPosition.x < leftBound + puck.radius ||
            puckPosition.x > rightBound - puck.radius) {
            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
        }
        if (puckPosition.z < farBound + puck.radius ||
            puckPosition.z > nearBound - puck.radius) {
            puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
        }

        //clamp(mencepit) posisi keping
        puckPosition = new Geometry.Point(clamp(puckPosition.x, leftBound+puck.radius, rightBound-puck.radius), puckPosition.y,
                                 clamp(puckPosition.z, farBound+puck.radius, nearBound-puck.radius)
        );

        /*
        menambahkan frictio(gesekan)
            masih ada satu masalah dengan cara keping bergerak, tidak pernah
        melambat itu tidak terlihat sangat realistis jadi kami akan menambahkan
        beberapa kode peredam untuk memperlambat keping dari waktu ke waktu.
            jika kita menjalanakan lagi kita akan melihat keping melambat dan
        akhirnya berhenti. kita dapat membuat segalanya lebih realistis dengan
        menambahkan peredam tambahan ke bouncing. tambahkan kode berikut dua kali
        sekali didalam tubuh setiap pemeriksaan bouncing
        */
        puckVector = puckVector.scale(0.99f);


        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        
        /*
        panggilan ini akan membuat matrix terbalik yang dapat kita gunakan untuk
        menonversi titik sentuh dua dimensi menjadi sepasang koordinat tiga dimensi
        jika bergerak dikancah kita, itu akan mempengaruhi bagian dimana dari adegan
        itu dibawah jari-jari kita, jadi kita juga ingin memperhitungkan matrix tampilan.
        kami melakukan ini dengan mengambiil kebalikan dari pandangan gabungan dan matrix
        proyeksi.
        */
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

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

        //Draw the mallet 2
        /*
        contoh tes persimpangan ray-plane
            seperti sebelumnya kita juga akan berjalan melalui contoh
        hanya untuk melihat bagaimana angka-angka keluar. mari kita
        gunakan contoh plane gambar page:180 dengan Ray(sinar).
        kami memiliki plane di (0,0,0) dengan normal (0,1,0) dan kami memiliki
        sinar pada (-2, 1, 0) dengan vector (1, -1, 0). jika kita memperpanjang
        vector cukup jauh dimana sinar ini akan menabrak plane? mari kita pergi
        melalui matematika dan mencari tahu.
            pertama kita perlu menerapkan rayToPlaneVector ke vector antara
        plane dan sinar. ini harus diatus ke (0,0,0) - (-2,1,0) = (2,-1,0).
        selanjutnya adalah menghitung scaleFactor. setelah kita menghitung
        produk dot persamaa tersebut berkurang ke -1/-1 yang memberi kita
        faktor penskalaan 1.
        untuk mendapatkan titik persimpangan kita hanya perlu menerjemahkan
        titik ray dengan vector sinar yang diskalakan. Ray vecto diskalakan oleh 1
        jadi kita bisa menambahkan vetor ke titik untuk mendapatkan (-2,1,0) + (1,-1,0) = (-1,0)
        disinilah sinar berpotongan dengan plane.
            kami sekarang telah menambahkan semua yang kami butuhkan untuk
        mendapatkan handleTourchDrag() untuk bekerja. hanya ada satu bagian
        yang tersisa, kita kembali ke GameRenderer dan menggunakan poin baru
        ketika menggambar mallet biru. mari perbarui positionObjectInScene().
        maka anda dapat menyeret mallet dilayar menikuti ujung jari anda.
        */
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

        //Draw the puck(keping)
        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    private void positionTableInScene() {
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
    membalikan proyeksi perspektif dan divide perseptif
    */
    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        /*
        kami akan menkonversi koordinat perangkat normal ini menjadi koordinat ruang dunia
        kami akan mengambil titik di plane dekat dan jauh, dan menggambar garis diantara
        mereka. untuk melakukan transformasi ini, kita perlu pertama kali dikalikan dengan
        matrix terbalik dan kemudian kita perlu membatalkan divide perspektif.
        */
        final float[] nearPointNdc = { normalizedX, normalizedY, -1, 1 };
        final float[] farPointNdc  = { normalizedX, normalizedY, 1, 1 };
    
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        /*
          untuk memetakan titik yang disentuh ke sinar kami mengatur dua titik
        dalam koordinat perangkat yang dinormlaisai, satu titik adalah titik yang
        disentuh dengan Z dari +1. kami menyimpan poin-point ini di nearPointNdc dan fatPointNdc
        masing-masing. karena kami tidak tau apa yang harus dilakikan komponen W kami menetapkan
        W dari 1 untuk keduanya. kami kemudian mengalikan setiap titik dengan invertedViewProjectionMatrix
        untuk mendapatkan koordinat diruang dunia. kita juga perlu membatalkan perbedaan persepektid
        ada properti yang menarik dari matrix proyeksi tampilan terbalik, setelah kami
        mengalikan simpul kami dengan matrix proyeksi tampilan terbalik nearPointWorld dan
        farPointWorld akan benar-benar bersisi nilai W terbitan. ini karena biasanya seluruh
        titik W yang berbeda sehingga divide(perbedaan) perspektif dapat melakukan sihirnya,
        jadi jika kita menggunakan matrix proyeksi terbalik kita juga akan mendapatkan W. yang
        perlu kita lakukan adalah membagi X,Y dan Z dengan terbaru ini dan kita membatalkan perbedaan
        perspektif.
        */
        multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
    
        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        /*
        mendefinisikan Ray
          kami sekarang telah berhasil menkonversi titik yang disentuh menjadi
        dua titik diruang dunia. kita sekarang dapat menggunakan dua poin ini untuk
        menentukan sinar yang mencakup adegan 3D.
        */
        Geometry.Point nearPointRay = new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay = new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
    
        return new Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
    }
    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Log.i("setsuna", ""+normalizedX+" "+normalizedY);
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

        /*
        melakukan tes persimpangan
            sebelumnya kami menyebutkan bahwa melakukan tes persimpangan akan jauh
        lebih mudah jika kami berpura-pura bahwa mallet kami adalah bola. Bahkan jika
        kita melihat ke belakang di handleTouchPress() kita mendefinisikan bola-bola
        pembatas dengan dimensi yang sama dengan mallet.
        */

        /*
        Menggunakan segitiga untuk menghitung jarak
            sebelum kita mendefinisikan kode untuk ini, mari kita visualisasikan tes
        persimpangan untuk membuat segalnya lebih mudah gambar:page174
        untul melakukan tes ini kita perlu mengikuti langkah-langkah ini:
        1. kita perlu mencari tahu jarak antara bola dan sinar. Kami melakukan ini
           dengan terlebih dahulu mendefinisikan dua poin pada ray, titik awal dan 
           titik akhir ditemukan dengan menambahkan vector ke ray ke titik awal. Kami
           kemudian membuat segitiga imajiner antara dua titik ini dan pusat bola, dan
           kemudian kami mendapatkan jarak dengan menghitung ketingian segitiga itu.
        2. kami kemudian membandingkan jarak itu ke jari-jari spere. jika jarak itu lebih
           kecil dari jari-jari, maka sinar akan berpotongan dengan bola. penjelasan yang
           lebih rinci dibalik algoritma ini dapat anda temukan di Wolfram mathWorld.
        */
        /*
        sekarang kita uji apakah sinar ini berpotongan dengan mallet
        dengan bounding sphere(meloatkan bola) yang membungkus mallet
        */
        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(
                                                                    blueMalletPosition.x,
                                                                    blueMalletPosition.y,
                                                                    blueMalletPosition.z),
                                                                mallet.height / 2f);

        /*
        jika ray berpotongan(jika pengguna menyentuh bagiian layar
        yang memotong biidang batas mallet) make atur malletPresed = true
        */
        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
    }
    
    /*
    moving disekitar object dengan drag
        sekarang kita dapat menguji apakah mallet telah disentuh, kita akan bekerja untuk
    memecahkan bagian selanjutnya dari puzzle, dimana mallet pergi ketika kita drag(menyeretnya)?
    kita dapa memikirkan hal-hal dengan cara ini, mallet terletak rata diatas meja jadi ketika
    kita memindahkan jari kita, mallet harus bergerak denan jari kita dan terus berbaring diatas
    meja. kita dapat mengetahui posisi yang tepat dengan melakukan tes persimpangan Ray-plane
    */

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        /*
        untuk meelihat apakah titik yang disentuh memotong mallet, pertama-tama
        kita melemparkan titik yang disentuh ke sinar, bungkus mallet dengan bola-bola
        pembatas, dan kemudian menguji untuk melihat apakah sinar itu berpotongan
        dengan bola itu. ini mungkin lebih masuk akal jika kita melihat hal-hal secara
        visual. mari kita pertimbangkan adegan imajiner dengan meja hoki kita, keping
        dan dua mallet, dan mari kita bayangkan bahwa kita menyentuh layar pada lingkaran
        gelap dalam gambar page:169.
        kami dengan jelas menyentuh salah satu mallet. Namun area kami yang disentuh berada
        di ruang 2D dan mallet berada diruang 3D bagaimana cara kami menguji apakah titik
        sentuhan berpotongan dengan mallet? untuk menguji ini, pertama-tama kita mengkonversi
        titik 2D menjadi dua titik 3D, satu diujung dekat frustum 3D dan satu diujung frustum 3D
        (jika kata "frustum" anda lupa sekarang mungkin waktu yang tepat untuk kembali page 101)
        kemudian menggambar garis antara dua titik ini untuk membuat sinar. jika kita melihat adegan
        kita dari samping inilah cara Ray memotong adegan 3D.
        Untuk membuat mamtematika lebih mudah kami akan berpura-pura bahwa mallet adalah bola ketika
        kami melakukan tes. Mari kita mulai dengan mendefinisikan convertNormalized2DPointToRay() dan
        memecahkan bagian pertama dari puzze, mengubah titik yang disentuh menjadi sinar 3D
        */
    
        if (malletPressed) {
            Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

            //tentukan plane yang mewakili meja hoki
            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0,0,0), new Geometry.Vector(0, 1, 0));
        
            /*
            cari tau dimana titik sentuhan memotong plane mewakili meja kami
            kami akan memindahkan mallet di sepanjang plane ini
            */
            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);

            previousBlueMalletPosition = blueMalletPosition;

            /*
            penambahan batas pemain mallet
                jika kita melihat ke belakang dan meninjau handelTouchDrag() kita akan
            ingat bahwa touchPoint mewakili persimpangan antara dimana kita menyentuh
            layar dan plane yang ada diatas meja. mallet ingin pindah ke titik ini.
            untuk menjaga mallet dari melebihi batas meja kami menjepit touchPoint ke
            batas meja. mallet tidak dapat melampui tepi meja. kami juga mengambiil garis
            pemisah tabel ke dalam akun dengan menggunakan 0f alih-alih jauh, sehingga pemain
            tidak dapat menyebrang kesisi lain, dan kami juga mempertimbangkan radius mallet
            sehingga tepi mallet tidak dapat melampui batas pemain.
            */
            blueMalletPosition = new Geometry.Point(clamp(touchedPoint.x,
                                                          leftBound + mallet.radius,
                                                          rightBound - mallet.radius),
                                                    mallet.height/2f,
                                                    clamp(touchedPoint.z,
                                                          0f + mallet.radius,
                                                          nearBound - mallet.radius));
            
            /*
            sekarang kita menambahkan kode tabrakan
                kode ini pertama-tama akan memeriksa jarak antara mallet
            biru dan keping dan kemudian akan melihat apakah jarak itu
            kurang dari dua radiasi mereka disatukan. jika ya maka mallet
            telah memukul keping dan kami mengambil posisi mallet sebelumnya
            dan posisi mallet saat ini telah membuat vector arak untuk keping.
            semakin cepat mallet semakin besar vector akan menjadi dan semakin
            cepat keping bergerak.
            */
            float distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length();
            if (distance < (puck.radius + mallet.radius))
            {
                /*
                mallet telah memukul keping sekarang kirim keping
                bergerak berdasarkan kecepatan mallet
                */
                puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
            }
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
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
}

class VertexArray {
    private final FloatBuffer floatBuffer;
    
    public VertexArray(float[] vertexData) {
        floatBuffer = ByteBuffer
            .allocateDirect(vertexData.length * GameAirHoki7.BYTES_PER_FLOAT)
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
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * GameAirHoki7.BYTES_PER_FLOAT;

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

    /*
    sebuah sinar terdiri dari titik awal dan vector yang mewakili arah sinar.
    untuk membuat vector ini kami sebut vectorBetween() untuk membuat vector
    mulai dari titik dekat ke titik jauh.
    */
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

        /*
        metode pertama length() mengembalikan panjang vector dengan
        menerapkan teorema pytagoras yang kedua crossProduct()
        menghitung produk lintas antara dua vector.
        */
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

        /*
        Contoh tes persimpangan ray-Sphere
            mari kita berjalan melalui kode dengan contoh aktual
        menggunakan gambar poin koodrinat page:176. kami memiliki sinar
        di (0,0,0) dengan vector (6,6,0) karena kita membutuhkan dua poin
        kita menambahkan vector ke titik pertama yang memberi kita titik
        kedua pada (6,6,0). kami juga memiliki bola dengan jari-jari 2 dan
        titik pusat di (5,1,0).
        hal pertama yang kami adalah menetapkan vector p1ToPoint dan p2ToPoint.
        untuk p1ToPoint kami akan mengaturnya ke vector antara titik awal sinar
        dan pusat sphere. jadi kami mengaturnya ke (5,1,0) - (0,0,0). untuk p2ToPoint
        kami mengaturnya ke vector antara titik kedua sinar dan pusat bola, jadi kami
        mengaturnya ke (5,1,0) - (6,6,0) = (-1, 0).
            langkah selanjutnya adalah mendapatkan ketinggian segitiga. Pertama kita
        mendapatkan area ganda dengan mengambil panjang produk lintas p1ToPoint dan p2ToPoint
        kami tidak akan melalui semua langkah perantara, tetapi jika anda mengikuti matematika
        anda harus berakhir dengan produk silang dari (0,0,-24) dan panjang 24. dengan
        kata lain area segitiga sma denan 24 dibagi dengan 2. untuk mendapatkan ketinggian 
        segitiga area dikalikan dengan 2 sama dengan 24, jadi sekarang kita hanya perlu
        menemukan basis dengan mengambil panjang vector ray (6,6,0).
            pada panjang vector ini adalah sekitar 8,49 jadi jika kita pecahkan untuk
        ketinggian kita berakhir dengan 24/8.49 = 2.82. sekarang kita bisa melakukan tes
        akhir kita. 2.82 lebih besar dari jari-jari 2, jadi sinar ini pasti tidak berpotongan
        dengan bola ini. definisi kami untuk handleTouchPress() sekarang lengkap.
        coba jalankan aplikasi dan tambahkan beberapa debug untuk melihat apa yang terjadi
        jika anda menyentuh mallet.
        */

        /*
        metode pertama dotProduct() menghitung produk titik antara dua vector
        yang kedua scale() skala setiap komponen vector secara merata dengan jumlah
        skala.
        */
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
    /*
    kami sekarang telah menyelesaikan bagian pertama dari puzzle,
    mengkonversi titik yang disentuh menjadi sinar 3D. sekarang
    kita perlu menambahkan tes persimpangan
    */

    public static class Sphere {
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    /*
    metode ini akan menentukan jarak antara pusat sphere dan sinar lalu
    periksa apakah jarak itu kurang dari jari-jari bola. jika itu maka
    bola berpotongan.
    */
    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    /*
    Menghitung jarak dengan matematika vector
        metode ini mungkin terlihat sedikit intents, tetapi hanya melakukan metode
    segitiga yang baru saja kita sebutkan. Pertama kami mendefinisikan dua vector,
    satu dari titik pertama ray ke pusat sphere dan satu dari titik kedua ray ke pusat
    sphere. kedua vector ini bersama-sama mendefinisikan segitiga. untuk mendapatkan
    area segitiga ini, pertama-tama kita perlu menghitung produk silang dari dua vector ini.
    menghitung prorduk silang akan memberi kita vector ketiga yang tegak lurus dengan dua
    vector pertama, tetapi yang lebih penting bagi kita panjang vector ini akan sama
    dengan dua kali area segitiga yang ditentukan oleh dua vector pertama. Begitu kita memiliki
    area segitiga kita dapat menggunakan rumus segitiga untuk menghitung ketinggian segitiga
    yang akan memberi kita jarak dari sinar(ray) ke tengah bola. Tinggi akan sama dengan (area*2)/lengthOfBase.
    kami memiliki area * 2 dia diarea areaOfTriangleTimesTwo dan kami dapat menghitung panjang
    pangkalan(base) dengan mengambil panjang ray.vector
        untuk menghitung ketinggian segitiga kita hanya membagi satu dengan yang lain
    setelah kita memiliki jarak ini, kita dapat membandingkanya dengan jari-jari Sphere
    untuk melihat apakah sinar berpotongan dengan bola.
    */
    public static float distanceBetween(Point point, Ray ray) {
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);

        /*
        panjang produk silang memberikan area imajiner jajar genhang memiliki du vector
        sebagai sisi. pararelogram dapat dianggap terdiri dari dua segitiga jadi ini sama
        dengan dua kali area segitiga yang ditentukan oleh dua vector. http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning
        */
        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();

        /*
        area segitiga juga sama dengan (base * height)/2. dalam kata lain ketinggian
        sama dengan (area * 2)/base. tinggi dari segitiga ini adalah jarak dari 
        (point to ray) titik ke sinar.
        */
        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }

    /*
    kami hanya ingin menyeret mallet disekitar jika kami awalnya menekanya dengan jari
    jadi pertama-tama kami memeriksa untuk melihat bahwa pijakan itu benar. jika
    ya maka kami melakukan konversi sinar(ray) yang sama yang kami lakukan di handleTouchPress()
    begitu kita memiliki sinar yang mewakili titik yang disentuh kita mengetahui kemana
    ray berpotongan dengan plane yang diwakili oleh meja hoki kita, dan kemudian memindahkan
    mallet ke titik itu.
    definisi plane ini sangat sederhana terdiri dari vector normal dan titik pada bidang itu
    vector normal plane hanyalah vector yang tegak lurus terhadap bidang itu. ada definisi plane
    yang mungkin, tetapi ini adalah salah satu yang akan kami kerjakan. Dalam gambar(page:178) kita
    dapat melihat contoh plane yang terletak di (0,0,0) denan normal (0,1,0).
    ada juga sinar di (-2, 1, 0) dengan vector (1, -1, 0) kami akan menggunakan plane ini dan ray
    untuk menjelaskan persimpangan.
    */
    public static class Plane {
        public final Point point;
        public final Vector normal;

        public Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;
        }
    }
    /*
    untuk menghitung titik persimpangan
        untuk menghitung titik persimpangan kita perlu mencari tau seberapa banyak kita
    perlu menskala vector ray sampai menyentuh plane dengan tepan, ini adalah vector penskalaan
    kami kemudian dapan menerjemahkan titik ray dengan vector skala ini untuk menemukan titik
    persimpangan. Untuk menghitung faktor penskalaan, pertama-tama kita membuat vector antara
    titik awal ray dan titik di plane. kami kemudian menghitung produk titik antara vector dan pesawat
    normal. produk titik dari dua vector terhubung langsung dengan (meskipun bisanya tidak setara dengan)
    kosinus antara kedua vector itu. sebagai contoh jika kami memiliki dua vector pararel (1,0,0) dan (1,0,0)
    maka sudut diantara mereka akan menjadi 0 derajat dan kosinus dari sudut ini akan 1. jika kita
    memiliki dua vector tegak lurus dari (1, 0, 0) dan (0,0,1) maka sudut diantara meraka akan menjadi
    90 derajat dan kosinus dari sudut ini akan menjadi 0.
    untuk mengetahui jumlah penskalaan kita dapat mengambil titik antara vector
    ray-to-plane dan plane normal dan membagi dengan produk titik antara vector
    sinar dan plane normal, ini akan memberi kita faktor penskalaan yang kita butuhkan.
        kasus khusus terjadi ketika sinar sejajar dengan plane, dalam hal ini tidak ada
        titik persimpangan yang memungkinkan antara sinar dan plane. Ray akan tegak lurus
        terhadap bidang normal, produk titik akan 0, dan kami akan mendapatkan divisi dengan 0
        ketika mencoba menghitung faktor penskalaan. kami akan berakhir dengan titik
        persimpangan yang penuh dengan NaNs floating point yang merupakan tangan pendek
        untuk "bukan angaka". jangan kuatir jika anda tidak mengerti ini secara lengkap.
        bagian pentingnya adalah itu berhasil, untuk belajar matematika lebih serius ada
        penjelasan yang lebih bagus di wikipedia yang dapat anda baca.
        http://en.wikipedia.org/wiki/Dot_product
        http://en.wikipedia.org/wiki/Line-plane_intersection
    */
    public static Point intersectionPoint(Ray ray, Plane plane) {
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);
        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal) /
                            ray.vector.dotProduct(plane.normal);

        Point intersectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
        return intersectionPoint;
    }

}

class ObjectBuilder {
    private static final int FLOATS_PER_VERTEX = 3;
    private final float[] vertexData;
    private int offset = 0;
   
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    static interface DrawCommand {
        void draw();
    }
    
    static class GenerateData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GenerateData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

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
    
    static GenerateData createPuck(Geometry.Cylinder puck, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) +
                   sizeOfOpenClylinderInVertices(numPoints);

        ObjectBuilder builder = new ObjectBuilder(size);

        Geometry.Circle puckTop = new Geometry.Circle(puck.center.translateY(puck.height / 2f), puck.radius);
        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCylinder(puck, numPoints);

        return builder.build();
    }

    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    private static int sizeOfOpenClylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    private void appendCircle(Geometry.Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        // Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        for (int i=0; i<=numPoints; i++) {
            float angleInRadians = ((float)i / (float)numPoints) *
                                   ((float)Math.PI * 2f);

            vertexData[offset++] = circle.center.x +
                                   circle.radius * (float)Math.cos(angleInRadians);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = circle.center.z +
                                   circle.radius * (float)Math.sin(angleInRadians);
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

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
