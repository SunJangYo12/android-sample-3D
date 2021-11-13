/*

* BAB 12
* Lighting of the world
*
*
* bug untuk tutorial terakhir entah kenapa highmap dan partikel tidak
* tampil dan setelah saya telusuri dilogcat tidak ditemukan error
*/

package com.gles.GameTerainLighting;

import android.util.Log;
import static android.opengl.GLSurfaceView.Renderer;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
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
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDepthMask;
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
import static android.opengl.Matrix.transposeM;
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
Simulasi cahaya telah menjadi topik penelitian utama di dunia grafis
perusahaan dan kita dapat melihat dampak penelitian ini tidak hanya dalam
peningkatan visual game yang stabil dari waktu ke waktu tetapi juga
didaerah-daerah seperti citra yang dihasilkan komputer (CGI) difilm tv.
    Penggunaan algoritma pencahayaan dapat membuat perbedaan besar dalam
cara penampilan scene(adegan). Hari dapat melewati malam, area dapat jatuh
kedalam bayangan dan gunung dapat mengungkapkan puncak, lembah dan celah,
bahkan adegan 2D dapat memanfaatkan sifat-sifat cahaya untuk hal-hal seperti
depth(kedalaman) visual, ledakan dan efek khusus lainya. Dalan bab ini kita
akan belajar bagaimana menggunakan beberapa algoritma pencahayaan sederhana
untuk menambah beberapa kontras dengan pegunungan kita dan kemudian kita
akan menggelapkam ke latar belakang malam dan membuat air mancur partikel bersinar.
Rencana dalam bab ini sebagai berikut:
    1. kami pertama-tama akan belajar bagaimana menerapkan refleksi difus menggunakan
       sumber cahaya arah, sehingga muncul seolah-olah adegan itu menyala oleh
       matahari di skybox dan kemudian kita akan belajar cara meminimalkan bayangan
       gelap dengan menambahkan beberapa pencahayaan ambient.
    2. kami kemudian akan mengaktifkan skybox kami untuk skybox yang lebih gelap dan
       menolak kecerahan, dan kami akan belajar cara menggunakan lampu titik untuk
       menyalakan setiap air mancur partikel. 

Simulating Efek of light (mensimulasikan efek cahaya)
    ketika kita melihat dunia disekitar kita, kita benar-benar melihat efek
kumulatif triliunan pada triliunan partikel kecil disebut foton. foton-foton
ini dipancarkan oleh sumber energi seperti matahari setelah berpergian jauh,
mereka akan memantul beberapa benda dan dibiaskan oleh orang lain, sampai
mereka akhirnya mengenai retina dimata kita. mata dan otak kita mengambilnya
dari sana dan merekontruksi aktivitas semua foton ini ke dunia yang biasa kita
lihat disekitar kita.
    Grafis komputer secara historis disimulasikan efek cahaya dengan mensimulasikan
perilaku foton aktual atau dengan menggunakan pintasan untuk memalsukan perilaku itu.
salah satu cara mensimulasikan perilaku foton aktual adalah dengan pelacak sinar.
sebuah pelacak sinar mensimulasikan foton dengan memotret sinar ke dalam adegan dan
menghitung bagaimana sinar-sinar itu berinteraksi dengan object dalam adegan. teknik
ini cukup kuat dan dapat meminjamkan diri dengan baik untuk refleksi dan reflaksi yang
benar-benar baik dan efek khusus lainya seperti kuastik (pola yang anda lihat
ketika cahaya melewati air).
    Sayangnya ray(cahaya) tracking biasanya terlalu mahal untuk digunakan untuk
penentuan realtime. Sebaliknya sebagian besar game dan aplikasi menyederhanakan
hal-hal dan memperkirakan cara cahaya itu bekerja pada tingkat yang lebih tinggi
dari pada mensimulasikanya secara langsung. Algoritma pencahayaan sederhana dapat
berjalan jauh dan ada juga cara memalsukan refraksi dan banyak lagi. teknik-teknik
ini dapat menggunakan OpenGL untuk menempatkan sebagian besar beban kerja pada GPU
dan menjalankan blazing dengan cepat bahkan diponsel.

Using Light in OpenGL
    untuk menambahkan cahaya ke scene (adegan) OpenGL kita biasanya dapat mengatur
sumber cahaya yang berbeda kedalam kelompok-kelompok berikut:
1. Ambient Cahaya: Lampu ambient tampaknya berasal dari segala arah menyalakan
   segala sesuatu ditempat yang sama. Ini mendekati jenis pencahayaan yang kita
   dapatkan dari sumber cahaya yang besar dan sama, seperti langit dan cahaya
   sekitar juga dapat digunakan untuk memalsukan cara cahaya dapat memantul banyak
   object sebelum mencapai mata kita, jadi bayangan biasanya tidak berwarna hitam
2. Cahaya directional (cahaya arah): cahaya ini tampak dari satu arah, seolah-olah
   sumber cahaya sangat jauh. ini mirip dengan jenis pencahayaan yang kita dapat
   dari matahari atau bulan.
3. Cahaya point (titik): cahaya ini tampak casting mereka dari suatu tempat didekatnya
   dan intensitas cahaya berkurang dengan jarak, in bagus untuk menghargai sumber
   cahaya yang dekat yang melampirkan cahaya mereka ke segala arah seperti bola lampu
   atau lilin.
4. Spot light (lampu tempat): cahaya spot mirip dengan pencahayaan titik dengan
   pembatasan tambahan fokus kearah tertentu. ini adalah jenis cahaya yang akan kita
   dapatkan dari senter atau cahaya spot.
kami juga dapat mengelompokan cara cahaya itu memantulkan object menjadi dua katagori
utama:
1. diffuse reflection: cahaya ini menyebar secara merata ke segala arah dan baik untuk
   mewakili bahan dengan permukaan yang tidak dipoles seperti karpet atau dinding beton
   jenis permukaan ini muncul serupa dari berbagai sudut pandang.
