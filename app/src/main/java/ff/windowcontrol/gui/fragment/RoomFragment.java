package ff.windowcontrol.gui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suke.widget.SwitchButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ff.windowcontrol.R;
import ff.windowcontrol.bean.AllEvent;
import ff.windowcontrol.bean.SendData;
import ff.windowcontrol.bean.TemperatureEvent;
import ff.windowcontrol.gui.activities.NetworkCallable;

/**
 * Created by Feng on 2018/3/21.
 */

public class RoomFragment  extends Fragment {

    public SwitchButton window;
    public SwitchButton air;
    public TextView temperature;
    public TextView rain;
    public TextView airs;
    public TextView model_text;
    public TextView windowstate;
    public TextView airstate;
    public int state=0;
    public int tate=0;
    public int Window;
    public int Air;
    public int Model=0;
    public String Temperature;
    public int Rain;
    public int Airs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        setView(view, savedInstanceState);
        EventBus.getDefault().register(this);
        return view;
    }
    public void setView(View view, Bundle savedInstanceState) {

        temperature=(TextView)view.findViewById(R.id.tv_temperature);
        rain=(TextView)view.findViewById(R.id.tv_is_rain);
        airs=(TextView)view.findViewById(R.id.tv_air);
        model_text=(TextView)view.findViewById(R.id.tv_model_text);
        windowstate=(TextView)view.findViewById(R.id.tv_window_state);
        airstate=(TextView)view.findViewById(R.id.tv_air_state);
//        btn_window=(Button)view.findViewById(R.id.btn_window);
//        btn_air=(Button)view.findViewById(R.id.btn_air);
        window = (SwitchButton) view.findViewById(R.id.sb_window);
        window.setChecked(false);
        window.isChecked();
        window.toggle();     //switch state
        window.toggle(false);//switch without animation
        window.setShadowEffect(true);//disable shadow effect
        air = (SwitchButton) view.findViewById(R.id.sb_fan);
        air.setChecked(false);
        air.isChecked();
        air.toggle();     //switch state
        air.toggle(false);//switch without animation
        air.setShadowEffect(true);//disable shadow effect

        model_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO do your job
                if (Model == 0) {
                    Object activity = getActivity();
                    if(activity != null && activity instanceof NetworkCallable){
                        ((NetworkCallable)activity).getClient().turnSafe(SendData.model());
                        }

                    model_text.setText("自动");
                    Model = 1;
                    }
                    else if ( Model == 1) {
                    Object activity = getActivity();
                    if(activity != null && activity instanceof NetworkCallable){
                        ((NetworkCallable)activity).getClient().turnSafe(SendData.model_people());
                    }
                    model_text.setText("手动");
                    Model=0;
                }
            }
        });

        window.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton View, boolean isChecked) {
//                //TODO do your job
                if (Model == 0) {
                    if (state == 0) {
                        Object activity = getActivity();
                        if (activity != null && activity instanceof NetworkCallable) {
                            if (Air == 0) {
                                ((NetworkCallable) activity).getClient().turnSafe(SendData.openwindow());
                            } else {
                                ((NetworkCallable) activity).getClient().turnSafe(SendData.openwindow_airrun());
                            }
                        }
                        state = 1;
                        windowstate.setText("开");
                        window.setChecked(true);
                    } else if (state == 1) {
                        Object activity = getActivity();
                        if (activity != null && activity instanceof NetworkCallable) {
                            if (Air == 0) {
                                ((NetworkCallable) activity).getClient().turnSafe(SendData.closewindow());
                            } else {
                                ((NetworkCallable) activity).getClient().turnSafe(SendData.closewindow_airrun());
                            }
                        }
                        state = 0;
                        windowstate.setText("关");
                        window.setChecked(false);
                    }
                }
            }
        });
        air.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton View, boolean isChecked) {
//                //TODO do your job
                if (Model == 0) {
                    if (tate == 0) {
                        Object activity = getActivity();
                        if (activity != null && activity instanceof NetworkCallable) {
                            if (Window == 0) {
                                ((NetworkCallable) activity).getClient().turnSafe(SendData.air());
                            } else {
                                ((NetworkCallable) activity).getClient().turnSafe(SendData.air_windowopen());
                            }
                        }
                        tate = 1;
                        airstate.setText("开");
                        air.setChecked(true);
                    } else if (tate == 1) {
                        Object activity = getActivity();
                        if (activity != null && activity instanceof NetworkCallable) {
                            if (Window == 0) {
                                ((NetworkCallable) activity).getClient().turnSafe(SendData.closeair());
                            } else {
                                ((NetworkCallable) activity).getClient().turnSafe(SendData.closeair_windowopen());
                            }
                        }
                        tate = 0;
                        airstate.setText("关");
                        air.setChecked(false);
                    }
//
                }
            }
        });



    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AllEvent event) {
        Window=event.Getwindow();
        if (Window==0){
            windowstate.setText("关");

        }else if (Window==1){
            windowstate.setText("开");

        }
        Air=event.Getair();
        if (Air==0){
           airstate.setText("关");

        }else if (Air==1){
            airstate.setText("开");

        }

        Rain=event.Getrain();
        if (Rain==0){
            rain.setText("否");
        }else if (Rain==1){
            rain.setText("是");

        }

        Airs=event.Getairs();
        if (Airs==0){
           airs.setText("合格");

        }else if (Airs==1){
          airs.setText("有毒");
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TemperatureEvent event) {
       Temperature=event.getMsg();
        temperature.setText(Temperature+"°C");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
    }

}
