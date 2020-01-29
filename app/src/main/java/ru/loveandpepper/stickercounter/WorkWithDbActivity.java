package ru.loveandpepper.stickercounter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.view.PieChartView;

import static ru.loveandpepper.stickercounter.R.string.rub;


public class WorkWithDbActivity extends AppCompatActivity {

    public DataBaseOperations dataBaseOperations;
    public SQLiteDatabase database;
    public TextView usdcurrency;
    public TextView statview;
    public TextView monthSells;
    public TextView marginaltotal;
    public Date currentDate;
    public List<Product> thisMonthStatistics;
    double value_usd;
    double value_usd_dhgate;
    public PieChartView pieChartView;
    private ProgressBar progressBar;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_with_db);
        statview = findViewById(R.id.statview);
        dataBaseOperations = new DataBaseOperations(this);
        database = dataBaseOperations.getWritableDatabase();
        usdcurrency = findViewById(R.id.usd_cur_textView);
        monthSells = findViewById(R.id.monthsells);
        marginaltotal = findViewById(R.id.marginal_textview);
        pieChartView = findViewById(R.id.pie_chart);
        progressBar = findViewById(R.id.progressBar);
        usdValueChecker usdValueChecker = new usdValueChecker();
        usdValueChecker.execute();
    }

    public void calculateEverything() {
        usdcurrency.setText(String.format("%s %s %s", getString(R.string.kurs_po_cb), Math.round(value_usd * 100.00) / 100.00, getString(rub)));
        value_usd_dhgate = value_usd + value_usd * 0.042;
        StringBuilder stb = new StringBuilder();
        stb.append(String.format(("%s %s %s"), getString(R.string.kurs_dhgate), Math.round(value_usd_dhgate * 100.00) / 100.00, getString(rub)));
        statview.setText(stb);
        getThisMonthDB();
    }




    public void getThisMonthDB() {
        thisMonthStatistics = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("d MMMM yyyy", MainActivity.myDateFormatSymbols);
        currentDate = new Date();
        Cursor cursor = database.query(DataBaseOperations.DEFAULT_TABLE, null,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("_id");
            int nameIndex = cursor.getColumnIndex("product");
            int priceIndex = cursor.getColumnIndex("price");
            int quantityIndex = cursor.getColumnIndex("quantity");
            int dateIndex = cursor.getColumnIndex("date");
            do {
                try {
                    Date dbdate = format.parse(cursor.getString(dateIndex));
                    if (currentDate.getMonth() == dbdate.getMonth() && currentDate.getYear() == dbdate.getYear()) {
                        int total = cursor.getInt(priceIndex) * cursor.getInt(quantityIndex);
                        thisMonthStatistics.add(new Product(cursor.getInt(idIndex), cursor.getString(nameIndex), cursor.getInt(priceIndex), cursor.getInt(quantityIndex), dbdate, total));
                    }
                } catch (ParseException e) {
                    System.out.println("!!!!-EXCEPTION WITH DATE PARSE-!!!!  " + e.getMessage());
                }
            }
            while (cursor.moveToNext());
        } else {
            System.out.println("Операций за этот месяц пока нет!");
        }
        cursor.close();
        int value_usd_int = (int) Math.round(value_usd_dhgate);
        new ShowStatistics().collectAndShow(monthSells, marginaltotal, thisMonthStatistics, value_usd_int, pieChartView); // запускаем метод, который будет расчитывать статистику из ЭррэйЛиста с добавленным продуктами за этот месяц
    }



    class usdValueChecker extends AsyncTask<Void, Double, Double> {

        @Override
        protected Double doInBackground(Void... voids) {
            try {
                JSONObject url = new JSONObject(IOUtils.toString(new URL("https://www.cbr-xml-daily.ru/daily_json.js"), Charset.forName("UTF-8")));
                url = (JSONObject) url.get("Valute");
                url = (JSONObject) url.get("USD");
                return (double) url.get("Value");

            } catch (Exception e) {
                new ToastMaker().showToast(WorkWithDbActivity.this, "Ошибка! " + e.getMessage());
                return null;
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            usdcurrency.setText("Курс загружается");
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Double s) {
            super.onPostExecute(s);
            value_usd = s;
            calculateEverything();
            progressBar.setVisibility(View.GONE);
        }
    }
}


