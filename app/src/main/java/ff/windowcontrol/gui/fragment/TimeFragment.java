package ff.windowcontrol.gui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ff.windowcontrol.R;
import ff.windowcontrol.bean.CustomAdapter;
import ff.windowcontrol.bean.SendData;
import ff.windowcontrol.bean.SpTimeController;
import ff.windowcontrol.bean.TimeService;
import ff.windowcontrol.gui.activities.NetworkCallable;

/**
 * Created by Feng on 2018/3/21.
 */

public class TimeFragment extends Fragment {
    public static final String KEY_STORE_TIME_STATE = "key-get-stored-time-from-saved";
    private static final String TAG = "TimeFragment";
    public int Window;



    private TextView tvTime;
    private RecyclerView rcvTimeList;
    private CustomAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setView(view, savedInstanceState);
    }

    public void setView(View view, Bundle savedInstanceState) {
        rcvTimeList = view.findViewById(R.id.rcv_time_list);
        tvTime = (TextView) view.findViewById(R.id.tv_time_frag_test);

        view.findViewById(R.id.btn_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePickerFragment();
            }
        });
        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpTimeController.clear(getContext());
                Activity activity = getActivity();
                if (activity != null) {
                    activity.stopService(new Intent(getContext(), TimeService.class));
                }
                tvTime.setText("STOPPED");
                Object test = getActivity();
                if (test != null && test instanceof NetworkCallable) {
                    if (Window == 0) {
                        ((NetworkCallable) test).getClient().turnSafe(SendData.model_people());
                    } else {
                        ((NetworkCallable) test).getClient().turnSafe(SendData.openwindow());
                    }
                }
            }
        });

        //Restore state
        if (savedInstanceState != null) {
            // Restore value from saved state
            tvTime.setText(savedInstanceState.getCharSequence(KEY_STORE_TIME_STATE));
        }

        rcvTimeList.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvTimeList.setAdapter((adapter = new CustomAdapter<String>(null) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null, false);
                return new MyHolder<String>(itemView) {
                    @Override
                    public void updateView(String data) {
                        TextView tv = itemView.findViewById(android.R.id.text1);
                        tv.setText(data);
                    }
                };
            }
        }));
        updateTimeList();
    }

    private void updateTimeList() {
        adapter.getData().clear();
        List<Long> timeList = SpTimeController.getTimeList(getContext());
        for (Long mTime : timeList) {
            adapter.getData().add(String.valueOf(mTime));
        }
        adapter.notifyDataSetChanged();
    }

    private void showDateTimePickerFragment() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            final android.app.FragmentManager manager = activity.getFragmentManager();
            if (manager == null) return;
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerDialog view, final int year, final int monthOfYear, final int dayOfMonth) {
                            view.dismiss();
                            TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                    view.dismiss();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.YEAR, year);
                                    calendar.set(Calendar.MONTH, monthOfYear);
                                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendar.set(Calendar.MINUTE, minute);
                                    calendar.set(Calendar.SECOND, second);
                                    TimeFragment.this.onDateSet(calendar.getTime());
                                }
                            }, true);
                            tpd.setAccentColor(getResources().getColor(R.color.main_color));
                            tpd.setOkText(android.R.string.ok);
                            tpd.setCancelText(android.R.string.cancel);
                            tpd.show(manager, "Timepickerdialog");
                        }
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            Calendar calendar = Calendar.getInstance();
            dpd.setMinDate(calendar);
            dpd.setAccentColor(getResources().getColor(R.color.main_color));
            dpd.setOkText(android.R.string.ok);
            dpd.setCancelText(android.R.string.cancel);
            dpd.show(manager, "Datepickerdialog");
        }
    }

    private void onDateSet(Date date) {
        /*if(onDateBack != null){
            onDateBack.onDateBack(date);
        }*/
        setDate(date);
    }

    private void setDate(Date date) {
        // Init format
        if (date != null) {
            String timeText = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", java.util.Locale.getDefault()).format(date);
            Log.i(TAG, timeText);
            Long timeMills = date.getTime();
            System.out.println("TIME is" + timeMills);
            SpTimeController.addTime(getContext(), timeMills);
            updateTimeList();
//          timeMills.byteValue();
            Activity mActivity = this.getActivity();
            if (mActivity != null) {
                Intent intent = new Intent(mActivity, TimeService.class);
                if (!TimeService.isMyServiceRunning(getContext(), TimeService.class)) {
                    mActivity.startService(intent);
                } else {
                    EventBus.getDefault().post(new TimeService.TimeUpdateEvent(timeMills));
                }
            }
            tvTime.setText(timeText);
        } else {
            tvTime.setText("");
        }
    }
}