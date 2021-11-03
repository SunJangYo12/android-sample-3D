/*

* BAB 6
* Menambah detail dengan Texture
*
* PERBAIKI SEGERA: gambar mallet tidak bulat
*/

package com.gles.GameAirHoki5;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.gles.R;

/*
    Kami telah berhasil menyelesaikan banyak dengan bentuk dan warna
sederhana. Ada sesuatu yang hilang, bagaimana jika kita bisa melukis pada bentuk kita
dan menambahkan detail halus? kita dapat memulai dengan bentuk dasar dan warna
dan menambahkan detail extra ke permukaan kita dengan menggunakan texture
teksture hanyalah gambar atau gambar yang telah diunggah ke OpenGL.
    kami dapat menambahkan jumlah detail yang luar biasa dengan texture
dijantung hal-hal, game hanya menggunakan titik, garis dan segitiga seperti
program 3D lainya namun dengan detail tekstur dan sentuhan seniman yang terampil
segitiga ini dapat berteksture untuk membangun adegan 3D yang indah. setelah
kami mulai menggunakan texture, kami juga akan mulai menggunakan lebih dari satu
program shader. Untuk membuat ini lebih mudah dikelola kami akan belajar cara
menyesuaikan kode kami sehingga kami dapat menggunakan beberapa program shader
dan sumber data vertex dan beralih diantara mereka.

a. Pengertian Texture
    Tekstur di OpenGL dapat digunakan untuk mewakili gambar dan bahkan data fraktal
yang dihasilkan oleh algoritma matematika. Setiap texture dua dimensi terdiri dari banyak
'texel' kecil yang merupakan blok kecil data analog dengan fragmen dan pixel yang telah
dibicarakan di bab sebelumnya. cara paling umum untuk menggunakan teksture adalah memuat
dalam data langsung dari file gambar.
    setiap teksure dua dimensi memiliki ruang koordinat sendiri, mulai dari (0,0) di satu sudut
sampai (1,1) disudut lain. Dengan konvensi satu dimensi disebut S dan yang lainya disebut T
ketika kami ingin menerapkan teksture ke segitiga atau set segitiga kami akan menentukan
satu set koordinat tekture ST untuk setiap vertex sehingga OpenGL yang dibutuhkan setiap bagian
dari texture yang diperlukan untuk menarik masing-masing koordinat texture ini juga kadang-kadang
disebut sebagai koordinat texture UV, seperti yang terlihat pada gambar dibawah ini:


         Gambar: OpenGL texture koordinat
   (0,1)  __________________ (1,1)
      ^  |                  |
      |  |                  |
      |  |                  |
    t |  |                  |
      |  |     Texture      |
      |  |                  |
      |  |                  |
      |  |                  |
      |  |                  |
         |__________________|
           --------------->
    (0,0)         s          (1,0)
        

    Tidak ada orientari yang melekat untuk texture OpenGL, karena kita dapat menggunakan koordinat
yang berbeda untuk mengarahkanya dengan cara yang kita sukai. namun ada orientasi default untuk sebagian
besar file gambar komputer, mereka biasanya ditentukan dengan sumbu Y yang menunjuk kebawah
seperti gambar dibawah:
          __________________
      |  |                  |
      |  |                  |
      |  |                  |
    Y |  |                  |
      |  |   gambar.png     |
      |  |                  |
      |  |                  |
      |  |                  |
      |  |                  |
      v  |__________________|
            --------------->
                    X
Nilai Y meningkat saat kita bergerak ke bagian bawah gambar. Ini tidak menyebabkan
masalah bagi kita selama kita ingat bahwa jika kita ingin melihat gambar kita dengan
orientasi yang tepat maka koordinat texture kita perlu memperhitungkan hal ini.
    Dalam standar OpenGL 2.0 tekture tidak harus persegi tetapi setiap dimensi harus
menjadi kekuatan of two(POT). Ini berarti bahwa setiap dimensi harus angka seperti
128,256,512 dan sebagainya. Alasan untuk ini adalah bahwa teksture no-pot sangant terbatas
dimana mereka dapat digunakan, sementeara texture pot baik untuk semua penggunaan.
    Ada juga ukuran maksimum yang bervariasi dari implementasi ke implementasi tetapi
biasanya sesuatu yang besar seperti 2048x2048.
*/

