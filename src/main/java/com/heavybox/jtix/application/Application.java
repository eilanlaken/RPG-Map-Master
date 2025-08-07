package com.heavybox.jtix.application;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.async.Async;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.input.Input;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

// TODO:
// handle recreating the window
// handle changing and saving settings
// refactor a little-bit more (separate window creating into its own function, apply settings etc).
// test: invisible frame buffer, resizeable windows, MSAA, vsync, etc.
// IGNORE multi-window applications. Useful for no-one and will ruin important stuff.
public class Application {

    @Deprecated private static boolean initialized = false;
    private static boolean running = false;

    /* tasks */
    private static final Array<Runnable> tasks       = new Array<>();
    private static final Array<Runnable> windowTasks = new Array<>();

    /* window attributes */
    private static long    windowHandle           = -1;
    private static boolean windowFocused          = false; // TODO
    private static boolean windowRequestRendering = false;
    private static int     windowPosX             = -1;
    private static int     windowPosY             = -1;
    private static int     windowWidth            = 640*2;
    private static int     windowHeight           = 480*2;
    private static boolean windowMinimized        = false;
    private static boolean windowMaximized        = false;
    private static String  windowIconPath         = null;
    private static String  windowTitle            = "HeavyBox Game";
    private static boolean windowVSyncEnabled     = false;
    private static int     MSAA                   = 0;

    /*
    private static boolean windowAutoMinimized    = true;
    private static boolean windowVisible          = true;
    private static boolean windowFullScreen       = false;
    private static boolean windowResizeable       = true;
    private static boolean windowDecorated        = true;
     */

    // TODO: improve. Should be handled by the callback. Just invoke an Asset.draggedAndDroppedFiles callback or something.
    private static final Array<String> windowFilesDraggedAndDropped   = new Array<>(); // TODO: improve
    private static       int           windowLastDragAndDropFileCount = 0;

    /* current Scene */
    private static Scene currentScene = null;

    /* GLFW Window callbacks */
    private static final GLFWErrorCallback           errorCallback              = GLFWErrorCallback.createPrint(System.err);
    private static final GLFWFramebufferSizeCallback windowResizeCallback       = new GLFWFramebufferSizeCallback() {

        private volatile boolean requested;

        @Override
        public void invoke(long windowHandle, final int width, final int height) {
            if (Configuration.GLFW_CHECK_THREAD0.get(true)) {
                renderWindow(width, height);
            } else {
                if (requested) return;
                requested = true;
                addTask(() -> {
                    requested = false;
                    renderWindow(width, height);
                });
            }
            windowWidth = width;
            windowHeight = height;
        }

    };
    private static final GLFWWindowFocusCallback windowFocusChangeCallback  = new GLFWWindowFocusCallback() {
        @Override
        public synchronized void invoke(long handle, final boolean focused) {
            windowTasks.add(() -> {
                windowFocused = focused;
                if (currentScene != null) currentScene.windowFocused(focused);
            });
        }
    };
    private static final GLFWWindowIconifyCallback   windowMinimizedCallback    = new GLFWWindowIconifyCallback() {
        @Override
        public synchronized void invoke(long handle, final boolean minimized) {
            windowTasks.add(() -> {
                windowMinimized = minimized;
                if (currentScene != null) currentScene.windowMinimized(minimized);
            });
        }
    };
    private static final GLFWWindowMaximizeCallback  windowMaximizedCallback    = new GLFWWindowMaximizeCallback() {
        @Override
        public synchronized void invoke(long windowHandle, final boolean maximized) {
            windowTasks.add(() -> windowMaximized = maximized);
        }
    };
    private static final GLFWWindowCloseCallback     windowCloseCallback        = new GLFWWindowCloseCallback() {
        @Override
        public synchronized void invoke(final long handle) {
            windowTasks.add(() -> GLFW.glfwSetWindowShouldClose(handle, false));
        }
    };
    private static final GLFWDropCallback            windowFilesDroppedCallback = new GLFWDropCallback() {
        @Override
        public synchronized void invoke(final long windowHandle, final int count, final long names) {
            windowTasks.add(() -> {
                windowLastDragAndDropFileCount = count;
                for (int i = 0; i < count; i++) {
                    windowFilesDraggedAndDropped.add(GLFWDropCallback.getName(names, i));
                }
            });
        }
    };
    private static final GLFWWindowPosCallback windowPositionCallback = new GLFWWindowPosCallback() {
        @Override
        public void invoke(long handle, int xPos, int yPos) {
            windowPosX = xPos;
            windowPosY = yPos;
        }
    };

