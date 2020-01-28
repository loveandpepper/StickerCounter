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
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private Spinner spinnerOfProducts;
    public static SQLiteDatabase database;
    private EditText editPrice;
    private EditText editQuantity;
    private TextView date;
    public static SimpleDateFormat sdfDate;
    private int spinInt;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataBaseOperations dataBaseOperations = new DataBaseOperations(this);
        database = dataBaseOperations.getWritableDatabase();
        spinnerOfProducts = findViewById(R.id.products);
        editPrice = findViewById(R.id.editText_Price);
        editQuantity = findViewById(R.id.editText_Quantity);
        getCurrentTime();
        updateStatField();
        priceSetterForItem();
        aSwitch = findViewById(R.id.switch_beznal);
        if (aSwitch != null) {
            aSwitch.setOnCheckedChangeListener(this);
        }

    }

    public void priceSetterForItem(){
        spinnerOfProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {      //Listener для спиннера. Подставляет цену по умолчанию для продукта.
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                aSwitch.setChecked(false);
                switch (i){
                    case 0: editPrice.setText("1500"); break;
                    case 1: editPrice.setText("1300"); break;
                    case 2: editPrice.setText("1100"); break;
                    case 3: editPrice.setText("1000"); break;
                    case 4: editPrice.setText("1500"); break;
                    case 5: editPrice.setText("1900"); break;
                    case 6: editPrice.setText("2800"); break;
                    case 7: editPrice.setText(null); break;
                }
                spinInt = Integer.valueOf(String.valueOf(editPrice.getText()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void addOperation (int epr, int equ, String sop, View v){
        ContentValues contentValues = new ContentValues();
        contentValues.put("product", sop);
        contentValues.put("price", epr);
        contentValues.put("quantity", equ);
        contentValues.put("date", date.getText().toString());
        database.insert(DataBaseOperations.DEFAULT_TABLE, null, contentValues);
        updateStatField();
        editQuantity.setText(null);
        editPrice.setText(null);
        new ToastMaker().showToast(this, "Успешно добавлено :)");
    }

    public void AddSellToBase(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);   //скрывает клаву по клику
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        try {
            String sop = spinnerOfProducts.getSelectedItem().toString();
            int epr = Integer.parseInt(editPrice.getText().toString());
            int equ = Integer.parseInt(editQuantity.getText().toString());
            if (epr < 450) {                                                  //если вбита цена менее 450 руб, то возникает Alert, который в случае подтверждения перенаправляет в addOperation
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Тут нет ошибки?")
                        .setMessage("Цена продукта очень низкая")
                        .setCancelable(false)
                        .setPositiveButton("Да, тут ошибка", (dialog, id) -> {
                            dialog.cancel();
                            editQuantity.setText(null);
                            editPrice.setText(null);
                        })
                        .setNegativeButton("Всё правильно!", (dialog, id) -> {
                            addOperation(epr, equ, sop, v);
                            dialog.dismiss();});
                AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                addOperation(epr, equ, sop, v);
            }
        } catch (Exception e) {
            new ToastMaker().showToast(this, "Наверное что-то не заполнено, случилось исключание :(");
        }
    }


    public void updateStatField() {
        TextView statisticsField = findViewById(R.id.statisticsField);
        StringBuilder stringBuilder = new StringBuilder();
        for (Product s : readFromDatabase()) {
            stringBuilder.append(s.toString()).append("\n").append("--------------\n");
        }
        statisticsField.setText(stringBuilder.toString());

    }

    public static ArrayList<Product> readFromDatabase() {
        ArrayList<Product> datalist = new ArrayList<>();
        Cursor cursor = database.query(DataBaseOperations.DEFAULT_TABLE, null, null, null, null, null, "_id DESC");
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("_id");
            int nameIndex = cursor.getColumnIndex("product");
            int priceIndex = cursor.getColumnIndex("price");
            int quantityIndex = cursor.getColumnIndex("quantity");
            int dateIndex = cursor.getColumnIndex("date");
            do {
                Date intoList = null;
                try {
                    intoList = sdfDate.parse(cursor.getString(dateIndex));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int total = cursor.getInt(priceIndex) * cursor.getInt(quantityIndex);
                datalist.add(new Product(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getInt(priceIndex), cursor.getInt(quantityIndex), intoList, total));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return datalist;
    }


    @SuppressLint("SimpleDateFormat")
    public void getCurrentTime() {
        sdfDate = new SimpleDateFormat("d MMMM yyyy", myDateFormatSymbols);
        Date now = new Date();
        String strDate = sdfDate.format(now);
        date = findViewById(R.id.currentDate);
        date.setText(strDate);
    }

    public static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {
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
        alertDialog.setIcon(R.drawable.ic_delete);

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

    public void exportButtonClick(View view) {
        Intent exportIntent = new Intent(this, Export_activity.class);
        startActivity(exportIntent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        double processed = spinInt*0.94;
        if (Integer.parseInt(String.valueOf(editPrice.getText())) != spinInt){
            processed = Integer.parseInt(String.valueOf(editPrice.getText()))*0.94;}
        if (isChecked) {
                editPrice.setText(String.valueOf((int)processed));
        } else {
            editPrice.setText(String.valueOf(spinInt));
        }
    }

    }










