/*

* BAB 9
* Membumbui segalanya dengan partikel
*
*/

package com.gles.GamePartikel;

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
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.glEnable;
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
    Kami akan mengubah arah dan mulai menjelajahi lebih banyak seni
di belakang OpenGL. Selama beberapa bab berikutnya kami akan mulai
membangun lanskap dengan pegunungan, awan, dan beberapa efek yang
dilemparkan hanya untuk bersenang-senang. kami akan belajar cara
menggunakan pencahayaan, memadukan dan banyak lagi dan bagaimana kami
dapat menyatukan untuk membuat adegan. Dalam bab ini kami akan menjelajahi
dunia particels - simple object yang ditarik sebagai poin. dengan
partikel kita dapat menggunakan kombinasi fisika dasar dan rendering
untuk menghasilkan beberapa efek yang sangat rapi. kita dapat membuat
air mancur yang menembak tetesan di udara dan menyaksikan mereka jatuh
kembali ke bumi. kita dapat mensimulasikan efek hujan atau kita dapat
menciptakan ledakan dan kembang api. Matematika dibalik partikel tidak
terlalu rumit, membuatnya mudah ditambahkan ke adegan 3D apapun.
sebelum kita lanjut kita tinjau rencana game kita untuk bab ini:
    1. pertama kita akan membahas apa yang kita butuhkan untuk mengatur
       sistem partikel.
    2. kami kemudian akan menambahkan beberapa air mancur untuk menembak
       beberapa partikel ke udara.
    3. kami juga akan belajar cara meningkatkan tampilan partikel dengan
       menggunakan teknik seperti blending dan sprites.

*/

public class GamePartikel implements Renderer
{
    private int tutorial = 0;
    /*
    kami membuat variable standar kami untuk kontext android
    dan matrix kami, dan kami memiliki shader partikel, sistem
    dan tidak shooter partikel. kami juga memiliki variable
    untuk waktu mulai global dan konsturktor standar.
    */
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

    public GamePartikel() {
    }
    public GamePartikel(Context context, int tutorial) {
        this.context = context;
        this.tutorial = tutorial;
    }

    public GamePartikel(Context context) 
    {
        this.context = context;
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        if (tutorial >= 3) {
            /*
            Mixing(mencampur) partikel dengan Blending(campuran) aditif
                ketika kita melakukan efek berbeda di OpenGL kita sering
            harus berfikir kembali pada efek yang kita coba reproduksi.
            jika kita membayangkan tiga partikel kita mengalirkan air mancur
            kembang api, seperti yang kita lihat dipertunjukan kembang api,
            maka kita akan mengharapkan partikel memiliki untuk mengeluarkan
            cahaya dan semakin banyak dari mereka ada hal-hal yang lebih cerah seharusnya.
            salah satu cara yang dapat kita mereproduksi efek ini adalah dengan
            menggunakan campuran aditif.
                itu pertama kami mengaktifkan campuran(mixing) dan kemudian kami
            mengatur blending mode ke campuran aditif. untuk lebih memahami cara
            kerjanya mari kita lihat persamaan blending default OpenGL.

                output = (source factor * source fragment) + (destination factor * destination fragment)
            
            di OpenGL memadukan(blending) pekerjaan dengan blending hasil shader fragmen
            dengan warna yang sudah ada dibuffer-buffer. nilai untuk fragmen sumber
            berasal dari shader fragmen kami, fragmen tujuan adalah apa yang sudah ada
            di buffer bingkai, dan nilai-nilai untuk faktor sumber dan faktor tujuan
            dikonfigurasi dengan memanggil glBlendFunc(). dalam kode yangg baru saja
            tambahkan kami sebut glBlendFunc() dengan setiap faktor yang diatur ke GL_ONE
            yang mengubah persamaan blending sebagai berikut

                output = (GL_ONE * source_fragment) + (GL_ONE * destination_fargment)
            
            GL_ONE hanyalah placeholder untuk 1 dan sejak mengalikan dengan 1 hasil dalam
            jumlah yang sama, persamman dapat disederhanakan sbb:

                output = source_fragment + destination_fragmen
            
            dengan mode pencampuran ini fragmen ini, fragmen dari shader fragmen kami
            akan ditambahkan k fragmen yang sudah ada dilayar dan itulah cara kami mendapatkan
            campuran aditif. Ada banyak mode pencampuran yang mungkin dapat anda cari
            disitus khronos web.
            partikel kita sekarang akan terlihat lebih cerah dan mereka berbaur bersama
            sesuatu yang perlu anda ingat adalah bahwa OpenGL menjepit nilai masing-masing
            komponen warna jadi jika kita menambahkan warna hijau padat ke hijau solid
            kita masih akan memiliki hijau yang solid. namun jika kita menambahkan hanya
            sedikit merah dan menambahkan warna-warna yang cukup pada waktu yang cukup
            kita akan benar-benar menggeser rona dan berakhir dengan kuning. menambahkan
            sedikit biru ke yang cukup kuning maka akan berakhir dengan putih.
            kita dapat menemukan beberapa efek yang rapi dengan mempertimbangkan perilaku
            penjepit OpenGL misalnya pada gambar air mancur kembang api merah, kami sebenarnya
            ini sebenarnya menjadi agak kuning dimana itu paling terang dan ini karena kami
            menambahkan sedikit hijau dan sedikit kurang biru ke warna dasar. 
            */
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE);
        }
        