    public static void init() {
        final ApplicationSettings settings = new ApplicationSettings(); // defaults.
        init(settings);
    }

    public static void init(final ApplicationSettings settings) {
        if (initialized) throw new ApplicationException("Application window already created and initialized. Cannot call init() twice.");
        //errorCallback = GLFWErrorCallback.createPrint(System.err);
        GLFW.glfwSetErrorCallback(errorCallback);
        GLFWErrorCallback.createPrint(System.err).set();

        // TODO: see if this causes problems. Also, must call a cleanup function before exit.
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace(); // Optional: log the error
            System.exit(1);              // Crash the program
        });

        if (!GLFW.glfwInit()) throw new ApplicationException("Unable to initialize GLFW.");
        //window = new ApplicationWindow();
        // initialize window
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, settings.resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, settings.maximized ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, settings.autoMinimized ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, settings.transparentWindow ? GLFW.GLFW_TRUE : GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, settings.MSAA);


        if (settings.fullScreen) {
            // compute and auxiliary buffers
            long monitor = GLFW.glfwGetPrimaryMonitor();
            GLFWVidMode videoMode = GLFW.glfwGetVideoMode(monitor);
            assert videoMode != null;
            GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, videoMode.refreshRate());
            windowHandle = GLFW.glfwCreateWindow(settings.width, settings.height, settings.title, videoMode.refreshRate(), MemoryUtil.NULL);
        } else {
            GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, settings.decorated ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
            windowHandle = GLFW.glfwCreateWindow(settings.width, settings.height, settings.title, MemoryUtil.NULL, MemoryUtil.NULL);
        }
        if (windowHandle == MemoryUtil.NULL) throw new RuntimeException("Unable to create window.");
        windowSetSizeLimits(settings.minWidth, settings.minHeight, settings.maxWidth, settings.maxHeight);

        // we need to set window position
        if (!settings.fullScreen) {
            if (settings.posX == -1 && settings.posY == -1) windowSetPosition(Graphics.getMonitorWidth() / 2 - settings.width / 2, Graphics.getMonitorHeight() / 2 - settings.height / 2);
            else windowSetPosition(settings.posX, settings.posY);
            if (settings.maximized) windowMaximize();
        }

        if (settings.iconPath != null) {
            windowSetIcon(settings.iconPath);
        }

        // register callbacks
        GLFW.glfwSetFramebufferSizeCallback(windowHandle, windowResizeCallback);
        GLFW.glfwSetWindowFocusCallback(windowHandle, windowFocusChangeCallback);
        GLFW.glfwSetWindowIconifyCallback(windowHandle, windowMinimizedCallback);
        GLFW.glfwSetWindowMaximizeCallback(windowHandle, windowMaximizedCallback);
        GLFW.glfwSetWindowCloseCallback(windowHandle, windowCloseCallback);
        GLFW.glfwSetDropCallback(windowHandle, windowFilesDroppedCallback);
        GLFW.glfwSetWindowPosCallback(windowHandle, windowPositionCallback);
        GLFW.glfwMakeContextCurrent(windowHandle);
        GLFW.glfwSwapInterval(settings.vSyncEnabled ? 1 : 0);
        GLFW.glfwShowWindow(windowHandle);

        // apply settings
        windowSetTitle(settings.title);

        //
        GL.createCapabilities();
        Async.init(); // TODO: probably remove.
        initialized = true;
    }

    public static void launch(@NotNull Scene scene) {
        if (running) throw new ApplicationException("Application already running. Function run() already called - Cannot call run() twice.");

        /* start the application with active scene */
        currentScene = scene;
        currentScene.setup();
        currentScene.start();

        /* main thread game loop */
        running = true;
        while (running && !GLFW.glfwWindowShouldClose(windowHandle)) {
            //if (windowFocused) GLFW.glfwMakeContextCurrent(windowHandle);
            GLFW.glfwMakeContextCurrent(windowHandle); // was
            boolean windowRendered = windowRefresh();
            int targetFrameRate = Graphics.getTargetFps();

            Assets.update();
            Input.update();
            GLFW.glfwPollEvents();

            boolean requestRendering;
            for (Runnable task : tasks) {
                task.run();
            }
            synchronized (tasks) {
                requestRendering = tasks.size > 0;
                tasks.clear();
            }

            if (requestRendering && !Graphics.isContinuousRendering()) windowRequestRendering();

            if (!windowRendered) { // Sleep a few milliseconds in case no rendering was requested with continuous rendering disabled.
                try {
                    Thread.sleep(1000 / Graphics.getIdleFps()); // TODO: fix the busy waiting.
                } catch (InterruptedException ignored) {
                    // ignore
                }
            } else if (targetFrameRate > 0) {
                Async.sync(targetFrameRate); // sleep as needed to meet the target frame-rate
            }
        }

        /* clean memory resources */ // TODO: clear Assets.
        currentScene.finish();

        Assets.cleanup(); // TODO: implement
        Input.cleanup();
        Graphics.cleanup();

        GLFW.glfwSetWindowFocusCallback(windowHandle, null);
        GLFW.glfwSetWindowIconifyCallback(windowHandle, null);
        GLFW.glfwSetWindowCloseCallback(windowHandle, null);
        GLFW.glfwSetDropCallback(windowHandle, null);
        GLFW.glfwSetFramebufferSizeCallback(windowHandle, null);
        GLFW.glfwDestroyWindow(windowHandle);



        windowResizeCallback.free();
        windowFocusChangeCallback.free();
        windowMinimizedCallback.free();
        windowMaximizedCallback.free();
        windowCloseCallback.free();
        windowFilesDroppedCallback.free();
        windowPositionCallback.free();
        GLFW.glfwTerminate();
        errorCallback.free();

        /* terminate any thread associated with AWT in a case it was spawned (for example, when opening a file dialog). */
        EventQueue.invokeLater(() -> {
            // Ensure AWT event queue is cleared
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new java.awt.event.WindowEvent(new Frame(), java.awt.event.WindowEvent.WINDOW_CLOSING)
            );
        });
        System.exit(0);
    }

    public static void playScene(@NotNull Scene scene) {
        if (!running) throw new ApplicationException("Application not running. Function playScene() must be called with the starting scene, after init.");
        if (currentScene != null) {
            currentScene.finish();
        }
        currentScene = scene;
        currentScene.setup();
        currentScene.start();
    }

    public static synchronized void addTask(Runnable task) {
        tasks.add(task);
    }

    public static void renderWindow(final int width, final int height) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer fbWidth = stack.mallocInt(1);
            IntBuffer fbHeight = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize(windowHandle, fbWidth, fbHeight);
            GL20.glViewport(0, 0, fbWidth.get(0), fbHeight.get(0));
        }
        GLFW.glfwMakeContextCurrent(windowHandle);
        currentScene.windowResized(width, height);
        Graphics.update();
        currentScene.update();
        GLFW.glfwSwapBuffers(windowHandle);
    }

    public static void windowRequestRendering() {
        synchronized (tasks) {
            windowRequestRendering = true;
        }
    }

    public static boolean windowRefresh() {
        for (Runnable task : windowTasks) {
            task.run();
        }
        boolean shouldRefresh = windowTasks.size > 0 || Graphics.isContinuousRendering();
        synchronized (windowTasks) {
            windowTasks.clear();
            shouldRefresh |= windowRequestRendering && !windowMinimized;
            windowRequestRendering = false;
        }

        if (shouldRefresh) {
            Graphics.update();
            currentScene.update();
            GLFW.glfwSwapBuffers(windowHandle);
        }

        return shouldRefresh;
    }

    /* Setters & Actions */

    public static void restart() { // TODO

    }

    public static void exit() {
        running = false;
    }

    public static void windowClose() {
        GLFW.glfwSetWindowShouldClose(windowHandle, true);
    }

    // window set size and window increase size.
    public static void windowSetWidth(int width) {
        GLFW.glfwSetWindowSize(windowHandle, width, windowHeight);
    }

    public static void windowSetHeight(int height) {
        GLFW.glfwSetWindowSize(windowHandle, windowWidth, height);
    }

    public static void windowResize(int deltaWidth, int deltaHeight) {
        GLFW.glfwSetWindowSize(windowHandle, windowWidth + deltaWidth, windowHeight + deltaHeight);
    }

    public static void windowSetSizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        GLFW.glfwSetWindowSizeLimits(windowHandle, minWidth > -1 ? minWidth : GLFW.GLFW_DONT_CARE,
                minHeight > -1 ? minHeight : GLFW.GLFW_DONT_CARE, maxWidth > -1 ? maxWidth : GLFW.GLFW_DONT_CARE,
                maxHeight > -1 ? maxHeight : GLFW.GLFW_DONT_CARE);
    }

    public static void windowSetPosition(int x, int y) {
        GLFW.glfwSetWindowPos(windowHandle, x, y);
    }

    public static void windowMinimize() {
        GLFW.glfwIconifyWindow(windowHandle);
        windowMinimized = true;
    }

    public static void windowMaximize() {
        GLFW.glfwMaximizeWindow(windowHandle);
        windowMaximized = true;
    }

    public static void windowFocus() {
        windowFocused = true;
        GLFW.glfwFocusWindow(windowHandle);
    }

    public static void windowFlash() {
        GLFW.glfwRequestWindowAttention(windowHandle);
    }

    protected static void windowSetIcon(final String path) {
        windowIconPath = path;
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer imageData = STBImage.stbi_load(path, width, height, channels, 4);
        if (imageData == null) throw new ApplicationException("Failed to load icon image: " + STBImage.stbi_failure_reason());
        GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
        iconBuffer.position(0).width(width.get(0)).height(height.get(0)).pixels(imageData);
        GLFW.glfwSetWindowIcon(windowHandle, iconBuffer);
        STBImage.stbi_image_free(imageData);
    }

    public static void windowSetTitle(final String title) {
        windowTitle = title == null ? "" : title;
        GLFW.glfwSetWindowTitle(windowHandle, windowTitle);
    }

    // TODO: test
    public static void enableVSync() {
        windowVSyncEnabled = true;
        GLFW.glfwSwapInterval(1);
        // need to restart.
    }

    // TODO: test
    public static void disableVSync() {
        windowVSyncEnabled = false;
        GLFW.glfwSwapInterval(1);
        // need to restart.
    }

    // TODO: test
    public static boolean isVSyncEnabled() {
        return windowVSyncEnabled;
    }

    /* Getters */

    public static boolean isWindowMinimized() {
        return windowMinimized;
    }

    public static boolean isWindowMaximized() {
        return windowMaximized;
    }

    public static String getWindowIconPath() { return windowIconPath; }

    public static long getWindowHandle() { return windowHandle; }

    public static int getWindowWidth() {
        return windowWidth;
    }

    public static int getWindowHeight() {
        return windowHeight;
    }

    public static int getWindowPosX() { return windowPosX; }

    public static int getWindowPosY() { return windowPosY; }

    public String getWindowTitle() {
        return windowTitle;
    }

    public static int getWindowLastDragAndDropFileCount() {
        return windowLastDragAndDropFileCount;
    }

    public static Array<String> getWindowFilesDraggedAndDropped() {
        return windowFilesDraggedAndDropped;
    }





}
