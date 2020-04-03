package zjhj.com.demo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;


import java.util.List;

import zjhj.com.demo.utils.InstallAppUtil;
import zjhj.com.demo.utils.L;
import zjhj.com.demo.utils.Tools;


public class MainActivity extends AppCompatActivity {

    private TextView text;
    private String succ = "安装成功，";
    private String err = "安装失败，";
    private String un_err = "卸载失败，";
    private final int HANDLER_TIME_SUCC = 1;
    private final int HANDLER_TIME_ERR = 2;
    private final int HANDLER_WAIT_UNINSTALL = 3;
    private final int HANDLER_UNINSTALL_ERR = 4;
    private final int HANDLER_FINISH_SELF = 5;
    private final int HANDLER_FINISH_SELF_DELAY = 60 * 1000;//结束当前页面的延时时长
    private int err_times = 0;
    private int time = 10;
    private String packageName = null;
    private String className = null;
    private String apkPath = null;
    private boolean isNeedUninstall = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_WAIT_UNINSTALL://等待卸载
                    text.setText("卸载中,请稍后...");
                    startUninstall();
                    break;
                case InstallAppUtil.HANDLER_INSTALL_BACK:
                    int installResult = (int) msg.obj;
                    L.d("安装APP返回：" + installResult);
                    if (installResult == InstallAppUtil.SUCCESS) {//安装成功
                        text.setText(succ + time + "秒后进入主界面");
                        handler.sendEmptyMessageDelayed(HANDLER_TIME_SUCC, 1000);
                    } else {
                        text.setTextColor(Color.RED);
                        text.setText(err + time + "秒后进入主界面");
                        handler.sendEmptyMessageDelayed(HANDLER_TIME_ERR, 1000);
                    }
                    break;
                case InstallAppUtil.HANDLER_UNINSTALL_APP_BACK:
                    int uninstallResult = (int) msg.obj;
                    if (uninstallResult == InstallAppUtil.SUCCESS) {//卸载成功
                        text.setText("卸载成功，正在安装...");
                        startInstallApp();
                    } else {
                        text.setTextColor(Color.RED);
                        text.setText(un_err + time + "秒后进入主界面");
                        handler.sendEmptyMessageDelayed(HANDLER_UNINSTALL_ERR, 1000);
                    }
                    break;
                case HANDLER_TIME_SUCC:
                    time--;
                    if (time < 0) {
                        handler.removeMessages(HANDLER_TIME_SUCC);
                        InstallAppUtil.startMainApp(MainActivity.this, packageName, className, 1);
                        handler.sendEmptyMessageDelayed(HANDLER_FINISH_SELF, HANDLER_FINISH_SELF_DELAY);
                    } else {
                        text.setText(succ + time + "秒后进入主界面");
                        handler.sendEmptyMessageDelayed(HANDLER_TIME_SUCC, 1000);
                    }
                    break;
                case HANDLER_TIME_ERR:
                    time--;
                    if (time < 0) {
                        handler.removeMessages(HANDLER_TIME_ERR);
                        InstallAppUtil.startMainApp(MainActivity.this, packageName, className, 2);
                        handler.sendEmptyMessageDelayed(HANDLER_FINISH_SELF, HANDLER_FINISH_SELF_DELAY);
                    } else {
                        text.setText(err + time + "秒后进入主界面");
                        handler.sendEmptyMessageDelayed(HANDLER_TIME_ERR, 1000);
                    }
                    break;
                case HANDLER_UNINSTALL_ERR:
                    time--;
                    if (time < 0) {
                        handler.removeMessages(HANDLER_UNINSTALL_ERR);
                        InstallAppUtil.startMainApp(MainActivity.this, packageName, className, 2);
                        handler.sendEmptyMessageDelayed(HANDLER_FINISH_SELF, HANDLER_FINISH_SELF_DELAY);
                    } else {
                        text.setText(un_err + time + "秒后进入主界面");
                        handler.sendEmptyMessageDelayed(HANDLER_UNINSTALL_ERR, 1000);
                    }
                    break;
                case HANDLER_FINISH_SELF:
                    if (Tools.isForeground(MainActivity.this)) {
                        if (Tools.isAppExist(MainActivity.this, packageName)) {
                            L.d("还在安装助手页面，未跳转，App存在 → 重启");
                            reboot();
                        } else {
                            if (err_times == 3) {
                                finish();
                            } else {
                                startInstallApp();
                            }
                            err_times++;
                        }
                    } else {
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.text);
        initData();
    }

    private void initData() {
        err_times = 0;
        Intent intent = this.getIntent();
        packageName = intent.getStringExtra("packageName");
        className = intent.getStringExtra("className");
        apkPath = intent.getStringExtra("apkPath");
        isNeedUninstall = intent.getBooleanExtra("isNeedUninstall", false);
        L.d("packageName:" + packageName);
        L.d("className:" + className);
        L.d("apkPath:" + apkPath);
        L.d("isNeedUninstall:" + isNeedUninstall);
        if (isNeedUninstall) {//先卸载
            text.setText("等待卸载,请稍后...");
            handler.sendEmptyMessageDelayed(HANDLER_WAIT_UNINSTALL, 15 * 1000);
        } else {
            startInstallApp();
        }
    }

    private void startInstallApp() {
        text.setTextColor(Color.WHITE);
        if (apkPath == null) {
            L.d("apkPath is null");
            Message message = new Message();
            message.obj = -1;
            message.what = InstallAppUtil.HANDLER_INSTALL_BACK;
            handler.sendMessage(message);
            return;
        }
        InstallAppUtil.runInstallAppCommand(handler, apkPath);
    }

    private void startUninstall() {
        InstallAppUtil.runUninstallAppCommand(MainActivity.this, packageName, handler);
    }

    /**
     * 通过Runtime，发送指令，重启系统
     */
    private void reboot() {
        L.d("reboot");
        try {
            Process proc = Runtime.getRuntime().exec("su -c reboot"); //重启
            proc.waitFor();
        } catch (Exception e) {
            L.d("reboot Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

}
