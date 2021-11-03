/*

* BAB 5
* Memasuki dimensi ketiga
*/

package com.gles.GameAirHoki4;

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
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import android.opengl.Matrix;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/*
a. Mengubah koordinat dari shader ke layar
    kami sekarang terbiasa dengan koordinat perangkat yang dinormalisasi
dan kami tau bahwa agar vertex di tampilkan pada layar komponen x,y dan z
semua harus berada dalam kisaran [-1, 1]. Mari kita lihat diagram alir berikut
untuk meninjau bagaimana koordinat diubah dari gl_Position asli yang ditulis
oleh vertex shader ke koordinat akhir layar.
                  _________________                       ________________
                 |                 |                     |                |
  gl_Positioon   |                 | Normalized device   |   Viewport     | Window coordinates
________________>     perspective  |___coordinate_______>| Transformation |____________________>
                 |      Division   |                     |                |
                 |_________________|                     |________________|

ada dua langkah transformasi dan tiga ruang koordinat yang berbeda

b. Clip Space
    ketika shader vertex menulis nilai ke gl_Position OpenGL mengharapkan posisi
ini berada diruang clip. Logika dibalik ruang klip sangat sederhana, untuk setiap
posisi tertentu komponen x, y dan z semua harus antara -W dan W untuk posisi itu.
Misalnya jika posisi E adalah 1 maka komponen X, Y, Z semua perlu antara -1 dan 1
apapun diluar kisaran ini tidak akan terlihat dilayar. Alasan ini tergantung pada
posisi W akan terlihat begitu kita belajar tentang pembagian prespektif.

c. Prespective Division(Pembagian prespetif)
    sebelum titik menjadi koordinat diperangkat yang dinormalisasi, OpengGL sebenarnya
melakukan langkah extra yang dikenal divisi perspektif. setelah itu posisi akan berada
dalam koordinat perangkat yang dinormalisasi, dimana setiap koordinat terlihat akan
terletak pada kisaran [-1, 1] untuk komponen X, Y dan Z terlepas dari ukuran atau betuk
area rendering.
untuk menciptakan ilusi 3D dilayar OpenGL akan mengambil setiap gl_Position dan menbagi
komponen X, Y dan Z dengan komponen W. Ketika komponen W digunakan untuk mewakili jarak
ini menyebabkan object yang lebih jauh untuk dipindahkan lebih dekat ke pusat area rendering
yang kemudian bertindak seperti titik hilang. Ini adalah OpenGL membodohi kita untuk melihat
adegan dalam 3D, artis menggunakan trik yang sama selama berabad-abad.
    Misalnya, katakanlah kita memiliki object dengan dua vertices, masing-masing dilokasi
yang sama diruang 3D dengan komponen X, Y dan Z yang sama tetapi dengan komponen W yang berbeda.
katakanlah dua koordinat ini (1,1,1,1) dan (1,1,1,2). Sebelum OpenGL menggunakan ini sebagai
koordinat perangkat yang dinormalisasi, itu akan melakukan perspektif bagi dan membagi tiga
komponen pertama dengan W. Setiap koordinat akan dibagi sebagai berikut
(1/1, 1/1, 1/1) dan (1/2, 1/2, 1/2). Setelah pembagian ini koordinat perangkat yang dinormalisasi
akan (1,1,1,1) dan (0.5, 0.5, 0.5). Koordinat dengan W yang lebih besar dipindahkan lebih dekat
ke (0,0,0,0) pusat area rendering dalam koordinat perangkat yang dinormalisasi.
Dalam berikut kita dapat melihat contoh efek ini dalam aksi, sebagai koordinat dengan X, Y dan Z
yang sama akan dibawa lebih dekat ke pusat karena nilai W meningkat.

                  ^   ^
(-1, -1, 0, 4)   /     \(1,-1,0,4)
                /       \
(-1, -1, 0, 3) /         \(1,-1,0,3)
              /           \
             /             \
            /(-1,-1,0,2)    \(1,-1,0,2)
           /                 \
          /                   \
        (-1,-1,0,1)           (1,-1,0,1)

diOpenGL efek 3D adalah linear dan dilakukan sepanjang garis lurus


d. Homogenous Coordinates(koordinat homogen)
    karena divisi perspektif, koordinat dalam ruang klip sering disebut sebagai
koordinat homogen, diperkenalkan oleh august ferdinand mobius pada tahun 1827
alasan mereka disebut homogen adalah karena beberapa koordinat dalam ruang klip
dapat memetakan ke titik yang sama misalnya ambil point dibawah ini:
(1,1,1,1), (2,2,2,2), (3,3,3,3), (4,4,4,4), (5,5,5,5)
setelah divisi perspektif semua point-point ini akan memetakan
ke (1,1,1) dalam koordinat perangkat yang dinormalisasi.


e. keuntungan membagi dengan W
    anda bertanya-tanya mengapa kita tidak hanya membagi dengan Z sebagai gantinya.
Lagi pula jika kita menafsirkan z sebagai jarak dan memiliki dua koordinat (1,1,1)
dan (2,2,2) kita dapat membagi dengan Z untuk mendapatkan dua koordinat normal (1,1)
dan (0.5, 0.5). Meskipun ini dapat bekerja, ada keuntungan tambahan untuk menambahkan W
sebagai komponen keempat. Kita dapat memisahkan efek perspektif
dari koordinat Z aktual sehingga kita dapat beralih antara proyeksi ortografis dan perspektif
ada juga manfaat untuk melestarikan komponen Z sebagai buffer kedalaman, yang akan kita bahas
dalam masa mendatang.

f. Viewport Transformation
    sebelum kita dapat melihat hasil akhir, OpenGL perlu memetakan komponen X dan Y dari
perangkat yang dinormalisasi koordinat ke area layar yang disisihkan oleh sistem oprasi
yang disebut viewport. Koordinat yang dipetakan ini dikenal sebagai koordinat jendela,
kami tidak perlu kuatir tentang koordinat ini diluar memeberi tahu OpenGL bagaimana melakukan
pemetaan. Kami saat ini melakukan ini dalam kode dengan panggilan glViewport() di onSurfaceCreated().
ketika OpenGL melakukan pemetaan ini, itu akan memetakan rentang (-1, -1, -1) ke (1, 1, 1) ke jendela
yang telah disisihkan untuk ditampilkan. koordinat perangkat yang dinormalisasi diluar kisaran ini
akan dipotong.

Semuanya mulai terlihat lebih 3D! kami dapat melakukan ini hanya
dengan meletakan di W kami sendiri. Namun bagaimana jika kita ingin
membuat segalanya lebih dinamis seperti menggati sudut tabel atau
memperbesar dan memperkecil? Alih-alih mengkode harding nilai W
kami akan menggunakan matrix untuk menghasilkan nilia bagi kami.
dibagian berikutnya kita akan belajar cara menggunakan matrix proyeksi
perspektif untuk menghasilkan nilai W secara otomatis.

g. Pindah ke proyeksi perspektif
    Sebelum kita masuk ke matematika matrix dibali proyeksi perspektif
mari kita periksa hal-hal pada tingkat visual. Pada pembahasan sebelumnya
kami menggunakan matrix proyeksi orthoGraphichs untuk mengkompensasi rasio
aspek layar dengan menyesuaikan lebar dan tinggi area yang diubah menjadi
koordinat perangkat yang normal.
dalam gambar page 101 kami memvisualisasikan proyeksi ortografis sebagai
kubus yang melampirkan seluruh adegan, mewakili OpenGL apa yang akan mencapai
rendering viewport dan apa yang dapat kami lihat.

h. The frustum
    kita beralih ke matrix proyeksi, garis pararel dalam adegan akan bertemu
bersama pada titik lenyap pada layar dan benda akan menjadi lebih kecil saat
mereka semakin jauh. Misalnya sebuah kubus, wilayah ruang yang bisa kita lihat
akan terlihat seperti gambar 31 page 102. Bentuk ini disebut frustum, 2 ruang tampilan
ini dibuat dengan matrix proyeksi perspektif dan divide(pembagian) perspektif.
Frustum hanyalah sebuah kubus yang telah berubah menjadi piramida terpotong dengan
membuat sisi jauh lebih besar dari sisi dekat. Semakin besar perbedaanya semakin
    luas bidang pandang dan semakin banyak yang bisa kita lihat.
dengan frustum ada juga titik fokus, titik fokus ini dapat ditemukan dengan mengikuti
garis-garis yang membentang dari ujung besar ke ujung kecil frustum kemudian mengikuti
mereka melewati ujung kecil sampai mereka bertemu bersama. Ketika anda melihat adegan
dengan proyeksi perspektif adegan itu akan muncul seolah-olah kepala anda ditempatkan
pada titik fokus ini. Jarak antara titik fokus dan ujung kcil frustum dikenal sebagai
panjang fokus dan ini mempengaruhi rasio antara ujung kecil dan besa frustrasi dan bidang
penglihatan yang sesuai. Dalam gambar page 103 kita dapat melihat adegan didalam frustrasi
seperti yang terlihat dari titik fokus.
    properti lain yang menarik dari titik fokus adalah bahwa itu juga merupakan tempat
dimana kedua ujung frustum akan muncul untuk mengambil jumlah ruang yang sama di layar.
Ujung frustum lebih besar tetapi karena itu juga jauh dibutuhkan jumlah ruang yang sama.
ini adalah efek yang sama yang kita lihat selama gerhana matahari dimana bulan jauh
lebih kecil dari matahari tetapi karena itu juga jauh lebih dekat, tampaknya hanya cukup
besar untuk menutupi ruang matahari! ini semua tergantung pada titik pandang kami.

i. Mendefinisikan prespectif projection
    untuk menciptakan kembali keajaiban 3D, matrix proyeksi perspektif kami perlu
bekerja bersams dengan perspektif kami. Matrix proyeksi tidak dapat melakukan pembagian
perspektif dengan sendirinya dan perbedaan perspektif membutuhkan sesuatu untuk dikerjakan
semua object harus bergerak ke arah tengah layar dan mengurangi ukuran karena semakin jauh
dari kami jadi tugas paling penting untuk proyeksi kami.
Matrik bisa untuk membuat nilai yang tepat untuk W sehingga ketika OpenGL melakukan perspektif
yang membelah objek jauh akan muncul lebih kecil dari object dekat. salah satu cara yang dapat
kita lakukan yaitu dengan menggunakan komponen Z sebagai jarak dari titik fokus dan kemudian
memetakan jarak ini ke W. Semakin besar jarak semakin besar W dan yang lebih kecil dari object
yang dihasilkan. Kami tidak akan pergi lebih ke dalam matematika di sini, tetapi jika anda ingin
detail lebih lanjut anda dapat melompat pada page 300

j. menyesuaikan rasio aspek dan bidang penglihatan
    mari kita lihat matriks proyeksi tujuan yang lebih umum yang akan memungkinkan
kita untuk menyesuaikan bidang penglihatan serta untuk rasio aspek layar:
     ______________________________________________
    |                                              |
    | __a___   0        0             0            |
    | aspect                                       |
    |                                              |
    |   0      a        0             0            |
    |                                              |
    |                                              |
    |   0      0    _ __f+n___   _ __2f_n__        |
    |                   f-n           f-n          |
    |                                              |
    |   0      0       -1             0            |
    |______________________________________________|

berikut penjelasan dari variable yang didefinisikan dalam matrix ini:

a = jika kita membayangkan adegan seperti yang ditangkap kamera maka variable
       int mewakili panjang fokus kamera itu. Panjang fokus dapat dihitung dengan
       1/tangent of (bidang penglihatan/2). bidang penglihatan harus kurang dari
       180 derajat. misalnya dengan bidang visi 90 derajat panjang fokus akan diatur
       ke 1/tangent of (90°/2) yang sama dengan 1/1 atau 1.
aspect = ini harus diatur ke aspek rasio layar yang sama dengan lebar/tinggi
f      = ini harus diatur ke jarak ke far plane dan has positiod dan lebih besar
         dari jarak ke bidang dekat.
n      = ini harus diatur ke jarak bidang dekat dan harus positif. Misalnya
         jika ini diatur ke 1, bidang dekat akan berlokasi di Z of -1.

karena bidang penglihatan semakin kecil dan panjang fokus semakin lama, kisaran nilai X dan Y
yang lebih kecil akan memetakan ke rantang [-1, 1] dalam koordinat perangkat yang dinormalisasi
ini akan membuat frustum lebih sempit. dalama gamba page105, frustum disebelah kiri memiliki
bidang visi 90° sedangkan frustum di sebelah kanan memiliki bidang visi 45°.
anda dapat melihat bahwa panjang fokus antara titik fokus dan sisi dekat frustum sedikit lebih lama
untuk frustum 45°. gambar kedua page105 adalah frustum yang sama seperti yang terlihat dari titik
fokus mereka.
biasanya ada beberapa masalah distorsi dengan bidang penglihatan yang lebih sempit. Disisi lain
karena bidang penglihatan menjadi semakin lebih luas, tepi gambar akhir akan muncul semakin banyak
terdistorsi. Dalam kehidupan nyata bidang visi yang luas akan membuat semuanya terlihat melengkung
seperti efek yang terlihat dari menggunakan lensa mata ikan pada kamera. karena OpenGL menggunakan
proyeksi linier sepanjang garis lurus gambar akhir akan terbentang.

*/