2. Specular reflection: cahaya ini mencerminkan lebih kuat kedalam arah tertentu dan baik
   untuk bahan yang dipoles atau menkilap seperti logam atau mobil baru.
banyak bahan menggabunggkan aspek keduanya, ambil permukaan jalan, misalnya sebuah jalan
umumya muncul sama dari berbagai arah tetapi ketika matahari rendah dilangit dan kondisinya
benar, satu arah dapat mencerminkan sinar matahari yang cukup untuk menunjukan pengemudi
yang menyilaukan dan menyebabkan bahanya jalan.

Menerapkan cahya terarah dengan refleksi lambertian
    Untuk menerapkan diffuse reflection kita dapat mengunakan teknik sederhana yang dikenal
sebagai reflektansi lambertian. dinamai setelah john heinrich lambertian, seorang ahli
matematika dan astronom swiss yang hidup pada abad 18, refeksi lambertian menggambarkan
permukaan yang mencerminkan cahaya memukulnya kesegala arah, sehingga muncul sama dari
semua sudut pandang penampilannya hanya bergantung pada orientasi dan jaraknya dari sumber cahaya.
    mari kita lihat contoh permukaan datar dan satu cahaya directional yang tidak berkurang
dengan jarak, jadi satu-satunya hal yang penting adalah orientasi permukaan sehubungan dengan
cahaya. pada gambar 64 page:256 kita dapat melihat permukaan yang menghadap kepala sumber cahaya.
pada sudut ini itu menangkap dan merefleksikan cahaya sebanyak mungkin. dalam angka selanjutnya
permukaan kini telah diputar 45 derajat sehubungan dengan sumber cahayanya sehingga tidak lagi
dapat menangkap dan mencerminkan sebanyak mungkin cahaya.
    Seberapa sedikit cahaya yang dicerminkan pada 45 derajat? jika kita mengukur penampang. kita
akan melihat bahwa itu hanya seitar 0.707 kali lebih lebar seperti permukaanya tidak diputar.
hubungan ini mengikuti cosinus sudut.

        |   |   |   |   |   |   |   | 
        |   |   |   |   |   |   |   | 
        |   |   |   |   |   |   |   | 
        |   |   |   |   |   |   |   | 
        |   |   |   |   |   |   |   | 
        |   |   |   |   |   |   |   | 
        |   |   |   |   |   |   |   | 
        |   |   |   |   |   |   |   | 
       _V___V___V___V___V___V___V___V__
                     0°

        |----------1.00-------------|

        Gambar 64: A surface direcly faccing a light score

Lihat gambar 43 page 148 unit circle, dan untuk mengetahui seberapa besar cahaya yang
diterima permukaan, yang perlu kita lakukan adalah mencari tahu seberapa banyak cahaya
yang akan diterima jika menhadap cahaya secara langsung dan kemudian mengalikan dengan
cosinus sudut.
Sebagai contoh: jika permukaan lambertian biasanya akan mencerminkan 5 lumen cahaya dari
sumber cahaya arah ketika pada 0°, maka itu akan mencerminkan (5 * cos 45°) = ~3.5 lumen
cahaya saat berorientasi pada cahaya 45° sehubungan dengan cahaya memahami hubungan ini
adalah semua yang perlu kita ketahui untuk memahami refleksi lambertian.
Gambar 65 page:256

Menghitung orientasi Heightmap
    sebelum kita bisa menambahkan refleksi lambertian ke heighmap, kita perlu
cara mengetahui apa yang orientasi permukaanya. karena heightmap bukanlah permukaan
yang rata kita harus menghitung orientasi ini untuk mewakili titik pada heighmap
kita dapa mewakili orientasi dengan menggunakan permukaan normal, jenis vector khusus
yang tegak lurus terhadap permukaan dan mewakili panjang satuan 1, kami pertama kali
belajar tentang ini kembali di page 177.
    karena permukaan normal digunakan untuk permukaan dan bukan untuk poin, kami akan
menghitung normal untuk setiap titik dengan bergabung bersama-sama menunjuk ke tempat
tetangga untuk membuat plane. kami akan mewakili plane ini dengan dua vector satu dari
titik kanan ke titik kiri, dan lainya dari titik atas ke titik bawah. jika kita menghitung
produk lintas dari dua vector ini kita akan mendapatkan vector tegak lurus terhadap plane
dan kita kemudian dapat menormalkan vector itu untuk mendapatkan permukaan normal untuk
titik tengah. lihat contoh angka page:257. katakanlah setiap titik ditempatkan satu unit
terpisah dengan X meningkat ke kanan dan z meningkat ke bawah. ketinggian untuk bagian
atas, kiri, kanan dan bawah masing-masing adalah 0.2, 0.1, 0.1 dan 0.1 untuk menghitung vector
pergi dari kanan ke kiri kami kurangi titik kiri dan titik kanan untuk mendapatkan vector
(-2, 0, 0) dan kami melakukan hal yang sama dengan titik atas dan bawah untuk mendapatkan
vector (0, 0.1, 2) setelah kami memiliki dua vector ini kami menghitung produk lintas mereka
untuk mendapatkan vector (0, 4, 0, 2) dan kami normalkan vector itu untuk mendapatkan permukaan
normal (0, 0.9988, 0.05).
    kenapa kita menggunakan vector dari kanan ke kiri dan tidak sebaliknya? kami ingin
permukaan normal menunjuk keatas jauh dari heightmap jadi kami menggunakan aturan kanan
produk lintas untuk mencari tau ke arah mana setiap vector perlu berada ke arah yang benar.
sekarang kita tau harus berbuat apa mari kita buka kelas HeighMap kita dan tambahkan konstanta.
*/

public class GameTerainLighting implements Renderer
{
    private int tutorial = 0;
    public static final int BYTES_PER_FLOAT = 4;
    public static final int BYTES_PER_SHORT = 2;
    private Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

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

