package yu.rainash.yix.app;

import android.app.Application;

import yu.rainash.yix.BuildConfig;
import yu.rainash.yix.HotFix;

/**
 * Author: yujingbo
 * Date: 19/5/14
 * Description:
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HotFix.getInstance().initialize(this, BuildConfig.VERSION_CODE, false);
        HotFix.getInstance().loadPatchIfExist();
    }
}