public class GameAirHoki5 implements Renderer
{
    public static final int BYTES_PER_FLOAT = 4;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    /*
    sekarang kami telah membagi data vertex kami dan program shader
    ke kelas yang berbeda, mari kita tambahkan variable ini untuk
    menggambar texture.
    */
    private Context context;
    private Table table;
    private Mallet mallet;
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;
    private int texture;

    public GameAirHoki5() {
    }

    public GameAirHoki5(Context context) 
    {
        this.context = context;
        
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        table = new Table();
        mallet = new Mallet();

        /*
        Sebelum kita dapat menggambar texture ke layar, kita harus
        membuat set shader baru yang akan menerima texture dan
        menerapkanya pada fragmen yang ditarik. shader baru ini
        akan mirip dengan yang telah kami kerjakan hingga sekarang
        hanya dengan beberapa perubahan sedikit untuk menambah
        dukungan untuk texture.
            kami telah mendefinisikan uniform untuk matrix kami dan
        kami juga memiliki atribut untuk posisi kami untuk mengatur
        gl_Position final. Kami juga menambahkan atribut baru untuk
        koordinat texture kami yang disebut a_TextureCoordinates ini
        didefinisikan sebagai vec2 karena ada dua komponen yaitu
        S komponen dan T komponen, kami mengirim koordinat ini ke
        shader fragmen sebagai variasi yang diinterpolasi bernama v_TextureCoordinates
            untuk menggambar fragmen pada suatu object, OpenGL akan
        memanggil fragmen shader untuk setiap fragmen dan setiap panggilan
        akan menerima koordinat texture di koordinat v_Texture. Fragmen
        shader juga akan menerima data texture aktual melalui uniform
        u_TextureUnit yang didefinisikan sebagai sampler2D jenis variable
        ini mengacu pada array data texture dua dimensi.
        koordinat texture yang diinterpolasi dan data texture diteruskan
        ke fungsi shadere texture2D() yang akan membaca dalam nilai warna
        untuk texture pada koordinat tertentu. Kami kemudian mengatur fragmen
        untuk warna itu dengan menetapkan hasilnya ke gl_FragColor.
            beberapa bagian berikutnya akan agak lebih terlibat, kita akan
        membuat set kelas baru dan menempatkan kode kita yang ada untuk data
        tabel dan program shader ke dalam kelas-kelas ini. kami kemudian akan
        beralih diantara merekan saat runtime.
        */

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

        String vertexShaderCode = "attribute vec4 a_Position;  \n"+
                                    "attribute vec4 a_Color;     \n"+
                                    "varying vec4 v_Color;     \n"+
                                    "uniform mat4 u_Matrix;     \n"+

                                    "void main()                 \n"+
                                    "{                           \n"+
                                    "   v_Color = a_Color;\n"+
                                    "   gl_Position = u_Matrix * a_Position;\n"+
                                    "   gl_PointSize = 10.0;\n"+
                                    "}";

        String fragmentShaderCode = "precision mediump float;    \n"+
                                      "varying vec4 v_Color;       \n"+
                                      "void main()                 \n"+
                                      "{                           \n"+
                                      "   gl_FragColor = v_Color;\n"+
                                      "}";

        textureProgram = new TextureShaderProgram(textureVertexShaderCode, textureFragmentShaderCode);
        colorProgram = new ColorShaderProgram(vertexShaderCode, fragmentShaderCode);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
        
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float)height, 1f, 10f);
        setIdentityM(modelMatrix, 0);
        
        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    /*
    pertama kami membersihkan permukaan rendering dan kemudian hal pertama
    yang kami lakukan adalah menggambar meja, pertama kita sebut textureProgram.
    useProgram() untuk memberi tau OpenGL untuk mengunakan program ini dan
    kemudian kami meneruskan uniform dengan panggilan ke textureProgram.setUniforms()
    langkah selanjutnya adalah mengikat data array vertex dan program shader kami
    dengan panggilan ke table.bindData(), kami akhirnya mendapatkan meja dengan panggilan
    table.draw(), kami mengulangi urutan yang sama dengan program shader warna untuk
    menggambar mallets.
    */

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        //Clear rendering surface
        glClear(GL_COLOR_BUFFER_BIT);

        //Draw the table
        textureProgram.useProgram();
        textureProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        //Draw the mallets
        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();

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
        /*
        Kami menghasilkan satu object texture dengan memanggil glGenTextures()
        melewati 1 sebagai parameter pertama OpenGL akan menyimpan ID yang dihasilkan
        dalam textureObjectIds. Kami juga memeriksa bahwa panggilan ke glGenTextures()
        berhasil dengan melanjutkan hanya jiak itu tidak sama dengan 0, kalau tidak
        kita mencatat kesalahan dan mengembalikan 0.
        */
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            Log.i("setsuna", "Could not generate a new OpenGL texture object.");
            return 0;
        }

        /*
        a. Memuat dalam data bitmap dan mengikat texture
            langkah selanjutnya adalah menggunakan API android untuk dibaca
        dalam data dari file gambar kami. OpenGL tidak dapat membaca data dari
        file PNG atau JPEG secara langsung karena file-file ini dikodekan kedalam
        format terkompresi tertentu. OpenGL membutuhkan data mentah dalam bentuk
        yang tidak terkompresi jadi kita perlu menggunakan decoder bitmap bawaan
        android untuk mendekompres file gambar kita ke dalam formulir yang dipahami
        OpenGL.
        pertama-tama kami membuat instance baru BitmapFactory.Options yang disebut options
        dan kami menetapkan inScaled ke false, ini memberi tau android bahwa kami menginginkan
        data gambar asli bukan versi scaled dari data gambar.
        Kami kemudian memanggil BitmapFactory.decodeResource() untuk melakukan decode
        actual melewati kontext android, id sumber dan opsi decoding yang baru saja
        didefinisikan. panggilan ini akan mengdecode gambar menjadi bitmap atau akan
        mengembalikan nol jika gagal dan jika berhasil kami akan terus memproses texture.
        */
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceID, options);

        if (bitmap == null) {
            Log.i("setsuna", "Resource ID "+resourceID+ " could not be decoded.");
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        /*
        sebelum kita dapat melakukan hal lain dengan object texture yang baru dihasilkan
        kita perlu memberi tau OpenGL yang harus diterapkan oleh panggilan texture dimasa
        depan ke object texture ini.
        parameter pertama GL_TEXTURE_2D memberi tahu OpenGL bahwa ini harus diperlakukan sebagai
        texture dua dimensi dan parameter kedua memberi tau OpenGL bahwa itu ID object untuk mengikat/bind
        */
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        /*
        b. Memahami pemfilteran texture
            kita juga perlu menentukan apa yang harus terjadi ketika texture diperluas atau
        dikurangi ukuran menggunakan pemfilteran texture. ketikan kita menggambar texture ke permukaan
        rendering texels texture mungkin tidak memetakan persis ke fragmen yang dihasilkan oleh OpenGL
        ada dua kasus 'minification' dan perbesaran. Minification terjadi ketika kita mencoba
        menjalankan beberapa texel ke fragmen yang sama, dan pembesaran terjadi ketika kita
        menyebarkan satu texel di banyak fragmen. kami dapat mengkonfigurasi OpenGL menggunakan filter
        texture untuk setiap kasus.
            untuk memulai kami mencakup dua mode pemfilteran dasar, nearest-neighbor filtering dan bilinear interploation
        atau dalam bahasa indonesi penyaringan tetangga terdekat dan interpolasi bilinear. ada dua penyaringan
        tambahan yang akan segera kami tutupi lebih detail. Kami akan menggunakan gambar pada page121 untuk
        mengilustrasikan setiap momen penyaringan.
            - Nearest Neighbor filtering
                ini memilih texel terdekat untuk setiap fragmen. Ketika kita memperbesar texture
                itu akan terlihat agak blok, Setiap texel terlihat lebih jelas sebagai alun-alun kecil
                ketika kita menambah texture banyak detail akan hilang karena kita tidak memiliki cukup
                fragmen untuk semua texel
            - Bilinear filteting
                penyaringan bilinear menggunakan interpolasi bilinear untuk memperlancar transisi antara pixel
                alih-alih menggunakan texel terdekat untuk setiap fragmen, OpenGL akan menggunakan empat tetangga
                texels dan menginterpolasi mereka bersama-sama menggunakan jenis interpolasi linear yang sama
                dengan yang kita bahas pada page66.
                Texturenya sekarang terlihat jauh lebih halus dari sebelumnya walaupun masih ada sediki blok
                yang hadir karena kami telah memperluas texturenya begitu banyak tetapi itu tidak seubahnya dengan
                penyaringan tetanga terdekat.
        
        c. mipmaping
            sementara filter bilinear bekeraj dengan baik untuk pembesaran, itu tidak berfungsi
        juga untuk minifikasi diluar ukuran tertentu. semakin kita mengurangi ukuruan texture pada
        permukaan rendering semakin banyak texel akan dijejalkan ke setiap fragmen. Karena penyaringan
        bilinear OpenGL hanya akan menggunakan empat texel untuk setiap fragmen kami masih kehilangan
        banyak detail. ini dapat menyebabkan kebisingan dan artefak berkilauan dengan benda bergerak sebagai
        texel berbeda dipilih dengan setiap bingkai.
            Untuk memerangi artefak ini kita dapat menggunakan mipmaping, teknik yang menghasilkan
        serangkaian tekstur yang dioptimalkan pada ukuran yang berbeda. saat menghasilkan serangkaian
        tekstur, OpenGL dapat menggunakan semua texel untuk menghasilkan setiap level, memastikan bahwa
        semua texel juga akan digunakan saat memfilter texture. pada waktu render Opengl akan memilih
        level yang paling tepat untuk setiap fragmen berdasarkan jumlah texel per fragmen.
        dengan mipmaps akan lebih banyak memori akan digunakan, tetapi rendering juga bisa cepat karena
        level yang lebih kecil mengambil lebih sedikit ruang dalam cache texture GPU
        untuk lebih memahami bagaimana mipmapping meningkatkan kualitas minifikasi mari kita bandingkan
        gambar page123 diminas menjadi 12,5 persen dari ukuran texel asli menggunakan filter bilinear
        dengan kualitas semacam ini kami telah tinggal dengan penyaringan tetangga terdekat.
        dengan mipmaps diaktifkan, OpenGL akan memilih level texture terdekat yang sesuai dan kemudian melakukan
        interpolasi bilinear menggunakan texture yang dioptimalkan. Setiap level dibangun dengan infomasi dari
        semua texel sehingga gambar yang dihasilkan terlihat jauh lebih baik dengan lebih banyak detail yang
        dipertahankan.

        d. trilinear filtering
            ketika kami menggunakan mipmaps dengan filter bilinear kita kadang dapat melihat lompatan
            atau garis yang nyata diadegan yang dirender dimana OpenGL beralih di antara berbagai level
            mipmap. kita dapat beralih ke penyaringan trilinear untuk memberi tahu OpenGL untuk juga menginterploasi
            antara dua level mipmap terdekat, menggunakan total delapan texel per fragmen. ini membantu untuk
            menghilangkan transisi antara setiap level mipmap dan menghasilkan gambar yang lebih halus.
        */

        /*
        Setting default Texture filtering parameters
            Kami mengatur setiap filter dengan panggilan ke glTexParameteri() GL_TEXTURE_MIN_FILTER mengacu
        pada minifikasi sedangkan GL_TEXTURE_MAG_FILTER mengacu pada pembesaran. Untuk minification
        kami memilih GL_LINEAR_MIPMAP_LINEAR yang memberi tau OpenGL untuk menggunakn penyaringan trilinear
        kami mengatur filter pembesaran ke GL_LINEAR yang memberi tau OpenGL untuk mengunakan filter bilinear.
            Penjelasan untuk minification dan magnifikation(pembesaran):
            
            Minification:
            GL_NEAREST                  = Nearest-neighbor (penyaringan tetangga terdekat)
            GL_NEAREST_MIPMAP_NEAREST   = Nearest-neighbor filtering dengan mipmap
            GL_NEAREST_MIPMAP_LINEAR    = Nearest-neighbor filtering dengan interpolasi antara level mipmap
            GL_LINEAR                   = Bilinear filtering
            GL_LINEAR_MIPMAP_NEAREST    = Bilinear filtering dengan mipmap
            GL_LINEAR_MIPMAP_LINEAR     = Trilinear filtering (filter bilinear dengan interpolasi antara level mipmap)

            Magnification:
            GL_NEAREST
            GL_LINEAR
        */
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    
        /*
        memuat texture ke OpenGL dan mengembalikan ID
            panggilan ini memberi tau OpenGL untuk membaca dalam
        data bitmap yang ditentukan oleh bitmap dan menyalinya
        ke object texture yang saat ini terikan.
        */
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        /*
        menghasilkan mipmaps juga merupakan cinch. Kita dapat memberi
        tau OpenGL untuk menghasilkan semua level yang diperlukan dengan
        panggilan cepat glGenerateMipmap().
        */
        glGenerateMipmap(GL_TEXTURE_2D);

        /*
        sekarang data bitmap telah dimuat ke OpenGL, kami tidak
        perlu lagi menyimpan bitmap android. Dalam keadaan normal
        mungkin perlu beberapa siklus gerbag collector untuk dalvik
        untuk merilis data bitmap ini jadi kita harus memanggil
        recycle() atau daur ulang pada object bitmap untuk segera
        melepaskan data.
        */
        bitmap.recycle();

        /*
        sekarang setelah kami selesai memuat texture praktik yang baik
        adalah kemudian mengobati texture sehingga kami tidak sengaja membuat
        perubahan lebih lanjut pada texture ini dengan panggilan texture lainya
        */
        glBindTexture(GL_TEXTURE_2D, 0);

        //langakah terakhir adalah mengembalikan ID object texture
        return textureObjectIds[0];

    }
}

