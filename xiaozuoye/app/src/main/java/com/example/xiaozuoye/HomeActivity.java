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
        private Button backButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    returnToMainActivity();
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

    private void returnToMainActivity() {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
        private void setupFriendList() {
            friendListView = findViewById(R.id.friendListView);

            friendList.add(new Friend("甲", "在线", R.drawable.dawa));
            friendList.add(new Friend("乙", "离线", R.drawable.erwa));
            friendList.add(new Friend("丙", "在线", R.drawable.sanwa));
            friendList.add(new Friend("丁", "忙碌", R.drawable.siwa));
            friendList.add(new Friend("戊", "在线", R.drawable.qiwa));

            FriendAdapter adapter = new FriendAdapter(this, friendList);
            friendListView.setAdapter(adapter);

            friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Friend friend = friendList.get(position);
                    Toast.makeText(HomeActivity.this, "点击了: " + friend.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
}
