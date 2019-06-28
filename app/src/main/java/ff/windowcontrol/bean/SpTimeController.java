package ff.windowcontrol.bean;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 2018/5/9.
 */

public class SpTimeController {
    public static final String KEY_SP_TIMES = "time-i-set-on-device";
    public static final String FILE_KEY_SP_TIMES = "TimeSettings";

    public static List<Long> getTimeList(Context context){
        String savedTimes = getString(context, FILE_KEY_SP_TIMES, KEY_SP_TIMES);
        if(savedTimes != null && savedTimes.length() > 0){
            try{
                Type type = new TypeToken<List<Long>>() {}.getType();
                return new Gson().fromJson(savedTimes, type);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public static void saveTimeList(Context context, List<Long> timeList){
        String timeText = new Gson().toJson(timeList);
        save(context, FILE_KEY_SP_TIMES, KEY_SP_TIMES, timeText);
    }

    public static void clear(Context context){
        save(context, FILE_KEY_SP_TIMES, KEY_SP_TIMES, "");
    }

    public static void addTime(Context context, Long time){
        List<Long> timeList = getTimeList(context);
        if(timeList == null){
            timeList = new ArrayList<>();
        }
        timeList.add(time);
        saveTimeList(context, timeList);
    }

    public static void removeTime(Context context, Long time){
        List<Long> timeList = getTimeList(context);
        if(timeList == null){
            timeList = new ArrayList<>();
        }
        timeList.remove(time);
        saveTimeList(context, timeList);
    }

    public static String getString(Context context, String fileKey, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static void save(Context context, String fileKey, String fieldKey, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(fieldKey, value);
        editor.apply();
    }
}