    /*
    Melihat arah cahaya kita dalam scene (aksi)
        vector ini menunjukan sekitar ke arah matahari di skybox. Anda dapat
    membayangkan hasil yang serupa dengan langkah-langkah ini:
    1. buat vector yang menunjuk kearah (0, 0, -1) yaitu menunjuk lurus
       ke depan.
    2. putar vector ini diarah balik rotasi pemandangan.
    3. tambahkan pernyataan logging untuk mencetak arah saat ini dari vector
       dan kemudian jalankan aplikasi dan putar scene sampai matahari
       berpusat di tengah layar.
    */
    private Geometry.Vector vectorToLight = new Geometry.Vector(0.61f, 0.64f, -0.47f).normalize();

    //tutorial 4
    /*
    kenapa definisi xvectorToLight menyimpanya dalam array floating point
    yang polos? Kami juga menyimpan posisi dan warna untuk setiap lampu pada
    array masing-masing dengan posisi dan warna secara kasar yang cocok
    dengan posisi dan warna. warna yang kami berikan ke setiap penembak
    partikel yang membedakan masing-masing point light dinaikan satu unit
    diatas penembak partikelnya dan karena teran itu sendiri berwarna hijau
    lampu hijau agak redup sehingga tidak terlalu meningkatkan cahaya merah
    dan cahaya biru.
    */
    private final float[] modelViewMatrix = new float[16];
    private final float[] it_modelViewMatrix = new float[16];
    final float[] xvectorToLight = { 0.30f, 0.35f, -0.89f, 0f };

    private final float[] pointLightPositions = new float[] {
        -1f, 1f, 0f, 1f,
         0f, 1f, 0f, 1f,
         1f, 1f, 0f, 1f
    };
    private final float[] pointLightColors = new float[] {
        1.00f, 0.20f, 0.02f,
        0.02f, 0.25f, 0.02f,
        1.02f, 0.20f, 1.00f
    };


    public GameTerainLighting() {
    }
    public GameTerainLighting(Context context, int tutorial) {
        this.context = context;
        this.tutorial = tutorial;
    }

    public GameTerainLighting(Context context) 
    {
        this.context = context;
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        
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
        
        if (tutorial >= 3) {
            skyboxTexture = TextureHelper.loadCubeMap(context, new int[] {
                R.drawable.night_left, R.drawable.night_right,
                R.drawable.night_bottom, R.drawable.night_top,
                R.drawable.night_front, R.drawable.night_back
            });

            /*
            vector baru ini menunjuk ke bulan diskybox
            */
            vectorToLight = new Geometry.Vector(0.30f, 0.35f, -0.89f).normalize();
        }
        else {
            skyboxTexture = TextureHelper.loadCubeMap(context, new int[] {
                R.drawable.left, R.drawable.right,
                R.drawable.bottom, R.drawable.top,
                R.drawable.front, R.drawable.back
            });
        }

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
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawHeightmap();
        drawSkybox();
        drawParticles();
    }

    private void drawSkybox() {
        glDepthFunc(GL_LEQUAL);

        setIdentityM(modelMatrix, 0);
        updateMvpMatrixForskybox();
        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(modelViewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
        glDepthFunc(GL_LESS);
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticle(particlesSystem, currentTime, 5);
        greenParticleShooter.addParticle(particlesSystem, currentTime, 5);
        blueParticleShooter.addParticle(particlesSystem, currentTime, 5);

        setIdentityM(modelMatrix, 0);
        updateMvpMatrix();

        glDepthMask(false);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particlesProgram.useProgram();
        particlesProgram.setUniforms(modelViewProjectionMatrix, currentTime, texture);
        particlesSystem.bindData(particlesProgram);
        particlesSystem.draw();

        glDisable(GL_BLEND);
        glDepthMask(true);
    }

    private void drawHeightmap() {
        setIdentityM(modelMatrix, 0);
        scaleM(modelMatrix, 0, 100f, 10f, 100f);
        updateMvpMatrix();
        heightmapProgram.useProgram();

        if (tutorial >= 1) 
        {
            if (tutorial >= 4) 
            {
                /*
                kita perlu meletakan vector cahaya directional dan posisi cahaya
                pada ruang mata, dan untuk melakukan ini kita menggunakan kelas
                matrix android untuk mengalikanya dengan matrix tampilan. posisi
                sudah ada diruang dunia jadi tidak perlu juga melipatgandakan mereka
                dengan matrix model sebelumnya. Setelah selesai kami meneruskan semua
                data ke shader dengan panggilan heighmapProgram.setUniform().
                */
                final float[] vectorToLightInEyeSpace = new float[4];
                final float[] pointPositionsInEyeSpace = new float[12];

                multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix, 0, xvectorToLight, 0);
                multiplyMV(pointPositionsInEyeSpace, 0, viewMatrix, 0, pointLightPositions, 0);
                multiplyMV(pointPositionsInEyeSpace, 4, viewMatrix, 0, pointLightPositions, 4);
                multiplyMV(pointPositionsInEyeSpace, 8, viewMatrix, 0, pointLightPositions, 8);
            
                heightmapProgram.setUniforms(modelViewMatrix, it_modelViewMatrix,
                                             modelViewProjectionMatrix, vectorToLightInEyeSpace,
                                             pointPositionsInEyeSpace, pointLightColors);
            }
            else {
                /*
                kami juga normalize (menormalkan) vector sehingga kami dapat meneruskan
                ke shader dan menggunakanya untuk menghitung relfleksi lambertian.
                */
                heightmapProgram.ysetUniforms(modelViewProjectionMatrix, vectorToLight);
            }
        }
        else {
            heightmapProgram.xsetUniforms(modelViewProjectionMatrix);
        }
        heightMap.bindData(heightmapProgram);
        heightMap.draw();
    }

    private void updateViewMatrixs() {
        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        System.arraycopy(viewMatrix, 0, viewMatrixForSky, 0, viewMatrix.length);

        translateM(viewMatrix, 0, 0, -1.5f, -5f);
    }

