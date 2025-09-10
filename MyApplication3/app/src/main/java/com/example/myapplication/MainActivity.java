package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.util.Log;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private EditText editText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ImageView imageView=(ImageView)findViewById(R.id.image_view);
        imageView.setAlpha(80);
        progressBar = findViewById(R.id.progress_bar);
        if (progressBar != null) {
            int progress = progressBar.getProgress();
            progressBar.setProgress(progress + 10);
        } else {
            Log.e("MainActivity", "ProgressBar not found!");
        }
        button=(Button)findViewById(R.id.button);
        editText=(EditText)findViewById(R.id.edit_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setProgress(0);
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          String inputText = editText.getText().toString();
                                          Toast.makeText(MainActivity.this, inputText, Toast.LENGTH_SHORT).show();
                                          showAlertDialog();
                                          showProgressDialog();
                                          ValueAnimator animator = ValueAnimator.ofInt(0, progressBar.getMax());
                                          animator.setDuration(5000);
                                          animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                              @Override
                                              public void onAnimationUpdate(ValueAnimator animation) {
                                                  int progress = (int) animation.getAnimatedValue();
                                                  progressBar.setProgress(progress);
                                              }
                                          });
                                          animator.start();
                                      }
        });
                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
            }
    private void showAlertDialog() {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
        alertdialog.setTitle("登录成功");
        alertdialog.setCancelable(false);
        alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "OK clicked", Toast.LENGTH_SHORT).show();
            }
        });
        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });
        alertdialog.show();
    }
    private void showProgressDialog() {
        ProgressDialog progressdialog = new ProgressDialog(MainActivity.this);
        progressdialog.setTitle("Loading...");
        progressdialog.setMessage("Please wait while loading...");
        progressdialog.setCancelable(false);
        progressdialog.show();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressdialog.isShowing()) {
                    progressdialog.dismiss();
                }
            }
        }, 5000);
    }
}
