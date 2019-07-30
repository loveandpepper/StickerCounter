package ru.loveandpepper.stickercounter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.karan.churi.PermissionManager.PermissionManager;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.siegmar.fastcsv.writer.CsvWriter;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.os.Environment.getExternalStorageState;


public class Export_activity extends AppCompatActivity {

    TextView date1;
    TextView date2;
    Calendar calendar;
    int day, month, year;
    SQLiteDatabase database;
    PermissionManager permissionManager;
    private static final int WRITE_REQUEST_CODE = 43;
    public Uri uri;
    Date d1;
    Date d2;

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
        setDates();
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
                System.out.println("!!!!!PROBLEMS!!!!!");
            }
        }
        else {
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
        }
        else {
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
            writer.writeNext(new String[] {"id", "Продукт", "Цена", "Количество", "Дата", "Итого"});
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
            }
            else {new ToastMaker().showToast(this, "Записей за эти даты не нашлось!");}


        } catch (Exception e) {
            new ToastMaker().showToast(this, "Возникла ошибка при сохранении! \n" + e.getMessage());
            e.printStackTrace();
        }
    }
}