    private void updateMvpMatrix() {
        if (tutorial >= 4) 
        {
            /*
            ini menetapkan modelViewMatrix ke modelview matrix dan it_modelViewMatrix
            ke transpose dari kebalikan dari matrix itu.
            */
            multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelViewMatrix, 0);
            invertM(tempMatrix, 0, modelViewMatrix, 0);
            transposeM(it_modelViewMatrix, 0, tempMatrix, 0);
            multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
        }
        else {
            multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0);
            multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
        }
    }
    private void updateMvpMatrixForskybox() {
        multiplyMM(tempMatrix, 0, viewMatrixForSky, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }

    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;
        
        if (yRotation < -90)
            yRotation = -90;
        else if(yRotation > 90)
            yRotation = 90;

        updateViewMatrixs();

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
            sekarang setelah heightmap kami mencakup normals, tugas kami
            selanjutnya adalah memperbarui heightmap shader dan menambahkan
            dukungan untuk penerangan arah.
            */
            StringBuffer strbuffHMV = new StringBuffer();

            if (tutorial >= 1) {
                /*
                vector ini akan berisi vector yang dinormalisasi ke arah sumber
                cahaya arah kami. kami juga akan memerlukan atribut baru untuk
                heightmap normal. vector ini akan berisi vector yang dinormalisasi
                ke arah sumber cahaya arah kami.
                */
                strbuffHMV.append("uniform vec3 u_VectorToLight;\n");
                strbuffHMV.append("attribute vec3 a_Normal;\n");
            }
            strbuffHMV.append("uniform mat4 u_Matrix;\n");
            strbuffHMV.append("attribute vec3 a_Position;\n");
            strbuffHMV.append("varying vec3 v_Color;\n");
            strbuffHMV.append("void main()         \n");           
            strbuffHMV.append("{   \n");
            strbuffHMV.append("    v_Color = mix(vec3(0.180, 0.467, 0.153),    // A dark green \n");
            strbuffHMV.append("                  vec3(0.660, 0.670, 0.680),    // A stony gray \n");
            strbuffHMV.append("                  a_Position.y);\n");

            if (tutorial >= 1) {
                /*
                anda mungkin ingat bahwa ketika kita menggambar heighmap, kita saat ini
                memperluasnya dengan menggunakan scaleM() untuk membuatnya sepuluh kali
                lebih tinggi dan seratus kali lebih lebar jadi dengan kata lain heightmap
                sekarang lebih dari itu. Sementara hal-hal dengan cara ini mengubah bentuk
                heightmap, yang berarti bahwa normals kami telah diselamatkan tidak lagi
                benar. untuk mengimbangi ini kita mengukur normal ke arah yang berlawanan
                membuat normal sepuluh kali lebih tinggi dari lebar. setelah menjalin ulang
                normal sekarang akan cocok dengan geometri baru.
                    alasan ini bekerja melibatkan beberapa matematika lanjutan jadi anda
                harus mempercayai ini. kemudian kita akan melihat cara yang lebih umum untuk
                menyesuaikan normals dalam penambahan titik ke shader pada page:265
                dibawah ini kita telah menyesuaikan permukaan normal.
                */
                strbuffHMV.append("    vec3 scaledNormal = a_Normal;\n");
                strbuffHMV.append("    scaledNormal.y *= 10.0;\n");
                strbuffHMV.append("    scaledNormal = normalize(scaledNormal);\n");

                /*
                sekarang kita hitung refleksi lambertian
                    untuk menghitung sudut cosinus antara permukaan dan cahaya, kami menghitung
                produk titik vector ke cahaya dan permukaan normal. alasan ini bekerja adalah karena
                ketika dua vector dinormalisasi, produk dot dari kedua vector itu akan memberi kita
                cosinus sudut di antara mereka, yang persis apa yang kita butuhkan untuk menghitung
                refleksi lambertian. untuk menghindari hasil negatif kami clamp(menjepit) cosinus minimum 0
                dengan max() dan kemudian kami menerapkan pencahayaan dengan mengalikan warna titik
                saat ini dengan cosinus. cosinus akan diantara 0 dan 1 jadi warna terakhir akan berada
                diantara warna hitam dan warna asli.
                */
                strbuffHMV.append("    float diffuse = max(dot(scaledNormal, u_VectorToLight), 0.0);\n");
                
                if (tutorial >= 3) {
                    /*
                    kita perlu menurunkan kekuatan cahaya
                    */
                    strbuffHMV.append("    diffuse *= 0.3;\n");
                }

                strbuffHMV.append("    v_Color *= diffuse;\n");

                if (tutorial >= 2) {
                    /*
                    kita sekarang dapat melihat bentuk dan bentuk pegunungan tetapi anda mungkin
                    akan melihat bahwa area gelap terlalu gelap. masalahnya adalah kita tidak
                    memiliki iluminasi global, dalam kehidupan nyata cahaya berdifusi melalui langit
                    dan memantulkan banyak benda sebelum mencapai mata kita jadi bayangan yang
                    dilemparkan oleh matahari tidak ada didekat pitch black. kita dapat memalsukan ini
                    dikancah kami dengan menambahkan level cahaya sekitar yang akan berlaku sama untuk
                    semuanya. ini akan menambah tingkat dasar pencahayaan ke seluruh heightmap jadi
                    tidak ada yang tampak terlalu gelap.
                    */
                    strbuffHMV.append("    float ambient = 0.2;\n");

                    if (tutorial >= 3) {
                        /*
                        kita juga harus menurunkan pencahayaan sekitar.
                        */
                        strbuffHMV.append("   ambient = 0.1;\n");
                    }

                    strbuffHMV.append("    v_Color += ambient;\n");
                }
            }

            strbuffHMV.append("    gl_Position = u_Matrix * vec4(a_Position, 1.0);    \n");
            strbuffHMV.append("}\n");

            /*
            memahami sumber cahaya titik
                karena kita berpegang teguh pada reflektansi difusi dengan model reflektansi
            lambertian, math untuk lampu titik kita akan mirip dengan cahaya arah kita. namun
            ada beberapa perbedaan utama yang perlu kita ingat:
            1. untuk cahaya terarah kami hanya menyimpan vector untuk cahaya itu karena vector
               vector itu sama untuk semua titik dalam sebuah adegan. untuk cahaya titik kami
               kami akan menyimpan posisi dan kami akan menggunakan posisi itu untuk menghitung
               vector ke titik cahaya untuk setiap titik ditempat kejadian.
            2. dalam kehidupan nyata kecerahan sumber cahaya titik cenderung menurun dengan
               kuadrat jarak, ini dikenal sebagai hukum kuadarat terbalik. kami akan menggunakan
               posisi titik cahaya untuk mencari tahu jarak untuk setiap titik ditempat kejadian.
            
            menambahkan pencahayaan titik ke shader.
                untuk menerapkan lampu titik kita harus membuat beberapa perubahan pada shader kita
            dan kita akan menggunakan kesempatan ini untuk mengambil pendekatan yang lebih terstruktur
            dan umum untuk pencahayaan dishader. mari kita lihat perubahan paling penting:
            1. kami akan menempatkan posisi dan normals kami ke ruang mata, ruang dimana semua posisi
               dan normals relatif terhadap posisi dan orientasi kamera, kami akan melakukan ini
               sehingga kami dapat membandingkan jarak dan orientasi dengan segala sesuatu diruang
               koordinat yang sama. alasan mengapa kami menggunakan ruang mata daripada ruang dunia
               adalah karena pencahayaan speculer juga tergantung pada posisi kamera dan meskipun kami
               tidak menggunakan pencahayaan speculer dalam bab ini, masih ide yang baik untuk
               mempelajari cara menggunakan ruang mata sehingga kita dapat menggunakanya di masa depan.
            2. untuk menempatkan posisi ke dalam ruang mata kami hanya mengalikanya dengan matrix model
               untuk memasukanya kedalam ruang dunia, dan kemudian kami mengalikanya dengan matrix
               tampilan untuk mendapatkanya keruang mata. Untuk menyederhanakan hal-hal kita dapat
               mengalikan matrix tampilan dengan matrix model untuk mendapatkan satu matrix yang
               disebut matrix modelview dan kami menggunakan matrix itu untuk menempatkan posisi kami
               keruang mata.
            3. ini juga berfungsi untuk normals jika matrix model tampilan hanya berisi translate dan rotate
               tetapi bagaimana jika kita juga meningkatkan object? Jika skalanya dilakukan secara merata
               ke segala arah maka kita hanya perlu memperkenalkan kambali normalisasi sehingga panjangnya
               tetap 1 tetapi jika obect itu juga diratakan dalam satu arah maka kita harus mengkompensasi
               hal itu juga.
            ketika kami menambahkan pencahayaan arah, kami tahu persis berapa banyak heightmap yang ditingkatkan
            jadi kami dapat secara langsung mengkompensasi untuk itu. ini buka solusi yang fleksibel dan cara
            umum melakukan ini adalah dengan membalikan matrix modelview, mentransposikan matrix terbalik,
            multipling normal dengan matrix itu, dan kemudian normalizing hasilnya. Alasan ini bekerja melibatkan
            beberapa math lanjutan jika anda ingin tahu ada beberapa penjelasan hebat yang dapat anda akses
            dilink ini http://arcsynthesis.org/gltut/index.html dan http://www.cs.uaf.edu/2007/spring/cs481/lecture/01_23_matrices.html
            */
            if (tutorial >= 4) {
                strbuffHMV.delete(0, strbuffHMV.length()); //hapus semua string diatas untuk mengganti shader

                /*
                kami sekarang akan menggunakan u_MVMatrix untuk mewakili matrix modelview
                u_IT_MVMatrix untuk mewakili transpos dari kebalikan dari matrix itu dan
                uMVPMatrix untuk mengiril model proyeksi tampilan model gabungan seperti
                yang kami lakukan dengan u_Matrix sebelumnya.
                */
                strbuffHMV.append("uniform mat4 u_MVMatrix;\n");
                strbuffHMV.append("uniform mat4 u_IT_MVMatrix;\n");
                strbuffHMV.append("uniform mat4 u_MVPMatrix;\n");

                /*
                vector cahaya directional tetap sama seperti sebelumnya kecuali bahwa kita sekarang
                mengharapkaya berada diruang mata. kami melewati posisi titik cahaya dengan u_PointLightPosition
                yang juga diruang mata dan kami meneruskan warna dengan u_PointLightColors.
                Dua uniform terakhir ini didefinisikan sebagai array sehingga kita dapat melewati beberapa
                vector melelui satu uniform.
                */
                strbuffHMV.append("uniform vec3 u_VectorToLight;\n");           //in eye space
                strbuffHMV.append("uniform vec3 u_PointLightColors[3];\n");
                strbuffHMV.append("uniform vec4 u_PointLightPositions[3];\n");   //in eye space

                /*
                untuk atribute kami sekarang mewakili posisi sebagai vec4, mengurangi jumlah konversi
                vec3 dan vec4 diperlukan. kami tidak perlu mengubah data vertex kami karena OpenGL
                akan menggunakan default 1 untuk komponen keempat tetapi hati-hati, uniform tidak
                bekerja dengan cara yang sama dan mereka harus memiliki semua komponen yang ditentukan.
                */
                strbuffHMV.append("attribute vec4 a_Position;\n");
                strbuffHMV.append("attribute vec3 a_Normal;\n");

                /*
                untuk varying kami tetap sama seperti sebelumnya, kami menambahkan beberapa
                variable baru yang akan kami gunakan untuk menghitung pencahayaan, dan kami
                juga memiliki deklarasi ulang untuk tiga fungsi baru yang akan kami definisikan
                nanti dishadder.
                */
                strbuffHMV.append("varying vec3 v_Color;\n");

                strbuffHMV.append("vec3 materialColor;\n");
                strbuffHMV.append("vec4 eyeSpacePosition;\n");
                strbuffHMV.append("vec3 eyeSpaceNormal;\n");

                strbuffHMV.append("vec3 getAmbientLighting();\n");
                strbuffHMV.append("vec3 getDirectionLighting();\n");
                strbuffHMV.append("vec3 getPointLighting();\n");

                /*
                di fungsi main kami menetapkan warna dan bahan seperti sebelumnya, dan kemudian
                kami menghitung posisi saat ini dan normalize di ruang mata. kami kemudian
                menghitung setiap jenis cahaya, menambahkan warna hasil ke v_Color dan
                kemudian kami memproyeksikan posisi seperti sebelumnya.
                */
                strbuffHMV.append("void main() {\n");
                strbuffHMV.append("   materialColor = mix(vec3(0.180, 0.467, 0.153),\n");
                strbuffHMV.append("                       vec3(0.660, 0.670, 0.680), a_Position.y);\n");
                strbuffHMV.append("   eyeSpacePosition = u_MVMatrix * a_Position;\n");

                /*
                model normals perlu disesuaikan sesuai transpose
                kebalikan dari matrix modelview.
                */
                strbuffHMV.append("   eyeSpaceNormal   = normalize(vec3(u_IT_MVMatrix * vec4(a_Normal, 0.0)));\n");
                strbuffHMV.append("   v_Color  = getAmbientLighting();\n");
                strbuffHMV.append("   v_Color += getDirectionLighting();\n");
                strbuffHMV.append("   v_Color += getPointLighting();\n");
                strbuffHMV.append("   gl_Position = u_MVPMatrix * a_Position;\n");
                strbuffHMV.append("}\n");

                /*
                kedua fungsi ini menghitung pencahayaan ambien dan arah seperti yang kami
                lakukan sebelumnya.
                */
                strbuffHMV.append("vec3 getAmbientLighting() {\n");
                strbuffHMV.append("   return materialColor * 0.1;\n");
                strbuffHMV.append("}\n");

                strbuffHMV.append("vec3 getDirectionLighting() {\n");
                strbuffHMV.append("   return materialColor * 0.3 * max(dot(eyeSpaceNormal, u_VectorToLight), 0.0);\n");
                strbuffHMV.append("}\n");

                /*
                cara kerjanya adalah bahwa kita loop melalui setiap titik cahaya, menghitung
                pencahayaan untuk masing-masing dan menambahkan hasilnya pada pencahayaan.
                kode ini menghitung level cahaya dengan reflektansi lambertian seperti halnya
                pencahayaan arah sebelumnya tetapi ada beberapa perbedaan penting:
                1. untuk setiap titik cahaya kami menghitung vector dari posisi saat ini ke
                   cahaya itu dan kami juga menghitung jarak dari posisi saat ini ke cahaya itu.
                2. setelah kami memiliki vector yang dinormalisasi kami dapat menghitung reflektansi
                   lambertian. kami kemudian mengalikan warna bahan dengan warna cahaya untuk
                   menerapkan warna itu ke titik saat ini. kami mengukur hasilnya dengan 5 untuk
                   membuat segalanya sedikit lebih cerah dan kemudian kami mengalikanya dengan
                   cosinus untuk menerapkan reflektansi lambertian.
                3. sebelum menambahkan hasil pencahayaan kami mengurangi intensitas cahaya dengan
                   jarak dengan membagi hasil dari langkah sebelumnya dengan jarak.
                */
                strbuffHMV.append("vec3 getPointLighting() {\n");
                strbuffHMV.append("   vec3 lightingSum = vec3(0.0);\n");
                strbuffHMV.append("   for (int i=0; i<3; i++) {\n");
                strbuffHMV.append("      vec3 toPointLight = vec3(u_PointLightPositions[i]) - vec3(eyeSpacePosition);\n");
                strbuffHMV.append("      float distance = length(toPointLight);\n");
                strbuffHMV.append("      toPointLight   = normalize(toPointLight);\n");
                strbuffHMV.append("      float cosine  = max(dot(eyeSpaceNormal, toPointLight), 0.0);\n");
                strbuffHMV.append("      lightingSum  += (materialColor * u_PointLightPositions[i] * 5.0 * cosine) / distance;\n");
                strbuffHMV.append("   }\n");
                strbuffHMV.append("   return lightingSum;\n");
                strbuffHMV.append("}\n");

                /*
                Sifat nonliniear dari tampilan anda
                    Pencahayaan dan warna terkadang sulit untuk mendapatkan hak di OpenGL
                karena perbedaan pendapat antara OpenGL dan tampilan anda. Untuk OpenGL
                warna terletak pada spektrum linear sehingga nilai warna 1.0 dua kali lebih
                terang dari nilai warna 0.5 namun karean sifat nonlinear dari banyak tampilan
                perbedaan aktual kecerahan pada layar anda bisa jauh lebih besar dari ini.
                    Alasan hal-hal bekerja dengan cara ini sebagian karena sejarah. sekali
                waktu kita semua menggunakan monitor CRT yang besar sebagai tampilan utama kami
                dan monitor ini bekerja dengan memotret balok elektron pada layar fosfor.
                fosfor-fosfor ini cenderung memiliki respon exponensial daripada respon linear
                sehingga 1.0 jauh lebih dari dua kali lebih terang dari 0.5 untuk kompatinilitas
                dan alasan lain banyak tampilan mempertahankan perilaku serupa hari ini.
                    Perilaku nonlinear ini dapat memucat pencahayaan kita membuat segalanya tampak
                lebih gelap dari yang seharusnya. Biasanya falling off ringan harus dilakuan dengan
                membagi intensitas dengan jarak kuadrat tetapi untuk menjaga lampu titik kami jatuh
                terlalu cepat kita dapat menghapus exponen dan hanya membagi dengan jarak.
                */
                String oke = ""+
"uniform mat4 u_MVMatrix;\n"+
"uniform mat4 u_IT_MVMatrix;\n"+
"uniform mat4 u_MVPMatrix;\n"+


"uniform vec3 u_VectorToLight;\n"+             

"uniform vec4 u_PointLightPositions[3];\n"+    
"uniform vec3 u_PointLightColors[3];\n"+


"attribute vec4 a_Position;\n"+
"attribute vec3 a_Normal;\n"+


"varying vec3 v_Color;\n"+
"vec3 materialColor;\n"+
"vec4 eyeSpacePosition;\n"+
"vec3 eyeSpaceNormal;\n"+

"vec3 getAmbientLighting();\n"+
"vec3 getDirectionalLighting();\n"+
"vec3 getPointLighting();\n"+
"void main()          \n"+          
"{    \n"+
                     
    
"    materialColor = mix(vec3(0.180, 0.467, 0.153),    // A dark green\n"+ 
"                        vec3(0.660, 0.670, 0.680),    // A stony gray \n"+
"                        a_Position.y);\n"+
"    eyeSpacePosition = u_MVMatrix * a_Position;   \n"+                          
                             
"    eyeSpaceNormal = normalize(vec3(u_IT_MVMatrix * vec4(a_Normal, 0.0)));   \n"+
                                                                                  
"    v_Color = getAmbientLighting();\n"+
"    v_Color += getDirectionalLighting();   \n"+                                                                                                               
"    v_Color += getPointLighting();\n"+
        
"    gl_Position = u_MVPMatrix * a_Position;\n"+
"}  \n"+
"vec3 getAmbientLighting() \n"+
"{    \n"+
"    return materialColor * 0.1;   \n"+   
"}\n"+

"vec3 getDirectionalLighting()\n"+
"{   \n"+
"    return materialColor * 0.3 \n"+
"         * max(dot(eyeSpaceNormal, u_VectorToLight), 0.0);     \n"+  
"}\n"+
"vec3 getPointLighting()\n"+
"{\n"+
"    vec3 lightingSum = vec3(0.0);\n"+
    
"    for (int i = 0; i < 3; i++) {                  \n"+
"        vec3 toPointLight = vec3(u_PointLightPositions[i]) \n"+
"                          - vec3(eyeSpacePosition);          \n"+
"        float distance = length(toPointLight);\n"+
"        toPointLight = normalize(toPointLight);\n"+
        
"        float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0); \n"+
"        lightingSum += (materialColor * u_PointLightColors[i] * 5.0 * cosine) \n"+
"                       / distance;\n"+
"    }  \n"+
    
"    return lightingSum;       \n"+
"}\n";
                return oke;
            }
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
            .allocateDirect(vertexData.length * GameTerainLighting.BYTES_PER_FLOAT)
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

class VertexBuffer {
    private final int bufferId;

    public VertexBuffer(float[] vertexData) 
    {
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
            .allocateDirect(vertexData.length * GameTerainLighting.BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData);

        vertexArray.position(0);

        //transfer data from native memory to the GPU buffer
        glBufferData(GL_ARRAY_BUFFER, vertexArray.capacity() * GameTerainLighting.BYTES_PER_FLOAT, vertexArray, GL_STATIC_DRAW);
    
        //PENTING: unbind dari buffer saat kita selesai dengan itu.
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride)
    {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, dataOffset);
        glEnableVertexAttribArray(attributeLocation);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}

class IndexBuffer {
    private final int bufferId;

    public IndexBuffer(short[] indexData) 
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
        ShortBuffer indexArray = ByteBuffer
            .allocateDirect(indexData.length * GameTerainLighting.BYTES_PER_SHORT)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(indexData);

        indexArray.position(0);

        //transfer data from native memory to the GPU buffer
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexArray.capacity() * GameTerainLighting.BYTES_PER_SHORT, indexArray, GL_STATIC_DRAW);
    
        //PENTING: unbind dari buffer saat kita selesai dengan itu.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    
    public int getBufferId() {
        return bufferId;
    }
}