        /*
        Membuat serangkain shader untuk sistem partikel sederhana
            mari kita mulai dengan menambahkan sistem partikel yang
        sangat sederhana untuk mewakili air mancur. kita dapat
        membayangkan ini sebagai air mancur yang diterangi oleh
        cahaya dibawahnya atau kita dapat membayangkan sebagai
        air mancur kembang api seperti yang mungkin kita lihat
        dipertunjukan kembang api. untuk memulai air mancur ini
        kita harus mengurus beberapa detail teknis.
            pertama kita perlu beberapa cara untuk mewakili semua
        partikel dalam memori. kita bisa menggunakan array object java
        untuk ini tetapi bisa mahal untuk membuat dan menghapus banyak
        object selama runtime, dan tidak ada cara mudah untuk mengirim
        data ke OpenGL. kami dapat menyimpan semua data partike sebaliknya
        di dalam satu array, seperti gambar dibawah. Untuk menambahkan
        partikel kita hanya perlu manambah jumlah pandang, tulis data ke
        array partikel kami dan salin ketika kehabisan ruang, kita dapat
        mendaur ulang array dengan mulai dari awal.
            kami juga akan membutuhkan cara untuk menggambar setiap partikel.
        kami dapat mewakili setiap partikel sebagai titik tunggal dan menggambar
        simpul ini sebagai satu set titik, masing-masing dengan posisi dan
        warna yang unik. terakhir kita juga membutuhkan cara memperbarui partikel.
        kita dapa melakukan beberapa pekerjaan ini pada GPU dengan meletakan logika
        dalam program shader. kami akan menyimpan vector arah dan waktu pembuatan
        untuk setiap partikel, dengan waktu pembuatanya kita dapat mengetahui
        beberapa banyak waktu yang telah berlalu karena partikel dibuat, dan
        kemudian kita dapat menggunakan waktu yang telah berlalu dengan vector arah
        dan posisi untuk mencari tau posisi partikel saat ini. kami akan menggunakan
        floating point untuk menyimpan waktu dengan 0.0 mewakili ketika kami mulai
        menjalankan sistem partikel kami. dengan persyaratan dasar ini kami dapat
        menemukan serangkaian spesifikasi awal untuk program shader kami.

         _____________________________________
        |                |    float[]         |
        |                |--------------------|
        |    partikel 0  |    position.x      |
        |                |    position.y      |
        |                |    position.z      |
        |                |    direction.x     |
        |                |    direction.y     |
        |                |        ...         |
        |________________|____________________|

        pertama kita perlu uniform untuk matrix projection dan uniform untuk waktu saat ini
        sehingga shader dapat mengetahui berapa banyak waktu yang telah berlalu
        karena setiap partikel dibuat. kami juga akan memerlukan empat atribut yang
        sesuai dengan sifat partikel: posisi, warna, vector arah dan waktu pembuatan.
        */

