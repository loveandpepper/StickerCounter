package ru.loveandpepper.stickercounter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import java.net.URL;
import java.nio.charset.Charset;

import static ru.loveandpepper.stickercounter.R.string.rub;


public class WorkWithDbActivity extends AppCompatActivity {

    private DataBaseOperations dataBaseOperations;
    private SQLiteDatabase database;
    public TextView usdcurrency;
    public TextView statview;
    double value_usd;
    double value_usd_dhgate;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_with_db);
        statview = findViewById(R.id.statview);
        dataBaseOperations = new DataBaseOperations(this);
        database = dataBaseOperations.getWritableDatabase();
        usdcurrency = findViewById(R.id.usd_cur_textView);

        usdValueChecker usdValueChecker = new usdValueChecker();
        usdValueChecker.execute();
    }

    public void calculateEverything(){
        usdcurrency.setText(String.format("%s %s %s", getString(R.string.kurs_po_cb), Math.round(value_usd*100.00)/100.00, getString(rub)));
        value_usd_dhgate = value_usd + value_usd*0.059;
        StringBuilder stb = new StringBuilder();
        stb.append(String.format(("%s %s %s"),getString(R.string.kurs_dhgate), Math.round(value_usd_dhgate*100.00)/100.00,getString(rub)));
        statview.setText(stb);

    }



    class usdValueChecker extends AsyncTask<Void, Double, Double> {

        @Override
        protected Double doInBackground(Void... voids) {
            try {
                JSONObject url = new JSONObject(IOUtils.toString(new URL("https://www.cbr-xml-daily.ru/daily_json.js"), Charset.forName("UTF-8")));
                url = (JSONObject) url.get("Valute");
                url = (JSONObject) url.get("USD");
                return (double) url.get("Value");

            }
            catch (Exception e) {
                System.out.println("!!!!!!!!!!!!!!!!" + e.getMessage() );
                return null;
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            usdcurrency.setText("Курс загружается");
        }

        @Override
        protected void onPostExecute(Double s) {
            super.onPostExecute(s);
            value_usd = s;
            calculateEverything();
        }
    }



// Код чтения из таблицы, чисто для теста:
/*        Cursor cursor = database.query("sells", null, null, null, null, null, null);
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

    }*/
}
