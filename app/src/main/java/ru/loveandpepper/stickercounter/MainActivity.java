package ru.loveandpepper.stickercounter;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;

import android.text.InputType;
import android.view.Gravity;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Spinner spinnerOfProducts;
    private DataBaseOperations dataBaseOperations;
    private SQLiteDatabase database;
    private EditText editPrice;
    private EditText editQuantity;
    private TextView date;
    private TextView statisticsField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataBaseOperations = new DataBaseOperations(this);
        database = dataBaseOperations.getWritableDatabase();
        spinnerOfProducts = findViewById(R.id.products);
        editPrice = findViewById(R.id.editText_Price);
        editQuantity = findViewById(R.id.editText_Quantity);
        getCurrentTime();
        updateStatField();

    }


    public void AddSellToBase(View v) {
        try {
            String sop = spinnerOfProducts.getSelectedItem().toString();
            int epr = Integer.parseInt(editPrice.getText().toString());
            int equ = Integer.parseInt(editQuantity.getText().toString());
            ContentValues contentValues = new ContentValues();
            contentValues.put("product", sop);
            contentValues.put("price", epr);
            contentValues.put("quantity", equ);
            contentValues.put("date", date.getText().toString());
            database.insert(DataBaseOperations.DEFAULT_TABLE, null, contentValues);
            updateStatField();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);   //скрывает клаву по клику
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            editQuantity.setText(null);
            editPrice.setText(null);
            new ToastMaker().showToast(this, "Успешно добавлено :)");
        } catch (Exception e) {
            new ToastMaker().showToast(this, "Наверное что-то не заполнено, случилось исключание :(");
        }
    }

    public void updateStatField() {
        statisticsField = findViewById(R.id.statisticsField);
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : readFromDatabase()) {
            stringBuilder.append(s).append("\n").append("--------------\n");
        }
        statisticsField.setText(stringBuilder.toString());

    }

    public ArrayList<String> readFromDatabase() {
        ArrayList<String> datalist = new ArrayList<>();
        Cursor cursor = database.query(DataBaseOperations.DEFAULT_TABLE, null, null, null, null, null, "_id DESC");
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("_id");
            int nameIndex = cursor.getColumnIndex("product");
            int priceIndex = cursor.getColumnIndex("price");
            int quantityIndex = cursor.getColumnIndex("quantity");
            int dateIndex = cursor.getColumnIndex("date");
            do {
                datalist.add("id " + cursor.getInt(idIndex) + " | " +
                        cursor.getString(nameIndex) + " | " +
                        cursor.getInt(priceIndex) + " руб. | " +
                        cursor.getInt(quantityIndex) + " шт. | \n" +
                        cursor.getString(dateIndex) + " | Итог: " + cursor.getInt(priceIndex) * cursor.getInt(quantityIndex) + " руб.");
            }
            while (cursor.moveToNext());
        } else {
            datalist.add("Нет записей!");
        }
        cursor.close();
        return datalist;
    }


    public void getCurrentTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDate = new SimpleDateFormat("d MMMM yyyy", myDateFormatSymbols);
        Date now = new Date();
        String strDate = sdfDate.format(now);
        date = findViewById(R.id.currentDate);
        date.setText(strDate);
    }

    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }

    };


    public void deleteEntry(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Удаление записы из базы");
        alertDialog.setMessage("Введите id записи в БД:");
        EditText input = new EditText(this);
        LinearLayout.LayoutParams paramDate = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        input.setLayoutParams(paramDate);
        input.setHint("id записи");
        input.setGravity(Gravity.CENTER);
        input.setInputType(3);
        input.setLayoutParams(paramDate);
        alertDialog.setView(input);
        /*alertDialog.setIcon(R.drawable.key);*/

        alertDialog.setPositiveButton("Удалить",
                (dialog, which) -> {
            try {
                int id = Integer.parseInt(input.getText().toString());
                Cursor testForExists = database.query(DataBaseOperations.DEFAULT_TABLE, new String[]{"_id"},
                        "_id = ?", new String[] {String.valueOf(id)}, null, null, null);
                if (testForExists.moveToFirst()){
                    database.delete(DataBaseOperations.DEFAULT_TABLE, "_id = " + id, null);
                updateStatField();
                new ToastMaker().showToast(this, "Готово! :)");
                }
                else {
                    new ToastMaker().showToast(this, "id не получилось найти :(");
                    deleteEntry(view);
                }
                testForExists.close();
            }
            catch (Exception e){
                new ToastMaker().showToast(this, "Пустой id!");
                deleteEntry(view);
            }
        });

        alertDialog.setNegativeButton("Выйти",
                (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }

    public void statStart(View view) {
        Intent intent1 = new Intent(this, WorkWithDbActivity.class);
        startActivity(intent1);

    }
}









