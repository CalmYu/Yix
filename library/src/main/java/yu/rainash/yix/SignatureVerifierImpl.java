package yu.rainash.yix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.File;
import java.util.Arrays;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class SignatureVerifierImpl implements SignatureVerifier {

    private boolean checkSignature = true;

    public SignatureVerifierImpl(boolean checkSig) {
        this.checkSignature = checkSig;
    }

    public void checkSignature(Context baseContext, File patchFile) throws SignatureInvalidateException {
        if (!checkSignature) {
            return;
        }
        final PackageManager packageManager = baseContext.getPackageManager();
        final PackageInfo patchPackageInfo = packageManager.getPackageArchiveInfo(patchFile.getAbsolutePath(), PackageManager.GET_SIGNATURES);
        if (patchPackageInfo == null || patchPackageInfo.signatures == null) {
            signatureInvalidate("The signature of patch is empty!");
        }
        try {
            @SuppressLint("PackageManagerGetSignatures")
            final PackageInfo appPackageInfo = packageManager.getPackageInfo(baseContext.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] patchSignature = patchPackageInfo.signatures;
            Signature[] appSignature = appPackageInfo.signatures;
            if (!Arrays.equals(appSignature, patchSignature)) {
                signatureInvalidate("The signature of patch is different from app's");
            }
        } catch (PackageManager.NameNotFoundException e) {
            signatureInvalidate("App package info not found!");
        }
    }

    private static void signatureInvalidate(String reason) throws SignatureInvalidateException {
        throw new SignatureInvalidateException(reason);
    }

}
