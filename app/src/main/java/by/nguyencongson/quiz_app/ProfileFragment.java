package by.nguyencongson.quiz_app;

import static by.nguyencongson.quiz_app.HomeNavigationActivity.MY_REQUEST_CODE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.PrimitiveIterator;

import by.nguyencongson.quiz_app.R;
import by.nguyencongson.quiz_app.common.Common;
import by.nguyencongson.quiz_app.model.User;

public class ProfileFragment extends Fragment {
    private View myFragment;
    private ImageView imageAvatar;
    private EditText edtFullname, edt_email;
    private TextView Fullname;
    private Button btnUpdateProfile;
    private Uri uri;
    private SharedPreferences sharedPreferences;
    private RelativeLayout backgroundView;
    private ProgressDialog dialog;
    private HomeNavigationActivity homeNavigationActivity;

    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        sharedPreferences = getContext().getSharedPreferences("THEME", Context.MODE_PRIVATE);
        Common.is_night = sharedPreferences.getBoolean("is_night", false);
        if (Common.is_night == true) {
            Drawable drawable = getResources().getDrawable(R.drawable.background_btn_menu);
            backgroundView.setBackground(drawable);
        }
        else {
            Drawable drawable = getResources().getDrawable(R.drawable.background_banner);
            backgroundView.setBackground(drawable);
        }
        homeNavigationActivity = (HomeNavigationActivity) getActivity();
        dialog = new ProgressDialog(getActivity());
        setUserInformation();
        initListener();
        return myFragment;
    }

    private void initListener() {
        imageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequesPermission();
            }
        });
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateProfile();
            }
        });
    }

    private void onClickUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        dialog.show();
        String strFullName = edtFullname.getText().toString().trim();
        if (uri == null) {
            uri = user.getPhotoUrl();
        }
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(strFullName)
                .setPhotoUri(uri).build();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    User user1 = new User(strFullName, user.getEmail());
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user1);
                    dialog.dismiss();
                    Toast.makeText(getActivity(), "Update profile succes", Toast.LENGTH_SHORT).show();
                    homeNavigationActivity.showUserInfomation();
                    setUserVisibleHint(true);
                }
            }
        });
    }

    private void onClickRequesPermission() {
        if (homeNavigationActivity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            homeNavigationActivity.openGallery();
            return;
        }
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            homeNavigationActivity.openGallery();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }

    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photo = user.getPhotoUrl();
        edtFullname.setText(name);
        Fullname.setText(name);
        edt_email.setText(email);
//        Common.currentUser.setUserName(user.getDisplayName());
//        Common.currentUser.setEmail(user.getEmail());
        Glide.with(getActivity()).load(photo).centerCrop().into(imageAvatar);
    }

    private void init() {
        imageAvatar = (ImageView) myFragment.findViewById(R.id.image_avatar);
        edtFullname = myFragment.findViewById(R.id.edt_full_name);
        Fullname = myFragment.findViewById(R.id.txt_name);
        edt_email = myFragment.findViewById(R.id.edt_email);
        btnUpdateProfile = myFragment.findViewById(R.id.btn_update_profile);
        backgroundView=myFragment.findViewById(R.id.background_view);
    }

    public void setBitmapImageView(Bitmap bitmapImageView) {

        imageAvatar.setImageBitmap(bitmapImageView);
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            Log.i("IsRefresh", "Yes");
        }
    }
}
