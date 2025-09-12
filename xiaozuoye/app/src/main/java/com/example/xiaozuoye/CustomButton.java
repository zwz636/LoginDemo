package com.example.xiaozuoye;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;

public class CustomButton extends LinearLayout {

    private Button button;

    public CustomButton(Context context) {
        super(context);
        init(context, null);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_button, this, true);
        button = findViewById(R.id.customBtn);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);
            String text = typedArray.getString(R.styleable.CustomButton_buttonText);
            int backgroundColor = typedArray.getColor(
                    R.styleable.CustomButton_buttonBackgroundColor,
                    ContextCompat.getColor(context, android.R.color.holo_blue_dark)
            );

            button.setText(text != null ? text : "按钮");
            button.setBackgroundColor(backgroundColor);

            typedArray.recycle();
        }
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performClick();
            }
        });
    }

    public void setOnCustomClickListener(OnClickListener listener) {
        setOnClickListener(listener);
    }

    public void setButtonText(String text) {
        button.setText(text);
    }

    public void setButtonEnabled(boolean enabled) {
        button.setEnabled(enabled);
        setEnabled(enabled);
    }
}