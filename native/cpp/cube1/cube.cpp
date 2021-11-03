#include "cube.h"

GLint vertices[][3] = {
            { -0x10000, -0x10000, -0x10000 },
            {  0x10000, -0x10000, -0x10000 },
            {  0x10000,  0x10000, -0x10000 },
            { -0x10000,  0x10000, -0x10000 },
            { -0x10000, -0x10000,  0x10000 },
            {  0x10000, -0x10000,  0x10000 },
            {  0x10000,  0x10000,  0x10000 },
            { -0x10000,  0x10000,  0x10000 }
        };
GLint colors[][4] = {
            { 0x00000, 0x00000, 0x00000, 0x10000 },
            { 0x10000, 0x00000, 0x00000, 0x10000 },
            { 0x10000, 0x10000, 0x00000, 0x10000 },
            { 0x00000, 0x10000, 0x00000, 0x10000 },
            { 0x00000, 0x00000, 0x10000, 0x10000 },
            { 0x10000, 0x00000, 0x10000, 0x10000 },
            { 0x10000, 0x10000, 0x10000, 0x10000 },
            { 0x00000, 0x10000, 0x10000, 0x10000 }
        };
GLubyte indices[] = {
            0, 4, 5,    0, 5, 1,
            1, 5, 6,    1, 6, 2,
            2, 6, 7,    2, 7, 3,
            3, 7, 4,    3, 4, 0,
            4, 7, 6,    4, 6, 5,
            3, 0, 1,    3, 1, 2
        };

CubeRenderer::CubeRenderer() : _msg(MSG_NONE), _display(0), _surface(0), _context(0), _glfloatangle(0) {
    pthread_mutex_init(&_mutex, 0);
}

CubeRenderer::~CubeRenderer() {
    pthread_mutex_destroy(&_mutex);
}

void CubeRenderer::start() {
    pthread_create(&_threadId, 0, threadStartCallback, this);
}
void CubeRenderer::stop() {
    pthread_mutex_lock(&_mutex);
    _msg = MSG_RENDERER_LOOP_EXIT;
    pthread_mutex_unlock(&_mutex);
    pthread_join(_threadId, 0);
}

void CubeRenderer::setWindow(ANativeWindow *window) {
    pthread_mutex_lock(&_mutex);
    _msg = MSG_WINDOW_SET;
    _window = window;
    pthread_mutex_unlock(&_mutex);
}

//private
void CubeRenderer::renderLoop() {
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
    }
}
void CubeRenderer::initialize() {
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

void CubeRenderer::destroy() {
    eglMakeCurrent(_display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    eglDestroyContext(_display, _context);
    eglDestroySurface(_display, _surface);
    eglTerminate(_display);
    _display = EGL_NO_DISPLAY;
    _surface = EGL_NO_SURFACE;
    _context = EGL_NO_CONTEXT;
}

void CubeRenderer::drawFrame() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    glTranslatef(0, 0, -3.0f);
    glRotatef(_glfloatangle, 0, 1, 0);
    glRotatef(_glfloatangle * 0.25f, 1, 0, 0);

    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_COLOR_ARRAY);

    glFrontFace(GL_CW);
    glVertexPointer(3, GL_FIXED, 0, vertices);
    glColorPointer(4, GL_FIXED, 0, colors);
    glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indices);

    _glfloatangle += 1.2f;
}

void *CubeRenderer::threadStartCallback(void *myself)
{
    CubeRenderer *cubeRenderer = (CubeRenderer*) myself;
    cubeRenderer->renderLoop();
    pthread_exit(0);
}
