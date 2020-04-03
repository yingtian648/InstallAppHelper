package zjhj.com.demo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import zjhj.com.demo.MainActivity;
import zjhj.com.demo.utils.L;
import zjhj.com.demo.utils.Tools;

/**
 * CreateTime 2019/9/24 15:58
 * Author LiuShiHua
 * Description：开启启动广播
 */
public class AutoOpenAppReceiver extends BroadcastReceiver {
    private String packageName = "zjhj.com.myapplication";
    private String className = "zjhj.com.myapplication.MainActivity";
    private String apkPath = Environment.getExternalStorageDirectory() + "/zjhj.com.dev/mix.apk";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Tools.isAppExist(context, packageName)) {
            L.d("APP不存在，启动APP去安装MixApp");
            Intent i = new Intent(context, MainActivity.class);
            i.putExtra("packageName", packageName);
            i.putExtra("className", className);
            i.putExtra("apkPath", apkPath);
            i.putExtra("isNeedUninstall", false);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
