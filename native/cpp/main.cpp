#include <stdint.h>
#include <jni.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include "cube/cube.h"

ANativeWindow *window = 0;
CubeRenderer *cubeRenderer = 0;

extern "C" JNIEXPORT void JNICALL Java_com_objcplus_MainActivity_nativeOnStart(JNIEnv *jenv, jobject obj)
{
    cubeRenderer = new CubeRenderer();
}

extern "C" JNIEXPORT void JNICALL Java_com_objcplus_MainActivity_nativeOnResume(JNIEnv *jenv, jobject obj)
{
    cubeRenderer->start();
}

extern "C" JNIEXPORT void JNICALL Java_com_objcplus_MainActivity_nativeOnPause(JNIEnv *jenv, jobject obj)
{
    cubeRenderer->stop();
}

extern "C" JNIEXPORT void JNICALL Java_com_objcplus_MainActivity_nativeOnStop(JNIEnv *jenv, jobject obj)
{
    delete cubeRenderer;
    cubeRenderer = 0;
}

extern "C" JNIEXPORT void JNICALL Java_com_objcplus_MainActivity_nativeSetSurface(JNIEnv *jenv, jobject obj, jobject surface)
{
    if (surface != 0) {
        window = ANativeWindow_fromSurface(jenv, surface);
        cubeRenderer->setWindow(window);
    }
    else {
        ANativeWindow_release(window);
    }
}
/*
class First {
	enum CubeRendererThreadMessage {
         MSG_NONE = 0,
         MSG_WINDOW_SET,
         MSG_RENDERER_LOOP_EXIT
    };

    pthread_t _threadId;
    pthread_mutex_t _mutex;
    enum CubeRendererThreadMessage _msg;
        
    ANativeWindow *_window;
    EGLDisplay _display;
    EGLSurface _surface;
    EGLContext _context;
    GLfloat _glfloatangle;
	
	public:
       First() {
	   }
	   virtual ~First()
	   {
	   }
	   void setWindow(ANativeWindow *window) {
            pthread_mutex_lock(&_mutex);
            _msg = MSG_WINDOW_SET;
            _window = window;
            pthread_mutex_unlock(&_mutex);
       }
	   void start() {
	      pthread_create(&_threadId, 0, thradStartCallback, this);
	   }
	   void stop() {
		  pthread_mutex_lock(&_mutex);
          _msg = MSG_RENDERER_LOOP_EXIT;
          pthread_mutex_unlock(&_mutex);
          pthread_join(_threadId, 0);
	   }
	
	   void destroy() {
          eglMakeCurrent(_display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
          eglDestroyContext(_display, _context);
          eglDestroySurface(_display, _surface);
          eglTerminate(_display);
          _display = EGL_NO_DISPLAY;
          _surface = EGL_NO_SURFACE;
          _context = EGL_NO_CONTEXT;
       }
	   void initialize() {
          const EGLint attribs[] = {
             EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
             EGL_BLUE_SIZE, 8,
             EGL_GREEN_SIZE, 8,
             EGL_RED_SIZE, 8,
             EGL_NONE
        };
        int errorcode = 0;
        EGLDisplay display;
        EGLConfig config;
        EGLint numConfigs;
        EGLint format;
        EGLSurface surface;
        EGLContext context;
        EGLint width;
        EGLint height;
        GLfloat ratio;

        if ((display = eglGetDisplay(EGL_DEFAULT_DISPLAY)) == EGL_NO_DISPLAY)
           errorcode += 1;
        if (!eglInitialize(display, 0, 0))
           errorcode += 2;
        if (!eglChooseConfig(display, attribs, &config, 1, &numConfigs))
           errorcode += 3;
        if (!eglGetConfigAttrib(display, config, EGL_NATIVE_VISUAL_ID, &format))
        {
            errorcode += 4;
            destroy();
        }

        ANativeWindow_setBuffersGeometry(_window, 0, 0, format);

        if (!(surface=eglCreateWindowSurface(display, config, _window, 0)))
        {
           errorcode += 5;
           destroy();
        }
        if (!(context=eglCreateContext(display, config, 0, 0)))
        {
           errorcode += 6;
           destroy();
        }
        if (!eglMakeCurrent(display, surface, surface, context))
        {
           errorcode += 7;
           destroy();
        }
        if (!eglQuerySurface(display, surface, EGL_WIDTH, &width) ||
           !eglQuerySurface(display, surface, EGL_HEIGHT, &height))
        {
           errorcode += 8;
           destroy();
        }

        _display = display;
        _surface = surface;
        _context = context;
        glDisable(GL_DITHER);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);
        glClearColor(0, 0, 0, 0);
        glEnable(GL_CULL_FACE);
        glShadeModel(GL_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        glViewport(0, 0, width, height);

        ratio = (GLfloat) width / height;
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glFrustumf(-ratio, ratio, -1, 1, 1, 10);
    }

	void renderLoop() {
        int error = 0;
        bool renderingEnabled = true;
        while (renderingEnabled)
        {
            pthread_mutex_lock(&_mutex);
            switch (_msg) {
                case MSG_WINDOW_SET:
                   initialize();
                   break;
                case MSG_RENDERER_LOOP_EXIT:
                   renderingEnabled = false;
                   destroy();
                   break;
                default:
                   break;
            }
            _msg = MSG_NONE;

            if (_display) {
               drawFrame();
               if (!eglSwapBuffers(_display, _surface)) {
                    error += 1;
               }
            }
            pthread_mutex_unlock(&_mutex);
       } //while
    }
    
	void *threadStartCallback(void *myself)
    {
         First *first = (first*) myself;
         first->renderLoop();
         pthread_exit(0);
    }
}*/