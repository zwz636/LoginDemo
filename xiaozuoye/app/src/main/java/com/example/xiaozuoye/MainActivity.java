package com.example.xiaozuoye;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.xiaozuoye.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView resultTextView;
    private ProgressBar progressBar;
    private RadioGroup modeRadioGroup;
    private RadioButton userModeRadio;
    private RadioButton adminModeRadio;
    private CustomButton registerButton;
    private boolean isUserMode = true;
    private int selectedAvatarId = R.drawable.two;
    private CustomButton loginButton;
    private AppDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new AppDatabaseHelper(this);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        progressBar = findViewById(R.id.progressBar);
        resultTextView = findViewById(R.id.resultTextView);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        modeRadioGroup = findViewById(R.id.modeRadioGroup);
        userModeRadio = findViewById(R.id.userModeRadio);
        adminModeRadio = findViewById(R.id.adminModeRadio);

        setupAvatarSelection();
        setupModeSelection();

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptLogin();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
    }
    private void setupModeSelection() {
        modeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.userModeRadio) {
                    isUserMode = true;
                    registerButton.setVisibility(View.VISIBLE);
                    loginButton.setButtonText("登录");
                } else if (checkedId == R.id.adminModeRadio) {
                    isUserMode = false;
                    registerButton.setVisibility(View.GONE);
                    loginButton.setButtonText("管理员登录");
                }
            }
        });
        userModeRadio.setChecked(true);
    }

    private void setupAvatarSelection() {
        List<ImageView> avatars = new ArrayList<>();
        avatars.add(findViewById(R.id.avatar1));
        avatars.add(findViewById(R.id.avatar2));
        avatars.add(findViewById(R.id.avatar3));

        for (final ImageView avatar : avatars) {
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (ImageView av : avatars) {
                        av.setBackgroundResource(R.drawable.one);
                    }
                    avatar.setBackgroundResource(R.drawable.one);

                    int avatarId = avatar.getId();
                    if (avatarId == R.id.avatar1) {
                        selectedAvatarId = R.drawable.two;
                    } else if (avatarId == R.id.avatar2) {
                        selectedAvatarId = R.drawable.three;
                    } else if (avatarId == R.id.avatar3) {
                        selectedAvatarId = R.drawable.four;
                    } else {
                        selectedAvatarId = R.drawable.two;
                    }

                    Toast.makeText(MainActivity.this, "选择了头像", Toast.LENGTH_SHORT).show();
                }
            });
        }

        avatars.get(0).performClick();
    }

    private void attemptLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isUserMode) {
            if (dbHelper.checkUser(username, password)) {
                progressBar.setVisibility(View.VISIBLE);
                resultTextView.setVisibility(View.GONE);
                progressBar.setProgress(0);
                simulateLoginProcess(username, false);
            } else {
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (dbHelper.checkUser(username, password)&&dbHelper.isAdmin(username)) {
            progressBar.setVisibility(View.VISIBLE);
            resultTextView.setVisibility(View.GONE);
            progressBar.setProgress(0);
            simulateLoginProcess(username,true);
        } else {
            Toast.makeText(this, "管理员用户名或密码错误", Toast.LENGTH_SHORT).show();
               }
        }
    }

    private void attemptRegister() {
        if (!isUserMode) {
            Toast.makeText(this, "管理员模式不支持注册", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.userExists(username)) {
            Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.addUser(username, password, selectedAvatarId)) {
            Toast.makeText(this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
            passwordEditText.setText("");
        } else {
            Toast.makeText(this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    private void simulateLoginProcess(final String username,final boolean isAdmin) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final int totalProgress = 100;
        final int[] currentProgress = {0};

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                currentProgress[0] += 5;
                progressBar.setProgress(currentProgress[0]);

                if (currentProgress[0] < totalProgress) {
                    handler.postDelayed(this, 50);
                } else {
                    if (isAdmin) {
                        navigateToAdminActivity(username);
                    }else{
                        navigateToHomeActivity(username);
                    }
                }
            }
        };

        handler.postDelayed(runnable, 50);
    }

    private void navigateToHomeActivity(String username) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("AVATAR_ID", selectedAvatarId);
        startActivity(intent);
        finish();
    }
    private void navigateToAdminActivity(String username) {
        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
        intent.putExtra("ADMIN_NAME", username);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}