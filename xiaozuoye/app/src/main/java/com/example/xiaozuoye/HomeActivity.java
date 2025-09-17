package com.example.xiaozuoye;

import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ListView friendListView;
    private List<Friend> friendList;
    private CustomButton backButton;
    private CustomButton refreshButton;
    private FloatingActionButton addFriendButton;
    private FriendAdapter adapter;
    private AppDatabaseHelper dbHelper;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbHelper = new AppDatabaseHelper(this);

        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addFriendButton = findViewById(R.id.addFriendButton);
        Intent intent = getIntent();
        currentUser = intent.getStringExtra("USERNAME");
        int avatarId = intent.getIntExtra("AVATAR_ID", R.drawable.two);

        TextView usernameTextView = findViewById(R.id.usernameTextView);
        ImageView avatarImageView = findViewById(R.id.avatarImageView);

        usernameTextView.setText("欢迎, " + currentUser + "!");
        avatarImageView.setImageResource(avatarId);

        setupFriendList();

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

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFriendDialog();
            }
        });
    }

    private void showAddFriendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加好友");

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_friend, null);
        builder.setView(dialogView);

        builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextView friendUsernameView = dialogView.findViewById(R.id.friendUsername);
                String friendUsername = friendUsernameView.getText().toString().trim();

                if (!friendUsername.isEmpty()) {
                    addFriend(friendUsername);
                }
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void addFriend(String friendUsername) {
        if (friendUsername.equals(currentUser)) {
            Toast.makeText(this, "不能添加自己为好友", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.addFriend(currentUser, friendUsername)) {
            Toast.makeText(this, "好友添加成功", Toast.LENGTH_SHORT).show();
            refreshFriendList();
        } else {
            Toast.makeText(this, "添加失败，用户不存在或已是好友", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshFriendList() {
        friendList = dbHelper.getFriends(currentUser);
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(friendList);
            adapter.notifyDataSetChanged();
        }
        Toast.makeText(this, "好友列表已刷新", Toast.LENGTH_SHORT).show();
    }

    private void setupFriendList() {
        friendListView = findViewById(R.id.friendListView);
        friendList = dbHelper.getFriends(currentUser);

        adapter = new FriendAdapter(this, friendList);
        friendListView.setAdapter(adapter);

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = friendList.get(position);
                Intent chatIntent = new Intent(HomeActivity.this, ChatActivity.class);
                chatIntent.putExtra("FRIEND_NAME", friend.getName());
                chatIntent.putExtra("CURRENT_USER", currentUser);
                startActivity(chatIntent);
            }
        });

        friendListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = friendList.get(position);
                showDeleteFriendDialog(friend.getName());
                return true;
            }
        });
    }
    private void showDeleteFriendDialog(final String friendName) {
        new AlertDialog.Builder(this)
                .setTitle("删除好友")
                .setMessage("确定要删除好友 " + friendName + " 吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dbHelper.deleteFriend(currentUser, friendName)) {
                            refreshFriendList();
                            Toast.makeText(HomeActivity.this, "好友删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

        private void returnToMainActivity () {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    @Override
    protected void onResume() {
        super.onResume();
        refreshFriendList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
