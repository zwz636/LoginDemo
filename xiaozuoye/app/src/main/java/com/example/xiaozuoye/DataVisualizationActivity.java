package com.example.xiaozuoye;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class DataVisualizationActivity extends AppCompatActivity {

    private AppDatabaseHelper dbHelper;
    private DataChartView chartView;
    private String selectedUser;
    private List<String> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new AppDatabaseHelper(this);

        allUsers = dbHelper.getAllUsers();
        selectedUser = allUsers.isEmpty() ? "" : allUsers.get(0);

        chartView = new DataChartView(this);
        setContentView(chartView);
    }

    private class DataChartView extends View {
        private Paint paint;
        private RectF userSelectRect;
        private RectF totalUserRect;

        public DataChartView(Context context) {
            super(context);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(36);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.WHITE);

            paint.setColor(Color.BLACK);
            canvas.drawText("数据库数据可视化", getWidth()/2 - 200, 80, paint);

            int totalUserCount = dbHelper.getAllUsers().size();
            paint.setColor(Color.BLUE);
            int userBarWidth = Math.min(totalUserCount * 30, getWidth() - 200);
            totalUserRect = new RectF((getWidth() - userBarWidth)/2,120,(getWidth() - userBarWidth)/2 + userBarWidth, 170);
            canvas.drawRect(totalUserRect, paint);

            paint.setColor(Color.BLACK);
            canvas.drawText("总用户数量: " + totalUserCount,
                    getWidth()/2 - 150, 220, paint);

            paint.setColor(Color.LTGRAY);
            userSelectRect = new RectF(getWidth()/2 - 200, 280, getWidth()/2 + 200, 340);
            canvas.drawRoundRect(userSelectRect, 10, 10, paint);

            paint.setColor(Color.BLACK);
            canvas.drawText("当前用户: " + selectedUser,
                    getWidth()/2 - 180, 320, paint);
            canvas.drawText("(点击选择其他用户)",
                    getWidth()/2 - 180, 360, paint);

            if (!selectedUser.isEmpty()) {
                int friendCount = dbHelper.getFriends(selectedUser).size();
                paint.setColor(Color.GREEN);
                int friendBarWidth = Math.min(friendCount * 40, getWidth() - 200);
                RectF friendBar = new RectF((getWidth()-friendBarWidth)/2,400,(getWidth()-friendBarWidth)/2+friendBarWidth,450);
                canvas.drawRect(friendBar, paint);

                paint.setColor(Color.BLACK);
                String friendText = selectedUser + " 的好友数量: " + friendCount;
                canvas.drawText(friendText,
                        getWidth()/2 - (friendText.length() * 12), 500, paint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                float y = event.getY();
                if (userSelectRect != null && userSelectRect.contains(x, y)) {
                    showUserSelectionDialog();
                    return true;
                }
            }
            return super.onTouchEvent(event);
        }
    }

    private void showUserSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择用户");

        final String[] usersArray = allUsers.toArray(new String[0]);

        builder.setItems(usersArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedUser = usersArray[which];
                chartView.invalidate();
                Toast.makeText(DataVisualizationActivity.this,
                        "已选择用户: " + selectedUser, Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}