package ff.windowcontrol.gui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import ff.windowcontrol.R;
import ff.windowcontrol.gui.utils.VirtualKeyTools;

// Add in your root build.gradle at the end of repositories:
// maven { url 'https://jitpack.io' }
public class IntroActivity extends AppIntro {

    private String[] titles;
    private String[] descriptions;

    private int[] drawables={R.drawable.pone,R.drawable.ptwo,R.drawable.pthree,R.drawable.pfour,R.drawable.pfive};
    private int[] colors = {Color.parseColor("#8d95a7")};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.titles = getResources().getStringArray(R.array.intro_title);
        this.descriptions = getResources().getStringArray(R.array.intro_description);

        addSlide(AppIntroFragment.newInstance("功能1","实现自动关窗",this.drawables[0],this.colors[0]));
        addSlide(AppIntroFragment.newInstance("功能2","天气预报查看",this.drawables[1],this.colors[0]));
        addSlide(AppIntroFragment.newInstance("功能3","自动更新空气",this.drawables[2],this.colors[0]));
        addSlide(AppIntroFragment.newInstance("功能4","红外报警装置",this.drawables[3],this.colors[0]));
        addSlide(AppIntroFragment.newInstance("功能5","定时自动开窗",this.drawables[4],this.colors[0]));
        showStatusBar(false);
        showSkipButton(true);

        setBarColor(Color.parseColor("#8d95a7"));//覆盖栏颜色
        setSeparatorColor(Color.parseColor("#8d95a7"));//分隔符颜色
        setNavBarColor("#8d95a7");//设备的导航栏的颜色

        setFadeAnimation();//转换动画
        VirtualKeyTools.hideKeys(this);

    }


    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
       Intent i=new Intent(IntroActivity.this,ChangeActivity.class);
       startActivity(i);
       finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent i=new Intent(IntroActivity.this,ChangeActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }


}