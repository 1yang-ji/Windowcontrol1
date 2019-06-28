package ff.windowcontrol.gui.activities;

/**
 * Created by Feng on 2017/6/6.
 */

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ff.windowcontrol.R;
import ff.windowcontrol.gui.utils.VirtualKeyTools;
import ff.windowcontrol.sqlite.MyDatabaseHelper;
import ff.windowcontrol.sqlite.MyloginCursor;

public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText loginId;
    private EditText loginPassword;
    private TextView loginChangePw;
    private Button loginBtn;
    private CardView cv;
    private String isId, isPs;
    private SQLiteOpenHelper helper;
    private FloatingActionButton fab;

    AnimationDrawable anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(android.R.id.content).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        setContentView(R.layout.activity_login);
        VirtualKeyTools.hideKeys(this);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(3000);
        anim.setExitFadeDuration(1000);

        helper = new MyDatabaseHelper(this);
        initView();
        Intent i = super.getIntent();
        String Id = i.getStringExtra("myId");
        loginId.setText(Id);

    }
    protected int setLayoutId() {
        return R.layout.activity_login;
    }

    // 控件的初始化
    private void initView() {
        loginId = (EditText) findViewById(R.id.loginId);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        loginChangePw=(TextView)findViewById(R.id.loginChangePw);
        loginChangePw.setOnClickListener(this);
        cv = (CardView) findViewById(R.id.cv);
        fab=(FloatingActionButton)findViewById(R.id.loginNewUser);
        fab.setOnClickListener(this);


    }
    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }

    // 控件的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                isId = loginId.getText().toString();
                isPs = loginPassword.getText().toString();
                if (new MyloginCursor(
                        LoginActivity.this.helper.getReadableDatabase()).find(isId)
                        .size() == 0) {
                    // Toast弹窗
                    Toast.makeText(LoginActivity.this, "账号没有注册，请注册后登录",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String lph = new MyloginCursor(
                            LoginActivity.this.helper.getReadableDatabase()).find(
                            isId).toString();
                    // 对查询出来的数据进行拆分
                    String result[] = lph.split(",");
                    if (result[1].equals(isId) && result[2].equals(isPs)) {
                        Toast.makeText(LoginActivity.this, "登录成功",
                                Toast.LENGTH_SHORT).show();
                        Intent a = new Intent(LoginActivity.this,
                                TabWindowActivity.class);
                        startActivity(a);
                    } else {
                        Toast.makeText(LoginActivity.this, "用户名或密码错误",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.loginNewUser:
                Intent i = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(i);
                break;

            case R.id.loginChangePw:
                Intent l = new Intent(LoginActivity.this,
                        ChangePasswordActivity.class);
                startActivity(l);
                break;
            default:
                break;
        }

    }


}