/*
membuat struktur kelas baru untuk data vertex(titik) kami
    kami akan mulai dengan memisahkan data vertex kami menjadi kelas
yang terpisah, dengan satu kelas untuk mewakili setiap jenis object fisik
kami akan membuat satu kelas untuk meja kami dan lainya untuk mallets.
kami tidak akan membutuhkan satu untuk line karena sudah ada line di texture kami.
    kami juga akan membuat kelas terpisah untuk merangkum array vertex yang
sebenarnya. dan strukture kami akan terlihat seperti dibawah ini:
    __________          ___________
   |          |        |           |
   |  Mallet  |        |   Table   |
   |__________|        |___________|
        \                   /
         \                 /
          \               /
          v              v
           ________________
          |                |
          |  VertexArray   |
          |________________|
        
Kami akan membuat mallet untuk mengelola data mallet dan tabel untuk
mengelola data tabel, dan setiap kelas akan memiliki turunan dari vertexArray
yang akan merangkum floatBuffer menyimpan array vertex.
kami akan mulai dengan VertexArray.
kode ini berisi floatBuffer yang akan digunakan untuk menyimpan data
array vertex kami dalam kode native, seperti yang dibahas dibab sebelumnya
konstruktor mengambil array data floating point java dan menuliskanya ke buffer.
kami juga telah membuat metode generik untuk mengaitkan atribut di shader
kami dengan data. Ini mengikuti pola yang sama seperti yang kami jelaskan
pada bab sebelumnya.
*/
class VertexArray {
    private final FloatBuffer floatBuffer;
    
