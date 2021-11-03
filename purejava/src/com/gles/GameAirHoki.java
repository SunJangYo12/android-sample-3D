/*

* BAB 2
* Basic Shader

*/

package com.gles;

import static android.opengl.GLSurfaceView.Renderer;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_TRIANGLES;
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


public class GameAirHoki implements Renderer
{
    /*
    Sebelum kita dapat menggambar meja ke layar kita harus
    memberi tahu OpenGL apa yang harus digambar. Langkah
    pertama dalam rantai itu adalah mendefinisikan struktur
    table dalam bentuk yang dipahami OpenGL.
        Di OpenGL struktur dimulai dg titik. Sebuah titik hanyalah
    titik yang mewakili satu sudut objeck geometris dengan berbagai
    atribut yang terkait dengan titik itu. Atribut yang paling penting
    adalah posisi yang mewakili dimana titik itu berada diruang kosong.
    */

    /*
    data vertex dibawah menggunakan list berurutan nomor floating point
    sehingga dapat menyimpan posisi dengan titik desimal. kami akan
    merujuk array ini sebagai array atribut vertex. dan kami hanya
    menyimpan posisi untuk saat ini, tetapi kemudian kami juga akan
    menyimpan warna dan atribut lainya menggunakan konsep yang terlihat
    disini.
    */


    /*
    table dibawah terlihat seperti dibawah ini:
        
  (0,14) ________ (9,14)
        |       /|
        |4.5,2 / |
        |     /  |
  (0,7) |___ /___| (9,7)
        |   /    |
        |  /     |
        | / 4.5,2|
        |/_______|
  (0,0)           (9,0)

    array diatas memegang enam simpul(vertices) yang akan digunakan
    untuk mewakili dua segitiga.
    segitiga pertama dibatasi oleh point pada (0,0), (9, 14) dan (0, 14)
    segitiga kedua berbagi dua dari posisi ini dan di batasi oleh
    (0,0), (9,0) dan (9, 14).

    MENAMBAHKAN GARIS TENGAH DAN DUA MALLETS
    kami hampir selesai mendefinisikan simpul kami
    kita hanya perlu menambahkan beberapa simpul lagi untuk garis tengah
    dan dan mallets.

    NOTE: ketika kita mendefinisikan segitiga, kami memesan simpul dalam
    urutan berlawanan arah jarum jam, ini dikenal dengan urutan berliku.
    ketika kita konsisten dalam menggunakan urutan berliku yang sama dimana-mana
    kita sering dapat mengoptimalkan kinerja dengan menggunakan urutan berliku
    untuk mencari tahu apakah segitiga milik depan atau ke belakang dari object
    apapun yang diberikan, dan kita dapat meminta OpenGL untuk melewati
    segitiga belakang karena kita tidak akan dapat melihatnya

    setiap kali kita ingin mewakili objeck di OpenGL kita perlu
    memikirkan bagaimana kita dapat membuatnya dalam hal titik,
    garis dan segitiga.

    ingat ketika saya mengatakan bahwa cara termudah untuk mewakili tabel
    hoki kami adalah sebagai persegi panjang? tapi diOpenGL kita hanya
    bisa menggambar poin/titik, garis dan segitiga.

    segitiga adalah bentuk geometris paling dasar, kita melihatnya dimana-mana
    seperti alam komponen struktur jembatan karena itu bentuk yang kuat.
    ini memiliki tiga sisi yg terhubung ketiga simpul/vertices. Jika mengambil
    satu titik maka akan berakhir dengan garis dan jika mengambil lagi akan berakhir satu titik

    poin dan garis dapat digunakan untuk efek tertentu, tetapi hanya segitiga
    yang dapat digunakan untuk membangun seluruh adegan objeck dan tekstur komplex
    
    kami membangun segitiga di OpenGL dengan menglompokan simpul/vertices individu
    bersama-sama, kemudian kami memberi tahu OpenGL secara harfiah cara
    menghubungkan titik-titik sesuai kebutuhan dan jika kita ingin membangun
    bentuk lebih komplex seperti lingkararn maka kita perlu menggunakan point
    yang cukup untuk memperkirakan kurva.
    */
    float[] tableVerticesWithTriangles = {
        //triangle 1
        /*
        0f, 0f,
        9f, 14f,
        0f, 14f,

        //triangle 2
        0f, 0f,
        9f, 0f,
        9f, 14f,

        //line 1
        0f, 7f,
        9f, 7f,

        //mallets
        4.5f, 2f,
        4.5f, 12f*/

        /*
        Perbaikan koordinat kenapa meja ada disamping
            jawaban untuk pertanyaan ini rumit dan akan dibahas pada lain bab
        untuk saat ini yang perlu kita ketahui adalah OpenGL akan memetakan layar
        ke rantang [-1, 1] untuk koordinat X dan Y. ini berarti tepi kiri layar
        akan sesuai dengan -1 pada sumbu X sedangkan tepi kanan layar akan sesuai
        dengan +1 tepi bawah layar akan sesuai dengan -1 pada sumbu y sedangkan tepi
        atas layar sesuai dengan +1.

        kisaran ini tetap sama terlepas dari bentuk atau ukuran layar, dan semua yang
        kita gambar perlu pas dalam kisaran ini jika kita ingin muncul dilayar.
        dan ini perbaikanya supaya tampil ditengah.

        */
        //triangle 1
        -0.5f, -0.5f,
        0.5f, 0.5f,
        -0.5f, 0.5f,

        //triangle 2
        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,

        //line 1
        -0.5f, 0f,
        0.5f, 0f,

        //mallets
        0f, -0.25f,
        0f, 0.25f
    };

