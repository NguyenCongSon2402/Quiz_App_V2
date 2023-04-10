package by.nguyencongson.quiz_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import by.nguyencongson.quiz_app.common.Common;
import by.nguyencongson.quiz_app.model.User;

public class HomeNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    public static final int MY_REQUEST_CODE = 10;
    private ImageView image_avatar;
    private TextView tv_name_user, tv_email_user;
    private NavigationView navigationView;
    private Fragment selectFragment = null;
    private User user1;
    final private ProfileFragment profileFragment = new ProfileFragment();
    final private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
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
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_navigation_layout);

        initUi();
        showUserInfomation();

        // Chưa đến 1 s thì hệ thống nó đã load đến đây r => Common.user null
        //showUserInfomation(emailUsers, Common.currentUser);
        loadDefaultFragment();
    }

    private void initUi() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        tv_name_user = navigationView.getHeaderView(0).findViewById(R.id.tv_name_user);
        tv_email_user = navigationView.getHeaderView(0).findViewById(R.id.tv_email_user);
        image_avatar = navigationView.getHeaderView(0).findViewById(R.id.image_avatar);
    }
    public void showUserInfomation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photo = user.getPhotoUrl();
        if (name == null) {
            tv_name_user.setVisibility(View.GONE);
        } else {
            tv_name_user.setVisibility(View.VISIBLE);
            tv_name_user.setText(name);
            user1 = new User(name, email);
            Common.currentUser = user1;
        }
        tv_email_user.setText(email);
        Glide.with(this).load(photo).error(R.drawable.baseline_manage_accounts_24).into(image_avatar);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //Chỗ này đang có 1 bug đó là khi selectFragmentCategory đang đươc hiện thì rồi nhưng khi click vào item home vẫn tạo mới
        // Mình phải bắt được hiện tại nó đang ở fragment nào
        // Sau đo sử lý trong switch case bên dưới

        switch (item.getItemId()) {
            case R.id.nav_home:// giờ xử lí ntn anh
                if (selectFragment != null && selectFragment instanceof CategoryFragment) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    //a vừa thêm z
                    //getSupportFragmentManager().popBackStack("FRAG", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    selectFragment = null;
                    selectFragment = CategoryFragment.newInstance();
                    loadFragment(selectFragment);
                }
                break;
            case R.id.nav_ranking:
                if (selectFragment != null && selectFragment instanceof RankingFragment) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
//                    getSupportFragmentManager().popBackStack("FRAG", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    selectFragment = null;
                    selectFragment = RankingFragment.newInstance();
                    loadFragment(selectFragment);
                }
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.nav_profile:// giờ xử lí ntn anh
//                if (selectFragment != null && selectFragment instanceof ProfileFragment) {
//                    mDrawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    //a vừa thêm z
//                    //getSupportFragmentManager().popBackStack("FRAG", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    selectFragment = null;
//                    selectFragment = profileFragment;
//                    loadFragment(selectFragment);
//                }
                selectFragment = profileFragment;
                loadFragment(selectFragment);
                break;

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment selectFragment) {
        //Nên sẽ bị ảnh hưởng đến đoạn bên dưới này
        //Nó sẽ add 2 thằng category vào backstack
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectFragment);
        transaction.addToBackStack("FRAG");
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //Nó chạy vào case này ?
            //Khi đấy ở đây phải ấn 2 lần mới chạy vào case else
            //

            navigationView.getMenu().getItem(0).setChecked(true);
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                FragmentManager fm = getSupportFragmentManager(); // or 'getSupportFragmentManager();'
                int count = fm.getBackStackEntryCount();
                for (int i = 0; i < count; ++i) {
                    fm.popBackStack();
                }
                Log.e("LOG", "HE");
                getSupportFragmentManager().popBackStack("FRAG", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else {

                // Close
                // getSupportFragmentManager().popBackStack("FRAGHOME", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Log.e("LOG", "HA");
//                super.onBackPressed();
                System.exit(0);
            }
//

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TESTING", "onDestroy: " + HomeNavigationActivity.class.getName());
    }

    private void loadDefaultFragment() {
        selectFragment = CategoryFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectFragment);
        transaction.commit();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {

            }
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction((Intent.ACTION_GET_CONTENT));
        resultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

}
