package by.nguyencongson.quiz_app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import by.nguyencongson.quiz_app.common.Common;
import by.nguyencongson.quiz_app.model.User;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeNavigationActivity extends AppCompatActivity {
    public static final int MY_REQUEST_CODE = 10;
    private ImageView image_avatar;
    private TextView tv_name_user, tv_email_user;
    private MeowBottomNavigation meowBottomNavigation;
    private User user1;
    public Fragment currentFragment = null;
    final private ProfileFragment profileFragment = new ProfileFragment();
    final private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent == null) {
                        return;
                    }
                    Uri uri = intent.getData();
                    profileFragment.setUri(uri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        profileFragment.setBitmapImageView(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_navigation_layout);
        initUi();
        // Kiểm tra kết nối mạng
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            // Hiển thị thông báo
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeNavigationActivity.this);
            builder.setTitle("No network connection")
                    .setMessage("Please check your network connection and try again.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        showUserInfomation();
        //loadFragment(currentFragment);
        defaultFragment();
        meowBottomNavigation.setOnClickMenuListener(model -> {
            switch (model.getId()) {
                case 1:
                    currentFragment = new CategoryFragment(); // Lưu trữ đối tượng Fragment được chuyển đổi gần đây nhất
                    loadFragment(currentFragment);
                    break;

                case 2:
                    currentFragment = new RankingFragment(); // Lưu trữ đối tượng Fragment được chuyển đổi gần đây nhất
                    loadFragment(currentFragment);
                    break;

                case 3:
                    currentFragment = new SettingFragment(); // Lưu trữ đối tượng Fragment được chuyển đổi gần đây nhất
                    loadFragment(currentFragment);
                    break;

                case 4:
                    currentFragment = profileFragment; // Lưu trữ đối tượng Fragment được chuyển đổi gần đây nhất
                    loadFragment(currentFragment);
                    break;
                default:
                    currentFragment = new CategoryFragment(); // Lưu trữ đối tượng Fragment được chuyển đổi gần đây nhất
                    loadFragment(currentFragment);
                    break;

            }
            return null;
        });
    }

    private void initUi() {
        meowBottomNavigation = findViewById(R.id.meowBottomNavigation);
        meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_home_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.rank));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_settings_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.baseline_manage_accounts_24));
        meowBottomNavigation.show(1, true);
    }

    public void showUserInfomation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        user1 = new User(name, email);
        Common.currentUser = user1;
        Uri photo = user.getPhotoUrl();
//        if (name == null) {
//            tv_name_user.setVisibility(View.GONE);
//        } else {
//            tv_name_user.setVisibility(View.VISIBLE);
//            tv_name_user.setText(name);
//            user1 = new User(name, email);
//            Common.currentUser = user1;
//        }
        //tv_email_user.setText(email);
        //Glide.with(this).load(photo).error(R.drawable.baseline_manage_accounts_24).into(image_avatar);
    }

    private void loadFragment(Fragment selectFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectFragment);
        transaction.commit();
    }

    private void defaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new CategoryFragment());
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TESTING", "onDestroy: " + HomeNavigationActivity.class.getName());
    }
    // gọi khi người dùng cấp quyền sử dụng ứng dụng.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {// quyền đã được cấp
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {

            }
        }
    }
//mở thư viện ảnh
    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction((Intent.ACTION_GET_CONTENT));
        resultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            // Hiển thị thông báo
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeNavigationActivity.this);
            builder.setTitle("No network connection")
                    .setMessage("Please check your network connection and try again.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
    }
}