        /*
        kami pertama-tama mengirim warna ke shader fragmen, seperti
        yang terlihat pada baris ketiga dan kemudian kami menghitung
        berapa banyak waktu yang telah berlalu karena partikel ini dibuat
        dan mengirimkanya ke shader fragmen juga. untuk menghitung posisi
        partikel saat ini kami mengalikan vector arah dengan waktu yang
        telah berlalu dan menambahkanya ke posisi, semakin banyak waktu
        yang berlalu semakin jauh partikel akan pergi.
            untuk melengkapi kode shader, kami memproyeksikan partikel
        dengan matrix dan karena kami membuat partikel sebagai titik kami
        menetapkan ukuran titik menjadi 10 pixel. penting untuk memastikan
        bahwa kami tidak sengaja mengacaukan komponen W ketika melakukan
        metamatika kami, jadi kami menggunakan vector 3 komponen untuk
        mewakili posisi dan arah, mengkonversi ke vector 4 komponen penuh
        hanya ketika kita perlu memperbanyak dengan u_Matrix. ini memastikan
        bahwa matematika kami diatas hanya mempengaruhi komponen x,y,z
        */
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
        
        if (tutorial >= 2) {
            /*
            int akan menghitung faktor grafitasi yang mempercepat dengan
            menerapkan rumus akselerasi grafitasi dan mengapu waktu yang telah
            berlalu, kami juga membagi hal-hal dengan 8 untuk meredam efeknya.
            angka 8 adalah arbiter kita bisa menggunakan nomor lain yang juga
            membuat segalanya terlihat bagus dilayar. sekarang kita perlu
            menerapkan gravitasi pada posisi kita saat ini.
            */
            strbuffV.append("   float grafityFactor = v_ElapsedTime * v_ElapsedTime / 8.0;\n");
            strbuffV.append("   currentPosition.y -= grafityFactor;\n");
        }

        strbuffV.append("    gl_Position = u_Matrix * vec4(currentPosition, 1.0);\n");

        /*
        pertama kita ambil ukuran poin sehingga ini akan
        lebih mudah dilihat.
        */
        if (tutorial >= 4)
            strbuffV.append("    gl_PointSize = 25.0;\n");
        else
            strbuffV.append("    gl_PointSize = 10.0;\n");
        strbuffV.append("}   ");
        String partikelVertexShader = strbuffV.toString();

        /*
        shader ini akan mencerahan partikel muda dan partikel tua redup
        dengan membagi warna dengan waktu yang berlalu. apa yang terjadi
        jika ada kesenjangan dengan nol? Menurut spesifikasi ini dapat
        menyebabkan hasil yang tidak ditentukan tetapi tidak boleh mengarah
        pada penghentian program shader. untuk hasil yang lebih diprediksi
        anda selalu dapat menambah sejumlah kecil ke penyebut.
        */
        StringBuffer strbuff = new StringBuffer();
        strbuff.append("precision mediump float;    \n");
        strbuff.append("varying vec3 v_Color;       \n");
        strbuff.append("varying float v_ElapsedTime;\n");

        if (tutorial >= 5) {
            /*
            menggambar setiap titik sebagai sprite
                teknik yang ada di tutorial 4 bekerja tapi kadanga texture
            bekerja lebih baik. menggunakan gl_PointCoord dan texture yang sama
            kita beran dapat menggambar setiap titik sebagai titik sprite.
            kami akan mengubah shader partikel kami menggunakan texture
            untuk setiap partikel.
            */
            strbuff.append("uniform sampler2D u_TextureUnit;");
        }
        strbuff.append("void main()                 \n");
        strbuff.append("{                           \n");