    /*
    karena kami memiliki dua komponen per vertex jadi kita buat
    konstanta untuk memuat fakta itu
    */
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTE_PER_FLOAT = 4;
    private final FloatBuffer vertexData;

    private int program = 0;


    /*
    Mendapatkan lokasi uniform(seragam)
        setelah shader telah didefinisikan itu benar-benar akan
    mengaitkan setiap uniform yang ditentukan dalamm vertex shader
    dengan lokasi. Nomor lokasi ini digunakan untuk mengirim data
    ke shader dan kita membutuhkan lokasi ini untuk u_Color sehingga
    kita dapat mengatur wara saat kita akan menggambar
    */
    private static final String U_COLOR = "u_Color";
    private int uColorLocation;

    /*
    Mendapatkan lokasi attribute
    */
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    public GameAirHoki() {
        /*
        CALLING NATIVE CODE DARI JAVA

        pendekatan dalvik adalah salah satu kekuatan utama android
        tetapi jika kode kita berada di dalam mesin virtual lalu
        bagaimana kita berkomunikasi dengan OpenGL, nah ada dua trik
        
        trik pertama adalah menggunakan java native interface(jni)
        dan trik ini sudah dilakukan untuk android SDK yaitu pada
        paket android.opengl.gles20 dan ini sebenarya menggunakan
        JNI dibelakang layar untuk memanggil perpustakaan asli system.

        trik kedua adalah mengubah cara kami mengalokasikan memori kami
        kami memiliki akses ke satu set class khusus dijava yang akan
        mengalokasikan blok memori native dan menyalin data kami ke memori itu.
        memori native ini dapat diakses oleh lingkungan native dan itu
        tidak akan dikelola oleh gerbage collector.

        kami telah menambahkan konstan BYTE_PER_FLOAT dan FloatBuffer
        float dijava memiliki 32bit presisi sedangkan byte memiliki 8bit presisi
        ini nampak seperti poin yang jelas untuk dibuat tetapi ada 4byte disetiap float
        kita perlu merujuknya di banyak tempat dijalan.

        FloatBuffer akan digunakan untuk menyimpan data dalam memori native
        */
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTE_PER_FLOAT)
                               .order(ByteOrder.nativeOrder())
                               .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);

        /*
        PENJELASAN:
        pertama kita mengalokasikan blok memori native mengunakan ByteBuffer
        allocateDirect() memori ini tidak akan dikelola oleh pengumpul sampah(gerbage collector)
        kita perlu memberi tau metode seberapa besar blok memori harus dalam byte.
        karena simpul(vertex) kami disimpan dalam array float dan ada 4 byte per float
        maka tableVerticesWithTriangle.length * BYTES_PER_FLOAT

        baris berikutnya buffer byte bahwa itu harus mengatur byte dalam urutan native
        ketika datang ke nilai-nilai yang menjangkau beberapa byte seperti bilangan bulat
        32bit, byte dapat dipesan baik dari yang paling least segnificant ke least to most
        pikirkan itu sama dengan menulis angka baik dari kiri ke kanan atau sebaliknya.
        tidak penting bagi kita untuk mengetahui apa urutan itu tetapi penting bahwa kita
        menggunakan urutan yang sama dengan platfrom. Kami melakukaan itu dengan memanggil
        pesanan order(ByteOrder.nativeOrder())

        selanjutnya kami memanggil asFloatBuffer() untuk mendapatan FloatBuffer yang mencerminkan
        byte yang mendasarinya, kemudian menyalin data dari memori dalvik ke memori native
        dengan memanggil VertexData.put(tableVertivesWithTriangle).

        memori akan dibebaskan ketika proses hancur jadi tak perlu khawatir tentang itu
        jika anda menulis code yang banyak bytebuffer anda bisa membaca tentang
        teknik heap fragmentasi dan managemen memori.
        */
    }


    private int compileShader(int type, String shader) 
    {
        /*
        return glCreateShader() adalah refrefrensi ke objeck OpenGL
        setiap kali kita ingin merujuk ke object ini di masa depan
        kita melewati bilangan bulat yang sama kembali ke OpenGL.
        */
        final int shaderObjectId = glCreateShader(type);
        String log = "";

        if (shaderObjectId == 0) {
            log = "Could not create new shader";
        }
        else {
            /*
            setelah kita meiliki object shader yang valid, kita call glShaderSoure()
            untuk mengupload kode source, panggilan ini memberi tahu OpenGL untuk membaca
            dalam kode souce yang didefinisikan dalam string shader dan mengaitkanya dengan
            object shader yang disebut oleh shaderObjectId. kita kemudian memanggil glCompileShader()
            untuk menkomilasi shader. ini memberi tau OpenGL untuk mengkompilasi kode source
            yang sebelumnya diupload ke shaderObjectId.
            */
            glShaderSource(shaderObjectId, shader);
            glCompileShader(shaderObjectId);

            final int[] compileStatus = new int[1];
            glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
        
            if (compileStatus[0] == 0)
            {
                glDeleteShader(shaderObjectId);
                log = "Compile of shader failed";
            }
            else {
                log = "Results of compiling source: "+shader+"\n"+
                    glGetShaderInfoLog(shaderObjectId);
            }
        }
        return shaderObjectId;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        //glClearColor(1.0f, 0.0f, 0.0f, 0.0f); perbaiki background diawal bab
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String log = "";

        /*
        Perkenalan pipa line OpenGL

            kami sekarang telah mendefinisikan struktur table hoki dan
        telah menyalin data ke memori native dimana OpenGL akan dapat mengaksesnya
        sebelum kuta dapat menggambar tabel hoki ke layar kita perlu mengirimkanya
        melalui pipa OpenGL, dan untuk melakukanya kita perlu menggunakan subrutin
        kecil yang dikenal sebagai shader, shader memberi tahu unit pemrosesan grafis (GPU)
        cara menggambar data kami. Ada dua jenis shader dan kita perlu mendefinisikan keduanya
        sebelum kita dapat menggambar apa pun ke layar.

        1.  Shader vertex, shader ini menghasilkan posisi akhir setiap titik dijalankan sekali per titik
            setelah posisi akhir diketahui OpenGL akan mengambil ikatan rangakaian simpul(vertices) dan
            merakitnya menjadi titik, garis dan segitiga.
        2.  Shader fragment, shader ini menghasilkan warna akhir dari setiap fragment titik, garis
            atau segitiga dan dijalankan sekali perfragment. Sebuah fragment adalah area kecil persegi panjang
            dengan warna tunggal, analog dengan pixel pada layar komputer. 
        
        Tampilan pipa line OpenGL:
       ____________________________________________________________________________________________________________________________________________________
      |                                                                                                                                                    | 
      | Read Vertex ==> Execute Vertex ===> Assembly  ====> rasterize(menghidupkan kembali) ====> execute fragment ====> write to frame ====> terlihat di  |
      |     data           shader           Primitive                  primitive                      shader                 buffer              layar     |
      |____________________________________________________________________________________________________________________________________________________|
        
        Kenapa kita harus menggunakan shader:
            Sebelum shader ada, OpenGL menggunakan serangkaian fungsi tetap yang memungkinkan kami
        mengendalikan beberapa hal terbatas seperti banyak lampu ditempat kejadian atau banyak kabut
        untuk ditambahkan. API tetap ini mudah digunakan tetapi tidak mudah untuk diperpanjang, anda
        memiliki apa yang diberikan API dan hanya itu. jika anda ingin menambahkan efek khusus seperti
        kartun shading anda cukup beruntung, ketika perangkat keras yang mendasarinya meningkat seiring
        waktu orang-orang menyadari bahwa OpenGL juga harus berefolusi dan mengikuti perubahan.

        di OpenGL ES 2.0 meraka menambahkan API yang dapat diprogram menggunakan shader dan untuk menjaga
        hal-hal ringkas meraka mengeluarkan API tetap sepenuhnya dan inilah yang namanya shader. sekarang
        kami memiliki shader untuk mengontrol bagaimana masing-masing vertex ditarik kelayar  dan kami juga
        dapat mengontrol bagaimana setiap fragment setiap titik, garis dan segitiga ditarik.

        kita sekarang dapat melakukan pencahayan per-pixel dan efek rapi lainya seperi cartoon-cell shading
        kita dapat menambahkan efek khusus yang kita impikan selama kita bisa mengekspresikan dalam bahasa shader.
        */

        /*
        Membuat shader pertama kali:
        shader ini didefinisikan menggunakan glsl bahasa shader OpenGL
        bahasa ini memiliki struktur sintaks dengan bahasa C.

        1.  Shader vertex: shader ini akan dipanggil sekali untuk setiap
            titik yang telah kami tetapkan. ketika disebut, itu akan menerima
            posisi titik saat ini dalam atribut a_Position yang didefinisikan
            sebagai vec4. vec4 adalah vector yang terdiri dari empat komponen.
            dalam kontext suatu posisi kita dapat memikirkan empat komponen sebagai
            koordinasi posisi x,y,z dan w. X,Y,Z sesuai dengan posisi 3D sementara
            W adalah koordinat khusus lebih detail bab 6. jika ditentukan berperilaku
            default OpenGL adalah mengatur tiga koordinat pertama dari vertor ke 0 dan
            koordinat terakhir ke 1.
            fungsi main() atau titik masuk utama shader pertama menyalin posisi yang
            telah kita tetapkan pada variable output khusus gl_Position.
            shader harus menulis sesuatu ke gl_Position, OpenGL akan menggunakan nilai
            yang disimpan dalam gl_Position sebagai posisi akhir untuk titik saat ini
            dan mulai merakit simpul ke titik, garis dan segitiga.

        2.  Shader fragment: kita telah membuat shader vertex, sekarang kita memiliki subrutin
            untuk menghasilkan posisi akhir setiap titik. Kita masih perlu membuat subrutin untuk
            menghasilkan warna akhir setiap fragment. Sebelum kita melakukan itu mari kita belajar
            lebih lanjut apa itu fragmen dan bagaimana itu dihasilkan:
            a. The art of rasterization
                Tampilan ponsel anda terdiri dari ribuan hingga jutaan komponen individu kecil yang
                dikenal sebagai pixel, masing2 pixel ini tampaknya mampu menampilkan satu warna dari
                kisaran jutaan warna yang berbeda namum ini sebenarnya adalah trik visual sebagian besar
                tampilan tidak dapat benar-benar membuat jutaan warna yang berbeda, jadi alih-alih
                setiap pixel biasanya terdiri dari tiga subkomponen individu yang memancarkan cahaya
                merah, hijau dan biru dan karena setiap pixelnya sangat kecil mata kita memadukan merah.
                masukan cukup dari pixel individu ini dan kita dapat menampilkan halaman text atau hatsune miku.

                OpenGL menciptakan gambar yang dapat kami papatkan ke pixel ponsel dengan memecah setiap titik,
                garis dan segitiga menjadi kelompok fragment kecil melalui proses yang dikenal rasterisasi.
                Fragmen-fragmen ini analog dengan pixel pada tampilan ponsel, dan masing-masing juga terdiri dari
                satu warna solid. Untuk mewakili warna ini, setiap fragmen memiliki empat komponen yaitu merah, hijau dan biru
                untuk warna dan alpha untuk transparansi. lebih detail bab 2.6
                kita dapat melihat contoh bagaimana OpenGL dapat merasikan garis ke serangkaian fragmen. Sistem tampilan
                biasanya memetakan fragmen ini langsung ke pixel pada layar sehingga satu fragmen sesuai dengan satu pixel.
                Namun ini tidak selalu benar, perangkat super tinggi mungkin ingin menggunakan fragmen yang lebih besar sehingga
                GPU akan memiliki pekerjaan yang lebih sedikit untuk dilakukan fragment primitif jadi jika segitiga memetakan
                ke 10.000 fragmen maka shader akan disebut 10.000 kali. Gambar page 33(atril linux).
            b. Kualifikasi presisi
                bagian pertama di fragmenShaderCode mendefinisikan presisi default untuk semua tipe data floating point
                dalam fragmen shader ini seperti memilih antara float dan double pada java.
                kita dapat memilih antara lowp, mediump dan highp yang sama seperti presisi rendah, sedang dan tinggi
                Namun highp hanya didukung dalam fragmen shader pada beberapa implementasi.
                Kenapa kita tida melakukan ini untu shader vertex? karena shader vertex juga dapat memiliki presisi default berubah,
                tetapi akuransi lebih penting dalam hal posisi titik, pengembang OpenGL memutuskan untuk mengatur vertex shader ke pengaturan
                tertinggi atau highp secara default. karena tipe data presisi yang lebih tinggi akan lebih akurat tetapi mereka akan
                menurunkan kinerja. Untuk shader fragment kami kita memilih mediump untuk kompatibilitas maksimum dan sebagai pertukaran
                yang baik antara kecepatan dan kualitas.
            c. Menghasilkan fragment color's
                sisa fragmen shader mirip dengan shader vertex yang kami definisikan sebelumnya, kali ini kita melewati uniform(seragam) yang
                disebut u_Color, tidak seperti atribut yang diatur pada setiap titik uniform memnyimpan nilai yang sama untuk semua simpul sampai
                kami mengubahnya lagi. seperti atribut yang kami gunakan untuk posisi di vertex, u_Color juga merupakan vector empat komponen dan
                dalam kontext warna yaitu merah, hijau, biru dan alpha. kemudian kita mendefinisikan main() titik masuk utama shader, ini menyalin
                warna yang telah kami definisikan dalam uniform kami dengan variable output khusus gl_FragColor. Shader kita harus menulis sesuatu
                ke gl_FragColor, OpenGL akan menggunakan warna ini sebagai warna akhir untuk fragmen saat ini.
                OpenGL menggunakan model warna RGB aditif yang berfungsi dengan hanya tiga warna primer merah, hijau dan biru. banyak warna dapat
                dibuat dengan mencampurkan warna primer ini bersama dalam berbagai proporsi. Misalnya merah dan hijau menjadi kuning, merah dan biru
                menghasilkan magenta dan biru dan hijau menghasilkan cyan serta gabungan ketiganya akan menghasilkan putih, ini berbeda jika kita
                mencapurkan warna pada cat dan ini akan menghasilkan warna yang lebih gelap karena cat tidak memancarkan cahaya melainkan meyerap
                dan ini berbeda dengan pixel dimana dia menghasilkan cahaya jika dicampur warna akan lebih terang.
            d. mapping(memetakan) warna ke layar
                OpenGL mengasumsian bahwa warna-warna ini memiliki hubungan linear satu sama lain, nilai merah 0,5 harus dua kali lebih terang dari nilai merah 0,25
                dan nilai merah 1 harus dua kali lebih terang dai nilai merah 0,5. warna-warna primer ini dijepit ke kisaran [0, 1] dengan 0 yang mewakili tidak
                adanya warna pada primer tertentu dan 1 mewakili kekuatan maksimum untuk warna.
                model warna ini memetakan dengan baik untuk perangkat keras display yang digunakan oleh ponsel dan tampilan komputer namun dalam hal sifat nonlinear dari
                tampilan anda pada hal 269 bahwa pemetaan tidak cukup satu. Tampilan ini hampir selalu menggunakan tiga warna primer merah, hijau dan biru beberapa mungkin termasuk
                kuning sebagai warna primer tambahan untuk kuning murni, dengan 0 memetakan ke komponen pixel yang tidak terlit dan 1 pemetaan hingga kecerahan penuh untuk warna.
                dengan model warna hampir setiap warna yang dapat dilihat mata dapat kita program ke OpenGL dan ditampilkan kelayar. 

        */

        /*
        jika menggunakan GL_POINTS kita wajib mengatur
        seberapa besar setiap titik akan muncul dilayar
        dengan menambahkan gl_PointSize = 10.0; di vertexShaderCode.

        dengan menulis ke variable output khusus lainya gl_PointSize
        kami memberi tau OpenGL bahwa ukuran poin harus 10. Sepuluh
        dari apa? anda mungkin bertanya, nah ketika OpenGL menghancurkan
        titik ke bawah menjadi fragmen itu akan menghasilkan fragmen dalam kotak
        yang berpusat di sekitar gl_Position dan panjang masing-masing sisi ini
        kuadrat akan sama dengan gl_PointSize. gl_PointSize yang lebih besar
        adalah semakin besar titik yang ditarik ke layar.
        */
        String vertexShaderCode = "attribute vec4 a_Position;  \n"+
                                    "void main()                 \n"+
                                    "{                           \n"+
                                    "   gl_Position = a_Position;\n"+
                                    "   gl_PointSize = 10.0;\n"+
                                    "}";

        String fragmentShaderCode = "precision mediump float;    \n"+
                                      "uniform vec4 u_Color;       \n"+
                                      "void main()                 \n"+
                                      "{                           \n"+
                                      "   gl_FragColor = u_Color;\n"+
                                      "}";
    
        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentShaderCode);
        

        /*
        Sekarang kami telah memuat dan menyusun shader vertex dan shader fragmen
        langkah selanjutnya adalah mengikat(bind) meraka menjadi satu program.
        program OpenGL hanyalah satu vertex shader dan satu fragment shader yang
        terhubung bersama menjadi satu object. shader vertex dan shader fragmen
        selalu berjalan bersama, tanpa shader fragmen OpenGL tidak akan tahu cara
        menggambar fragmen yang membentuk setiap titik, garis dan segitiga. Dan
        tanpa vertex(simpul) shader OpenGL tidak akan tahu dimana harus menarik(draw)
        fragmen-fragmen ini.

        kita tahu bahwa vertex shader menghitung posisi akhir setiap titik pada layar
        kita juga tahu bahwa ketika OpenGL mengulas simpul ini ke titik, garis dan segitiga
        dan memecahnya menjadi fragmen kemudian akan meminta shader fragmen untuk warna akhir
        setiap fragmen. Shader titik dan fragmen bekerja sama untuk menghasilkan gambar
        pada akhir layar.

        meskipun shader vertex dan shader fragmen selalu berjalan bersama meraja tidak harus
        tetap monogami, kita dapat menggunakan shader yang sama di lebih dari satu program
        sekaligus

        */

        //membuat object program baru dan menyimpan ID project di programObjectID
        final int programObjectId = glCreateProgram();

        if (programObjectId == 0)
        {
            log = "Could not create new program";
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
                log = "Linking of program failed.";
            }
            else {
                /*
                sebelum kita mulai menggunakan program OpenGL, kita harus
                memvalidasi terlebih dahulu untuk melihat apakah program ini
                berlaku untuk kondisi OpenGL saat ini. Menurut dokumentasi
                OpenGL ES 2.0 itu juga menyediakan cara untuk OpenGL untuk
                memberi tau kami mengapa program saat ini mungkin tidak efisien
                gagal dijalankan dan sebagainya.
                */
                glValidateProgram(programObjectId);

                final int[] validateStatus = new int[1];
                glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);

                if (validateStatus[0] != 0) {
                    program = programObjectId;
                }
                else {
                    log = "Results of validating program: "+validateStatus[0]
                        +"\nLog: "+glGetProgramInfoLog(programObjectId);
                }
            }
        }
        if (program != 0) {
            glUseProgram(program);

            /*
            kita telah membuat konstan untuk nama uniform dan variabel
            untuk memegang lokasinya di object program OpenGL. Lokasi
            yang seragam tidak ditentukan sebelumnya, jadi kita harus
            menanyakan lokasi setelah program terhubung, Lokasi uniform
            adalah unik untuk object program bahkan jika kita memiliki nama
            uniform yang sama dalam dua program yang berbeda, itu tidak
            berarti bahwa mereka akan bebagi lokasi yang sama.

            kami memanggil glGetUniformLocation() untuk mendapatkan lokasi
            uniform kami, dan kami menyimpan lokasi itu di uColorLocation
            dan kami menggunakanya ketika ingin memperbarui nilai uniform nanti.
            */
            uColorLocation = glGetUniformLocation(program, U_COLOR);

            /*
            Mendapatkan lokasi attribute
                seperti dengan uniform kita juga perlu mendapatkan lokasi
            atribut kita sebelum kita dapat menggunakanya. Kita dapat membiarkan
            OpenGL menetapkan atribut ini ke nomor lokasi secara otomatis, atau
            kita dapat menetapkan angka-angka sendiri dengan panggilan ke glBindAttribLocation()
            sebelum kita menghubungkan shader bersama. Kami akan membiarkan OpenGL menetapkan
            lokasi atribut secara otomatis karena akan membuat kode lebih mudah dikelola.
            */
            aPositionLocation = glGetAttribLocation(program, A_POSITION);
        
            /*
            Mengaitkan Array of vertex data dengan attribut
                dikode teratas kita menciptakan array nilai floating point untuk
            mewakili posisi simpul yang membentuk table hoki. kami membuat buffer
            dalam memori native yang disebut vertexData dan menyalin posisi ini
            ke buffer ini.

            sebelum kami memberi tau OpenGL untuk membaca data dari buffer ini, kita
            perlu memastikan bahwa itu akan membaca data kita mulai dari awal dan bukan di
            tengah atau akhir, setiap buffer memiliki pointer internal yang dapat dipindahkan
            dengan memanggil position(int), dan ketika OpenGL membaca dari buffer kami, itu akan
            mulai membaca pada posisi ini. untuk memastikan bahwa ia mulai membaca dari awal
            kami memanggil position(0) untu mengatur posisi ke awal data.

            kemudian memanggil glVertexAttribPointer() untuk memberi tahu OpenGL yang dapat
            menemukan data untuk a_Position di buffer vertexData. Ini adalah fungsi yang sangat
            penting. Melewati argumen yang salah ke glVertexAtribPointer() dapat menyebabkan hasil
            yang aneh dan bahkan dapat menyebabkan program crash, jenis crash ini bisa sulit untuk
            dilacak jadi harus hati-hati dan baca dokumentasinya:

            glVertexAttribPointer(int index, int size, int type, boolean normalize, int stride, Buffer ptr)
            1. int index: ini adalah lokasi atribut dan kami melewati aposis untuk merujuk ke lokasi yang kami ambil
                          sebelumnya dalam mendapatkan lokasi atribut.
            2. int size: ini adalah jumlah data per atribut atau bebrapa banyak komponen yang dikaitkan dengan setiap
                          titik dengan setiap atribut ini. Pada saat kita mendefinikan struktur diatas kita memutuskan
                          untuk menggunakan dua nilai titik mengambang per vertex: koordinat X dan koordinat Y untuk
                          mewakili posisi, ini berarti bahwa kami memiliki dua komponen dan sebelumnya kami telah membuat
                          posisi POSITION_COMPONENT_COUNT untuk memuat fakta itu jadi kami meneruskan konstan itu disini
                          perhatikan bahwa kami hanya melewati dua komponen per vertex tetapi dalam shader a_Position
                          didefinisikan sebagai vec4 yang mewakili empat komponen. jika memang tidak ditentukan OpenGL
                          akan mengatur ketiga komponen pertama ke dan komponen terakhir ke 1 secara default.
            3. int type: ini adalah jenis data, kami mendefinisikan data kami sebagai daftar nilai titik apung
                          jadi kami menggunakan GL_FLOAT.
            4. boolean normalize: ini hanya berlaku jika kita menggunakan data integer jadi kita dapat mengabaikanya
                                  dengan aman untuk saat ini.
            5. int stride: berlaku ketika kita menyimpan lebih dari satu atribut dalam satu array. karena kami hanya
                            memiliki satu atribut dalam bab ini jadi kami dapat mengabaikan ini dan melewati 0 untuk saat ini.
            6. Buffer ptr: ini memberi tahu OpenGL dimana membaca data. dimana ia akan mulai membaca dari posisi buffer saat ini
                            jadi jika belum memanggil vertexData, position(0) mungkin akan mencoba membaca melewati akhir buffer dan
                            crash aplikasi kami.

            setelah memanggil glVertexAttribPointer() OpenGL sekarang tahu dimana membaca
            data untuk atribut a_Position.
            */

            vertexData.position(0);
            glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        
            /*
            Enabbling the vertex Array
                sekarang kami telah menghubungkan data kami ke attribut, kita
            perlu mengaktifkan atribut dengan panggilan glEnableVertexAttribArray()
            sebelum kita mulai menggambar. dengan panggilan terakhir ini OpenGL sekarang
            tahu dimana menemukan semua data yang dibutuhkan.

            pada bagian ini kami mengambil lokasi uniform u_Color dan attribut a_Position
            setiap variable memiliki lokasi dan OpenGL berfungsi dengan lokasi ini daripada
            dengan nama variabel secara langsung. Kami kemudian sebut glVertexAttribPointer()
            untuk memberi tau OpenGL supaya dapat menemukan data untuk attribut a_Position dari vertexData.


            */
            glEnableVertexAttribArray(aPositionLocation);
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

        /*
        dengan koneksi akhir ditempat kami sekarang siap untuk mulai
        menggambar ke layar, kami akan menggambar meja terlebih dahulu
        dan kemudian kami akan mengambar garis tengah dan mallets.

        pertama kita memperbarui nilai u_Color dalam kode header kami
        dengan memanggil glUniform4f(). Tidak seperti attribut, uniform
        tidak memiliki komponen default jadi jika uniform didefinisikan
        sebagai vec4 di shader kita perlu menyediakan keempat komponen
        kita ingin mulai memulai dengan menggambar meja putih, jadi kita
        mulai mengatur warna merah, hijau dan biru ke 1.0f untuk kecerahan
        penuh. Nilai alpha tidak masalah tetapi kami masih perlu menentukanya
        karena warna memiliki empat komponen.

        setelah kita menentukan warna kemudian menggambar meja dengan panggilan
        glDrawArrays(GL_TRIANGLES, 0, 6) argumen pertama memberi tau OpenGL kalau
        kita akan menggambar segitiga, untuk menggambar segitiga kita perlu melewati
        setidaknya tiga vertices(simpul). argumen kedua memberi tau OpenGL untuk membaca
        dalam vertices mulai dari awal array vertex kami dan argumen ketiga memberi tau
        OpenGL untuk membaca dalam enam vertices. Karena ada tiga vertices per segitiga
        panggilan ini akhirnya akan menggambar dua segitiga.

        Ingatlah bahwa ketika kami menyebut glVertexAttribPointer() kami memberi tau
        OpenGL bahwa setiap posisi titik vertex terdiri dari dua komponen floating point
        panggilan kami ke glDrawArrays() meminta OpenGL untuk menggambar segitiga menggunakan
        enam vertices(simpul) pertama, jadi OpenGL akan menarik mereka menggunakan posisi
        seperti diatas dalam variable tableVerticesWithTriangles.
        */
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);

        /*
        menggambar garis pemisah tengah ditengah meja
            kami mengatur warna menjadi merah dengan melewati 1.0f ke komponen
        pertama dan 0.0f menjadi hijau dan biru, kali ini kita juga meminta OpenGL
        untuk menggambar garis. kami mulai dengan enam vertices setelah vertices pertama
        dan meminta OpenGL untuk menggambar garis dengan membaca dalam dua vertices.
        Sama seperti array java kami menggunakan penomoran berbasis nol disini: 0,1,2,3,4,5,6
        berarti bahwa angan 6 sesuai dengan enam vertices setelah vertices pertama, atau
        vertice ketujuh. Karena ada dua vertices per baris kami akan berakhir menggambar satus baris
        menggunakan posisi seperti variblel tableVerticesWithTriangles pada koment line 1
        OpenGL akan menggambar garis dari (0,7) ke (9,7);
        */
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

        /*
        menggambar dua mallets
            kami meminta OpenGL untuk menggambar poin dengan melewati GL_POINTS ke
        glDrawArrays(). Untuk mallet pertama kami mengatur warna menjadi biru mulai
        dari offset 8 dan gambar satu titik menggunakan satu titik. untuk mallet kedua
        kami mengatur warna menjadi merah mulai dari offset 9 dan gambar satu titik menggunakan
        satu titik seperti pada variable tableVerticesWithTriangles pada koment mallets
        OpenGL akan menggambar titik pertama pada (4.5, 2) dan yang kedua (4.5, 12)
        */
        //draw the first mallet blue
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);

        //draw the second mallet red
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);

    }
}
