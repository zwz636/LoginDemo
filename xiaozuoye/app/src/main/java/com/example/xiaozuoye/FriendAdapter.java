package com.example.xiaozuoye;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class FriendAdapter extends ArrayAdapter<Friend> {

    private Context context;

    public FriendAdapter(Context context, List<Friend> friends) {
        super(context, 0, friends);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_friend, parent, false);
        }

        Friend friend = getItem(position);
        ImageView avatarImageView = view.findViewById(R.id.friendAvatar);
        TextView nameTextView = view.findViewById(R.id.friendName);
        TextView statusTextView = view.findViewById(R.id.friendStatus);

        if (friend != null) {
            avatarImageView.setImageResource(friend.getAvatarId());
            nameTextView.setText(friend.getName());
            statusTextView.setText(friend.getStatus());
        }

        return view;
    }
}