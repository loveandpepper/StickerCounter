package ru.loveandpepper.stickercounter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WorkWithDbActivity extends AppCompatActivity {
    private String productString;
    private EditText price;
    private int productPrice;
    private EditText quantity;
    private ProductFinder productFinder = new ProductFinder();
    private DataBaseOperations dataBaseOperations;
    private SQLiteDatabase database;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_with_db);
/*        Intent productIntent = getIntent();
        TextView product = findViewById(R.id.productfield);
        quantity = findViewById(R.id.editText_Quantity);
        productString = productIntent.getStringExtra("product_name");
        product.setText(productString);
        price = findViewById(R.id.editText_Price);
        productPrice = Integer.parseInt(productFinder.getProduct(productString).get(1));
        price.setHint(productPrice + " руб. (за штуку!)");*/
        dataBaseOperations = new DataBaseOperations(this);
        database = dataBaseOperations.getWritableDatabase();
    }

    public void talkToSomebody(View view) {
/*        Intent sendMessage = new Intent();
        sendMessage.setType("text/plain");
        String msg = "Продали тут " + productString + " в количестве " + quantity + " штук. Эта программка пока не знает, радоваться ли такому количеству. Но позже мы её научим =)";
        sendMessage.putExtra(Intent.EXTRA_TEXT, msg);
        Intent chooseAlways = Intent.createChooser(sendMessage, "Через что отправим?");
        startActivity(chooseAlways);*/

// Код чтения из таблицы, чисто для теста:
        Cursor cursor = database.query("sells", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("_id");
            int nameIndex = cursor.getColumnIndex("product");
            int priceIndex = cursor.getColumnIndex("price");
            int quantityIndex = cursor.getColumnIndex("quantity");
            int dateIndex = cursor.getColumnIndex("date");
            do {
                System.out.println(cursor.getInt(idIndex) + " " +
                        cursor.getString(nameIndex) + " " +
                        cursor.getInt(priceIndex) + " " +
                        cursor.getInt(quantityIndex) + " " +
                        cursor.getString(dateIndex));
            }
            while (cursor.moveToNext()); }
        else {
            System.out.println("NO ROWS!!");
        }
        cursor.close();
    }

    public void processSQL(View view) {

    }
}
