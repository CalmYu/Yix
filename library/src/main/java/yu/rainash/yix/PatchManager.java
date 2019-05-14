package yu.rainash.yix;

import java.io.File;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public interface PatchManager {

    /**
     * 安装补丁
     * @param patchFile 补丁文件
     * @param targetVersion 目标版本
     */
    void installPatch(File patchFile, int targetVersion);

    /**
     * 加载补丁
     * @param versionCode 当前版本
     * @return 补丁文件
     */
    void loadPatch(int versionCode) throws PatchLoadFailureException, SignatureInvalidateException;

    /**
     * 卸载补丁
     */
    void unInstallPatch(int versionCode);

    /**
     * 获取当前生效的补丁
     * @param versionCode 当前版本
     */
    boolean hasWorkingPatch(int versionCode);

}
