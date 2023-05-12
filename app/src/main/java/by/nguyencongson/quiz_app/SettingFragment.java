package by.nguyencongson.quiz_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vimalcvs.switchdn.DayNightSwitch;
import com.vimalcvs.switchdn.DayNightSwitchListener;

import by.nguyencongson.quiz_app.common.Common;

public class SettingFragment extends Fragment {
    private SwitchCompat switchCompat;
    private LinearLayout backgroundView;
    private View myFragment;
    private ImageView imageAvatar;
    private SharedPreferences sharedPreferences;
    private HomeNavigationActivity homeNavigationActivity;

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_setting, container, false);
        init();
        sharedPreferences = getContext().getSharedPreferences("THEME", Context.MODE_PRIVATE);
        Common.is_night = sharedPreferences.getBoolean("is_night", false);
        if (Common.is_night == true) {
            switchCompat.setChecked(true);
            Drawable drawable = getResources().getDrawable(R.drawable.background_btn_menu);
            backgroundView.setBackground(drawable);
        } else {
            switchCompat.setChecked(false);
            Drawable drawable = getResources().getDrawable(R.drawable.background_banner);
            backgroundView.setBackground(drawable);
        }
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if ((isChecked == true)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_night", isChecked);
                    editor.apply();
                    Drawable drawable = getResources().getDrawable(R.drawable.background_btn_menu);
                    backgroundView.setBackground(drawable);
                    Toast.makeText(getContext(), "Light mode", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_night", isChecked);
                    editor.apply();
                    Drawable drawable = getResources().getDrawable(R.drawable.background_banner);
                    backgroundView.setBackground(drawable);
                    Toast.makeText(getContext(), "Dark mode", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return myFragment;
    }

    private void init() {
        imageAvatar = (ImageView) myFragment.findViewById(R.id.image_avatar);
        switchCompat = myFragment.findViewById(R.id.theme);
        backgroundView = myFragment.findViewById(R.id.background_view);
    }
}