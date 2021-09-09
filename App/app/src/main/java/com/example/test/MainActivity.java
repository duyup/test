package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private RecyclerView mRv_todo;
    private FloatingActionButton mBtn_write;
    private ArrayList<TodoItem> mTodoItems;
    private DBHelper mDBHelper;
    private CustomAdapter mAdapter;
    Button nbtn;
    private Context mContext, nContext;
    int MULTIPLE_PERMISSION = 1000;
    private final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.VIBRATE

    };
    String beaconUUID="ffffffee-b644-4520-8f0c-720eaf059935"; // beacon -uuid
    private String TAG = "BeaconActivity";
    private BeaconManager beaconManager;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, MULTIPLE_PERMISSION);
        }

        nbtn = findViewById(R.id.btn_next);
        nbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //블루투스 퍼미션
        if(bluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않는 기기입니다..", Toast.LENGTH_SHORT).show();
        }
        else{
            if(!bluetoothAdapter.isEnabled()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);

            }

        }
        setInit();

        //비콘 매니저





    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onBeaconServiceConnect()
    {
        beaconManager.setMonitorNotifier(new MonitorNotifier()
        {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");

                Intent intent = new Intent(MainActivity.this, PopupActivity.class);
                startActivity(intent);
            }


            @Override
            public void didExitRegion(Region region)
            {
                Log.i(TAG, "I no longer see an beacon");

            }

            @Override
            public void didDetermineStateForRegion(int state, Region region)
            {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "

                        +state);
            }

        });
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("beacon", Identifier.parse(beaconUUID), null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }// onBeaconServiceConnect()..

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);

    }

    @Override
    public void finish() {
        super.finish();
    }





    private void setInit()
    {
        mDBHelper = new DBHelper(this);
        mRv_todo = findViewById(R.id.rv_todo);
        mBtn_write = findViewById(R.id.btn_write);
        mTodoItems = new ArrayList<>();
        loadRecentDB();
        mContext = this;
        nContext = this;

        mBtn_write.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //팝업창띄우기
                Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Material_Light_Dialog);
                dialog.setContentView(R.layout.dialog_edit);
                EditText et_title = dialog.findViewById(R.id.et_title);
                EditText et_content = dialog.findViewById(R.id.et_content);
                Button btn_ok = dialog.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //Insert Datebase
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        mDBHelper.InsertTodo(et_title.getText().toString(), et_content.getText().toString(), currentTime);
                        //Insert UI

                        TodoItem item = new TodoItem();
                        item.setTitle(et_title.getText().toString());
                        item.setContent(et_content.getText().toString());
                        item.setWriteDate(currentTime);
                        mAdapter.addItem(item);

                        mRv_todo.smoothScrollToPosition(0);
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this,"등록 되었습니다.", Toast.LENGTH_SHORT).show();
                        String title = et_title.getText().toString();
                        String content = et_content.getText().toString();
                        PreferenceManager.setString(mContext, "title", title);
                        PreferenceManager.setString(nContext, "content", content);

                    }
                });
                dialog.show();

            }
        });
    }


    private void loadRecentDB()
    {
        //저장되었던 DB를 가져온다
        mTodoItems = mDBHelper.getTodoList();
        if(mAdapter == null){
            mAdapter = new CustomAdapter(mTodoItems, this);
            mRv_todo.setHasFixedSize(true);
            mRv_todo.setAdapter(mAdapter);
        }

    }

}