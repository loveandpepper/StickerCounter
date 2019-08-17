package ru.loveandpepper.stickercounter.callback_dialogs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.loveandpepper.stickercounter.Export_activity;
import ru.loveandpepper.stickercounter.R;

public class CallLogClass extends AppCompatActivity {
    private ListView callLogList;
    private Export_activity ex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
        ex = new Export_activity();
        callLogList = findViewById(R.id.calloglistview);
        callLogList.setDividerHeight(20);
        collectCalls();
    }

    public void collectCalls() {
        ArrayList<CallLogClass.Calls> callslist = new ArrayList<>();     //собираем последние 15 вызовов из журнала в коллекцию и выводим в listview
        Uri allCalls = Uri.parse("content://call_log/calls");
        Cursor cursor = getContentResolver().query(allCalls, null, null, null, "DATE DESC");
        int fifteen = 0;
        assert cursor != null;
        if (cursor.moveToFirst() ) {
            do {
                String num = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));// номер телефона из журнала
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                callslist.add(new Calls(num, name, date));
                fifteen++;
            } while (cursor.moveToNext() && fifteen < 15);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        ArrayAdapter<CallLogClass.Calls> arrayAdapter = new ArrayAdapter<>(this, R.layout.mylistview, callslist);
        callLogList.setAdapter(arrayAdapter);
        callLogList.setOnItemClickListener((adapterView, view, i, l) -> {
            ex.setNameForNum(callslist.get(i).callName);
            ex.setPhoneNum(callslist.get(i).callNumber);
            Intent intent = new Intent(this, Export_activity.class);
            intent.putExtra("Status", "Added");
            startActivity(intent);
            finish();
        });
    }


    public class Calls {
        private String callName;
        private String callNumber;
        private String callDate;

        public Calls(String callNumber, String callName, String callDate) {
            this.callName = callName;
            this.callNumber = callNumber;
            this.callDate = callDate;
        }

        @Override
        public String toString() {
            SimpleDateFormat sdfForCalls = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                if (callName != null) {
                    return callNumber + " || " + callName + " || " + sdfForCalls.format(new Date(Long.parseLong(callDate)));
                }
                else {
                    return callNumber + " || " + sdfForCalls.format(new Date(Long.parseLong(callDate)));
                }
        }
    }
}