abstract class ShaderProgram 
{

    //tutorial 4
    protected static final String U_MV_MATRIX            = "u_MVMatrix";
    protected static final String U_IT_MV_MATRIX         = "u_IT_MVMatrix";
    protected static final String U_MVP_MATRIX           = "u_MVPMatrix";
    protected static final String U_POINT_LIGHT_POSITIONS= "u_PointLightPositions";
    protected static final String U_POINT_LIGHT_COLORS   = "u_PointLightColors";

    //tutorial 3
    protected static final String U_VECTOR_TO_LIGHT = "u_VectorToLight";
    protected static final String A_NORMAL = "a_Normal";

    protected static final String U_TIME = "u_Time";
    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
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

        /*
        untuk mendapatkan ray normal
        */
        public Vector normalize() {
            return scale(1f / length());
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

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * new GameTerainLighting().BYTES_PER_FLOAT;
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

    //update disini
    private final int uVectorToLightLocation;
    private final int aNormalLocation;

    private int uMatrixLocation = 0;
    private final int aPositionLocation;

    //tutorial 4
    private int uMVMatrixLocation = 0;
    private int uIT_MVMatrixLocation = 0;
    private int uMVPMatrixLocation = 0;
    private int uPointLightPositionsLocation = 0;
    private int uPointLightColorsLocation = 0;

    public HeightMapShaderProgram(String vertex, String fragment, int tutorial) {
        super(vertex, fragment);

        if (tutorial >= 4) {
            uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX);
            uIT_MVMatrixLocation = glGetUniformLocation(program, U_IT_MV_MATRIX);
            uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);
            uPointLightPositionsLocation = glGetUniformLocation(program, U_POINT_LIGHT_POSITIONS);
            uPointLightColorsLocation = glGetUniformLocation(program, U_POINT_LIGHT_COLORS);
        }
        else {
            uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        }
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        uVectorToLightLocation = glGetUniformLocation(program, U_VECTOR_TO_LIGHT);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
    }

    //tutorial 4
    public void setUniforms(float[] mvMatrix, float[] it_mvMatrix,
                            float[] mvpMatrix, float[] vectorToDirectionalLight,
                            float[] pointLightPositions, float[] pointLightColors) {
        /*
        kami sekarang lulus dalam beberapa matrix serta lampu terarah dan titik
        cahaya titik dan warna. tiga baris pertama dari metode ini mengirim semua
        matrix ke shader.
        */
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniformMatrix4fv(uIT_MVMatrixLocation, 1, false, it_mvMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);

        /*
        baris pertama melewati vector cahaya directional ke shader dan dua
        baris berikutnya melewati posisi cahaya titik dan warna pada shader
        juga. kami telah mendefinisikan dua uniform terakhir ini di shader
        sebagai array dengan panjang tiga vector masing-masing, jadi untuk
        setiap uniform kami menyebut glUniform*fv() dengan parameter kedua
        diatur ke 3 yang merupakan hitungan. ini memberi tahu OpenGL yang perlu
        dibaca dalam tiga vector dari array ke dalam uniform.
        */
        glUniform3fv(uVectorToLightLocation,       1, vectorToDirectionalLight, 0);
        glUniform4fv(uPointLightPositionsLocation, 3, pointLightPositions, 0);
        glUniform3fv(uPointLightColorsLocation,    3, pointLightColors, 0);
    }
    
    /*
    tutorial 1-3
    update disini
        dengan ini kita dapat merekam hal-hal bersama di kelas renderer dan
    melihat pencahayaan baru kita dalam aksi.
    */
    public void ysetUniforms(float[] matrix, Geometry.Vector vectorToLight) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform3f(uVectorToLightLocation, vectorToLight.x, vectorToLight.y, vectorToLight.z);
    }

    //tutorial 0
    public void xsetUniforms(float[] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }
}

