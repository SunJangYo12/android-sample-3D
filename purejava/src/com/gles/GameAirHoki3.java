/*

* BAB 4
* Penggunaan Matrik dan Vector
* serta perbaikan gambar gepeng saat rotasi
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
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glViewport;
import android.opengl.Matrix;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/*
Meja kami tegencet dalam mode lanskap alasan ini terjadi adalah karena
kami telah melewati koordiant kami ke OpenGL secara langsung, tanpa
mengkompensasi rasio aspek layar. Setiap aplikasi 2d dan 3d berbagi
suara masalah besar bagaimana cara memutuskan apa yang harus ditampilakan
dilayar dan bagaimana mereka menyesuaikan dimensi layar? masalah ini
juga memiliki solusi umum. di OpenGL kita dapat menggunakan proyeksi untuk
memetakan bagian dunia kita ke layar dan kita dapat memetakan sedemikian rupa
sehingga terlihat benar diberbagai ukuran dan orientasi layar. Dengan
bebagai perangkat diluar sana penting untuk dapat menyesuaikan semuanya.
    Dalam bab ini kita akan mempelajari mengapa meja kita muncul tergencet
dan bagaimana kita menggunakan proyeksi untuk memperbaiki masalah
berikut rencana pembahasan ini:
1. Pertama kami akan meninjau beberapa aljabar linear dan belajar cara
   mengalikan matriks dan vector bersama.
2. kemudian kita akan belajar cara mendefinisikan dan menggunakan proyeksi
   dengan metriks, yang akan memungkinkan kita mengkompensasi orientasi
   layar sehingga meja kita tidak tergencet.

Kami sekarang cukup akrap dengan kenyataan bahwa semua yang kami render
di OpenGL dipetakan ke range [-1, 1] pada sumbu x dan y, ini berlaku dari
sumbu z. koordinat dalam kisaran ini dikenal sebagai perangkat yang dinormalisasi
dan tidak tergantung pada ukuran atau bentuk layar yang sebenarnya.

Sayangnya, karena mereka independen dari 
dimensi layar yang sebenarnya, kita dapat 
mengalami masalah jika kita menggunakannya 
secara langsung, seperti tabel yang tergencet 
dalam mode lansekap. Katakanlah bahwa resolusi
perangkat kami yang sebenarnya adalah 1280 x 720
dalam piksel, yang merupakan resolusi umum pada
perangkat Android baru. Mari kita berpura-pura
sejenak bahwa kita menggunakan seluruh tampilan
untuk OpenGL, karena akan membuat diskusi ini lebih 
mudah.

Jika perangkat kami dalam mode potret, maka [-1, 1]
akan berkisar lebih dari 1280 piksel ketinggian 
tetapi hanya 720 piksel lebar. Gambar kami akan tampak 
pipih sepanjang sumbu x. Masalah yang sama terjadi di
   sepanjang sumbu y jika kita berada dalam mode lansekap. 
Koordinat perangkat yang dinormalisasi berasumsi bahwa ruang
koordinat adalah kotak, seperti yang terlihat pada gambar
berikut: page79 (normalize device koordinat)

Namun, karena viewport yang sebenarnya mungkin bukan
alun-alun, gambar akan diregangkan dalam satu arah dan
tergencet di yang lain. Sebuah gambar yang ditentukan
 dalam koordinat perangkat yang dinormalisasi akan 
tergencet secara horizontal ketika dilihat pada perangkat
potret: page79 (accept ratio at 720*1280)
Gambar yang sama akan tergencet dengan 
cara lain ketika dalam mode lansekap: page80(accept ratio at 1280*780)

Menyesuaikan dengan rasio aspek
    Kita perlu menyesuaikan ruang koordinat sehingga
memperhitungkan bentuk layar, dan satu cara yang dapat kita lakukan adalah
untuk menjaga rentang yang lebih kecil tetap ke [-1, 1] dan menyesuaikan kisaran
yang lebih besar pada proporsi pada dimensi layar. Misalnya, dalam potret, lebar
adalah 720 sedangkan tingginya 1280, jadi kita dapat menjaga rentang lebar di [-1, 1]
dan menyesuaikan rentang tinggi ke [-1280/720, 1280/720] atau [-1.78, 1.78]. Kita juga
dapat melakukan hal yang sama dalam mode lansekap, dengan rentang lebar diatur ke [-1.78, 1,78]
dan rentang tinggi diatur ke [-1, 1]. Dengan menyesuaikan ruang koordinat yang kami miliki, kami
akan mengaktifkan ruang yang kami miliki: page80(Dengan cara ini, objek akan terlihat sama di mode potret dan lanskap.)

bekerja dengan ruang koordinat virtual
    Untuk menyesuaikan ruang koordinat kami dapat memperhitungkan orientasi
layar, kami harus berhenti bekerja secara langsung dalam koordinat perangkat
yang di normalisasi dan mulai bekerja d ruang koordinat virtual. Kami kemudian
perlu menemukan beberapa cara untuk mengkonversi koordinat dari ruang virtual
kami kembali ke koordinat perangkat yang dinormalisasi sehingga OpenGL dapat
membuatnya dengan benar. konversi ini harus memperhitungkan orientasi layar
sehingga tabel hoki kami akan terlihat benar di mode potret dan lanskap. Apa
yang ingin kita lakukan disebut ortografis, dengan proyeksi ortografis semuanya
selalu muncul dengan ukuran yang sama tidak peduli seberapa dekat dan jauh.
Untuk lebih memahami apa jenis proyeksi ini bayangkan bahwa kami memiliki satu
set rel kereta inilah yang mungkin terlihat dari atas: page(81).
ada juga jenis proyeksi ortografis khusus yang dikenal sebagai proyeksi isometrik
yang merupakan proyeksi ortografis yang ditunjukan dari sudut sisi. Jenis proyeksi
ini dapat digunakan untuk menciptakan kembali sudut 3D klasik, seperti terlihat
dibeberapa simulasi kota dan permainan strategi. page(81) gambar 2.

Dari koordinat virtual kembali ke koordinat perangkat yang dinormalisasi
    ketika kami menggunakan proyeksi ortografis untuk mengubah dari koordinat virual
ke koordinat perangkat yang dinormalisasi, kami sebenarnya mendefinisikan wilayah
didalam dunia 3D. Segala sesuatu di dalam wilayah itu akan ditampilkan di layar, dan
segala sesuatu diluar wilayah itu akan terpotong. Dalam gambar 22 page 82 kita
dapat melihat adegan sederhana dengan kubus. Ketika kita menggunakan matrix proyeksi
ortografis untuk memetakan kubus ini ke layar kita akan melihat gambar 23 proyeksi
ortografis page 82.
Dengan matrik proyeksi ortografis kita dapat mengubah ukuran kubus ini sehingga kita
dapat melihat kurang lebih adegan kita ke layar. Kami juga dapat mengubah bentuk kubus
ini untuk mengkompensasi rasio aspek layar. Sebelum kita mulai menggunakan proyeksi ortografis
kita harus meninjau bebrapa aljabar linear dasar.

Linear Aljabar 101
    begitu banyak dari OpenGL bekerja dengan vektor dan matrix dan salah satu penggunaan
matrik yang paling penting dalam menyiapkan proyeksi ortografis dan perspektif. Salah satu
alasan untuk ini adalah menggunakan matrik untuk melakukan proyeksi hanya melibatkan sekelompok
"Menambahkan" dan "Multiplies" melalui satu set data secara berurutan dan GPU modern sangat
cepat pada hal semacam itu. kami akan berjalan melalui matematika dasar untuk dapat menggunakan
matrix untuk melakukan proyeksi ortografis.

Vector
    Vector adalah susunan elemen satu dimensi, diOpenGL biasanya vector memiliki empat elemen
seperti warna, sebagian besar vector yang kami kerjakan pada umumnya akan memiliki empat elemen.
seperti contoh berikut dimana kita dapat melihat vector posisi dengan komponen x,y,z,w
             ___
            | x |
            | y |
            | z |
            |_w_|

Matriks
    matrix adalah susunan elemen dua dimensi, di OpenGL biasanya kami menggunakan matriks untuk
vector proyek menggunakan proyeksi ortografis atau perspektif dan kami juga dapat menggunakanya
untuk melakukan rotasi, terjemahan dan penskalaan objeck. Kami melakukan ini dengan mengalikan
matrix dengan setiap vector yang ingin kami ubah. misalnya contoh matrik dibawah ini. Pelabelan
akan masuk akal setelah kita melihat cara mengalikan matrix dan vector bersama.
             ________________
            | Xx  Xy  Xz  Xw |
            |                |
            | Yx  Yy  Yz  Yw |
            |                |
            | Zx  Zy  Zz  Zw |
            |                |
            | Wx  Wy  Wz  Ww |
            |________________|

Perkalian (multiplication) Matrix dan Vector
    untuk mengalikan vector dan matrix kami meletakan matrix disisi kiri dan vector di sisi kanan
kami kemudian mulai dengan baris pertama dari matrix dan mengalikan komponen pertama dair baris itu
dengan komponen pertama dari vektor, Komponen kedua dari baris itu dengan komponen kedua dari vector
dan seterusnya. Kami kemudian menambahkan semua hasil untuk baris itu bersama-sama untuk membuat komponen
pertama hasilnya. Berikut contohnya:
             ________________   ___      _____________________________
            | Xx  Xy  Xz  Xw | | X |    | XxX  +  XyY  +  XzZ  +  XwW |
            |                | |   |    |                             |
            | Yx  Yy  Yz  Yw | | Y |    | YxX  +  YyY  +  YzZ  +  YWW |
            |                | |   |  = |                             |
            | Zx  Zy  Zz  Zw | | Z |    | ZzX  +  ZyY  +  ZzZ  +  ZwW |
            |                | |   |    |                             |
            | Wx  Wy  Wz  Ww | | W |    | WxX  +  WyY  +  WzZ  +  WwW |
            |________________| |___|    |_____________________________|

untuk baris pertama kami mengalikan Xx dan X, Xy dan Y, Xz dan Z, Xw dan W
kemudian menambahkan keempat hasil bersama untuk menghasilkan komponen X.
pelabelan matriks semoga lebih masuk akal setelah melihatya beraksi. Keempat
komponen dari baris pertama matriks akan mempengaruhi X yang dihasilkan, keempat
komponen dari baris kedua matriks akan mempengaruihi Y yang dihasilkan dan begitu
seterusnya. Dalam setiap baris komponen pertama mendapat perkalian dengan X vector
komponen kedua akan dikalikan dengan Y dan seterusnya.

a. Identitas matrix
    mari kita lihat beberapa contoh dengan beberapa angka aktual, kami
akan mulai dengan matrix yang sangat dasar yang disebut matrix identitas
sebuat matrix identitas akan terlihat seperti berikut:
             ____________
            | 1  0  0  0 |
            |            |
            | 0  1  0  0 |
            |            |
            | 0  0  1  0 |
            |            |
            | 0  0  0  1 |
            |____________|
alasan ini disebut matrix identisas adalah karena kita dapat melipat gandakan
matrix ini dengan vector apa pun dan kita akan selalu mendapatkan kembali vector
yang sama, sama seperti kita mendapatkan kembali nomor yang sama jika kita mengalikan
angka sebanyak itu dengan 1.
Berikut contoh mengalikan matrix identitas dengan vector yang
berisi 1,2,3,4:
             ____________   ___      _____________________________
            | 1  0  0  0 | | 1 |    | 1*1  +  0*2  +  0*3  +  0*4 |
            |            | |   |    |                             |
            | 0  1  0  0 | | 2 |    | 0*1  +  1*2  +  0*3  +  0*4 |
            |            | |   |  = |                             |
            | 0  0  1  0 | | 3 |    | 0*1  +  0*2  +  1*3  +  0*4 |
            |            | |   |    |                             |
            | 0  0  0  1 | | 4 |    | 0*1  +  0*2  +  0*3  +  1*4 |
            |____________| |___|    |_____________________________|

Untuk baris pertama kami mengalikan komponen pertama dari vector dengan 1 dan
mengabaikan komponen lain dengan mengalikanya dengan 0. Untuk baris kedua kami
melakukan hal yang sama kecuali kami melestarikan komponen kedua dari vector.
Hasil bersih dari semua adalah jawabanya akan identik dengan vector asli.
jika menyederhanakan yang belipat ganda dan menambahkan hasilnya bersama
inilah yang kita dapatkan:
             ___
            | 1 |
            | 2 |
            | 3 |
            |_4_|

b. Translation menggunakan matrix
    Sekarang kita memahami matrix identitas sekarang kita bahas jenis matrik
yang sangat sederhana yang cukup sering digunakan di OpenGL yaitu Translation matrix
dengan jenis matrix ini kita dapat memindahkan salah satu object kit di sepanjang
kejauhan yang kita tentukan. Matriks ini terlihat seperti matriks identitas dengan
tiga elemen tambahan yang ditentukan disisi kanan.
             _______________________
            | 1  0  0  Xtranslation |
            |                       |
            | 0  1  0  Ytranslation |
            |                       |
            | 0  0  1  Ztranslation |
            |                       |
            | 0  0  0  1            |
            |_______________________|

mari kita lihat contoh dengan posisi (2, 2) dengan Z default 0 dan W default 1
kami ingin menerjemahkan vector 3 sepanjang sumbu X dan 3 disepanjang sumbu Y
jadi kami akan menempatkan 3 untuk Xtranslation dan 3 Ytranslation.
Hasilnya seperti ini:
             ____________   ___      _____________________________
            | 1  0  0  3 | | 3 |    | 1*2  +  0*2  +  0*0  +  3*1 |
            |            | |   |    |                             |
            | 0  1  0  3 | | 2 |    | 0*2  +  1*2  +  0*0  +  3*1 |
            |            | |   |  = |                             |
            | 0  0  1  0 | | 0 |    | 0*2  +  0*2  +  1*0  +  0*1 |
            |            | |   |    |                             |
            | 0  0  0  1 | | 1 |    | 0*2  +  0*2  +  0*0  +  1*1 |
            |____________| |___|    |_____________________________|

            penyerderhanaan perkalian:
             _______________
            | 2 + 0 + 0 + 3 |
            |               |
            | 0 + 2 + 0 + 3 |
            |               |
            | 0 + 0 + 0 + 0 |
            |               |
            | 0 + 0 + 0 + 1 |
            |_______________|
            
            hasil akhir seperti ini:
             ___
            | 5 |
            | 5 |
            | 0 |
            |_1_|

posisi sekarang di (5,5) seperti yang kami harapkan. alasan ini bekerja adalah
bahwa kami membangun matrix ini dari matrix identitas, jadi hal pertama yang
akan terjadi adalah bahwa vector asli akan disalin. Karena komponen terjemahan
dikalikan dengan W dan kami biasanya menentukan komponen W ke posisi sebagai 1
(ingat bahwa jika kami tidak menentukan omponen W OpenGL menetapkanya ke 1 secara default)
komponen tejemahan hanya ditambahkan ke hasil. Efek W penting untuk dicatat disini.
Jika kita melakukan terjemahan atau jenis lain transformasi dengan koordinat yang
setelah kami melakukan proyeksi dan komponen W tidak lagi 1, maka kami akan mengalami
masalah dan hal-hal akan terdistorsi. 


c. Mendefinisikan proyeksi ortografis
    Untuk menentukan proyeksi ortografis kami akan menggunakan kelas matrix android
yang berada dipaket android.opengl. Dikelas ini ada metode orthoM() yang menghasilkan
proyeksi ortografis bagi kita. Kami akan menggunakan proyeksi itu untuk menyesuaikan
ruang koordinat seperti yang baru kita bahas dalam menyesuaikan dengan rasio aspek.
proyeksi ortogragis sangat mirip dengan matrix terjemahan.
mari kita lihat semua parameter untuk orthoM();

orthoM(float[] m, int mOffset, float left, float right, float bottom, float top, float near, float far)

1. float[] m: array tujuan - panjang array ini harus setidaknya enam belas
              elemen sehingga dapat menyimpan matrix proyeksi ortografis.
2. int mOffset: offset ke m ke mana hasilnya ditulis
3. float left: range minimum sumbu x
4. float right: range maximum sumbu x
5. float bottom: range minimum sumbu y
6. float top : range maksimum sumbu y
7. float near: range minimum sumbu z
8. float far: range maksimum sumbu z

ketika kami menyebut metode ini, itu harus menghasilkan matrix
projection matrix seperti ini:
             _______________________________________________________
            | ___2______     0         0        _ __right+left____  |
            | right-left                            right-left      |
            |                                                       |
            |    0        ___2______   0        _ __top+bottom____  |
            |             top-bottom                top-bottom      |
            |                                                       |
            |    0           0       __-2____   _ __far+near_____   |
            |                        far-near       far-near        |
            |                                                       |
            |    0           0         0               1            |
            |_______________________________________________________|

jangan biarkan fraksi itu membuat anda sangat mirip dengan matrix
terjemahan yang lihat dalam terjemahan menggunakan matrix. Pryeksi
orthografik ini akan memetakan segalanya antar kiri dan kanan dan
dekat dan jauh ke dalam kisaran.
perbedaan utama adalah bahwa sumbu z memiliki tanda negatif diatasnya
yang mmiliki efek membalikan koordinat z. Ini berarti bahwa hal-hal
semakin jauh koordinat z mereka menjadi semakin negatif. Alasan ini
sepenuhnya disebabkan oleh sejarah konversi.

d. sistem koordinat kidal dan tangan kanan
    untuk memahami masalah dengan sumbu z kita perlu memahami perbedaan antara
sistem kidal dan sistem koordinat tangan kanan. Untuk melihat apakah sistem koordinat
ditangan atau kidal anda mengambil salah satu tangan anda dan mengarahkan ibu jari
anda sepanjang sumbu x positif. Anda kemudian mengambil finder indexs anda dan arahkan
sepanjang sumbu y positif, sekarang arahkan jari tengah anda sepanjang sumbu z.
jika anda perlu menggunakan tangan kiri anda untuk melakukan ini maka anda sedang
melihat sistem koordinat kidal. Jika anda perlu menggunakan tangan kanan maka ini
adalah sistem koordinat tangan kanan.

pilihan tangan kiri atau tangan kanan tidak masalah dan hanya masalah konversi.
sementara koordinat perangkat yang dinormalisasi menggunakan sistem koordinat kidal
dalam versi OpenGL yang lebih lama segala sesuatu yang digunakan secara default
dengan z negatif meningkat kedalam jarak. Inilah sebabnya mengapa metrix android
menghasilkan matrix membalikan z secara default. Jika anda lebih suka menggunakan
koordinat kidal dimana-mana dan tidak hanya dalam koordinat perangkat yang dinormalisasi
maka anda dapat membatalkan inversi pada sumbu z yang dilakukan oleh orthoM().

                    |+Y /+Z
                    |  /
                    | /
            ________|/_______+X
                    /
                   /|
                  / |
                 /  |
            
            left-handed coordinate system

                    |+Y /
                    |  /
                    | /
            ________|/_______+X
                    /
                   /|
                  / |
               +Z/  |
            
            Right-handed coordinate system
*/

