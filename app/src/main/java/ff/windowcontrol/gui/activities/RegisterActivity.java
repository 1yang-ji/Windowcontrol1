package ff.windowcontrol.gui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ff.windowcontrol.R;
import ff.windowcontrol.gui.utils.VirtualKeyTools;
import ff.windowcontrol.sqlite.MyDatabaseHelper;
import ff.windowcontrol.sqlite.MyloginCursor;
import ff.windowcontrol.sqlite.MytabOperate;

public class RegisterActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener {


    private Button registerBtn;
    private EditText registerId;
    private EditText registerPassword;
    private EditText turePassword;
    private String isPhone, isPassword, isTruePassword;
    private int  flagPassword, flagTruePassword;
    private SQLiteOpenHelper helper;
    private MytabOperate mylogin;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        helper = new MyDatabaseHelper(this);
        initView();

        VirtualKeyTools.hideKeys(this);
    }


    private void initView() {

        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);
        registerId = (EditText) findViewById(R.id.registerId);
        registerId.setOnFocusChangeListener(this);
        registerPassword = (EditText) findViewById(R.id.registerPassword);
        registerPassword.setOnFocusChangeListener(this);
        turePassword = (EditText) findViewById(R.id.turePassword);
        turePassword.setOnFocusChangeListener(this);
        fab=(FloatingActionButton)findViewById(R.id.returnlogin);
        fab.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerBtn:
                isPhone = registerId.getText().toString();
                isTruePassword = turePassword.getText().toString();
                if (isTruePassword.equals(isPassword)) {
                    flagTruePassword = 1;
                } else {
                    if (turePassword.length() != 0) {
                    }
                }
                if (flagPassword == 1 && flagTruePassword == 1) {
                    if (new MyloginCursor(
                            RegisterActivity.this.helper.getReadableDatabase())
                            .find(isPhone).size() == 0) {
                        mylogin = new MytabOperate(helper.getWritableDatabase());
                        mylogin.insert(isPhone, isPassword);
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("提示")
                                .setMessage("注册成功！")
                                .setPositiveButton("确认",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                Intent i = new Intent(
                                                        RegisterActivity.this,
                                                        TabWindowActivity.class);
                                                i.putExtra("myId", isPhone);
                                                RegisterActivity.this.finish();
                                                startActivity(i);
                                            }
                                        }).show();

                    } else {
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("提示").setMessage("用户已存在")
                                .setPositiveButton("确认", null).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败",
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
        isPhone = registerId.getText().toString();
        isPassword = registerPassword.getText().toString();
        isTruePassword = turePassword.getText().toString();
        switch (v.getId()) {
            case R.id.registerPassword:
                if (hasFocus == false) {
                    if ((isPassword.length() < 6 || isPassword.length() > 20)
                            && isPassword.length() != 0) {
                    } else {
                        flagPassword = 1;
                    }
                }
                break;
            default:
                break;
        }
    }
}