public class GameAirHoki4 implements Renderer
{
    /*
    penambahan komponen posisi 3D yaitu Z dan W
        kami menambahkan komponen Z dan W ke data vertex kami. Kami telah
    memperbarui semua vertices sehingga yang didekat bagian bawah layar
    memiliki W dari 1 dan yang didekat bagian atas layar memiliki W dari 2.
    kami juga memperbarui line dan mallets untuk memiliki fraksional yang
    diantaranya. Ini harus memiliki efek membuat bagian atas dari table tampak
    lebih kecil dari bawah seolah-olah kita sedang melihat dari kejauhan.
    Kami mengatur semua komponen Z ke no karena kami tidak perlu benar-benar
    memilki apapaun di Z untuk mendapatkan efek perspektif. OpenGL akan secara
    otomatis melakukan pembagian perspektif bagi kami menggunakan nilai W yang
    telah kami tentukan dan proyeksi ortografis kami, saat ini hanya akan menyalin
    nilai W ini.
    */
    /*float[] tableVerticesWithTriangles = {
        //coordinate: X, Y, Z, W, R, G, B

        //triangle Fan
           0f,     0f,  0f, 1.5f,   1f,   1f,   1f,
        -0.5f,  -0.8f,  0f,   1f, 0.7f, 0.7f, 0.7f,
         0.5f,  -0.8f,  0f,   1f, 0.7f, 0.7f, 0.7f,
         0.5f,   0.8f,  0f,   2f, 0.7f, 0.7f, 0.7f,
        -0.5f,   0.8f,  0f,   2f, 0.7f, 0.7f, 0.7f,
        -0.5f,  -0.8f,  0f,   1f, 0.7f, 0.7f, 0.7f,

        //line 1
        -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
         0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,

        //mallets update
        0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
        0f,  0.4f, 0f, 1.75f, 1f, 0f, 0f
    };*/
    float[] tableVerticesWithTriangles = {   
            // Order of coordinates: X, Y, R, G, B
            
            // Triangle Fan
                0f,     0f,    1f,    1f,    1f,         
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,            
             0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
             0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

            // Line 1
            -0.5f, 0f, 1f, 0f, 0f,
             0.5f, 0f, 1f, 0f, 0f,

            // Mallets
            0f, -0.4f, 0f, 0f, 1f,
            0f,  0.4f, 1f, 0f, 0f
        };   

