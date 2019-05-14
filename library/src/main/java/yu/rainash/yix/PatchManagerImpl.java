package yu.rainash.yix;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.PathClassLoader;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 * yix
 *  - 20000
 *  - 10000
 *  - 20000_pre
 * <p>
 * install:
 * patch.apk, xx.so -> XX_pre
 * load:
 * XX_pre -> XX
 * uninstall:
 * del XX, del XX_pre
 * <p>
 * patchdir:
 *  - patch.apk
 *  - lib
 *      - xx.so
 *      - yy.so
 */
public class PatchManagerImpl implements PatchManager {

    private static final String TAG = "PatchManager";

    private static final String ABI_ARM = "armeabi";

    private static final String WORKING_DIR_NAME = "yix";

    private static final String PATCH_FILE_NAME = "patch.apk";

    private static final String SUCCESS_FILE_NAME = "success";

    private static final String LIB_DIR_NAME = "lib";

    private Context mContext;

    private SignatureVerifier signatureVerifier;

    private boolean hasLoaded = false;

    private InterceptorClassLoader mInterceptorClassLoader;

    public PatchManagerImpl(Context context, SignatureVerifier signatureVerifier) {
        mContext = context.getApplicationContext();
        this.signatureVerifier = signatureVerifier;
    }

    /**
     * put patch into pre install directory, do dex opt
     *
     * @param patchFile     补丁文件
     * @param targetVersion 目标版本
     */
    @Override
    public void installPatch(File patchFile, int targetVersion) {
        preInstallPatchInternal(patchFile, targetVersion);
    }

    /**
     * rename pre-install patch directory and load patch
     *
     * @param versionCode 当前版本
     * @return
     */
    @Override
    public void loadPatch(int versionCode) throws PatchLoadFailureException, SignatureInvalidateException {
        PatchDirectory patchDirectory = preLoadPatch(versionCode);
        signatureVerifier.checkSignature(mContext, patchDirectory.apkFile);
        loadPatchInternal(mContext.getClassLoader(), patchDirectory);
    }

    @Override
    public void unInstallPatch(int versionCode) {
        // remove patch
        File patchDir = getPatchDir(versionCode);
        if (patchDir.exists()) {
            Utils.clearDirRecursively(patchDir);
        }
        File prePatchDir = getPrePatchDir(versionCode);
        if (prePatchDir.exists()) {
            Utils.clearDirRecursively(prePatchDir);
        }
    }

    @Override
    public boolean hasWorkingPatch(int versionCode) {
        File patchFile = new File(getPatchDir(versionCode), PATCH_FILE_NAME);
        File prePatchFile = new File(getPrePatchDir(versionCode), PATCH_FILE_NAME);
        if (patchFile.exists() || prePatchFile.exists()) {
            return true;
        }
        return false;
    }

    /**
     * all patch will be stored here
     */
    File getWorkingDir() {
        return Utils.makeDir(mContext.getFilesDir(), WORKING_DIR_NAME);
    }

    /**
     * patch dir for current version
     */
    File getPatchDir(int versionCode) {
        return Utils.makeDir(getWorkingDir(), getPatchDirName(versionCode));
    }

    /**
     * pre-installed patch directory
     */
    File getPrePatchDir(int versionCode) {
        return Utils.makeDir(getWorkingDir(), getPrePatchDirName(versionCode));
    }

    /**
     * get the library search dir for patch
     */
    File getPatchLibraryDir(File patchDir) {
        return Utils.makeDir(patchDir, LIB_DIR_NAME);
    }

    File getPatchApkFile(File patchDir) {
        return new File(patchDir, PATCH_FILE_NAME);
    }

    private String getPatchDirName(int versionCode) {
        return String.valueOf(versionCode);
    }

    private String getPrePatchDirName(int versionCode) {
        return String.format("%s_pre", getPatchDirName(versionCode));
    }

