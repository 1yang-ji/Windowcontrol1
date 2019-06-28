package ff.windowcontrol.gui.fragment;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suke.widget.SwitchButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import ff.windowcontrol.R;
import ff.windowcontrol.bean.AllEvent;


/**
 * Created by Feng on 2018/3/21.
 */
@SuppressWarnings("unused")
public class SafeFragment extends Fragment {

    private SwitchButton safe;
    public int state=0;
    public int strInput;
    public Vibrator vibrator;
    public MediaPlayer mediaPlayer;
    public TextView warm;
    public int beep;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safe, container, false);
        setView(view, savedInstanceState);
        warming();
        EventBus.getDefault().register(this);
        return view;

    }
    public void warming(){

        vibrator = (Vibrator)this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);//手机振动
        this.getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer player) {
                player.seekTo(0);
            }
        });
        AssetFileDescriptor file = this.getActivity().getResources().openRawResourceFd(R.raw.beep);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.prepare();
        } catch (IOException ioe) {
            mediaPlayer = null;
        }

    }




    public void setView(View view, Bundle savedInstanceState) {
        warm=(TextView)view.findViewById(R.id.warm);
        safe = (SwitchButton) view.findViewById(R.id.safe);
        safe.setChecked(false);
        safe.isChecked();
        safe.toggle();     //switch state
        safe.toggle(false);//switch without animation
        safe.setShadowEffect(true);//disable shadow effect

        safe.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton View, boolean isChecked) {


                //TODO do your job
                System.out.println(state);
                if (state == 0) {
                    warm.setText("安全模式已启用");
                    state = 1;
                } else if (state == 1) {
                       vibrator.cancel();
                       if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }

                    warm.setText("安全模式未启用");
                    state = 0;
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AllEvent event) {
        strInput=event.Getwarm();
        if(strInput==1) {

            if (state == 1) {

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
                warm.setText("有人靠近，有人靠近！");
                vibrator.vibrate(new long[]{300, 500}, 0);
                beep=1;


            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
    }



}
