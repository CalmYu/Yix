package yu.rainash.yix;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class Utils {

    private static final int BUFFER_SIZE = 8 * 1024;


    static File makeDir(File parent, String dirName) {
        File dir = new File(parent, dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    static boolean copy(File source, File dest) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(dest);
            byte[] buffer = new byte[8 * 1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }
        return false;
    }

    static void copyFile(InputStream source, OutputStream dest) {
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
    static void copyFile(File source, File dest) {
        try {
            copyFile(new FileInputStream(source), new FileOutputStream(dest));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static boolean isEmptyDir(File dir) {
        if (dir == null || !dir.exists()) {
            return true;
        }
        File[] files = dir.listFiles();
        return  (files == null || files.length == 0);
    }

    static String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    static boolean clearDirRecursively(File dir) {
        return cleanDir(dir);
    }

    private static boolean cleanDir(File dir) {
        if (dir.isDirectory()) {
            final File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        cleanDir(file);
                    }
                    if (!file.delete()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