    public VertexArray(float[] vertexData) {
        floatBuffer = ByteBuffer
            .allocateDirect(vertexData.length * GameAirHoki5.BYTES_PER_FLOAT)
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

/*
Kami sekarang akan mendefinisikan kelas untuk menyimpan data tabel kami
kelas ini akan menyimpan data posisi untuk tabel kami dan kami juga akan
menambahkan koordinat texture untuk menerapkan texture ke table.
*/

class Table {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * GameAirHoki5.BYTES_PER_FLOAT;

    /*
    a. adding vertex data
        array ini bersisi data vertex(titik) untuk meja kami. Kami
    telah mendefinisikan posisi X dan Y serta koordinat texture S dan T.
    anda mungkin memperhatikan bahwa T berjalan diarah yang berlawanan
    dari komponen Y.
    b. cliping the texture
        kami menggunakan koordinat T 0.1f dan 0.9f karena meja kami adalah
    satu unit lebar dan 1.6 unit tinggi, gambar texture kami adalah 512*1024
    dalam pixel jadi jika lebar sesuai dengan 1 unit, texturenya sebenarnya
    2 unit. Untuk menghindari meremas texture kami menggunakan kisaran 0.1f
    hingga 0.9f bukan 0.0f sampai 1.0f untuk memotong tepi dan hanya menggunakan
    bagan tengan seperti gambar dibawah ini:
             ___________
        ----|-----------|--- __________
            |           |   |          |
            |           |   |          |
            |           |   |          |
            | gambar.png|   |gambar.png|
            |           |   |          |
            |           |   |          |
        ----|-----------|---|__________|
            |___________|
    alih-alih terjepit kita juga bisa tetap dengan koordinasi texture
    dari 0.0-1.0 dan prestrech texture kita sehingga terlihat benar
    setelah terjepit ke meja hoki. Dengan cara ini kami tidak akan
    menggunakan memori pada bagian texture yang tidak akan ditampilkan 

    */
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

    /*
    membuat konstruktor untuk tabel.
    konstruktor ini akan menggunakan VertexArray untuk menyalin
    data kedalam FloatBuffer dalam memori native.
    */
    private final VertexArray vertexArray;
    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    /*
    kami juga akan menambahkan metode untuk mengikat array vertex
    ke program shader.
    Metode body memanggil setVertexAttribPointer() untuk setiap atribut,
    mendapatkan perbandingan masing-masing atribut dari program shader.
    ini akan mengikat posisi untuk atribut shader tang dirujuk oleh getPositionAttributeLocation()
    dan mengikat data koordinat texture ke atribut shader yang direferensikan oleh
    getTextureCoordinatesAttributLocation(). kami akan mendefinisikan metode ini
    ketika kami membuat shadeclasses.
    */
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

    //kita hanya perlu menambahkan dalam satu metode terakhir sehingga
    //kita dapat menggambar meja
    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}

/*
ini masih mengikuti pola yang sama dengan tabel dan kami masih menggambar
point mallets seperti sebelumnya. Data vertex kami sekarang didefinisikan,
kami memiliki satu kelas untuk mewakili data tabel yang lain untuk mewakili
data mallet dan kelas tiga untuk membuatnya lebih mudah untuk menomangi data
vertex itu sendiri. 
*/
class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * GameAirHoki5.BYTES_PER_FLOAT;
    private static final float[] VERTEX_DATA = {
        //Order of coordinate: X, Y, R, G, B
        0f, -0.4f, 0f, 0f, 1f,
        0f,  0.4f, 1f, 0f, 0f,
    };

    private final VertexArray vertexArray;

    public Mallet() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            colorProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            STRIDE);

        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            colorProgram.getColorAttributeLocation(),
            COLOR_COMPONENT_COUNT,
            STRIDE);
    }
    public void draw() {
        glDrawArrays(GL_POINTS, 0, 2);
    }
}

