package com.example.xiaozuoye;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class AppDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chat_app.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_AVATAR_ID = "avatar_id";
    private static final String COLUMN_IS_ADMIN = "is_admin";

    private static final String TABLE_FRIENDSHIPS = "friendships";
    private static final String COLUMN_FRIENDSHIP_ID = "friendship_id";
    private static final String COLUMN_USER_ID_1 = "user_id_1";
    private static final String COLUMN_USER_ID_2 = "user_id_2";

    private static final String TABLE_FRIEND_INFO = "friend_info";
    private static final String COLUMN_INFO_ID = "info_id";
    private static final String COLUMN_FRIEND_USERNAME = "friend_username";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_AVATAR = "avatar";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_REMARK = "remark";

    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_MESSAGE_ID = "message_id";
    private static final String COLUMN_SENDER = "sender";
    private static final String COLUMN_RECEIVER = "receiver";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_AVATAR_ID + " INTEGER DEFAULT " + R.drawable.two + ","
                + COLUMN_IS_ADMIN + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_FRIENDSHIPS_TABLE = "CREATE TABLE " + TABLE_FRIENDSHIPS + "("
                + COLUMN_FRIENDSHIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID_1 + " INTEGER,"
                + COLUMN_USER_ID_2 + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_USER_ID_1 + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "FOREIGN KEY(" + COLUMN_USER_ID_2 + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                + "UNIQUE(" + COLUMN_USER_ID_1 + ", " + COLUMN_USER_ID_2 + ")"
                + ")";
        db.execSQL(CREATE_FRIENDSHIPS_TABLE);

        String CREATE_FRIEND_INFO_TABLE = "CREATE TABLE " + TABLE_FRIEND_INFO + "("
                + COLUMN_INFO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FRIEND_USERNAME + " TEXT UNIQUE,"
                + COLUMN_STATUS + " TEXT DEFAULT '在线',"
                + COLUMN_AVATAR + " INTEGER DEFAULT " + R.drawable.two + ","
                + COLUMN_NICKNAME + " TEXT,"
                + COLUMN_REMARK + " TEXT"
                + ")";
        db.execSQL(CREATE_FRIEND_INFO_TABLE);

        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SENDER + " TEXT,"
                + COLUMN_RECEIVER + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_TYPE + " INTEGER,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_SENDER + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USERNAME + "),"
                + "FOREIGN KEY(" + COLUMN_RECEIVER + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USERNAME + ")"
                + ")";
        db.execSQL(CREATE_MESSAGES_TABLE);

        insertInitialData(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USERNAME, "admin");
        adminValues.put(COLUMN_PASSWORD, "admin123");
        adminValues.put(COLUMN_IS_ADMIN, 1);
        db.insert(TABLE_USERS, null, adminValues);

        String[] usernames = {"user1", "user2", "user3", "user4", "user5"};
        String[] passwords = {"pass1", "pass2", "pass3", "pass4", "pass5"};
        int[] avatarIds = {R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.two, R.drawable.three};

        for (int i = 0; i < usernames.length; i++) {
            ContentValues userValues = new ContentValues();
            userValues.put(COLUMN_USERNAME, usernames[i]);
            userValues.put(COLUMN_PASSWORD, passwords[i]);
            userValues.put(COLUMN_AVATAR_ID, avatarIds[i]);
            db.insert(TABLE_USERS, null, userValues);

            ContentValues friendInfoValues = new ContentValues();
            friendInfoValues.put(COLUMN_FRIEND_USERNAME, usernames[i]);
            friendInfoValues.put(COLUMN_STATUS, i % 2 == 0 ? "在线" : "离线");
            friendInfoValues.put(COLUMN_AVATAR, avatarIds[i]);
            friendInfoValues.put(COLUMN_NICKNAME, "用户" + (i + 1));
            db.insert(TABLE_FRIEND_INFO, null, friendInfoValues);
        }

        establishFriendship(db, "user1", "user2");
        establishFriendship(db, "user1", "user3");
        establishFriendship(db, "user2", "user3");

        insertInitialMessages(db);
    }

    private void insertInitialMessages(SQLiteDatabase db) {
        addMessageToDB(db, "user1", "user2", "你好！我是user1", Msg.TYPE_SENT);
        addMessageToDB(db, "user2", "user1", "你好！我是user2", Msg.TYPE_RECEIVED);
        addMessageToDB(db, "user1", "user2", "最近怎么样？", Msg.TYPE_SENT);
        addMessageToDB(db, "user2", "user1", "还不错，谢谢关心！", Msg.TYPE_RECEIVED);

        addMessageToDB(db, "user3", "user1", "你好！我是user3", Msg.TYPE_RECEIVED);
        addMessageToDB(db, "user1", "user3", "你好！很高兴认识你！", Msg.TYPE_SENT);
    }
    private void addMessageToDB(SQLiteDatabase db, String sender, String receiver, String content, int type) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER, sender);
        values.put(COLUMN_RECEIVER, receiver);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_TYPE, type);
        db.insert(TABLE_MESSAGES, null, values);
    }

    private void establishFriendship(SQLiteDatabase db, String username1, String username2) {
        int userId1 = getUserIdByUsername(db, username1);
        int userId2 = getUserIdByUsername(db, username2);

        if (userId1 != -1 && userId2 != -1) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID_1, userId1);
            values.put(COLUMN_USER_ID_2, userId2);
            db.insert(TABLE_FRIENDSHIPS, null, values);
        }
    }

    private int getUserIdByUsername(SQLiteDatabase db, String username) {
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null);

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            cursor.close();
            return userId;
        }
        cursor.close();
        return -1;
    }
    public void addMessage(String sender, String receiver, String content, int type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER, sender);
        values.put(COLUMN_RECEIVER, receiver);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_TYPE, type);
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }
    public List<Msg> getMessages(String currentUser, String friend) {
        List<Msg> msgList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_MESSAGES +
                " WHERE (" + COLUMN_SENDER + " = ? AND " + COLUMN_RECEIVER + " = ?) OR (" +
                COLUMN_SENDER + " = ? AND " + COLUMN_RECEIVER + " = ?) ORDER BY " + COLUMN_TIMESTAMP + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{currentUser, friend, friend, currentUser});

        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                int type = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                msgList.add(new Msg(content, type));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return msgList;
    }
    public boolean deleteMessages(String user1, String user2) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_MESSAGES,
                "(" + COLUMN_SENDER + " = ? AND " + COLUMN_RECEIVER + " = ?) OR " +
                        "(" + COLUMN_SENDER + " = ? AND " + COLUMN_RECEIVER + " = ?)",
                new String[]{user1, user2, user2, user1});
        db.close();
        return rowsAffected > 0;
    }
    public int getUnreadMessageCount(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_MESSAGES +
                " WHERE " + COLUMN_RECEIVER + " = ? AND " + COLUMN_TYPE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username, String.valueOf(Msg.TYPE_RECEIVED)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDSHIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND_INFO);
        onCreate(db);
    }

    public boolean addUser(String username, String password, int avatarId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_AVATAR_ID, avatarId);
        long result = db.insert(TABLE_USERS, null, values);

        if (result != -1) {
            ContentValues friendInfoValues = new ContentValues();
            friendInfoValues.put(COLUMN_FRIEND_USERNAME, username);
            friendInfoValues.put(COLUMN_STATUS, "在线");
            friendInfoValues.put(COLUMN_AVATAR, avatarId);
            friendInfoValues.put(COLUMN_NICKNAME, username);
            db.insert(TABLE_FRIEND_INFO, null, friendInfoValues);
        }

        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password},
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public boolean isAdmin(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_IS_ADMIN},
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null);

        if (cursor.moveToFirst()) {
            int isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ADMIN));
            cursor.close();
            return isAdmin == 1;
        }
        cursor.close();
        return false;
    }

    public boolean userExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // 好友操作
    public boolean addFriend(String currentUser, String friendUsername) {
        if (!userExists(friendUsername)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        int currentUserId = getUserIdByUsername(db, currentUser);
        int friendUserId = getUserIdByUsername(db, friendUsername);

        if (currentUserId == -1 || friendUserId == -1) {
            return false;
        }

        if (isFriend(currentUser, friendUsername)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_1, currentUserId);
        values.put(COLUMN_USER_ID_2, friendUserId);
        long result = db.insert(TABLE_FRIENDSHIPS, null, values);
        return result != -1;
    }

    public boolean isFriend(String user1, String user2) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId1 = getUserIdByUsername(db, user1);
        int userId2 = getUserIdByUsername(db, user2);

        if (userId1 == -1 || userId2 == -1) {
            return false;
        }

        Cursor cursor = db.query(TABLE_FRIENDSHIPS,
                new String[]{COLUMN_FRIENDSHIP_ID},
                "(" + COLUMN_USER_ID_1 + " = ? AND " + COLUMN_USER_ID_2 + " = ?) OR " +
                        "(" + COLUMN_USER_ID_1 + " = ? AND " + COLUMN_USER_ID_2 + " = ?)",
                new String[]{String.valueOf(userId1), String.valueOf(userId2),
                        String.valueOf(userId2), String.valueOf(userId1)},
                null, null, null);

        boolean isFriend = cursor.getCount() > 0;
        cursor.close();
        return isFriend;
    }

    public List<Friend> getFriends(String username) {
        List<Friend> friendList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = getUserIdByUsername(db, username);

        if (userId == -1) {
            return friendList;
        }

        String query = "SELECT u." + COLUMN_USERNAME + ", fi." + COLUMN_STATUS + ", fi." + COLUMN_AVATAR +
                ", fi." + COLUMN_NICKNAME + ", fi." + COLUMN_REMARK +
                " FROM " + TABLE_FRIENDSHIPS + " f " +
                " JOIN " + TABLE_USERS + " u ON (f." + COLUMN_USER_ID_2 + " = u." + COLUMN_USER_ID +
                " OR f." + COLUMN_USER_ID_1 + " = u." + COLUMN_USER_ID + ") " +
                " JOIN " + TABLE_FRIEND_INFO + " fi ON u." + COLUMN_USERNAME + " = fi." + COLUMN_FRIEND_USERNAME +
                " WHERE (f." + COLUMN_USER_ID_1 + " = ? OR f." + COLUMN_USER_ID_2 + " = ?) " +
                " AND u." + COLUMN_USERNAME + " != ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(userId), username});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
                int avatarId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AVATAR));
                String nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME));
                String remark = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REMARK));

                String displayName = remark != null && !remark.isEmpty() ? remark :
                        (nickname != null && !nickname.isEmpty() ? nickname : name);

                friendList.add(new Friend(displayName, status, avatarId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return friendList;
    }

    public boolean deleteFriend(String currentUser, String friendUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        int currentUserId = getUserIdByUsername(db, currentUser);
        int friendUserId = getUserIdByUsername(db, friendUsername);

        if (currentUserId == -1 || friendUserId == -1) {
            return false;
        }

        int rowsAffected = db.delete(TABLE_FRIENDSHIPS,
                "(" + COLUMN_USER_ID_1 + " = ? AND " + COLUMN_USER_ID_2 + " = ?) OR " +
                        "(" + COLUMN_USER_ID_1 + " = ? AND " + COLUMN_USER_ID_2 + " = ?)",
                new String[]{String.valueOf(currentUserId), String.valueOf(friendUserId),
                        String.valueOf(friendUserId), String.valueOf(currentUserId)});

        return rowsAffected > 0;
    }

    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USERNAME},
                COLUMN_IS_ADMIN + " = 0",
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                users.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    public boolean updateFriendInfo(String username, String status, String nickname, String remark) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (status != null) values.put(COLUMN_STATUS, status);
        if (nickname != null) values.put(COLUMN_NICKNAME, nickname);
        if (remark != null) values.put(COLUMN_REMARK, remark);

        int rowsAffected = db.update(TABLE_FRIEND_INFO, values,
                COLUMN_FRIEND_USERNAME + " = ?", new String[]{username});
        return rowsAffected > 0;
    }
}