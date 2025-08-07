package com.heavybox.jtix.assets;

import com.google.gson.Gson;
import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.async.AsyncTaskRunner;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Queue;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.memory.MemoryResource;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

// TODO: potential deadlock issue here.
public final class Assets {

    private static final HashMap<String, Asset> store                        = new HashMap<>();
    private static final Queue<AssetDescriptor> storeLoadQueue               = new Queue<>();
    private static final Set<AssetLoadingTask>  storeLoadTasks               = new HashSet<>();
    private static final Set<AssetLoadingTask>  storeCompletedLoadTasks      = new HashSet<>();
    private static final Set<AssetLoadingTask>  storeAfterLoadTasks          = new HashSet<>();
    private static final Set<AssetLoadingTask>  storeCompletedAfterLoadTasks = new HashSet<>();

    private Assets() {}

    /* store */

    public static synchronized String getStoreCatalog() {
        StringBuilder stringBuilder = new StringBuilder();
        for (var entry : store.entrySet()) {
            stringBuilder.append(entry.getKey()).append(" ::: ").append(entry.getValue().data.getClass().getSimpleName()).append("\n");
        }
        return stringBuilder.toString();
    }

    public static synchronized void update() {
        for (AssetLoadingTask task : storeLoadTasks) {
            if (task.readyToCreate())  {
                storeCompletedLoadTasks.add(task);
                storeAfterLoadTasks.add(task);
            }
        }

        storeLoadTasks.removeAll(storeCompletedLoadTasks);
        for (AssetDescriptor descriptor : storeLoadQueue) {
            AssetLoadingTask task = new AssetLoadingTask(descriptor);
            storeLoadTasks.add(task);
            AsyncTaskRunner.async(task);
        }
        storeLoadQueue.clear();

        storeAfterLoadTasks.removeAll(storeCompletedAfterLoadTasks);
        for (AssetLoadingTask task : storeAfterLoadTasks) {
            Asset asset = task.create();
            store.put(asset.descriptor.path, asset);
            storeCompletedAfterLoadTasks.add(task);
        }
    }

    static synchronized Array<Asset> getDependencies(final Array<AssetDescriptor> dependencies) {
        Array<Asset> assets = new Array<>();
        if (dependencies != null) {
            for (AssetDescriptor dependency : dependencies) {
                assets.add(store.get(dependency.path));
            }
        }
        return assets;
    }

    static synchronized boolean areLoaded(final Array<AssetDescriptor> dependencies) {
        if (dependencies == null || dependencies.size == 0) return true;
        for (AssetDescriptor dependency : dependencies) {
            Asset asset = store.get(dependency.path);
            if (asset == null) return false;
        }
        return true;
    }

    public static synchronized boolean isLoaded(final String path) {
        return store.get(path) != null;
    }

    public synchronized static void loadFont(final String filepath) {
        load(Font.class, filepath, null,false);
    }

    public synchronized static void loadTexturePack(final String filepath) {
        load(TexturePack.class, filepath, null,false);
    }

    public synchronized static void loadTexturePack(final String filepath,
                                       Texture.FilterMag magFilter, Texture.FilterMin minFilter,
                                       int anisotropy) {
        final HashMap<String, Object> options = new HashMap<>();
        options.put("anisotropy", anisotropy);
        options.put("magFilter", magFilter);
        options.put("minFilter", minFilter);
        load(Texture.class, filepath, options,false);
    }

    public synchronized static void loadTexture(String filepath) {
        load(Texture.class, filepath, null,false);
    }

    public synchronized static void loadTexture(String filepath,
                                   Texture.FilterMag magFilter, Texture.FilterMin minFilter,
                                   Texture.Wrap uWrap, Texture.Wrap vWrap,
                                   int anisotropy) {
        final HashMap<String, Object> options = new HashMap<>();
        options.put("anisotropy", anisotropy);
        options.put("magFilter", magFilter);
        options.put("minFilter", minFilter);
        options.put("uWrap", uWrap);
        options.put("vWrap", vWrap);
        load(Texture.class, filepath, options,false);
    }

