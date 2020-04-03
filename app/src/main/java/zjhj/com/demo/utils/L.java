package zjhj.com.demo.utils;

import android.util.Log;

/**
 * CreateTime 2017/7/14 09:10
 * Author LiuShiHua
 * Description：
 */

public class L {
    private static String TAG = "------->zjhj";

    public static void d(String msg) {
        if (msg == null) {
            msg = "null";
        }
        Log.d(TAG, msg);
    }
}
