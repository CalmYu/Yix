package yu.rainash.yix;

/**
 * Author: luke.yujb
 * Date: 19/4/12
 * Description:
 */
public class PatchLoadFailureException extends Exception {

    public PatchLoadFailureException(String message) {
        super(message);
    }

    public PatchLoadFailureException(Throwable e) {
        super(e);
    }

}
