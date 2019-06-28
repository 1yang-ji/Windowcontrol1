package ff.windowcontrol.gui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ff.windowcontrol.R;
import ff.windowcontrol.bean.AllEvent;
import ff.windowcontrol.bean.ClientWrapper;
import ff.windowcontrol.bean.SendData;
import ff.windowcontrol.bean.VoiceHelper;
import ff.windowcontrol.gui.fragment.RoomFragment;
import ff.windowcontrol.gui.fragment.SafeFragment;
import ff.windowcontrol.gui.fragment.TimeFragment;
import ff.windowcontrol.gui.fragment.WetherFragment;
import ff.windowcontrol.gui.utils.VirtualKeyTools;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;

import static android.media.AudioManager.STREAM_MUSIC;


public class TabWindowActivity extends FragmentActivity implements NetworkCallable {

    private EventManager mWpEventManager;
    private SpeechSynthesizer mSpeechSynthesizer;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
    private static final String TAG = "TabWindowActivity";
    private final int[] testColors = {0xFF8d95a7, 0xFF00796B, 0xFF795548, 0xFF5B4947, 0xFFF57C00};
    public int Window;
    public int Air;


    @Override
    public ClientWrapper getClient() {
        return ClientWrapper.getInstance();
    }

    @SuppressLint({"ObsoleteSdkInt", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_horizontal);
        initialEnv();
        initialTts();
        int color = getResources().getColor(R.color.main_color);
        StatusBarUtil.setColor(this, color, 0);
        EventBus.getDefault().register(this);
        this.setVolumeControlStream(STREAM_MUSIC);
        VirtualKeyTools.hideKeys(this);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            private SparseArray<Fragment> mFragments = new SparseArray<>();
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public Fragment getItem(int position) {
                Fragment fragment = mFragments.get(position);
                if(fragment == null){
                    switch (position){
                        case 0:fragment = new RoomFragment();break;
                        case 1:fragment = new WetherFragment();break;
                        case 2:fragment = new SafeFragment();break;
                        case 3:fragment = new TimeFragment();break;
                        default:fragment = null;break;
                    }
                    mFragments.put(position, fragment);
                }
                return fragment;
            }
        });
        viewPager.setOffscreenPageLimit(4);
        //Setting Navigation-View
        NavigationController navigator = ((PageNavigationView) findViewById(R.id.tab)).material()
                .addItem(R.drawable.room, "房间", testColors[0])
                .addItem(R.drawable.wetcher, "天气", testColors[0])
                .addItem(R.drawable.safe, "防盗", testColors[0])
                .addItem(R.drawable.time, "闹钟", testColors[0])
                .build();
        navigator.setSelect(0);//Select default index
        navigator.setupWithViewPager(viewPager);
        ClientWrapper.getInstance().connect(ClientWrapper.IP_ADDRESS, ClientWrapper.PORT);
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        try{
            ClientWrapper.getInstance().getClient().close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 唤醒功能打开步骤
        // 1) 创建唤醒事件管理器
        mWpEventManager = EventManagerFactory.create(this, "wp");

        // 2) 注册唤醒事件监听器
        mWpEventManager.registerListener(new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                Log.d(TAG, String.format("event: name=%s, params=%s", name, params));
                try {
                    JSONObject json = new JSONObject(params);
                    if ("wp.data".equals(name)) { // 每次唤醒成功, 将会回调name=wp.data的时间, 被激活的唤醒词在params的word字段
                        String word = json.getString("word");
//                        txtResult.append("唤醒成功, 唤醒词: " + word + "\r\n");
                        Toast.makeText(TabWindowActivity.this,"唤醒成功，请说出指令",Toast.LENGTH_LONG).show();
                        speak("奴才在，皇上请吩咐。");
                        //延时3秒，防止语音合成的内容又被语音识别
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent("com.baidu.action.RECOGNIZE_SPEECH");
                        intent.putExtra("grammar", "asset:///baidu_speech_grammar.bsg"); // 设置离线的授权文件(离线模块需要授权), 该语法可以用自定义语义工具生成, 链接http://yuyin.baidu.com/asr#m5
                        startActivityForResult(intent, 1);

                    }
                } catch (JSONException e) {
                    throw new AndroidRuntimeException(e);
                }
            }
        });

        // 3) 通知唤醒管理器, 启动唤醒功能
        HashMap<String, String> params = new HashMap<>();
        params.put("kws-file", "assets:///WakeUp.bin"); // 设置唤醒资源, 唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出
        mWpEventManager.send("wp.ASR_START", new JSONObject(params).toString(), null, 0, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bundle results = data.getExtras();
            assert results != null;
            ArrayList<String> results_recognition = results.getStringArrayList("results_recognition");
            String str = results_recognition + "";
            String res = str.substring(str.indexOf("[") + 1, str.indexOf("]"));
            VoiceHelper voice=new VoiceHelper(res);
            int i=voice.OpenWindow(res);
            int b=voice.OpenAir(res);
            int d=voice.CloseWindow(res);
            int e=voice.CloseAir(res);
            int f=voice.CloseTime(res);
            if (i==1) {
                if (Window==0){
                    speak("好的，马上" + res);
                    Toast.makeText(TabWindowActivity.this, "好的，马上" + res, Toast.LENGTH_LONG).show();
                    if (Air == 0) {
                        ClientWrapper.getInstance().getClient().writedata(SendData.openwindow());
                    } else {
                        ClientWrapper.getInstance().getClient().writedata(SendData.openwindow_airrun());
                    }
                }else if (Window==1){
                    speak("操作失败，因为当前窗户是打开状态");
                }
            }else if (b==2) {
                if (Air==0){
                    speak("好的，马上" + res);
                    Toast.makeText(TabWindowActivity.this, "好的，马上" + res, Toast.LENGTH_LONG).show();
                    if (Window == 0) {
                        ClientWrapper.getInstance().getClient().writedata(SendData.air());
                    } else {
                        ClientWrapper.getInstance().getClient().writedata(SendData.air_windowopen());
                    }
                }else if (Air==1){
                    speak("操作失败，因为当前风扇是打开状态");
                }
            } else if(d==3) {
                if (Window==0){
                    speak("操作失败，因为当前窗户是关闭状态");
                }else if (Window==1){
                    speak("好的，马上" + res);
                    Toast.makeText(TabWindowActivity.this, "好的，马上" + res, Toast.LENGTH_LONG).show();
                    if (Air == 0) {
                        ClientWrapper.getInstance().getClient().writedata(SendData.closewindow());
                    } else {
                        ClientWrapper.getInstance().getClient().writedata(SendData.closewindow_airrun());
                    }
                }
            } else if (e==4) {
                if (Air==0){
                    speak("操作失败，因为当前风扇是关闭状态");
                }else if (Air==1){
                    speak("好的，马上" + res);
                    Toast.makeText(TabWindowActivity.this, "好的，马上" + res, Toast.LENGTH_LONG).show();
                    if (Window == 0) {
                        ClientWrapper.getInstance().getClient().writedata(SendData.closeair());
                    } else {
                        ClientWrapper.getInstance().getClient().writedata(SendData.closeair_windowopen());
                    }
                }
            }else if(f==5){
                speak("好的，马上" + res);
                Toast.makeText(TabWindowActivity.this, "好的，马上" + res, Toast.LENGTH_LONG).show();
                ClientWrapper.getInstance().getClient().writedata(SendData.openwindow());

            }
            else{
                speak("指令有误，请重新输入");
            }
        }
    }


    private void initialTts() {
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        this.mSpeechSynthesizer.setContext(this);
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
            @Override
            public void onSynthesizeStart(String s) {
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

            }

            @Override
            public void onSynthesizeFinish(String s) {

            }

            @Override
            public void onSpeechStart(String s) {

            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {

            }

            @Override
            public void onSpeechFinish(String s) {

            }

            @Override
            public void onError(String s, SpeechError speechError) {

            }
        });
        // 文本模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        // 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了正式离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
        // 如果合成结果出现临时授权文件将要到期的提示，说明使用了临时授权文件，请删除临时授权即可。
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/"
                + LICENSE_FILE_NAME);
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        this.mSpeechSynthesizer.setAppId("11247996"/*这里只是为了让Demo运行使用的APPID,请替换成自己的id。*/);
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        this.mSpeechSynthesizer.setApiKey("IDpyax8kF2Q3qYh4VhuIoGCz",
                "1460951eb2aaf1382c360874f64d9763"/*这里只是为了让Demo正常运行使用APIKey,请替换成自己的APIKey*/);
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "3");
        // 设置Mix模式的合成策略
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 授权检测接口(只是通过AuthInfo进行检验授权是否成功。)
        // AuthInfo接口用于测试开发者是否成功申请了在线或者离线授权，如果测试授权成功了，可以删除AuthInfo部分的代码（该接口首次验证时比较耗时），不会影响正常使用（合成使用时SDK内部会自动验证授权）
        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);

        if (authInfo.isSuccess()) {
            Toast.makeText(this,"auth success",Toast.LENGTH_LONG).show();
        } else {
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            Toast.makeText(this,"auth failed errorMsg=" + errorMsg,Toast.LENGTH_LONG).show();
        }

        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        // 加载离线英文资源（提供离线英文合成功能）
//        int result =
//                mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath
//                        + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        //Toast.makeText(this,"loadEnglishModel result=" + result,Toast.LENGTH_LONG).show();

        //打印引擎信息和model基本信息
        //printEngineInfo();
    }
    private void speak(String text) {
        int result = this.mSpeechSynthesizer.speak(text);
        if (result < 0) {
            Toast.makeText(this,"error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ",Toast.LENGTH_LONG).show();
        }
    }

    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        copyFromAssetsToSdcard(LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);
        copyFromAssetsToSdcard("english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard("english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard("english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME);
    }

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *  @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(String source, String dest) {
        File file = new File(dest);
        if ((!file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getResources().getAssets().open(source);
                fos = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    protected void onStop () {
        super.onStop();
        // 停止唤醒监听
        mWpEventManager.send("wp.stop", null, null, 0, 0);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AllEvent event) {
        Window=event.Getwindow();
        Air=event.Getair();

    }

}
