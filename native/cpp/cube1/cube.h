#ifndef CUBERENDERER_H
#define CUBERENDERER_H

#include <android/native_window.h>
#include <pthread.h>
#include <EGL/egl.h>
#include <GLES/gl.h>

class CubeRenderer {
    public:
        CubeRenderer();
        virtual ~CubeRenderer();

        void start();
        void stop();
        void setWindow(ANativeWindow *window);
            
    private:
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

        void renderLoop();
        void initialize();
        void destroy();
        void drawFrame();
        static void *threadStartCallback(void *myself);
};

#endif