package zjhj.com.demo.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * CreateTime 2019/10/11 10:07
 * Author LiuShiHua
 * Description：
 */
public class InstallAppUtil {
    public static final int HANDLER_INSTALL_BACK = 65535;//执行安装命令后返回
    public static final int HANDLER_UNINSTALL_APP_BACK = 65534;//执行安装命令后返回
    public static final int SUCCESS = 0;//执行安装命令，成功后返回0
    private static String APK_PATH = Environment.getExternalStorageDirectory() + "/zjhj.com.dev/mix.apk";//APK文件存放路径
    private static final String INSTALLAPP_HELPER_APK_NAME = "installapphelper.apk";

    /**
     * 前置条件：设备已root
     * 执行终端命令
     *
     * @return 0安装正常 1安装失败 -1异常
     */
    public static void runInstallAppCommand(@Nullable final Handler handler, final String apkPath) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (apkPath != null) {
                    APK_PATH = apkPath;
                }
                File file = new File(APK_PATH);
                L.d("待安装APK是否存在：" + file.exists());
                int result = -1;
                DataOutputStream dos = null;
                String cmd = "pm install -r " + APK_PATH;
                try {
                    Process p = Runtime.getRuntime().exec("su");
                    dos = new DataOutputStream(p.getOutputStream());
                    dos.writeBytes(cmd + "\n");
                    dos.flush();
                    dos.writeBytes("exit\n");
                    dos.flush();
                    p.waitFor();
                    result = p.exitValue();
                } catch (Exception e) {
                    L.d("执行安装APP命令 Exception" + e.getMessage());
                } finally {
                    if (dos != null) {
                        try {
                            dos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (handler != null) {
                    Message message = new Message();
                    message.obj = result;
                    message.what = HANDLER_INSTALL_BACK;
                    handler.sendMessage(message);
                }
            }
        }.start();
    }

    /**
     * 启动安装好的APP并关闭当前APP
     *
     * @param activity
     */
    public static void startMainApp(Activity activity, String packageName, String className, int installFlag) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        intent.putExtra("flag", installFlag);
        if (intent.resolveActivityInfo(activity.getPackageManager(), PackageManager.MATCH_DEFAULT_ONLY) != null) {//启动的intent存在
            activity.startActivity(intent);
        } else {
            L.d("应用未安装");
        }
    }

    /**
     * 前置条件：设备已root
     * 执行终端命令
     *
     * @return 0卸载正常 1卸载失败 -1异常
     */
    public static void runUninstallAppCommand(Context context, final String packageName, @Nullable final Handler handler) {
        if (!isAppExist(context, packageName)) {
            L.d("卸载失败，要卸载APP(" + packageName + ")不存在");
            if (handler != null) {
                Message message = new Message();
                message.obj = -1;
                message.what = HANDLER_UNINSTALL_APP_BACK;
                handler.sendMessage(message);
            }
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                L.d("卸载APP(" + packageName + ")，进行中...");
                int result = -1;
                DataOutputStream dos = null;
                String cmd = "pm uninstall " + packageName;
                try {
                    Process p = Runtime.getRuntime().exec("su");
                    dos = new DataOutputStream(p.getOutputStream());
                    dos.writeBytes(cmd + "\n");
                    dos.flush();
                    dos.writeBytes("exit\n");
                    dos.flush();
                    p.waitFor();
                    result = p.exitValue();
                } catch (Exception e) {
                    L.d("执行安装APP命令 Exception" + e.getMessage());
                } finally {
                    if (dos != null) {
                        try {
                            dos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (handler != null) {
                    Message message = new Message();
                    message.obj = result;
                    message.what = HANDLER_UNINSTALL_APP_BACK;
                    handler.sendMessage(message);
                }
            }
        }.start();
    }

    /**
     * 判断应用是否存在
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 是否存在
     */
    private static boolean isAppExist(Context context, String packageName) {
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
}
