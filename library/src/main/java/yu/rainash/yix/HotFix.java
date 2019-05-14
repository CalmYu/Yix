package yu.rainash.yix;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public enum HotFix {

    INSTANCE;

    private static final String TAG = "htfx";

    private PatchManager patchManager;

    private int versionCode;

    public static HotFix getInstance() {
        return INSTANCE;
    }

    public void initialize(Context context, int versionCode, boolean checkSignature) {
        this.versionCode = versionCode;
        patchManager = new PatchManagerImpl(context.getApplicationContext(),
                new SignatureVerifierImpl(checkSignature));
    }

    public void installPatch(File patchFile) {
        patchManager.installPatch(patchFile, versionCode);
    }

    /**
     * @return if load successful
     */
    public boolean loadPatchIfExist() {
        try {
            patchManager.loadPatch(versionCode);
            return true;
        } catch (PatchLoadFailureException e) {
            e.printStackTrace();
            Log.i(TAG, "Patch load failed: " + e.getMessage());
        } catch (SignatureInvalidateException e) {
            e.printStackTrace();
            Log.w(TAG, "Patch load failed: " + e.getMessage());
        }
        return false;
    }

    public boolean hasWorkingPatch() {
        return patchManager.hasWorkingPatch(versionCode);
    }

    public void unInstallPatch() {
        patchManager.unInstallPatch(versionCode);
    }

}
