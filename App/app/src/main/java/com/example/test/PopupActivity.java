package com.example.test;


import android.app.Activity;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;




public class PopupActivity extends Activity {

    Button button1;

    private Context mContext, nContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_activity);
        button1 = findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PopupActivity.this, MainActivity.class);
                startActivity(intent);
                System.exit(0);
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendSMS();
                Intent intent = new Intent(PopupActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        }, 10000);

    }
    void sendSMS() {
        SmsManager sms = SmsManager.getDefault();
        mContext = this;
        nContext = this;

            String title = PreferenceManager.getString(mContext, "title");
            String content = PreferenceManager.getString(nContext, "content");


            sms.sendTextMessage("01029258187", null, "이름: "+ title + "\n주소: " + content + "\n-어플에서 발송된 메세지 입니다.-", null, null);

    }


}
