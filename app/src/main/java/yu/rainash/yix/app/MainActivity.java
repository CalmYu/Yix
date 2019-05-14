package yu.rainash.yix.app;

import android.content.DialogInterface;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import yu.rainash.yix.HotFix;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvJava = findViewById(R.id.tv_java);
        TextView tvNative = findViewById(R.id.tv_native);

        tvJava.setText(TextJava.getContent());
        tvNative.setText(TextNative.getContent());

        findViewById(R.id.bt_load_patch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPatch();
            }
        });

        findViewById(R.id.bt_unload_patch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unloadPatch();
            }
        });
    }

    void loadPatch() {
        File patchFile = new File(getFilesDir(), "patch.apk");
        FileUtils.copyAsset(this, "patch.apk", patchFile);
        HotFix.getInstance().installPatch(patchFile);
        new AlertDialog.Builder(this).setTitle("Hint")
                .setMessage("重启后生效")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Process.killProcess(Process.myPid());
                    }
                }).show();
    }

    void unloadPatch() {
        HotFix.getInstance().unInstallPatch();
        new AlertDialog.Builder(this).setTitle("Hint")
                .setMessage("重启后生效")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Process.killProcess(Process.myPid());
                    }
                }).show();
    }
}
