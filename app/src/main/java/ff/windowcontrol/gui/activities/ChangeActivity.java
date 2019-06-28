package ff.windowcontrol.gui.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;

import ff.windowcontrol.R;

/**
 * 视频资源要添加res文件夹下创建raw文件夹
 * 需要在onRestart()方法里重新加载视频，防止退出返回时视频黑屏
 * 我这样写简单粗暴而已，当然，也可优雅的以自己看播放控件的VideoView处理方法，去处理资源释放和播放显示的问题。
 * 记得修改布局控件<com.daqie.videobackground.CustomVideoView...引用的包名，不然会报错哦
 * android:screenOrientation="portrait" 习惯性的把横竖屏切换也设置一下
 * android:theme="@style/Theme.AppCompat.Light.NoActionBar" ActionBar也可以设置成不显示的状态，可以根据自己喜好和项目需求
 */
public class ChangeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置状态栏为透明
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_change);
        initPermission();
        initView();

    }

    private void initView() {

        //按钮初始化
       login=(Button)findViewById(R.id.login);
        login.setOnClickListener(this);
       skip=(Button)findViewById(R.id.skip);
        skip.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                Intent intent = new Intent(ChangeActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.skip:
                Intent intent1 = new Intent(ChangeActivity.this, TabWindowActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }
}