    public synchronized static void loadModel(String filepath) {
        final HashMap<String, Object> options = new HashMap<>();
        load(Model.class, filepath, options, false);
    }

    // use this if you have a models with common textures path.
    public synchronized static void loadModel(String filepath, String texturesPath) {
        final HashMap<String, Object> options = new HashMap<>();
        options.put("texturesFolderPath", texturesPath);
        load(Model.class, filepath, options, false);
    }

    public synchronized static void loadScene(String filepath, String texturesPath) {
        final HashMap<String, Object> options = new HashMap<>();
        options.put("texturesFolderPath", texturesPath);
        load(Scene3D.class, filepath, options, false);
    }

    // TODO: add geometry shader option
    public synchronized static void loadShader(final String name,
                                  final String vertexShaderFilepath,
                                  final String fragmentShaderFilepath) {
        final HashMap<String, Object> options = new HashMap<>();
        options.put("vertexShaderFilepath", vertexShaderFilepath);
        options.put("fragmentShaderFilepath", fragmentShaderFilepath);
        load(Shader.class, name, options,false);
    }

    // TODO: add geometry shader option
    public synchronized static void loadShader(final String name,
                                               final String vertexShaderFilepath,
                                               final String geometryShaderFilepath,
                                               final String fragmentShaderFilepath) {
        final HashMap<String, Object> options = new HashMap<>();
        options.put("vertexShaderFilepath", vertexShaderFilepath);
        options.put("geometryShaderFilepath", geometryShaderFilepath);
        options.put("fragmentShaderFilepath", fragmentShaderFilepath);
        load(Shader.class, name, options,false);
    }

    // TODO: added synchronized here.
    static synchronized void load(Class<? extends MemoryResource> type, String filepath, @Nullable final HashMap<String, Object> options, boolean isDependency) {
        final Asset asset = store.get(filepath);
        if (asset != null) {
            if (isDependency) asset.refCount++;
            return;
        }
        AssetDescriptor descriptor = new AssetDescriptor(type, filepath, options);
        storeLoadQueue.addFirst(descriptor);
    }

    // TODO.
    public static synchronized void unload(final String path) {

    }

    public static synchronized <T extends MemoryResource> T get(final String path) {
        var t = store.get(path);
        if (t == null) throw new AssetsException("File not loaded: " + path + System.lineSeparator() + "Make sure you spelled the file path correctly. You must " +
                "provide the full relative path.");
        return (T) t.data;
    }

    // TODO
    public static synchronized void cleanup() {

    }

    // TODO: removed synchronized; Possible deadlock here.
    public static void finishLoading() {
        while (isLoadingInProgress()) {
            update();
            Thread.yield();
        }
    }

    public static long getTotalStorageBytes() {
        long total = 0;
        for (Map.Entry<String, Asset> assetEntry : store.entrySet()) {
            total += assetEntry.getValue().descriptor.size;
        }
        return total;
    }

    public static synchronized boolean isLoadingInProgress() {
        return !storeLoadQueue.isEmpty() || !storeLoadTasks.isEmpty() || !storeAfterLoadTasks.isEmpty();
    }

    static synchronized AssetLoader<? extends MemoryResource> getNewLoader(Class<? extends MemoryResource> type) {
        if (type == Texture.class)     return new AssetLoaderTexture();
        if (type == Shader.class)      return new AssetLoaderShader();
        if (type == TexturePack.class) return new AssetLoaderTexturePack();
        if (type == Font.class)        return new AssetLoaderFont();
        if (type == Model.class)       return new AssetLoader3DModel();
        if (type == Scene3D.class)  return new AssetLoader3DScene();

        throw new AssetsException("Type: " + type.getSimpleName() + " is not a loadable class type. " +
                "Type must be one of the following: " +
                Texture.class.getSimpleName() + ", " +
                TexturePack.class.getSimpleName() + ", " +
                Font.class.getSimpleName() + ", " +
                Model.class.getSimpleName() + "."); // TODO: add audio.
    }

