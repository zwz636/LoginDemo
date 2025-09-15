package com.example.xiaozuoye;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ListView msgListView;
    private EditText inputText;
    private CustomButton sendButton;
    private CustomButton backButton;
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<>();
    private String friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        friendName = intent.getStringExtra("FRIEND_NAME");
        setTitle("与 " + friendName + " 聊天");

        backButton = findViewById(R.id.backButton);
        msgListView = findViewById(R.id.msg_list_view);
        inputText = findViewById(R.id.input_text);
        sendButton = findViewById(R.id.send);

        initMsgs();
        adapter = new MsgAdapter(this, R.layout.msg_item, msgList);
        msgListView.setAdapter(adapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!TextUtils.isEmpty(content)) {

                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size() - 1);
                    inputText.setText("");

                    simulateReply();
                } else {
                    Toast.makeText(ChatActivity.this, "消息不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initMsgs() {
        Msg msg1 = new Msg("你好！我是" + friendName, Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("你好！很高兴认识你！", Msg.TYPE_SENT);
        msgList.add(msg2);
        Msg msg3 = new Msg("最近怎么样？", Msg.TYPE_RECEIVED);
        msgList.add(msg3);
    }

    private void simulateReply() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] replies = {
                        "收到你的消息了！",
                        "好的，明白了",
                        "谢谢你的信息",
                        "很有趣的话题",
                        "我们改天再聊"
                };
                String reply = replies[(int) (Math.random() * replies.length)];

                Msg msg = new Msg(reply, Msg.TYPE_RECEIVED);
                msgList.add(msg);
                adapter.notifyDataSetChanged();
                msgListView.setSelection(msgList.size() - 1);
            }
        }, 1000);
    }
}