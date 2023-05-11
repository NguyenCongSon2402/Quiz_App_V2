package by.nguyencongson.quiz_app;

import android.content.SharedPreferences;
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

public class SettingFragment extends Fragment {
    private SwitchCompat switchCompat;
    private View myFragment;
    private ImageView imageAvatar;
    private HomeNavigationActivity homeNavigationActivity;

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_setting, container, false);
        init();
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Lưu trạng thái của SwitchCompat vào Preferences
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                editor.putBoolean("is_dark_mode", isChecked);
                editor.apply();
                // Thay đổi theme của ứng dụng
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                // Tải lại Activity để cập nhật theme mới
                //getActivity().recreate();
            }
        });
//        boolean isDarkMode = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("is_dark_mode", false);
//        if (isDarkMode) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            getContext().setTheme(R.style.Theme_Quiz_App);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            getContext().setTheme(R.style.Theme_Quiz_App);
//        }

        return myFragment;
    }

    private void init() {
        imageAvatar = (ImageView) myFragment.findViewById(R.id.image_avatar);
        switchCompat=myFragment.findViewById(R.id.theme);
    }
}