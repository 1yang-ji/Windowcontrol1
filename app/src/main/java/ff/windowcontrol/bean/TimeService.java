package ff.windowcontrol.bean;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Feng on 2018/5/8.
 */

public class TimeService extends Service {
    private List<Long> mTimes = new ArrayList<>();
    private boolean isNeed = true;
    private int delayTimeMsDuration = 30000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        improvePriority();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isNeed = false;
        EventBus.getDefault().unregister(this);
        this.countTimeHandler.removeMessages(0);
        this.stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TimService", "Start at " + new Date().toString());
        countTimeHandler.sendEmptyMessage(0);
        updateTimes(getTimers());
        return START_STICKY;
    }



    private List<Long> getTimers(){
        return SpTimeController.getTimeList(getBaseContext());
    }

    private void updateTimes(List<Long> newTimes){
        mTimes.clear();
        if(newTimes != null && newTimes.size() > 0){
            for(long time : newTimes){
                if(time > System.currentTimeMillis()){
                    mTimes.add(time);
                }
            }
            Collections.sort(mTimes);
        }
    }

    private void onTimeUpdated(){
        Log.d("Oops♂","I am coming.....");
        if(mTimes.size() == 0){
            return;
        }
        long lastAtTime = 0;
        for(int i = mTimes.size() - 1; i >= 0; i--){
            long differ = mTimes.get(i) - System.currentTimeMillis();
            if(differ < delayTimeMsDuration){
                long current = mTimes.remove(i);
                if(lastAtTime == 0){
                    lastAtTime = current;
                }
            }
        }
        SpTimeController.saveTimeList(getBaseContext(), mTimes);
        if(lastAtTime != 0){
            onAtTime(lastAtTime);
        }
    }

    private void onAtTime(Long time){
        Log.i("Oops!!!!!!!!!", "Fuck you");
        if(ClientWrapper.getInstance().getClient() == null
                || !ClientWrapper.getInstance().getClient().isConnect()){
            ClientWrapper.getInstance().connect(ClientWrapper.IP_ADDRESS, ClientWrapper.PORT);
        }
        ClientWrapper.getInstance().turnSafe(SendData.time());
    }

    private void improvePriority() {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, TimeService.class), 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Foreground Service")
                .setContentText("Foreground Service Started.")
                .build();
        notification.contentIntent = contentIntent;
        startForeground(0, notification);
    }

    private Handler countTimeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:{
                    message.getTarget().removeMessages(0);
                    onTimeUpdated();
                    if(isNeed){
                        message.getTarget().sendEmptyMessageDelayed(0, delayTimeMsDuration);
                    }
                }break;
            }
            return false;
        }
    });

    public static class TimeUpdateEvent{
        private long newTime;

        public TimeUpdateEvent(long newTime){
            this.newTime = newTime;
        }

        public long getNewTime() {
            return newTime;
        }
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}

