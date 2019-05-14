package yu.rainash.yix;

import android.content.Context;

import java.io.File;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public interface SignatureVerifier {

    void checkSignature(Context baseContext, File patchFile) throws SignatureInvalidateException;

}