/*
Selanjutnya adalah membuat kelas untuk shaderprogram kami
    dibagian ini kami membuat satu kelas untuk program shader texture
kami dan yang lain untuk program shader warna kami, kami akan menggunakan
program texture shader untuk menggambar table dan menggunakan program
warna shader untuk menggambar mallet. kami juga akan membuat kelas dasar
untuk fungsi umum. kita tidak perlu kuatir tentang line lagi karena itu
sudah menjadi bagian dari texture.

    ______________________          _________________________
   |                      |        |                         |
   |  ColorShaderProgram  |        |   TextureShaderProgram  |
   |______________________|        |_________________________|
        \                          /
         \                        /
          \                      /
          v                     /
           __________________  v
          |                  |
          |  ShaderProgram   |
          |__________________|

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

/*
kami sekarang akan mendefiniskan kelas untuk mengatur dan mewakili
program texture shader kami.
*/
class TextureShaderProgram extends ShaderProgram{
    //Uniform location
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    //atribut locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    /*
    konstruktor ini akan mengambil superclass dengan sumber daya
    yang dipilih kami, dan superclass akan membangun program shader
    kami kemudian akan membaca dan menyimpan lokasi uniform dan atribut.
    */

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

    /*
    mengatur uniform dan mengembalikan lokasi atribut
    selanjutnya adalah melewati matrik dan texture ke uniform mereka.
    Langkah pertama adalah melewati matrix ke uniform. ketika kita menggambar
    menggunakan texture di OpenGL kita tidak lulus texture langsung ke shader.
    sebaliknya kami menggunakan unit texture untuk menahan texture. Kami melakukan
    ini karena hanya dapat menggambar begitu banyak texture pada saat yang sama
    ini membutuhkan unit texture ini untuk mewujudkan texture aktif yang sedang
    ditarik.
    kita dapat menukar texture masuk dan keluar dari unit texture jika kita perlu
    beralih texture, meskipun ini dapa memperlambat rendering jika kita melakukan
    terlalu sering.
    kami juga dapat menggunakan beberapa unit texture untuk menggambar lebih dari
    satu texture pada saat yang sama. kami memulai bagian ini dengan mengatur unit
    texture aktif ke unit texture 0 dengan panggilan glActiveTexture(), dan kemudian
    mengikat texture kita ke unit ini dengan panggilan glBindTexture(), kami kemudian
    meneruskan unit texture yang dipilih ke u_TextureUnit dalam shader fragmen dengan
    memanggil glUniform1i(uTextureLocation, 0).
    kami hampir selesai dengan kelas shader texture kami, kami hanya perlu cara
    mendapatkan lokasi atribut sehingga kami dapat mengikatnya ke data array vertex
    yang benar.
    */
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

/*
menambahkan warna di shader program
    kami akan menggunakan program ini untuk menggambar mallet kami
dengan memisahkan program shader dari data yang ditarik dengan program ini,
kami telah memudahkan untuk menggunakn kembali kode kami. Misalnya kita
bisa menggambar benda apa pun dengan atribut warna menggunakan program
shader warna kita bukan hanya mallets.
*/
class ColorShaderProgram extends ShaderProgram {
    //uniform locations
    private final int uMatrixLocation;

    //atribute locations
    private final int aPositionLocation;
    private final int aColorLocation;

    public ColorShaderProgram(String vertexShaderCode, String fragmentShaderCode) {
        super(vertexShaderCode, fragmentShaderCode);

        //ambil lokasi uniform untuk program shader
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        
        //ambil lokasi atribut untuk program shader
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
    }

    public void setUniforms(float[] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getColorAttributeLocation() {
        return aColorLocation;
    }
}