    /*
    Menambahkan komponen W untuk melihat prespektif
        Akan lebih mudah untuk memahami efek dari komponen W jika kita
    benar-benar melihatnya beksi, jadi mari kita tambahkan ke data vertex tabel
    dan lihat apa yang terjadi. Karena kita sekarang akan menentukan komponen
    X, Y, Z dan W dari suatu posisi maka kita perlu update POSITION_COMPONENT_COUNT
    dari 2 ke 4.

    buat mempermudah efek 3D kita kembalikan POSITION_COM.. = 2
    dan tableVerticesWithTriangles kembali seperti bab sebelumnya
    */
    //private static final int POSITION_COMPONENT_COUNT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTE_PER_FLOAT = 4;
    private final FloatBuffer vertexData;

    private int program = 0;
    
    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTE_PER_FLOAT;
    private int aColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String U_MATRIX = "u_Matrix";
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;

    //Memindahkan object di sekitar dengan matrix model
    private final float[] modelMatrix = new float[16];

    public GameAirHoki4() 
    {
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTE_PER_FLOAT)
                               .order(ByteOrder.nativeOrder())
                               .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
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

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

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
    
        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderCode);
        

        final int programObjectId = glCreateProgram();

        if (programObjectId == 0)
        {
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

                if (validateStatus[0] != 0) {
                    program = programObjectId;
                }
                else {
                    Log.i("setsuna", "Results of validating program: "+validateStatus[0]
                        +"\nLog: "+glGetProgramInfoLog(programObjectId));
                }
            }
        }
        if (program != 0) {
            glUseProgram(program);

            aColorLocation = glGetAttribLocation(program, A_COLOR);
            aPositionLocation = glGetAttribLocation(program, A_POSITION);
            uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

            vertexData.position(0);
            glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
            glEnableVertexAttribArray(aPositionLocation);
            
            vertexData.position(POSITION_COMPONENT_COUNT);
            glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

            glEnableVertexAttribArray(aColorLocation);
            
        }
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        glViewport(0, 0, width, height);

        /*
        k. penggunakan perspectiveM()
            ini akan menciptakan proyeks perspektif dengan bidang visi 45 derajat
        frustum akan dimulai pada Z dari -1 dan berakhir pada Z dari -10.
        setelah menjalankan program anda akan melihat bahwa meja hoki telah menghilang
        karena kami tidak menentukan posisi Z untuk meja kami, itu terletak pada Z dari 0
        secara default. Karena frustum kami dimulai dari Z dari -1 kami tidak dapat melihat
        meja kecuali kami memindahkannya ke kejauhan.
        alih-alih hard-coding nilai Z mari kita gunakan matrix terjemahan untuk memindahkan
        tabel sebelum kita memproyeksikan menggunakan matrix proyeksi. dengan konvensi kami
        akan menyebut matrix model ini.
        */
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float)height, 1f, 10f);
    
        /*
        l. kami akan menggunakan matrix ini untuk memindahkan meja hoki kekejauhan.
            ini menetapkan matrix model ke matrix identitas dan kemudian
        menerjemahkan dengan -2 disepanjang sumbu Z. ketika kami mengalikan tabel
        kami berkoordinasi dengan matrix ini, mereka akan tersentuh oleh 2 unit
        disepanjang sumbu Z negatif.
        */
        setIdentityM(modelMatrix, 0);
        //translateM(modelMatrix, 0, 0f, 0f, -2f); untuk melihat rotasi baris ini sementara hapus

        /*
        m. Multiplying once versus Multiplyinh twice (mengalikan sekali vs mengalikan dua kali/berlipat ganda)
            kami sekarang memiliki pilihan. kami masih perlu menerapkan matrix ini ke setiap titik
        jadi pilihan pertama kami adalah menambahkan matrix tambahan ke vector shader. Kami
        mengalikan setiap titik dengan matrix model untuk memindahkan 2 unit di sepanjang sumbu
        Z negatif, dan kemudian kami mengalikan setiap titik dengan matrix proyeksi sehingga OpenGL
        dapat melakukan perspektif divide(membagi) dan mengubah veritices ke dalam koordinat
        perangkat yang dinormalisasi. Alih-alih melalui semua masalah ini ada cara yang lebih baik
        kita dapat melipatgandakan model dan matrix proyeksi menjadi satu matrix tunggal dan kemudian
        lulus matrix ini ke shader vertex, dengan begitu kita bisa tetap dengan satu matrix dan shader.
        
        n. Matrix multiplication (perkalian matrix)
            ini seperti perkalian matrix dan vector misalnya
        kami memiliki dua matrix generik dibawah ini:
             ____________________     ____________________     ________________________________________________
            | a11  a12  a13  a14 |   | b11  b12  b13  b14 |   | a11 b11 + a12 b21 + a13 b31 + a14 b41  ?  ?  ? |
            |                    |   |                    |   |                                                |
            | a21  a22  a23  a24 |   | b21  b22  b23  b24 |   |                   ?                    ?  ?  ? |
            |                    | x |                    | = |                                                |
            | a31  a32  a33  a34 |   | b31  b32  b33  b34 |   |                   ?                    ?  ?  ? |
            |                    |   |                    |   |                                                |
            | a41  a42  a43  a44 |   | b41  b42  b43  b44 |   |                   ?                    ?  ?  ? |
            |____________________|   |____________________|   |________________________________________________|

        untuk mendapatkan hasil elemen pertama, kami mengalikan
        baris pertama dari matrix pertama dengan kolom pertama dari
        matrix kedua dan menambah hasilnya.

        kemudian untuk hasil elemen kedua kami mengalikan baris
        kedua matrix dari matrix pertama dengan kolom pertama dari
        matrix kedua dan menambah hasilnya.
             ____________________     ____________________     ________________________________________________
            | a11  a12  a13  a14 |   | b11  b12  b13  b14 |   |                   ?                    ?  ?  ? |
            |                    |   |                    |   |                                                |
            | a21  a22  a23  a24 |   | b21  b22  b23  b24 |   | a21 b11 + a22 b21 + 23 b31 + a24 b41  ?  ?  ? |
            |                    | x |                    | = |                                                |
            | a31  a32  a33  a34 |   | b31  b32  b33  b34 |   |                   ?                    ?  ?  ? |
            |                    |   |                    |   |                                                |
            | a41  a42  a43  a44 |   | b41  b42  b43  b44 |   |                   ?                    ?  ?  ? |
            |____________________|   |____________________|   |________________________________________________|

        ini berlanjut untuk setiap elemen sampai mendapatkan hasilnya.

        
        o. Order of multiplication (urutan perkalian)
            Sekarang kita tau cara melipatgandakan dua matrix bersama-sama
        kita harus berhati-hati untuk memastikan bahwa kita melipatgandakanya
        dalam urutan yang benar. kita dapat mengembangkan dengan matrix proyeksi
        disisi kiri dan matrix model disisi kanan atau dengan matrix model disisi
        kirai dan matrix proyeksi di sisi kanan. 
            berbeda dengan penggandaan rutin, pesan penting! jika kita salah urutan
        segalanya mungkn terlihat aneh atau kita mungkin tidak melihat apa-apa
        misalnya contoh berikut dimana dua matrix dikalikan dalam urutan tertentu:
                 _____   _____     _______
                |     | |     |   |       |
                | 1  2| | 4 3 |   | 8   5 |
                |     | |     | = |       |
                | 3  4| | 2 1 |   | 20 13 |
                |_____| |_____|   |_______|
        beikut adalah dua matrix yang sama dikalikan dalam urutan terbalik:
                 _____   _____     _______
                |     | |     |   |       |
                | 4  3| | 1 2 |   | 13 20 |
                |     | |     | = |       |
                | 2  1| | 3 4 |   | 5   8 |
                |_____| |_____|   |_______|
        dengan urutan berbeda hasilnya juga berbeda.


        p. Memilih urutan yang sesuai
            untuk mengetahui urutan mana yang harus kita gunakan
        mair kita lihat matematika ketika kita hanya menggunakan matrix proyeksi

            vertex-clip = ProjectionMatrix * vertex-eye

        vertex-eye mewakili posisi titik di kancah kami sebelum mengalikan dengan
        matrix proyeksi. Setelah kami menambahkan matrix model untuk memindahkan
        table matematika maka akan terlihat seperti ini:

            vertex-eye = ModelMatrix * vertex-model 
            vertex-clip = ProjectionMatrix * vertex-eye

        vertex-model mewakili posisi titik sebelum kita menggunakan matrix model
        untuk mendorongnya ke dalam adegan. Gabungkan dua expresi dan akan berakhir
        seperti ini:

            vertex-clip = ProjectionMatrix * ModelMatrix * vertex-model
        
        untuk mengganti kedua matrix ini dengan satu, kita harus mengalikan matrix
        proyeksi dengan matrix model, dengan matriks proyeksi disisi kiri dan matrix
        model disisi kanan.
        */

        /*
        q. tambah kode untuk menggunakan satu matrixs
            kami pertama-tama membuat array floating point temp(sementara)
        untuk menyimpan hasil sementarat, lalu kita sebut multiplyMM() untuk
        mengalikan matriks proyeksi dan matrix model bersama-sama ke dalam
        aray temp ini. Selanjutnya kita sebut System.arraycopy() untuk menyimpan
        hasilnya kembali ke projectionMatrix yang sekarang berisi efek gabungan
        dari model matriks dan projection matrik.
        */

        //kode ini wajib dibawah walapun udah ada penambahan kode rotasi
        /*
        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);*/
        /*
        setiap kali kami mengalikan dua matrix kami membutuhkan area
        sementara(variable temp) untuk menyimpan hasilnya. Jika kita
        mencoba menulis secara langsung hasilnya tidak terdefinisi.
        
        jika aplikasinya dijalankan maka akan terlihat agak tegak
        */

        /*
        r. Adding rotation
            Sekarang kita memiliki matriks proyeksi yang dikonfigurasi dan
        matrix model ditempat untuk memindahkan meja, yang perlu kita lakukan
        adalah memutar table sehingga kita melihatnya dari sudut. kami akan
        melakukan ini dengan satu baris kode mengunakan matrix rotasi. Kami
        belum bekerja dengan rotasi jadi mari kita bahas.

        ra. arah rotasi
            hal pertama yang perlu kita cari tau adalah sekitar poros yang
        perlu kita putar dan seberapa banyak. kita lihat sistem koordinasi
        tangan kanan pada page89 untuk mengetahui bagaimana object akan berputar
        disekitar sumbu yang diberikan, kita akan menggunakan aturan tangan kanan.
        ambil tangan kanan anda dan arahkan ibu jari anda ke arah sumbu positif
        jari-jari anda akan menunjukan bagaimana suatu object akan berputar di
        sekitar sumbu itu, mengingat sudut rotasi positif. lihat gambar page 112
        Cobalah ini dengan sumbu X-, Y- dan Z. jika kita berputar disekitar sumbu
        Y meja kita akan berputar secara horizontal disekitar ujung atas dan bawah.
        jika kita berputar disekitar sumbu Z, meja akan berputar dalam lingkaran.
        Apa yang ingin kita lakukakn adalah memutar meja ke belakang di sekitar sumbu X
        karena ni akan membawa meja lebih banyak membawa level dengan mata kita.
        
        rb. Rotation matrix
            Untuk melakukan rotasi yang sebenarnya kami akan menggunakan matix rotasi
        rotasi matrix menggunakan fungsi trigonometri sinus dan kosinus untuk mengubah
        sudut rotasi menjadi faktor penskalaan. Berikut ini adalah definisi matrix untuk
        rotasi di sekitar sumbu x:
             ________________________
            | 1    0        0     0  |
            |                        |
            | 0  cos(a)  -sin(a)  0  |
            |                        |
            | 0  sin(a)   cos(a)  0  |
            |                        |
            | 0    0        0      1 |
            |________________________|

        berikut matrix untuk rotasi di sekitar sumbu y:
             ____________________________
            |  cos(a)    0    sin(a)   0 |
            |                            |
            |    0       1      0      0 |
            |                            |
            | -sin(a)    0     cos(a)  0 |
            |                            |
            |    0       0      0      1 |
            |____________________________|

        terakhir matrix untuk rotasi di sekitar sumbu z:
             _________________________
            |  cos(a)  -sin(a)  0   0 |
            |                         |
            |  sin(a)   cos(a)  0   0 |
            |                         |
            |    0       0      1   0 |
            |                         |
            |    0       0      0   1 |
            |_________________________|

        dimungkinkan juga untuk menggabungkan semua ini ke dalam
        matrix rotasi umum berdasarkan sudut arbitary dan vector.
        sekarang kita coba rotasi di sekitar sumbu x sebagai tes
        kami akan mulai dengan titik yang merupakan satu unit
        diatas asal, dengan Y adalah 1 dan memutarnya 90 derajat
        disekitar sumbu X. Pertama mari siapkan matrix rotasi:
             __________________________    ____________
            |  1    0        0       0 |  | 1  0  0  0 |
            |                          |  |            |
            |  0   cos(90) -sin(90)  0 |  | 0  0 -1  0 |
            |                          |  |            |
            |  0   sin(a)   cos(90)  0 | =| 0  1  0  0 |
            |                          |  |            |
            |  0    0        0       1 |  | 0  0  0  1 |
            |__________________________|  |____________|

        mari kita gandakan dari matrix yang dimaksud dan apa yang terjadi:
             _____________   _    _
            |  1  0  0  0 | |0|  |0|
            |             | | |  | |
            |  0  0 -1  0 | |1|  |0|
            |             | | |= | |
            |  0  1  0  0 | |0|  |1|
            |             | | |  | |
            |  0  0  0  1 | |1|  |1|
            |_____________| |_|  |_|

        The point telah dipindahkan dari (0,1,0) ke (0,0,1). jia kita meliaht
        rotasi disekitar sumbu x dengan menggunakan aturan tangan kanan di bab sebelumnya 
        dengan sumbu x, kita dapat melihat bagaimana rotasi positif akan bergerak satu titik
        di lingkaran di sekitar sumbu x.
        */

        /*
        Menambahkan rotasi
            kami akan mendorong meja sedikit lebih jauh, karena begitu
        kami memutarnya ujung bawah maka akan lebih dekat dengan kami
        dan kemudian memutarnya dengan -60 derajat di sekitar sumbu x
        yang membawa meja pada sudut yang bagus seolah-olah kami berdiri
        didepanya.

        Ini adalah bab yang agak intents. Matematika matrix semakin terlibat
        ketika kami belajar tentang proyeksi perspektif dan bagaimana mereka
        bekerja dengan bagian perspektif OpenGL. Kami kemudian belajar cara
        bergerak dan memutar meja kami dengan matrix. berita baiknya bahwa kita
        tidak perlu memiliki pemahanan yang sempurna tentang matematika dan teori
        yang mendasarinya di balik proyeksi dan rotasi untuk menggunakanya. Dengan
        pemahanan dasar tentang apa yang frustrasi dan bagaimana matrix membantu
        kami memindahkan barang-barang, anda akan merasa jauh lebih mudah untuk bekerja
        dengan OpenGL diujung jalan.
        */
        
        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        glClear(GL_COLOR_BUFFER_BIT);

        //mengirim matrix ke shader
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        
         //draw triangle Fan
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        //draw line
        glDrawArrays(GL_LINES, 6, 2);

        //draw the first mallet blue
        glDrawArrays(GL_POINTS, 8, 1);

        //draw the second mallet red
        glDrawArrays(GL_POINTS, 9, 1);

    }
}