        /*
        menyesesuaikan penampilan poin(titik)
            anda mungkin telah memperhatikan bahwa point kami ditampilkan
        sebagai kotak kecil dengan jumlah pixel di setiap sisi sama dengan
        nilai gl_PointSize. menggunakan variable OpenGL khusus lainya gl_PointCoord
        kami benar-banar dapat menyesuaikan tampilan point kami. untuk
        setiap titik ketika fragmen shader dijalankan kita akan mendapatkan
        koordinat gl_PointCoord dua dimensi dengan setiap komponen milai
        dari 0 hingga 1 pada setiap sumbu tergantung pada fragmen mana pada
        saat ini sedang diberikan.
        untuk melihat cara kerjanya kami pertama-tama akan menggunakan gl_PointCoord
        untuk menggambar fragmen kami sebagai lingkaran bukan kotak. bagaimana
        kita bisa melakukan ini? Nah setiap titik akan diturunkan dengan fragmen
        yang berkisar dari 0 hingga 1 pada setiap sumbu relatif terhadap gl_PointCoord
        sehingga menempatkan pusat titik pada (0.5, 0.5) dengan 0.5 unit ruang
        disetiap sisi. dengan kata lain kita dapat mengatakan bahwa jari-jari
        titik juga 0.5. untuk menggambar lingkaran yang perlu kita lakukan hanya
        menggambar fragmen yang terletak didalam jari-jari itu.
            ini adalah cara yang agak mahal untuk menggambar titik sebagai lingkaran
        tetapi berhasil. cara kerjanya adalah setiap fragmen kami menghitung jarak
        kepusat titik dengan teorema pytagoras. jika jarak itu lebih besar dari
        jari-jari 0.5 maka fragmen saat ini bukan bagian dari lingkaran dan kami
        menggunkan discard(buang) kata kunci khusus untuk memberi tau OpenGL untuk
        melupakan fragmen ini kalau tidak kita menggambar fragmen sebelumnya.  
        */
        if (tutorial >= 4) {
            strbuff.append("  float xDistance = 0.5 - gl_PointCoord.x;\n");
            strbuff.append("  float yDistance = 0.5 - gl_PointCoord.y;\n");
            strbuff.append("  float distanceFromCenter = sqrt(xDistance * xDistance + yDistance * yDistance);\n");
            strbuff.append("  if (distanceFromCenter > 0.5) {\n");
            strbuff.append("      discard;\n");
            strbuff.append("  } else {\n");

            if (tutorial >= 5)
                /*
                ini akan menggambar texture pada titik menggunakan gl_PointCoord untuk
                koordinat texture. warna texture akan dikalikan dengan warna titik
                sehingga titik akan diwarnai dengan cara yang sama seperti sebelumnya.
                */
                strbuff.append("      gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0) * texture2D(u_TextureUnit, gl_PointCoord);\n");
            else
                strbuff.append("      gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0);\n");
            strbuff.append("  }");
        }
        else {
            strbuff.append("  gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0);\n");
        }
        strbuff.append("}");

        String partikelFragmentShader = strbuff.toString();
    