    private synchronized void loadPatchInternal(ClassLoader appClassLoader, PatchDirectory patchDirectory) throws PatchLoadFailureException {
        if (hasLoaded || patchDirectory.apkFile == null) {
            return;
        }
        checkFilePath(patchDirectory.apkFile.getAbsolutePath());
        try {
            Field ClassLoader_parent = ClassLoader.class.getDeclaredField("parent");
            ClassLoader_parent.setAccessible(true);
            ClassLoader rawParent = (ClassLoader) ClassLoader_parent.get(appClassLoader);
            if (rawParent.getClass().getName().equals(InterceptorClassLoader.class.getName())) {
                // 重复加载patch
                Log.w(TAG, "duplicate load patch!");
                return;
            }
            mInterceptorClassLoader = new InterceptorClassLoader(patchDirectory.getApkPath(), patchDirectory.getLibrarySearchPath(), rawParent, appClassLoader);
            ClassLoader_parent.set(appClassLoader, mInterceptorClassLoader);
            hasLoaded = true;
        } catch (Throwable e) {
            // TODO report error
            e.printStackTrace();
            throw new PatchLoadFailureException(e);
        }
    }

    private void checkFilePath(String path) throws PatchLoadFailureException {
        if (path == null || path.contains("..")) {
            throw new PatchLoadFailureException("Patch file path is invalidate");
        }
    }

    PatchDirectory preLoadPatch(int versionCode) throws PatchLoadFailureException {
        File prePatchDir = getPrePatchDir(versionCode);
        File currentPatchDir = getPatchDir(versionCode);
        if (prePatchDir.exists() && checkPatchDir(prePatchDir)) {
            Utils.clearDirRecursively(currentPatchDir);
            currentPatchDir.delete();
            boolean renameSuccess = prePatchDir.renameTo(currentPatchDir);
            if (!renameSuccess) {
                Log.e(TAG, "rename dir failed");
            }
        }
        File patchApk = getPatchApkFile(currentPatchDir);
        if (!patchApk.exists()) {
            throw new PatchLoadFailureException("Patch not exist!");
        }
        File libraryDir = getPatchLibraryDir(currentPatchDir);
        return new PatchDirectory(patchApk, libraryDir);
    }

    private boolean preInstallPatchInternal(File patchFile, int targetVersionCode) {
        File preDir = getPrePatchDir(targetVersionCode);
        File prePatchFile = new File(preDir, PATCH_FILE_NAME);
        boolean clearSuccess = Utils.clearDirRecursively(preDir);
        if (!clearSuccess) {
            Log.e(TAG, "clear pre patch legacy failed");
            return false;
        }
        boolean copySuccess = Utils.copy(patchFile, prePatchFile);
        if (!copySuccess) {
            Log.e(TAG, "copy patch to pre patch directory failed");
            return false;
        }
        try {
            new InstallDexClassLoader(prePatchFile.getAbsolutePath(), getClass().getClassLoader());
            copySoLibrary(preDir, prePatchFile);
            markPreInstallSuccess(preDir);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "preInstall: dex opt failed");
        }
        Log.e(TAG, "pre patch dex opt failed");
        return false;
    }

    private void copySoLibrary(File patchDir, File patchFile) {
        File libDir = getPatchLibraryDir(patchDir);
        ZipFile apkZip = null;
        try {
            apkZip = new ZipFile(patchFile);
            Enumeration<? extends ZipEntry> entries = apkZip.entries();
            String[] supportAbis = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? Build.SUPPORTED_ABIS : new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            Set<String> existAbis = new HashSet<>();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().startsWith("lib/")) {
                    String[] split = entry.getName().split("/");
                    String abi = split[1];
                    existAbis.add(abi);
                }
            }
            String matchedAbi = ABI_ARM;
            for (String abi : supportAbis) {
                if (existAbis.contains(abi)) {
                    matchedAbi = abi;
                }
            }
            entries = apkZip.entries();
            String abiPrefix = String.format("lib/%s", matchedAbi);
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // lib/armeabi/xxx.so
                if (entry.getName().startsWith(abiPrefix)) {
                    String[] split = entry.getName().split("/");
                    String soName = split[split.length - 1];
                    InputStream ins = apkZip.getInputStream(entry);
                    FileOutputStream out = new FileOutputStream(new File(libDir, soName));
                    Utils.copyFile(ins, out);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Utils.closeQuietly(apkZip);
            }
        }
    }

    private void markPreInstallSuccess(File dir) {
        File file = new File(dir, SUCCESS_FILE_NAME);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPatchDir(File dir) {
        File successFile = new File(dir, SUCCESS_FILE_NAME);
        return successFile.exists();
    }

    static class InstallDexClassLoader extends PathClassLoader {

        InstallDexClassLoader(String dexPath, ClassLoader parent) {
            super(dexPath, parent);
        }

    }
}
