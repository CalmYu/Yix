package yu.rainash.yix.app;

/**
 * Author: yujingbo
 * Date: 19/5/14
 * Description:
 */
public class TextNative {

    static {
        System.loadLibrary("hello");
    }

    public static native String getContent();

}
