package zjhj.com.demo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import zjhj.com.demo.MainActivity;
import zjhj.com.demo.utils.L;

/**
 * CreateTime 2019/12/5 14:46
 * Author LiuShiHua
 * Description：
 */
public class AppProtectReceiver extends BroadcastReceiver {

    private final String CHECK_START_PROTECT_APP_ACTION_NAME = "ZJHJ_CHECT_TO_SHART_SELF";
    private String packageName, className;
    private final long TIME_CHECK_MILILLS = 40 * 1000;//超时时长
    private Context context;
    private final int HANDLER_TIME_OUT = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == HANDLER_TIME_OUT) {
                sendCheckBroadcastReceiver();
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        L.d("I收到广播:" + intent.getAction());
        packageName = intent.getStringExtra("packageName");
        className = intent.getStringExtra("className");
        startCheckTimer();
    }

    //执行任务
    private void startCheckTimer() {
        sendCheckBroadcastReceiver();
        handler.removeMessages(HANDLER_TIME_OUT);
        handler.sendEmptyMessageDelayed(HANDLER_TIME_OUT, TIME_CHECK_MILILLS);
    }

    private void sendCheckBroadcastReceiver() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("packageName", packageName);
        intent.putExtra("className", className);
        intent.setAction(CHECK_START_PROTECT_APP_ACTION_NAME);
        context.sendBroadcast(intent);//发送标准广播
    }

    private void startThisActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("packageName", packageName);
        intent.putExtra("className", className);
        intent.putExtra("isOpenApp", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void startProtectApp() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("packageName", packageName);
        intent.putExtra("className", className);
        intent.putExtra("isOpenApp", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
