package ff.windowcontrol.gui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ff.windowcontrol.R;
import ff.windowcontrol.gui.utils.VirtualKeyTools;

public class ChangePasswordActivity extends Activity implements OnClickListener, OnFocusChangeListener {

    private Button changePwBtn;
    private EditText changePwId;
    private EditText changePw;
    private EditText changePwNew;
    private String myPhone, myPassword, myPwNew;
    private int myflagPhone, myflagPassword, myflagPwNew;
    private SQLiteOpenHelper helper;
    private ff.windowcontrol.sqlite.MytabOperate mylogin;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_password);
        // 数据库辅助类的初始化
        helper = new ff.windowcontrol.sqlite.MyDatabaseHelper(this);
        VirtualKeyTools.hideKeys(this);
        initView();
    }

    /**
     * 控件的初始化
     */
    private void initView() {


        changePwBtn = (Button) findViewById(R.id.changePwBtn);
        changePwBtn.setOnClickListener(this);
        changePwId = (EditText) findViewById(R.id.changePwId);
        changePwId.setOnFocusChangeListener(this);

        changePw = (EditText) findViewById(R.id.changePw);
        changePw.setOnFocusChangeListener(this);

        changePwNew = (EditText) findViewById(R.id.changePwNew);
        changePwNew.setOnFocusChangeListener(this);
        changePwNew.setOnClickListener(this);
        fab=(FloatingActionButton)findViewById(R.id.returnlogin);
        fab.setOnClickListener(this);

    }

    /**
     * 控件的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changePwNew:
                // EditText重新获取焦点
                changePwNew.setFocusable(true);
                changePwNew.setFocusableInTouchMode(true);
                changePwNew.requestFocus();
                changePwNew.findFocus();
                break;
            case R.id.changePwBtn:
                // EditText失去焦点
                changePwNew.setFocusable(false);
                // 获取EditText中的内容
                myPhone = changePwId.getText().toString();
                myPwNew = changePwNew.getText().toString();
                if (myflagPhone == 1 && myflagPassword == 1 && myflagPwNew == 1) {
                    // 取得数据库的写权限
                    mylogin = new ff.windowcontrol.sqlite.MytabOperate(helper.getWritableDatabase());
                    // 更新数据
                    mylogin.updata(myPwNew, myPhone);
                    // Dialog弹窗的实现
                    new AlertDialog.Builder(ChangePasswordActivity.this)
                            .setTitle("提示").setMessage("修改成功！")
                            .setPositiveButton("确认",
                                    // 弹窗内按钮的点击事件
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // Intent跳转事件
                                            Intent i = new Intent(
                                                    ChangePasswordActivity.this,
                                                    LoginActivity.class);
                                            startActivity(i);
                                        }
                                    }).show();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "修改失败",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.returnlogin:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        myPhone = changePwId.getText().toString();
        myPassword = changePw.getText().toString();
        myPwNew = changePwNew.getText().toString();
        switch (v.getId()) {
            case R.id.changePwId:
                if (hasFocus == false) {
                    if (new ff.windowcontrol.sqlite.MyloginCursor(
                            ChangePasswordActivity.this.helper
                                    .getReadableDatabase()).find(myPhone).size() == 0
                            && changePwId.length() != 0) {
                        // 控件的可见性

                    } else {
                        myflagPhone = 1;
                    }
                }
                break;
            case R.id.changePw:
                if (hasFocus == false) {
                    if (myflagPhone == 1) {
                        // 数据的查找，并对查找到的数据进行拆分
                        String result[] = new ff.windowcontrol.sqlite.MyloginCursor(
                                ChangePasswordActivity.this.helper
                                        .getReadableDatabase()).find(myPhone)
                                .toString().split(",");
                        if (myPassword.equals(result[2]) && changePw.length() != 0) {
                            myflagPassword = 1;
                        } else {
                        }
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "请先输入正确的账号",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.changePwNew:
                if (hasFocus == false) {
                    if ((myPwNew.length() < 6 || myPwNew.length() > 20)
                            && myPwNew.length() != 0) {
                    } else {
                        myflagPwNew = 1;
                    }
                }
                break;
            default:
                break;
        }
    }
}
