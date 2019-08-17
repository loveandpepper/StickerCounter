package ru.loveandpepper.stickercounter;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;


import android.content.Intent;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import android.os.Bundle;

import android.os.ParcelFileDescriptor;

import android.view.View;

import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.TextView;


import com.karan.churi.PermissionManager.PermissionManager;
import com.opencsv.CSVWriter;


import java.io.FileOutputStream;

import java.io.OutputStreamWriter;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.Objects;

import ru.loveandpepper.stickercounter.callback_dialogs.AddCallbackDialog;



public class Export_activity extends AppCompatActivity {

    TextView date1, date2;
    Calendar calendar;
    int day, month, year;
    public static SQLiteDatabase database;
    PermissionManager permissionManager;
    private static final int WRITE_REQUEST_CODE = 43;
    public Uri uri;
    Date d1, d2;
    private static ListView callBackListView;
    private static String phoneNum, nameForNum;

    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        Export_activity.phoneNum = phoneNum;
    }
    public String getNameForNum() {
        return nameForNum;
    }
    public void setNameForNum(String nameForNum) {
        Export_activity.nameForNum = nameForNum;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.export_activity);
        date1 = findViewById(R.id.date1);
        date2 = findViewById(R.id.date2);
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        DataBaseOperations dataBaseOperations = new DataBaseOperations(this);
        database = dataBaseOperations.getWritableDatabase();
        permissionManager = new PermissionManager() {
        };
        permissionManager.checkAndRequestPermissions(this);
        callBackListView = findViewById(R.id.callback_listview);
        callBackListView.setDivider(new ColorDrawable(this.getResources().getColor(R.color.transparent))); // устанавливаем Divider для строчек в ListView
        callBackListView.setDividerHeight(15);
        setDates();
        callBackListUpdate();
        if (Objects.equals(getIntent().getStringExtra("Status"), "Added")){ //если активити вызвана из CallLogClass и булеан true, то снова запускается AddCallbackDialog
            new AddCallbackDialog().show(getSupportFragmentManager(), "AddCallbackDialog");
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);
    }

    public void setDates() {                     //Listenerы для вызова диалогов для дат.
        date1.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(Export_activity.this, (datePicker, year, month, day) -> {
                month = month + 1;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    d1 = simpleDateFormat.parse(day + "." + month + "." + year);
                    date1.setText(simpleDateFormat.format(d1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, year, month, day);
            datePickerDialog.show();
        });
        date2.setOnClickListener(view -> {
            DatePickerDialog date2PickerDialog = new DatePickerDialog(Export_activity.this, ((datePicker, year, month, day) -> {
                month = month + 1;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    d2 = simpleDateFormat.parse(day + "." + month + "." + year);
                    date2.setText(simpleDateFormat.format(d2));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }), year, month, day);
            date2PickerDialog.show();
        });
    }

    public void exportClick(View view) {
        if (d1 != null && d2 != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                String mimeType = "text/csv";
                String filename = "database " + MainActivity.sdfDate.format(new Date()) + ".csv";
                intent.setType(mimeType);
                intent.putExtra(Intent.EXTRA_TITLE, filename);
                startActivityForResult(intent, WRITE_REQUEST_CODE);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage() + e.getCause());
            }
        } else {
            new ToastMaker().showToast(this, "Даты пустые!");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                uri = resultData.getData();
                exporter();
            }
        } else {
            new ToastMaker().showToast(this, "Возникла ошибка при создании файла!");
        }
    }

    public void exporter() {
        try {
            ParcelFileDescriptor pfd = Export_activity.this.getContentResolver().
                    openFileDescriptor(uri, "w");
            CSVWriter writer = new CSVWriter(
                    new OutputStreamWriter(new FileOutputStream(pfd.getFileDescriptor()), StandardCharsets.UTF_8),
                    ',',
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END
            );
            writer.writeNext(new String[]{"id", "Продукт", "Цена", "Количество", "Дата", "Итого"});
            ArrayList<Product> products = MainActivity.readFromDatabase();
            boolean count = false;
            for (Product ex : products) {
                if (ex.getDate().getTime() >= d1.getTime() && ex.getDate().getTime() <= d2.getTime()) {
                    count = true;
                    writer.writeNext(new String[]{String.valueOf(ex.getId()), ex.getName(), String.valueOf(ex.getPrice()), String.valueOf(ex.getQuantity()), MainActivity.sdfDate.format(ex.getDate()), String.valueOf(ex.getTotal())});
                }
            }
            writer.close();
            if (count) {
                new ToastMaker().showToast(this, "База данных успешно сохранена в CSV файл.");
            } else {
                new ToastMaker().showToast(this, "Записей за эти даты не нашлось!");
            }


        } catch (Exception e) {
            new ToastMaker().showToast(this, "Возникла ошибка при сохранении! \n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public ArrayList<CallBackUnit> callBackItems() {
        ArrayList<CallBackUnit> list = new ArrayList<>();
        Cursor cursor = database.query(DataBaseOperations.CALLBACK_TABLE, null, null, null, null, null, "_id DESC");
        if (cursor.moveToFirst()) {
            int phoneIndex = cursor.getColumnIndex("phone");
            int nameIndex = cursor.getColumnIndex("name");
            int commentIndex = cursor.getColumnIndex("comment");
            do {
                list.add(new CallBackUnit(cursor.getString(phoneIndex), cursor.getString(nameIndex), cursor.getString(commentIndex)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }


    public void callBackListUpdate() {                                                               //обновления LISTView и обработка нажатий на него
        ArrayAdapter<CallBackUnit> arrayAdapter = new ArrayAdapter<>(this, R.layout.mylistview, callBackItems());
        callBackListView.setAdapter(arrayAdapter);
        callBackListView.setOnItemClickListener((adapterView, view, i, l) -> {                       //обработка нажатий на item в листе
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + callBackItems().get(i).getPhoneNumber()));
            startActivity(callIntent);                                                               //ВСЕ разрешения проверяет permissionManager в методе onCreate
        });

        callBackListView.setOnItemLongClickListener((adapterView, view, i, l) -> {                   //обработка долгих нажатий (Удаление записей)
            AlertDialog.Builder adb = new AlertDialog.Builder(Export_activity.this);
            adb.setTitle("Удалить?")
            .setMessage("Запись по поводу звонка " + callBackItems().get(i).getName() + " больше не нужна?")
                    .setPositiveButton("Удалить", (dial, phone) -> {
                        database.execSQL("DELETE FROM " + DataBaseOperations.CALLBACK_TABLE + " WHERE phone = '" + callBackItems().get(i).getPhoneNumber() + "' AND comment = '" + callBackItems().get(i).getComment() + "'");
                        callBackListUpdate();
                        dial.cancel();
                    })
                    .setNegativeButton("Не удалять!", (dialog, phone) -> {
                        dialog.cancel();});
            AlertDialog alert = adb.create();
            alert.show();
            return true;
        });
    }

    public void onClickAdd(View view) {
        AddCallbackDialog addCallbackDialog = new AddCallbackDialog();
        addCallbackDialog.show(getSupportFragmentManager(), "AddCallbackDialog");
    }

}



