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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.xiaozuoye.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView resultTextView;
    private ProgressBar progressBar;
    private int selectedAvatarId = R.drawable.two;
    private CustomButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        progressBar = findViewById(R.id.progressBar);
        resultTextView = findViewById(R.id.resultTextView);
        loginButton = findViewById(R.id.loginButton);

        setupAvatarSelection();

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "This is a CustomButton", Toast.LENGTH_SHORT).show();
                attemptLogin();
            }
        });
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
            Toast.makeText(this, "输入错误", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        resultTextView.setVisibility(View.GONE);
        progressBar.setProgress(0);

        simulateLoginProcess(username);
    }

    private void simulateLoginProcess(final String username) {
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
                    navigateToHomeActivity(username);
                    ;
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
}