package yu.rainash.yix;

import java.io.File;

/**
 * Author: luke.yujb
 * Date: 19/5/13
 * Description:
 */
class PatchDirectory {

    public File apkFile;

    public File libraryDir;

    public PatchDirectory(File apkFile, File libraryDir) {
        this.apkFile = apkFile;
        if (isEmptyDir(libraryDir)) {
            this.libraryDir = null;
        } else {
            this.libraryDir = libraryDir;
        }
    }

    private boolean isEmptyDir(File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            return true;
        }
        File[] subs = dir.listFiles();
        return subs == null || subs.length == 0;
    }

    String getApkPath() {
        return apkFile.getPath();
    }

    String getLibrarySearchPath() {
        if (libraryDir == null) {
            return null;
        }
        return libraryDir.getAbsolutePath();
    }

}
