package by.nguyencongson.quiz_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import by.nguyencongson.quiz_app.model.User;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {
    private TextView banner;
    private ImageView registerUser;
    private EditText editTextFullName, editTextPassword1, editTextEmail;
    private TextInputEditText editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user2);
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);
        registerUser = (ImageView) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);
        editTextFullName = findViewById(R.id.fullName);
        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullname = editTextFullName.getText().toString().trim();
        if (fullname.isEmpty()) {
            editTextFullName.setError("Full name is required");
            editTextFullName.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Min password length should be characters");
            editTextPassword.requestFocus();
            return;
        }
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = usersRef.orderByChild("userName").equalTo(fullname);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Tên người dùng đã tồn tại, hiển thị thông báo
                    editTextFullName.setError("Username already exists");
                    editTextFullName.requestFocus();
                    return;
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    dialog.show();
                    // Tiếp tục quá trình đăng ký
                    mAuth.createUserWithEmailAndPassword(email, password).
                            addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    User user = new User(fullname, email);
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(fullname)
                                                            .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/quizapp-7cf64.appspot.com/o/15b3eccca749f0f2fe24b3d410466ff3.jpg?alt=media&token=9fe416fe-61e1-4403-a7a4-f742b1bc91b1")).build();
                                                    user1.updateProfile(profileUpdates).addOnCompleteListener(task11 -> {
                                                        Toast.makeText(RegisterUser.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                                        progressBar.setVisibility(View.GONE);
                                                        dialog.dismiss();
                                                        startActivity(new Intent(RegisterUser.this, HomeNavigationActivity.class));
                                                        finish();
                                                    });
                                                    //Log.e("aaaaaaa", user.getEmail());
                                                } else {
                                                    Toast.makeText(RegisterUser.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    dialog.dismiss();
                                                }
                                            });
                                } else {

                                    // Quá trình đăng ký thất bại
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        // Địa chỉ email đã được đăng ký
                                        editTextEmail.setError("Email address is already registered");
                                        editTextEmail.requestFocus();
                                        progressBar.setVisibility(View.GONE);
                                        dialog.dismiss();
                                        return;
                                    } else {
                                        // Xử lý các lỗi đăng ký khác (ví dụ: mật khẩu không hợp lệ, lỗi mạng, ...)
                                        Toast.makeText(RegisterUser.this, "Failed to register: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }

                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần thiết
            }
        });
    }
}