        /*
        kami menetapkan warna yang jelas untuk hitam, menginisialisaasi program
        shader partikel kami dan menginisialisasi sistem partiel baru dengan batas
        maksimum sepuluh ribu partisi, dan kemudian kami menetapkan waktu mulai
        global ke sistem saat ini menggunakan System.nanoTime() sebagai pangkalan.
        kami ingin sistem partikel berjalan berdasarkan floating-point time basis
        sehingga ketika sistem partikel diinisialisasi, waktu saat ini akan 0.0 dan
        partikel yang dibuat pada saat itu akan memiliki waktu pembuatan 0.0, lima detik
        kemudian partikel baru akan memiliki waktu pembuatan 5.0. untuk melakukan ini
        kita dapat mengambil perbedaan antara waktu sistem saat ini dan gloatStartTime
        dan sejak System.nanoTime() mengembalikan waktu dalam nanoseconds kita hanya
        perlu membagi perbedaan 1 triliun untuk mengubahnya menjadi beberapa detik.
            bagian selanjutnya dari metode ini mengatur tiga air mancur partikel kami
        setiap air mancur diwakili oleh penembak(shooter) partikel, dan setiap penembak
        akan memotret partikelnya ke arah particleDirection atau lurus di sepanjang sumbu y.
        kami telah menyelaraskan tiga air mancur dari kiri ke kanan dan kami telah mengatur
        warna sehingga yang pertama berwarna merah, kedua hijau dan terakhir biru.
        */
        particlesProgram = new PartikelShaderProgram(partikelVertexShader, partikelFragmentShader, tutorial);
        particlesSystem = new PartikelSystem(10000);
        globalStartTime = System.nanoTime();

        if (tutorial >= 5) {
            texture = TextureHelper.loadTexture(context, R.drawable.particle_texture);
        }

        final Geometry.Vector particleDirection = new Geometry.Vector(0f, 0.5f, 0f);

        if (tutorial != 0) //tutorial 1 atau 2
        {
            /*
            kami telah mengatur semuanya sehingga setiap fountain
            partikel memiliki varian sudut 5 derajat dan varian
            kecepatan 1 unit. 
            */
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
        else { //tutorial 0
            redParticleShooter = new PartikelShooter(
                new Geometry.Point(-1f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5));

            greenParticleShooter = new PartikelShooter(
                new Geometry.Point(0f, 0f, 0f),
                particleDirection,
                Color.rgb(25, 255, 25));
        
            blueParticleShooter = new PartikelShooter(
                new Geometry.Point(1f, 0f, 0f),
                particleDirection,
                Color.rgb(5, 50, 255));
        }
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        glViewport(0, 0, width, height);

        /*
        ini adalah definisi standar dengan proyeksi perspektif reguler
        dan matrix tampilan yang mendorong semuanya kebawah dan ke kejauhan.
        */
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float)height, 1f, 10f);
        setIdentityM(viewMatrix, 0);
        translateM(viewMatrix, 0, 0f, -1.5f, -5f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        glClear(GL_COLOR_BUFFER_BIT);

        /*
        setiap kali bingkai baru ditarik kami menghitung waktu saat ini dan
        meneruskanya ke shader. itu akan memberi tau shader seberapa jauh setiap
        partikel telah pindah sejak diciptakan. kami juga menghasilkan lima
        partikel baru untuk setiap air mancur dan kemudian kami menggambar
        parikel dengan program shader partikel.
        */

        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticle(particlesSystem, currentTime, 5);
        greenParticleShooter.addParticle(particlesSystem, currentTime, 5);
        blueParticleShooter.addParticle(particlesSystem, currentTime, 5);

        particlesProgram.useProgram();
        if (tutorial >= 5)
            particlesProgram.setUniforms(viewProjectionMatrix, currentTime, texture);
        else
            particlesProgram.setUniforms(viewProjectionMatrix, currentTime, 0);
        particlesSystem.bindData(particlesProgram);
        particlesSystem.draw();
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
            .allocateDirect(vertexData.length * GamePartikel.BYTES_PER_FLOAT)
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

    /*
    kami perlu menambahkan definisi untuk updateBuffer() sebagai metode
    baru didalam vertexArray.
    */
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

        if (tutorial >= 5) {
            this.tutorial = tutorial;
            uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        }

        //ambil lokasi attribut untuk program shader
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aDirectionVectorLocation = glGetAttribLocation(program, A_DIRECTION_VECTOR);
        aParticleStartTimeLocation = glGetAttribLocation(program, A_PARTICLE_START_TIME);
    }

    public void setUniforms(float[] matrix, float elapsedTime, int textureID) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uTimeLocation, elapsedTime);
        if (tutorial >= 5) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureID);
            glUniform1i(uTextureUnitLocation, 0);
        }
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

