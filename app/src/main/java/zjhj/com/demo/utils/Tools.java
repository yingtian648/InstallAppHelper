package zjhj.com.demo.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * CreateTime 2019/11/21 13:46
 * Author LiuShiHua
 * Description：
 */
public class Tools {

    /**
     * 判断应用是否存在
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 是否存在
     */
    public static boolean isAppExist(Context context, String packageName) {
        try {
            List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(0);
            for (PackageInfo packageInfo : packageInfoList) {
                if (packageInfo.packageName.equalsIgnoreCase(packageName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            L.d("isAppExist Exception," + e.toString());
        }
        return false;
    }


    //杀死当前App进程
    public static void killSelfAppProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param activity 要判断的Activity
     * @return 是否在前台显示
     */
    public static boolean isForeground(Activity activity) {
        return isForeground(activity, activity.getClass().getName());
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }

    /**
     * 通过Runtime，发送指令，重启系统
     */
    public static void reboot() {
        L.d("reboot");
        try {
            Process proc = Runtime.getRuntime().exec("su -c reboot"); //重启
            proc.waitFor();
        } catch (Exception e) {
            L.d("reboot Exception" + e.getMessage());
            e.printStackTrace();
        }
    }

    public interface FileReadCallBack {
        void getFileContent(String content);
    }

    /**
     * 获取文件内容
     *
     * @param path
     * @return
     */
    public static void getFileContent(final String path, final FileReadCallBack callBack) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                File file = new File(path);
                if (!file.exists() || !file.isFile()) {
                    if (callBack != null)
                        callBack.getFileContent(null);
                }
                byte[] b = new byte[100000];
                try {
                    FileInputStream fis = new FileInputStream(file);
                    int len = fis.read(b);
                    String content = new String(b, 0, len);
                    if (callBack != null)
                        callBack.getFileContent(content);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (callBack != null)
                        callBack.getFileContent(null);
                }
            }
        }.start();
    }
}
