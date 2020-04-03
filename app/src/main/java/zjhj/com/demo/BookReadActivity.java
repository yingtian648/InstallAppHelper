package zjhj.com.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import zjhj.com.demo.utils.L;
import zjhj.com.demo.utils.Tools;

public class BookReadActivity extends AppCompatActivity {

    private TextView textView, title;
    private String filePath = null;

    // App 运行时确认麦克风和摄像头设备的使用权限。
    private final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final int PERMISSION_REQ_ID = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_book_read);
        textView = findViewById(R.id.textView);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        title = findViewById(R.id.title);
        title.setText(R.string.app_name);
        //接收文件
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Uri uri = null;
        if (Intent.ACTION_VIEW.equals(action) && "text/plain".equals(type)) {
            uri = intent.getData();
        }
        if (Intent.ACTION_VIEW.equals(action) && "text/html".equals(type)) {
            L.d("收到html文本");
            uri = intent.getData();
        }
        if (uri == null) {
            Toast.makeText(this, "读取文件失败", Toast.LENGTH_SHORT).show();
            return;
        }
        this.filePath = Uri.decode(uri.getEncodedPath());
        loadFile();
    }

    private void loadFile() {
        // 获取权限后，并加入频道。
        if (!checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)) {
            Toast.makeText(this, "读取文件失败,请开启“App安装助手”读写[存储]权限", Toast.LENGTH_SHORT).show();
            return;
        }
        L.d("收到文本-路径：" + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(this, "读取文件失败,文件丢失", Toast.LENGTH_SHORT).show();
            return;
        }
        title.setText(file.getName());
        Tools.getFileContent(filePath, new Tools.FileReadCallBack() {
            @Override
            public void getFileContent(final String content) {
                BookReadActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (content != null)
                            textView.setText(content);
                    }
                });

            }
        });
    }

    /**
     * 权限校验
     *
     * @param permission
     * @param requestCode
     * @return
     */
    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tools.killSelfAppProcess();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) !=
                PackageManager.PERMISSION_GRANTED) {//被拒绝
            Toast.makeText(this, "读取文件失败,请开启“App安装助手”读写[存储]权限", Toast.LENGTH_SHORT).show();
            return;
        }
        loadFile();
    }
}