class Heightmap {
    private static final int POSITION_COMPONENT_COUNT = 3;

    /*
    kami akan mengubah buffer vertex sehingga menyimpan posisi dan normal
    bersama dan untuk melakukan ini kita juga perlu mengetahui jumlah total
    komponen dan langkahnya.
    */
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * GameTerainLighting.BYTES_PER_FLOAT;

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

    private float[] loadBitmapData(Bitmap bitmap) {
        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        /*
        Perbarui kode ini untuk menambahkan beberapa ruang untuk normals
        ini memastikan bahwa kami memiliki cukup ruang untuk posisi dan normal.
        */
        final float[] heightmapVertices = new float[width * height * TOTAL_COMPONENT_COUNT];
        
        int offset = 0;

        //konvert pixel bitmap ke data heightmap
        for(int row=0; row<height; row++) {
            for (int col=0; col<width; col++) 
            {
                /*
                perbarui disini
                */
                final Geometry.Point point = getPoint(pixels, row, col);
                heightmapVertices[offset++] = point.x;
                heightmapVertices[offset++] = point.y;
                heightmapVertices[offset++] = point.z;

                /*
                kode untuk mendapatkan titik tetangga dan menghasilkan
                permukaan normal untuk titik saat ini.
                */
                final Geometry.Point top = getPoint(pixels, row-1, col);
                final Geometry.Point left = getPoint(pixels, row, col-1);
                final Geometry.Point right = getPoint(pixels, row, col+1);
                final Geometry.Point bottom = getPoint(pixels, row+1, col);
                /*
                untuk menghasilkan normal kita ikuti algoritma yang kita uraikan
                sebelumnya, pertama kita dapatkan poin tetangga maka kita menggunakan
                poin-poin ini untuk membuat dua vector yang mewakili plane dan akhirnya
                kita mengambil produk silang dari kedua vector itu dan menormalkanya untuk
                mendapatkan permukaan normal.
                */
                final Geometry.Vector rightToLeft = Geometry.vectorBetween(right, left);
                final Geometry.Vector topToBottom = Geometry.vectorBetween(top, bottom);
                final Geometry.Vector normal = rightToLeft.crossProduct(topToBottom).normalize();

                heightmapVertices[offset++] = normal.x;
                heightmapVertices[offset++] = normal.y;
                heightmapVertices[offset++] = normal.z;
            }
        }
        return heightmapVertices;
    }

