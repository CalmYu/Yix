package yu.rainash.yix.app;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    private static final int BUFFER_SIZE = 4096;

    /**
     * 递归删除文件和文件夹
     */
    public static boolean deleteRecursively(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        File[] childFile = file.listFiles();
        if (childFile == null || childFile.length == 0) {
            return file.delete();
        }
        for (File f : childFile) {
            deleteRecursively(f);
        }
        return file.delete();
    }

    /**
     * 关闭流
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存流到指定路径
     */
    public static void dump(InputStream inputStream, File dest) {
        try {
            copyFile(inputStream, new FileOutputStream(dest));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void copyAsset(Context context, String assetName, File target) {
        try {
            InputStream ins = context.getAssets().open(assetName);
            OutputStream out = new FileOutputStream(target);
            copyFile(ins, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(InputStream source, OutputStream dest) {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        try {
            while ((len = source.read(buffer)) > 0) {
                dest.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(source);
            closeQuietly(dest);
        }
    }

    /**
     * 拷贝文件
     */
    public static void copyFile(File source, File dest) {
        try {
            copyFile(new FileInputStream(source), new FileOutputStream(dest));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(File file) {
        try {
            return readStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 字节流读取成字符串
     */
    public static String readStream(InputStream ins) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        try {
            while ((len = ins.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return new String(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(ins);
            closeQuietly(baos);
        }
        return "";
    }

    public static File ensureDir(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

}