public class GameAirHoki3 implements Renderer
{
    /*
    tabel kami sekarang terlihat sama dalam mode potrait dan lanskap
    tetapi masih tidak terlihat benar. apa yang terjadi adalah bahwa
    meja itu disusun untuk tampil lebih sempitdalam model potret, dan
    sekarang tidak ada lagi squising kita dapat melihat tabel seperti
    yang kita tetapkan.
    Sekarang kita perbarui struktur tabel untuk membuatnya lebih tinggi
    perbarui hanya posisi Y (kolom kedua). yang awalnya 0.5 ke 0.8
    */
    float[] tableVerticesWithTriangles = {
        //coordinate: X, Y, R, G, B

        //triangle Fan
           0f,     0f,   1f,   1f,   1f,
        -0.5f,  -0.8f, 0.7f, 0.7f, 0.7f,
         0.5f,  -0.8f, 0.7f, 0.7f, 0.7f,
         0.5f,   0.8f, 0.7f, 0.7f, 0.7f,
        -0.5f,   0.8f, 0.7f, 0.7f, 0.7f,
        -0.5f,  -0.8f, 0.7f, 0.7f, 0.7f,

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
    
    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTE_PER_FLOAT;
    private int aColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    /*
    menambahkan uniform baru yang kami definisikan dalam shader vertex
    kami juga menambahkan array floating point untuk menyimpan matrix.
    kami juga perlu bilangan bulat untuk memegang lokasi uniform matrix.
    */
    private static final String U_MATRIX = "u_Matrix";
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;
 
    public GameAirHoki3() 
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
        kami telah menambahkan definisi uniform(seragam) baru, u_Matrix
        dan kami telah mendefinisikan sebagai matrix yang berarti bahwa
        uniform ini akan mewakili matrix 4 x 4. Kami juga update gl_Position
        di fungsi main yang sebelumnya hanya melewati posisi seperti yang
        telah ditetapkan dalam array kira, kami sekarang melipat gandakan
        matrix dengan posisi itu. Ini akan melakukan matematika yang sama seperti
        yang kita bicarakan kembali dalam perkalian matrix-vector. ini juga
        berarti array vertices kita tidak akan lagi ditafsirkan sebagai perangkat
        yang dinormalisasi tetapi sekarang akan ditafsirkan sebagai ruang koordinat
        virtual sebagai matrix akan mengubah koordinat dari ruang koordinat virtual
        ini kembali ke koordinat perangkat yang dinormalisasi.
        */
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
        kode ini akan membuat matrix proyeksi ortografis yang akan memperhitungkan
        orientasi layar saat ini. Ini akan mengatur ruang koordinat virtual.
        pertama-tama kita menghitung rasio aspect dengan mengambil lebih besar dari
        lebar dan tinggi dan membaginya dengan yang lebih kecil dari lebar dan tinggi.
        Nilai ini akan sama terlepas dari apakah kita berada di potret atau lanskap.

        kami kemudia memanggil orthoM(float[] m, int mOffset, float kiri, float kanan, float bawah, float atas, float dekat, float jauh)
        jika anda berada di dalam mode lanskap kita akan memperluas ruang koordinat lebar
        sehingga alih-alih mulai dari -1 hingga 1 lebar akan berkisar dari -aspectRatio ke aspectRatio
        ketinggian akan tetap dari -1 hingga 1. Jika kita dalam mode potret, kita memperluas
        ketinggian dan menjaga lebar -1 hingga 1.
        */

        final float aspectRatio = width > height ?
              (float)width / (float)height:
              (float)height / (float)width;

        if (width > height) {
            //landascape
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        }
        else {
            //portrait or square
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
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