    /*
    mendefinisikan getPoint()
        kode ini bekerja sama seperti sebelumnya ketika kode berada didalam loop
    tetapi kita sekarang memiliki clamp(penjepitan) untuk kasus-kasus ketika titik
    tetangga berada diluar batas. misalnya ketika kita menghasilkan normal untuk (0, 0)
    dan mengambil poin ke atas dan ke kiri, titik-titik ini sebenarnya tidak ada di
    heighmap. Dalam hal ini kami berpura-pura yang mereka lakukan dan kami memberi
    mereka tinggi yang sama dengan titik tengah. Dengan cara ini kita masih dapat
    menghasilkan permukaan normal untuk vertices(simpul) itu.
    */
    private Geometry.Point getPoint(int[] pixels, int row, int col) {
        float x = ((float)col / (float)(width-1)) - 0.5f;
        float z = ((float)row / (float)(height-1)) - 0.5f;

        row = clamp(row, 0, width - 1);
        col = clamp(col, 0, height -1);

        float y = (float)Color.red(pixels[(row*height) + col]) / (float)255;

        return new Geometry.Point(x, y, z);
    }
    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private int calculateNumElements() {
        return (width - 1) * (height - 1) * 2 * 3;
    }
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
    perbarui kode ini
        karena kita sekarang menyimpan posisi dan data normal dalam object buffer
    vertex yang sama kita sekarang harus lulus langkah setVertexAtribPointer() helper
    yang menyebut glVertexAttribPointer() sehingga OpenGL tau berapa byte untuk
    melewati untuk panggilan kedua ke setVertexAttribPointr(), sangat penting
    bahwa kami juga menentukan offset awal untuk normal dalam hal byte, kalau
    tidak OpenGL akan membaca bagian dari posisi dan bagian dari normal bersama-sama
    dan menafsirkan sebagai normal, yang akan terlihat sangat aneh.
    */
    public void bindData(HeightMapShaderProgram heightmapProgram) {
        vertexBuffer.setVertexAttribPointer(0,
                                            heightmapProgram.getPositionAttributeLocation(),
                                            POSITION_COMPONENT_COUNT, STRIDE);

        vertexBuffer.setVertexAttribPointer(POSITION_COMPONENT_COUNT * GameTerainLighting.BYTES_PER_FLOAT,
                                            heightmapProgram.getNormalAttributeLocation(),
                                            NORMAL_COMPONENT_COUNT, STRIDE);
    }
    public void draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}