    public static Yaml yaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(4);
        Representer representer = new Representer(options) {
            @Override
            protected MappingNode representJavaBean(Set<Property> properties, Object obj) {
                if (!classTags.containsKey(obj.getClass())) addClassTag(obj.getClass(), Tag.MAP);
                return super.representJavaBean(properties, obj);
            }
        };
        representer.getPropertyUtils().setSkipMissingProperties(true);
        return new Yaml(representer);
    }

    public static Gson json() {
        return new Gson();
    }

    public static String removeExtension(final String filename) {
        if (filename == null) return null;
        int pos = filename.lastIndexOf(".");
        if (pos == -1) return filename;
        return filename.substring(0, pos);
    }

    public static String getFileContent(final String path) {
        final StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        } catch (IOException e) {
            throw new AssetsException("Failed to read " + String.class.getSimpleName() + " contents of file: " + path + ": " + e.getMessage());
        }
        return builder.toString();
    }

    public static Array<String> getLastDroppedFilePaths() {
        int count = Application.getWindowLastDragAndDropFileCount();
        Array<String> allFileDraggedAndDroppedPaths = Application.getWindowFilesDraggedAndDropped();
        Array<String> lastDroppedFilePaths = new Array<>(5);
        for (int i = 0; i < count; i++) {
            lastDroppedFilePaths.add(allFileDraggedAndDroppedPaths.get(allFileDraggedAndDroppedPaths.size - 1 - i));
        }
        return lastDroppedFilePaths;
    }

    public static Array<String> getDroppedFilesHistory() {
        Array<String> allFileDraggedAndDroppedPaths = Application.getWindowFilesDraggedAndDropped();
        Array<String> droppedFilesHistory = new Array<>(20);
        for (int i = 0; i < allFileDraggedAndDroppedPaths.size; i++) {
            droppedFilesHistory.add(allFileDraggedAndDroppedPaths.get(i));
        }
        return droppedFilesHistory;
    }

    public static void showFileDialogLoad(final String title, final Consumer<String> callback) {
        EventQueue.invokeLater(() -> {
            // Use AWT FileDialog for cross-platform native UI
            FileDialog fileDialog = new FileDialog((Frame) null, title, FileDialog.LOAD);
            fileDialog.setVisible(true);

            String directory = fileDialog.getDirectory();
            String file = fileDialog.getFile();

            Path fullPath = Paths.get(directory, file);
            String fullPathString = fullPath.toString();
            callback.accept(fullPathString);
        });
    }

    public static void showFileDialogSave(final String title, final Consumer<String> callback) {
        EventQueue.invokeLater(() -> {
            FileDialog fileDialog = new FileDialog((Frame) null, title, FileDialog.SAVE);
            fileDialog.setVisible(true);

            String directory = fileDialog.getDirectory();
            String file = fileDialog.getFile();
            Path fullPath = Paths.get(directory, file);
            String fullPathString = fullPath.toString();
            callback.accept(fullPathString);
        });
    }

    public static long getFileSize(final String path) throws IOException {
        Path filePath = Paths.get(path);
        return Files.size(filePath);
    }

    public static Date lastModified(final String filepath) {
        File file = new File(filepath);
        // Get the last modified time in milliseconds since the epoch (Jan 1, 1970)
        long lastModifiedMillis = file.lastModified();
        return new Date(lastModifiedMillis);
    }

    public static boolean filesExist(final String ...filePaths) {
        for (String filepath : filePaths) {
            if (!fileExists(filepath)) return false;
        }
        return true;
    }

    public static boolean fileExists(final String filepath) {
        File file = new File(filepath);
        return file.exists() && file.isFile();
    }

    public static boolean directoryExists(final String path) {
        File directory = new File(path);
        return directory.exists() && directory.isDirectory();
    }

    // TODO: test
    public static Array<String> getDirectoryFiles(final String dirpath, final boolean recursive, final String ...extensions) {
        if (!directoryExists(dirpath)) throw new IllegalArgumentException("Path: " + dirpath + " does not exist or is not a directory.");
        Array<String> paths = new Array<>();
        File directory = new File(dirpath);
        File[] children = directory.listFiles();
        assert children != null;
        for (File child : children) {
            if (child.isFile() && hasExtension(child, extensions)) paths.add(child.getPath());
            else if (child.isDirectory() && recursive) getDirectoryFiles(child.getPath(), extensions, paths);
        }
        return paths;
    }

    private static void getDirectoryFiles(final String dirpath, final String[] extensions, Array<String> collector) {
        File directory = new File(dirpath);
        File[] children = directory.listFiles();
        assert children != null;
        for (File child : children) {
            if (child.isFile() && hasExtension(child, extensions)) collector.add(child.getPath());
            else if (child.isDirectory()) getDirectoryFiles(child.getPath(), extensions, collector);
        }
    }

    private static boolean hasExtension(final File file, final String ...extensions) {
        if (extensions == null || extensions.length == 0) return true;
        for (String extension : extensions) {
            if (file.getName().endsWith(extension)) return true;
        }
        return false;
    }

    public static void saveFile(final String directory, final String filename, final String content) throws IOException {
        if (!directoryExists(directory)) throw new AssetsException("Directory: " + directory + " does not exist.");
        String filePath = directory + File.separator + filename;
        File file = new File(filePath);
        FileWriter fileWriter = new FileWriter(file, false);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(content);
        bufferedWriter.close();
        fileWriter.close();
    }

    public static void saveImage(final String directory, final String filename, BufferedImage image) throws IOException {
        if (!directoryExists(directory)) throw new AssetsException("Directory: " + directory + " does not exist.");
        String filePath = directory + File.separator + filename + ".png";
        File file = new File(filePath);
        ImageIO.write(image, "png", file);
    }

    // TODO: test
    public static void saveImage(final String directory, final String filename, ByteBuffer buffer, int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = (x + width * y) * 4;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                int a = buffer.get(i + 3) & 0xFF;

                int argb = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, argb);
            }
        }
        saveImage(directory, filename, image);
    }

    /** Returns the file extension (without the dot) or an empty string if the file name doesn't contain a dot. */
    public static String getFileExtension(final String name) {
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return "";
        return name.substring(dotIndex + 1);
    }

    /** @return the name of the file, without parent paths or the extension. */
    public static String nameWithoutExtension(final String name) {
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) return name;
        return name.substring(0, dotIndex);
    }

    public static ByteBuffer fileToByteBuffer(String filePath) throws IOException {
        Path path = Path.of(filePath);
        byte[] fileBytes = Files.readAllBytes(path);
        ByteBuffer buffer = MemoryUtil.memAlloc(fileBytes.length);
        buffer.put(fileBytes);
        buffer.flip(); // Prepare the buffer for reading
        return buffer;
    }

    public static Map<String, String> getOSFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilies = ge.getAvailableFontFamilyNames();

        // Specify OS-specific font directories
        String[] fontDirs;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            fontDirs = new String[]{"C:\\Windows\\Fonts"};
        } else if (os.contains("mac")) {
            fontDirs = new String[]{"/System/Library/Fonts", "/Library/Fonts", System.getProperty("user.home") + "/Library/Fonts"};
        } else {
            fontDirs = new String[]{"/usr/share/fonts", "/usr/local/share/fonts", System.getProperty("user.home") + "/.fonts"};
        }

        Map<String, String> fontFamilyPaths = new HashMap<>();
        // Search for font files in the font directories
        for (String fontFamily : fontFamilies) {
            for (String fontDir : fontDirs) {
                File dir = new File(fontDir);
                File[] fontFiles = dir.listFiles((file, name) -> name.toLowerCase().endsWith(".ttf") || name.toLowerCase().endsWith(".otf"));
                if (fontFiles != null) {
                    for (File fontFile : fontFiles) {
                        if (fontFile.getName().toLowerCase().contains(fontFamily.toLowerCase())) {
                            fontFamilyPaths.put(fontFamily, fontFile.getAbsolutePath());
                        }
                    }
                }
            }
        }

        return fontFamilyPaths;
    }

}
