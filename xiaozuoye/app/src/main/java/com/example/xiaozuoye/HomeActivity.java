package com.example.xiaozuoye;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ListView friendListView;
    private List<Friend> friendList = new ArrayList<>();
    private CustomButton backButton;
    private CustomButton refreshButton;
    private FriendAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainActivity();
            }
        });

        refreshButton.setOnCustomClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshFriendList();
            }
        });

        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        int avatarId = intent.getIntExtra("AVATAR_ID", R.drawable.two);

        TextView usernameTextView = findViewById(R.id.usernameTextView);
        ImageView avatarImageView = findViewById(R.id.avatarImageView);

        usernameTextView.setText("欢迎, " + username + "!");
        avatarImageView.setImageResource(avatarId);

        setupFriendList();
    }

    private void refreshFriendList() {
        Toast.makeText(this, "好友列表已刷新", Toast.LENGTH_SHORT).show();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void setupFriendList() {
        friendListView = findViewById(R.id.friendListView);

        friendList.add(new Friend("甲", "在线", R.drawable.dawa));
        friendList.add(new Friend("乙", "离线", R.drawable.erwa));
        friendList.add(new Friend("丙", "在线", R.drawable.sanwa));
        friendList.add(new Friend("丁", "忙碌", R.drawable.siwa));
        friendList.add(new Friend("戊", "在线", R.drawable.qiwa));

        adapter = new FriendAdapter(this, friendList);
        friendListView.setAdapter(adapter);

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = friendList.get(position);
                Intent chatIntent = new Intent(HomeActivity.this, ChatActivity.class);
                chatIntent.putExtra("FRIEND_NAME", friend.getName());
                startActivity(chatIntent);
            }
        });
    }

        private void returnToMainActivity () {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
