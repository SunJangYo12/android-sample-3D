package com.gles;

/*
USING STATIC IMPORTS
   ini adalah titik pertama dimana kami menggunakan arahan statis impor
arahan ini membantu mengurangi verbosesitas dengan mengurangi panggilan
seperti GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT) ke glClear(GL_COLOR_BUFFER_BIT)
ini membuat perbedaan besar ketika sejumlah besar kode kami bekerja dengan opengl
dan utilitas lainya.
*/
import static android.opengl.GLSurfaceView.Renderer;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FirstRenderer implements Renderer
{

	/*
	glSurface memanggil ini ketika permukaan dibuat.Ini terjadi pertama
	kali aplikasi kami dijalankan dan itu juga dapat disebut ketika perangkat
	bangun atau ketika pengguna beralih kembali ke activity ini. Dalam
	praktiknya bahwa metode ini dapat disebut beberapa kali sementara
	aplikasi berjalan
	*/
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
	{
		/*
		Pertama-tama kita menetapkan warna bening di onSurfaceCreated()
		dengan panggilan glClearColor(1.0f, ...), tiga komponen pertama
		sesuai warna merah, hijau, biru dan yang terakhir berhubungan
		dengan komponen khusus yang disebut alpha yang sering digunakan
		untuk transparansi. Dengan mengatur komponen pertama 1 dan sisanya 0
		maka warna merah memenuhi layar saat dihapus.
		*/
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
	}

	/*
	glSurfaceView memangil ini setelah permukaan dibuat dan setiap kali
	ukuranya berubah. perubahan ukuran dapat terjadi saat beralih dari
	potret ke lanskap dan sebaliknya
	*/
	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height)
	{
		//atur viewport opengl untuk mengisi seluruh permukaan
		glViewport(0, 0, width, height);
	}


	/*
	glSurfaceView menyebut ini ketiak sudah waktunya untuk menggambar bingkai
	kita harus menggambar sesuatu bahkan jika itu hanya menghapus layar.
	buffer render akan ditukar dan ditampilkan dilayar setelah metode ini kembali
	jadi jika kita tidak menggambar apa-apa kita akan mendapatkan efek kerkedip-kedip
	yang buruk.
	*/
	@Override
	public void onDrawFrame(GL10 glUnused)
	{
		/*
		kami menghapus layar di onDrawFrame() dengan panggilan ke glClear(...)
		ini akan menghapus semua warna pada layar dan mengisi layar dengan
		warna yang sebelumnya didefinisikan oleh panggilan kami ke glClearColor()
		*/
		//clear the rendering surface(bersihkan permukaan rendering)
		glClear(GL_COLOR_BUFFER_BIT);
	}
}
