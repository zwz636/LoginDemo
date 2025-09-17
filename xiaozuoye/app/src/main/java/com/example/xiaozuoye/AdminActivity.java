package com.example.xiaozuoye;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private AppDatabaseHelper dbHelper;
    private ListView userListView;
    private TextView adminNameTextView;
    private CustomButton backButton;
    private CustomButton refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new AppDatabaseHelper(this);

        adminNameTextView = findViewById(R.id.adminNameTextView);
        userListView = findViewById(R.id.userListView);
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);

        Intent intent = getIntent();
        String adminName = intent.getStringExtra("ADMIN_NAME");
        adminNameTextView.setText("管理员: " + adminName);

        loadUserList();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainActivity();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUserList();
                Toast.makeText(AdminActivity.this, "用户列表已刷新", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserList() {
        List<String> users = dbHelper.getAllUsers();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, users);
        userListView.setAdapter(adapter);
    }

    private void returnToMainActivity() {
        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
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