//adding object partikel
class PartikelSystem {
    //definisi dasar untuk jumlah komponen dan langkah antar partikel
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;

    private static final int TOTAL_COMPONENT_COUNT =
                    POSITION_COMPONENT_COUNT +
                    COLOR_COMPONENT_COUNT +
                    VECTOR_COMPONENT_COUNT +
                    PARTICLE_START_TIME_COMPONENT_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * new GamePartikel().BYTES_PER_FLOAT;
    private final float[] particles;
    private final VertexArray vertexArray;
    private final int maxParticleCount;
    private int currentParticleCount;
    private int nextParticle;

    /*
    kami sekarang memilik array floating point untuk menyimpan partikel
    vertexArray untuk membatasi data yang akan kami kirim ke OpenGL dan
    maxParticleCount untuk menahan jumlah partikel maksimum karena ukuran
    array diperbaiki. kami akan menggunakan currentParticleCount dan nextParticle
    untuk melacak partikel dalam array.
    */
    public PartikelSystem(int maxParticleCount) {
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        vertexArray = new VertexArray(particles);
        this.maxParticleCount = maxParticleCount;
    }

    public void addParticle(Geometry.Point position, int color, Geometry.Vector direction, float particleStartTime)
    {
        /*
        untuk membuat partikel baru pertama-tama kita lewati posisi, warna
        arah dan waktu pembuatan partikel. warnanya dilewatkan sebagai warna
        android dan kami akan menggunakan kelas warna android untuk menguraikan
        warna ke komponen terpisah.
        sebelum kita dapat menambahkan partikel baru ke array kita, kita perlu
        menghitung kemana harus pergi. array kami agak seperti amophous blop(gumpalan amorf)
        dengan semua partikor yang disimpan bersama.
        untuk menghitung offset yang tepat kami menggunakan tempel(store) di nextParticle
        untuk menyimpan jumlah partikel berikutnya dengan partikel pertama mulai dari nol.
        kami kemudian dapat mengimbangi dengan mengalikan target next dengan jumlah
        komponen per partikel. kami menyimpan offset ini dalam particleOffset dan currentOffset
        kami akan menggunakan particleOffset untuk mengingat dimana partikel baru kami dimulai
        dan melonjak untuk mengingat posisi untuk setiap atribut partikel baru.
            setiap kali partikel baru ditambahkan kami menambahkan nextParticle dengan 1 dan
        ketika mencapai akhir kita akan mulai dengan 0 sehingga kita dapat mendaur ulang partikel
        tertua. kita juga perlu melacak berapa banyak partikel yang perlu ditarik dan kita
        melakukan ini dengan menambah currentParticleCount setiap kali partikel baru ditambahkan
        menjaganya secara maksimal.
        */
        final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;
        int currentOffset = particleOffset;
        nextParticle++;

        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++;
        }
        if (nextParticle == maxParticleCount) {
            //mulai dari awal tetapi simpan currentParticleCount
            //jadi bahwa semua partikel lain masih ditarik
            nextParticle = 0;
        }

        /*
        pertama kita menuliskan posisi, selanjutnya warnaya(menggunakan kelas
        warna android untuk menguraikan setiap komponen), arah vector dan terakhir
        waktu pembuatan partikel. kelas warna android mengembalikan komponen dalam
        kisaran 0 hingga 255 sementara OpenGL mengharapkan warna menjadi dari 0 hingga 1
        jadi kami mengkonversi dari android ke OpenGL dengan membagi setiap komponen
        dengan 255. lihat page 64 untuk detail.
        */
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

