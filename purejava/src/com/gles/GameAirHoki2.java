/*

* BAB 3
* Coloring fragmen shader
*/

package com.gles;

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
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glViewport;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class GameAirHoki2 implements Renderer
{
    float[] tableVerticesWithTriangles = {
        /*
        Menggunakan triangle fan dan penambahan atribut
            dengan titik baru ditengah-tengah meja, kita akan berakhir
        dengan empat segitiga bukan dua. Kami akan memusatkan poin baru
        di (0,0). menambahkan tiga nomor tambahan kesetiap titik. Angka-angka ini
        mewakili merah, hijau dan biru dan bersama-sama mereka akan membentuk
        warna untuk vertices(simpul) tertentu.
        
        cara termudah mendapatkan warna menggunakan java:
            ketika kita menggunakan atribut floating point kita perlu menentukan setiap
        komponen warna dalam kisaran dari 0 hingga 1 dimana 1 menjadi nilai maksimum untuk
        komponen warna itu. mencari tahu angka yang tepat untuk warna tertentu mungkin tidak
        jelas tetapi dengan menggunakan kelas warna android kita dapat dengan mudah menghasilkan
        nilai yang tepat misalnya jika ingin mendapatkan warna OpenGL untuk hijau:

        float green = Color.green(Color.GREEN) / 255f;
                atau
        int parseColor = Color.parseColor("#0099CC");
        float green = Color.green(parseColor) / 255f;
        niai-nilai yang dikembalikan color bekisar 0 hingga 255 sehingga untuk
        mengubahnya menjadi warna OpenGL kita hanya membagi dengan 255

        segitiga 1 = (1, 2.6, 3)
        segitiga 2 = (1, 3, 4)
        segitiga 3 = (1, 4, 5)
        segitiga 4 = (1, 5, 2.6)

                5_______4
                |\     /|
                | \   / |
                |  \ /  |
                |   \   |
                |  /1\  |
                | /   \ |
                |/_____\|
                2        3
        //Update triangle Fan
            0,      0,
        -0.5f,  -0.5f,
         0.5f,  -0.5f,
         0.5f,   0.5f,
        -0.5f,   0.5f,
        -0.5f,  -0.5f,

        //line 1
        -0.5f, 0f,
        0.5f, 0f,

        //mallets
        0f, -0.25f,
        0f, 0.25f
        */

        //Update1 with Color attribut
        //coordinate: X, Y, R, G, B

        //triangle Fan
           0f,     0f,   1f,   1f,   1f,
        -0.5f,  -0.5f, 0.7f, 0.7f, 0.7f,
         0.5f,  -0.5f, 0.7f, 0.7f, 0.7f,
         0.5f,   0.5f, 0.7f, 0.7f, 0.7f,
        -0.5f,   0.5f, 0.7f, 0.7f, 0.7f,
        -0.5f,  -0.5f, 0.7f, 0.7f, 0.7f,

        //line 1
        -0.5f, 0f, 1f, 0f, 0f,
         0.5f, 0f, 1f, 0f, 0f,

        //mallets
        0f, -0.25f, 0f, 0f, 1f,
        0f,  0.25f, 1f, 0f, 0f
    };

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTE_PER_FLOAT = 4;
    private final FloatBuffer vertexData;

    private int program = 0;
    
    //update
    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTE_PER_FLOAT;
    private int aColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;
    /*
    Apakah anda memperhatikan bahwa kami menambahkan konstanta khusus
    yang disebut stride? karena sekarang kami memiliki posisi dan atribut
    warna dalam array data yang sama, OpenGL tidak dapat lagi berasumsi
    bahwa posisi berikutnya mengikuti segera setelah posisi sebelumnya.
    setelah OpenGL telah membaca posisi untuk vertices, itu harus melewati
    warna untuk titik saat ini jika ingin membaca posisi vertex berikutnya.
    kami akan menggunakan langkah untuk memberi tahu OpenGL berapa banyak
    byte antara setiap posisi sehingga ia tau seberapa jauh ia harus melewati.
    */

    public GameAirHoki2() 
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

        /*
        langkah selanjutnya adalah menhapus uniform color(u_Color)
        dan menggantikanya dengan atribut. Kami menambahkan attribut
        baru yaitu a_Color dan kami juga menambahkan varying(varigasi)
        baru yaitu v_Color. Apa yang bervariasi? anda mungkin bertanya
        ingatlah bahwa kita ingin warna kami bervariasi di permukaan segitiga
        nah ini dilakukak dengan menggunakan jenis variable khusus yang dikenal
        sebagai varying(varigasi). Untuk lebih mengenal apa yang dilakukan
        varying mari kita tinjau bagaimana OpenGL menggabungkan vertices bersama
        untuk membuat object. Ketika OpenGL membangun garis dibutuhkan dua vertices
        yang membentuk garis itu dan menghasilkan fragmen. Ketika OpenGL membangun
        segitiga itu melakukan hal yang sama, dengan menggunakan tiga vertices untuk
        membangun segitiga. Shader fragmen kemudian akan dijalankan untuk ke setiap
        fragmen yang dihasilkan.

        berbagai variable adalah jenis variable khusus yang memadukan nilai yang diberikan
        padanya dan mengirimkan nilai-nilai in ke ke shader fragmen. Menggunakan garis sebagai
        contoh jika a_Color berwarna merah di vertex 0 dan hijau di vertex 1 kemudian dengan
        menetapkan a_Color ke v_Color kita memberi tau OpenGL yang kami ingin setiap fragmen
        menerima warna campuran. Dekat vertex 0 warna campuran sebagian besar merah, dan karena
        fragmen semakin dekat dengan vertex 1 maka warnaya akan mulai menjadi hijau.

        ubah juga fragmenShaderCode yaitu u_Color ke v_Color. jika fragmen itu milik garis
        maka OpenGL akan menggunakan dua vertices yang membentuk garis itu untuk menghitung
        warna campuran begitupun jika fragmen itu milik segitiga maka OpenGL akan menggunakan
        tiga vertices yang membentuk segitiga dan menghitung warna campuran.
        
        a. Linear Interpolasi sepanjang garis
            Disisi kiri garis, warna masing-masing fragmen sebagian besar
        berwarna merah. Ketika kita bergerak ke arah kanan fragmen menjadi
        kurang merah dan ditengah meraka berada di antara merah dan hijau
        ketika kita semakin dekat dengan vertices hijau fragmen menjadi
        semakin hijau.

        kita dapat melihat bahwa setiap skala warna secara linear sepanjang garis
        karena titik kiri garis berwarna merah dan vertices kanan berwarna hijau
        maka ujung kiri garis harus 100 persen merah, tengah harus 50 persen merah
        dan kanan harus 0 persen merah dengan kata lain semakin kanan semakin hitam.
        
        hal yang sama terjadi dengan hijau. karena titik kiri berwarna merah dan vertices
        kanan berwarna hijau, ujung kiri kiri garis akan berwarna hijau 0 persen(gelap)
        tengah menjadi 50 persen hijau dan kanan akan 100 persen hijau.

        setelah kita menambahkan keduanya bersama, maka akan terjadi dengan garis campuran
        dan ini yang dinamakan interpolasi linear dalam waktu singkat. Kekuatan setiap
        warna tergantung pada jarak masing-masing fragmen dari titik yang berisi warna itu.
        untuk menghitug itu, kita dapat mengambil nilai pada vertex 0 dan nilai pada vertex 1
        dan kemudian kita menghitung rasio jarak untuk fragmen saat ini. Rasio jarak hanyalah
        rasio antara 0 dan 100 persen dengan 0 menjadi titik kiri dan 100 persen menjadi titik
        yang tepat. Saat kami bergerak dari kiri ke kanan, rasio jarak akan meningkat secara linear
        dari 0 hingga 100 persen seperti gambar di bawah ini:

           _________________________________
          |   25%  |        |       |       |
          |  ----> |   50%  |       |       |
          |        |  ----> |  75%  |       |
          |        |        | ----> |       |
          |________|________|_______|__100%_|


        untuk menghitung nilai campuran aktual menggunakan iterpolasi linear
        kita dapat menggunakan rumus berikut:

            blended_value = (vertex_0_value * (100% - rasio_jarak)) + (vertex_1_value * rasio_jarak)        
        
        perhitungan ini dilakukan untuk setiap komponen jadi jika kita berurusan dengan nilai warna
        perhitungnan ini akan dilakukan untuk komponen merah, hijau, biru dan alpha secara terpisah
        dengan hasil yang dikombinasikan menjadi nilai warna baru.

        Berikut contoh dengan vertex_0_value adalah merah dengan nilai RGB (1,0,0)
        dan vertex_1_value adalah hijau dengan RGB (0,1,0):

        Rumus: (vertex_0_value * (1 - jarak_ratio)) + (vertex_1_value * jarak_ratio)

        1. kiri jauh (0%) = ((1,0,0) * (100% - 0%)) + ((0,1,0) * 0%)
                          = ((1,0,0) * 100%)
                          = (1,0,0)(red)

        2. seperempat garis (25%) = ((1,0,0) * (100% - 25%)) + ((0,1,0) * 25%)
                                  = ((1,0,0) * 75%) + ((0,1,0) * 25%)
                                  = (0.75, 0, 0) + (0, 0.25, 0)
                                  = (0.75, 0.25, 0) (kebanyakan merah)

        3. tengah garis (50%) = ((1,0,0) * (100% - 50%)) + ((0,1,0) * 50%)
                              = ((1,0,0) * 50%) + ((0,1,0) * 50%)
                              = (0.5, 0, 0) + (0, 0.5, 0)
                              = (0.5, 0.5, 0) (setengah merah, setengah hijau)

        4. kanan seperempat (75%) = ((1,0,0) * (100% - 75%)) + ((0,1,0) * 75%)
                                  = ((1,0,0) * 25%) + ((0,1,0) * 75%)
                                  = (0.25, 0, 0) + (0, 0.75, 0)
                                  = (0.25, 0.75, 5) (kebanyakan hijau)

        5. kanan jauh (100%) = ((1,0,0) * (100% - 100%)) + ((0,1,0) * 100%)
                             = ((1,0,0) * 0%) + ((0,1,0) * 100%)
                             = (0, 1, 0) (hijau)

        perhatikan bahwa setiap saat bobot kedua warna menambahkan hingga 100 perse
        jika merah 100 persen, hijau berada 0 persen. jika merah 50 persen hijau adalah
        50 persen. Menggunakan veriying kita dapat menyatukan dua. Menggunakan verying
        juga tidak terbatas pada warna, kita juga dapat menginterpolasi linear bekerja
        dengan garis. seperti segitiga tapi ini harus menggunakan rumus sebagai berikut:

        blended_value = (vertex_0_value * vertex_0_weight) + (vertex_1_value * vertex_1_weight) +
                        (vertex_2_value * (100% - vertex_0_weight))        
        */

        String vertexShaderCode = "attribute vec4 a_Position;  \n"+
                                    "attribute vec4 a_Color;     \n"+
                                    "varying vec4 v_Color;     \n"+

                                    "void main()                 \n"+
                                    "{                           \n"+
                                    "   v_Color = a_Color;\n"+
                                    "   gl_Position = a_Position;\n"+
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
        

        //membuat object program baru dan menyimpan ID project di programObjectID
        final int programObjectId = glCreateProgram();

        if (programObjectId == 0)
        {
            Log.i("setsuna", "Could not create new program");
        }
        else {
            glAttachShader(programObjectId, vertexShader);
            glAttachShader(programObjectId, fragmentShader);

            //kita sekarang sudah siap menggabungkan shader
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

            //uColorLocation = glGetUniformLocation(program, U_COLOR);
            aColorLocation = glGetAttribLocation(program, A_COLOR);
            aPositionLocation = glGetAttribLocation(program, A_POSITION);
        
            vertexData.position(0);
            glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
            glEnableVertexAttribArray(aPositionLocation);
            
            vertexData.position(POSITION_COMPONENT_COUNT);
            glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

            glEnableVertexAttribArray(aColorLocation);
            /*
            Ini adalah bit penting kode, jadi mari kita luangkan waktu untuk
            memahami setiap baris dengan hati-hati
            1. Pertama-tama kita mengatur posisi vertexData ke POSITION_COMPONENT_COUNT
               yang diatur ke 2. Mengapa kita melakukan ini? karena ketika OpenGL mulai
               membaca pada atribut warna, kami ingin mulai dari atribut warna bukan
               atribut posisi. Kita perlu melewatkan posisi pertama sendiri dengan mengambil
               ukuran komponen posisi ke akun, jadi kami mengatur posisi ke POSITION_COMPONENT_COUNT
               sehingga posisi buffer diatur ke posisi atribut warna pertama. Seandainya
               kita mengatur posisi ke 0 maka OpenGL akan membaca posisi sebagai warna.
            2. kemudian kami memanggil glVertexAttribPointer() untuk mengaitkan data warna
               kami dengan A_COLOR di shader kami. Langkah ini memberi tau OpenGL berapa banyak
               byte antara masing-masing warna, sehingga ketika dibaca dalam warna untuk semua
               vertices ia tahu berapa banyak byte vertex yang perlu dilompati untuk membaca warna
               meskipun warna di OpenGL memiliki empat komponen (rgb dan alpha) kami tidak perlu
               menentukan semuanya. Tidak seperti uniform, OpenGL akan menggantikan komponen yang
               tidak ditentukan dalam atribut default (rgb=0 dan alpha=1).
            3. terakhir kami mengaktifkan atribut vertex untuk atribut warna, seperti
               yang kami lakukan di atribut posisi
            */
        }
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        glClear(GL_COLOR_BUFFER_BIT);
        
        //Update1 with color attribute
         //Update draw triangle Fan
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        //draw line
        glDrawArrays(GL_LINES, 6, 2);

        //draw the first mallet blue
        glDrawArrays(GL_POINTS, 8, 1);

        //draw the second mallet red
        glDrawArrays(GL_POINTS, 9, 1);

    }
}