/*
membuat matrix proyeksi sendiri
    kami sekarang siap menambahkan proyeksi perspektif untuk kode kami.
kelas matrix android berisi dua metode untuk ini yaitu frustumM() dan perspective()
sayangnya frustumM() memiliki bug yang mempengaruhi beberapa jenis proyeks dan
perspective() hanya diperkenalkan di andoid sandiwich ke atas maka metode ini
tidak berfungsi di android sanwi kebawah. atau kita dapat membuat kode metode
sendiri untuk mengumplemtasikan matrix ini.
*/

class MatrixHelper 
{
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f)
    {
        /*
        menghitung panjang fokus
            Hal pertama yang akan kita lakukan adalah menghitung
        panjang fokus yang akan didasarkan pada bidang penglihatan
        di seluruh sumbu Y.
        kami menggunakan kelas Math java untuk menghitung garis tangent(singgung)
        dan karena kita menginginkan sudut pada radian, kami mengubah bidang penglihatan
        dari derajat ke radian. Kami kemudian menghitung panjang fokus seperti yang
        dijelaskan pada bagian sebelumnya.
        */
        final float angleInRadians = (float)(yFovInDegrees * Math.PI / 180.0);
        final float a = (float)(1.0 / Math.tan(angleInRadians / 2.0));

        /*
        menulis matrix
            ini menulis data matrix ke array floating point yang didefinisikan
        dalam arguman m, yang perlu memiliki setidaknya enam belas elemen. OpenGL
        menyimpan data matrix dalam urutan coloumn-major(kolom-utama) yang berarti
        bahwa kami menulis data satu kolom pada satu waktu daripada satu baris pada satu waktu
        Empat nilai pertama mengacu pada kolom pertama, empat nilai kedua ke kolom
        kedua dan seterusnya. fungsi ini sangat mirip dengan yang ditemukan dalam source android
        dengan perubahan kecil supaya mudah dibaca;
        */
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