        /*
        kami masih perlu menyalin partikel baru ke buffer native sehingga
        OpenGl dapat mengakses data baru.
        kami ingin menyalin hanya data baru sehingga kami tidak membuang
        waktu menyalin data yang tidak berubah, jadi kami lulus diawal offset
        untuk pandang baru dan perhitungan.
        */
        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    /*
    ini hanyalah beberapa kode plat boiler yang mengikuti pola
    yang sama seperti pada bab-bab sebelumnya, mengikat data vertex
    kami ke atribut yang tepat dapa program shader dan berhati-hatilah
    untuk pemesanan yang sama dengan yang kami gunakan di addParticle().
    jika kita mencampur warna dengan vector arah atau membuat kesalahan
    yang serupa kita akan mendapatkan hasil yang agak lucu ketika kita
    mencoba menggambar partikel.
    */
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
    /*
    kami sekarang memiliki sistem partikel ditempat. sistem ini akan
    membiarkan kami menambahkan partikel hingga batas tertentu, mendaur ulang
    partikel-partikel lama dan menemukan partikel secara efisien disamping
    satu sama lain dalam memori.
    */
}

/*
menambahkan partikel airmancur
    kita sekarang membutuhkan sesuatu yang benar-benar akan menghasilkan
beberapa partikel untuk kita dan menambahkanya ke sistem partikel. Mari
kita mulai dengan membuat airmancur.
*/
class PartikelShooter {
    private final Geometry.Point position;
    private final Geometry.Vector direction;
    private final int color;

    /*
    hal pertama yang akan kita lakukan adalah menyebar partikel-partikel
    kita, dan kita juga akan memvariasikan kecepatan masing-masing partikel
    untuk memberikan setiap air mancul partikel lagi.
    setiap shooter akan memiliki varian sudut yang akan mengontrol penyebaran
    partikor dan varians kecepatan untuk mengubah kecepatan masing-masing partikel.
    kami juga akan memiliki matrix dan dua vector sehingga kami dapat menggunakan
    kelas matrix android untuk melakukan beberapa (Math)matematika.
    */
    private final float angelVariance;
    private final float speedVariance;
    private final Random random = new Random();
    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];

    private int tutorial = 0;

    //tutorial 0
    public PartikelShooter(Geometry.Point position, Geometry.Vector direction, int color) {
        this.position = position;
        this.direction = direction;
        this.color = color;
        this.angelVariance = 0;
        this.speedVariance = 0;
    }

    //tutorial 1
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
        tutorial = 1;
    }

    /*
    kami telah memberika partikelShoter posisi, arah dan warna kami
    hanya akan melewati langsung ke sistem partikel.
    dalam addPartikel() kami pass(lulus) dalam sistem partikel dan berapa banyak
    partikel yang ingin kami tambahlan serta waktu saat ini untuk sistem
    partiel. kami sekarang memiliki semua komponen kami dan kami hanya
    perlu menambahkan beberapa panggilan kekelas renderer kami untuk merekam
    semuanya bersama.
    */
    public void addParticle(PartikelSystem particleSystem, float currentTime, int count)
    {
        for (int i=0; i<count; i++) 
        {
            if (tutorial != 0) 
            {
                /*
                untuk mengubah sudut penembakan kami menggunakan matrix android
                setRatorEulerM() untuk membuat matrix rotasi yang akan mengubah
                sudut dengan jumlah acak yang berbeda dalam derajat. kami
                kemudian mengalikan matrix ini dengan vector arah untuk mendapatkan
                vector yang sedikit berputar. untuk menyesuikan kecepatan kami
                melipatgandakan setiap omponen vector arah dengan penyesuaian acak
                yang sama dari speedVarian. setelah selesai kami menambahkan partikel
                baru dengan memanggil addParticle().
                */
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
            else {
                particleSystem.addParticle(position, color, direction, currentTime);
            }
        }
    }
}