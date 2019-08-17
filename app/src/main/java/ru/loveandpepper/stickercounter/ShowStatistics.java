package ru.loveandpepper.stickercounter;

import android.graphics.Color;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class ShowStatistics {
    private static int marginalForMonth;
    private static int allsells;
        //<item>Windows 10 Наклейка</item>
        //<item>Windows 7 Наклейка</item>
        //<item>Windows 10 Ключ</item>
        //<item>Windows 7 Ключ</item>
        //<item>Office 2016 PP Ключ</item>
        //<item>Office 2019 PP Ключ</item>
        //<item>OEM-Пакет Windows 10</item>
        //<item>Другое</item>
    //Очень душный метод, переписать бы его как-нибудь толково

    void collectAndShow(TextView textView, TextView marginal, List<Product> list, int usdForStats, PieChartView pieChartView){
        StringBuilder stb = new StringBuilder();
        allsells = list.stream().map(Product::getTotal).reduce(0,Integer::sum);
        textView.append(String.format(" на сумму %d руб.\n-------", allsells));

        //w10 наклейки
        int win10coaquantity = list.stream().filter(x -> x.getName().equals("Windows 10 Наклейка")).map(Product::getQuantity).reduce(0, Integer::sum);
        int win10coaprice = list.stream().filter(x -> x.getName().equals("Windows 10 Наклейка")).map(Product::getTotal).reduce(0, Integer::sum);
        int win10coatotal = win10coaprice - (6*usdForStats*win10coaquantity);
        if (win10coaquantity > 0) {
            stb.append(String.format("\nНаклеек Windows 10 : %d шт, на общую сумму %d руб. Чистыми ~ %d руб.\n-------", win10coaquantity, win10coaprice , win10coatotal));
        } else { stb.append("\nНаклеек Windows 10 пока в этом месяце не продано\n-------");}
        //w7 наклейки
        int win7coaquantity = list.stream().filter(x -> x.getName().equals("Windows 7 Наклейка")).map(Product::getQuantity).reduce(0, Integer::sum);
        int win7coaprice = list.stream().filter(x -> x.getName().equals("Windows 7 Наклейка")).map(Product::getTotal).reduce(0, Integer::sum);
        int win7coatotal = win7coaprice - (4*usdForStats*win7coaquantity);
        if (win7coaquantity > 0){
            stb.append(String.format("\nНаклеек Windows 7 : %d шт, на общую сумму %d руб. Чистыми ~ %d руб.\n-------", win7coaquantity, win7coaprice, win7coatotal));
        } else { stb.append("\nНаклеек Windows 7 пока в этом месяце не продано\n-------");}
        //w10 ключи
        int win10keyquantity = list.stream().filter(x -> x.getName().equals("Windows 10 Ключ")).map(Product::getQuantity).reduce(0, Integer::sum);
        int win10keyprice = list.stream().filter(x -> x.getName().equals("Windows 10 Ключ")).map(Product::getTotal).reduce(0, Integer::sum);
        int win10keytotal = win10keyprice - (350*win10keyquantity);
        if (win10keyquantity > 0){
            stb.append(String.format("\nКлючей Windows 10 : %d шт, на общую сумму %d руб. Чистыми ~ %d руб.\n-------", win10keyquantity, win10keyprice, win10keytotal));
        } else { stb.append("\nКлючей Windows 10 пока в этом месяце не продано\n-------");}
        //w7 ключи
        int win7keyquantity = list.stream().filter(x -> x.getName().equals("Windows 7 Ключ")).map(Product::getQuantity).reduce(0, Integer::sum);
        int win7keyprice = list.stream().filter(x -> x.getName().equals("Windows 7 Ключ")).map(Product::getTotal).reduce(0, Integer::sum);
        int win7keytotal = win7keyprice - (350*win7keyquantity);
        if (win7keyquantity > 0){
            stb.append(String.format("\nКлючей Windows 7 : %d шт, на общую сумму %d руб. Чистыми ~ %d руб.\n-------", win7keyquantity, win7keyprice, win7keytotal));
        } else { stb.append("\nКлючей Windows 7 пока в этом месяце не продано\n-------");}
        //Офисы ОБА!!
        int officekeyquantity = list.stream().filter(x -> x.getName().contains("Office")).map(Product::getQuantity).reduce(0, Integer::sum);
        int officekeyprice = list.stream().filter(x -> x.getName().contains("Office")).map(Product::getTotal).reduce(0, Integer::sum);
        int officekeytotal = officekeyprice - (650*officekeyquantity);
        if (officekeyquantity > 0){
            stb.append(String.format("\nКлючей MS Office 16\\19 : %d шт, на общую сумму %d руб. Чистыми ~ %d руб.\n-------", officekeyquantity, officekeyprice, officekeytotal));
        } else { stb.append("\nКлючей Office пока в этом месяце не продано\n-------");}
        //OEM-пакеты Windows 10
        int win10oempackquantyty = list.stream().filter(x -> x.getName().contains("OEM-Пакет Windows 10")).map(Product::getQuantity).reduce(0, Integer::sum);
        int win10oempackprice = list.stream().filter(x -> x.getName().contains("OEM-Пакет Windows 10")).map(Product::getTotal).reduce(0, Integer::sum);
        int win10oempacktotal = win10oempackprice - (19*usdForStats*win10oempackquantyty);
        if (win10oempackquantyty > 0){
            stb.append(String.format("\nOEM-пакетов Windows 10 : %d шт, на общую сумму %d руб. Чистыми ~ %d руб.\n-------", win10oempackquantyty, win10oempackprice, win10oempacktotal));
        }
        else {stb.append("\nOEM-пакетов Windows 10 пока в этом месяце не продано\n-------");}
        //Другое
        int otherquantity = list.stream().filter(x -> x.getName().equals("Другое")).map(Product::getQuantity).reduce(0, Integer::sum);
        int otherprice = list.stream().filter(x -> x.getName().equals("Другое")).map(Product::getTotal).reduce(0, Integer::sum);
        if (otherquantity > 0){
            stb.append(String.format("\nДругие товары проданы %d раз, на общую сумму %d руб. Считается чистыми.", otherquantity, otherprice));
        } else { stb.append("\nДругих товаров в этом месяце не продано");}
        textView.append(stb);
        marginalForMonth = (win10coatotal + win7coatotal + win10keytotal + win7keytotal + officekeytotal + otherprice);
        marginal.setText(String.format("Всего месяц (без рекламы) чистыми ~ %d руб.", marginalForMonth));

        pieChartDrawer(win10coaprice, win7coaprice, win10keyprice, win7keyprice, officekeyprice, win10oempackprice, otherprice, pieChartView);
    }

    private void pieChartDrawer(int win10coasells, int win7coasells, int w10keysells, int w7keysells, int office, int win10oem, int other, PieChartView pcw){
        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(new SliceValue((win10coasells), Color.parseColor("#1E90FF")).setLabel("Win 10 COA"));
        pieData.add(new SliceValue((win7coasells), Color.parseColor("#7FFFD4")).setLabel("Win 7 COA"));
        pieData.add(new SliceValue((w10keysells), Color.parseColor("#6495ED")).setLabel("Win 10 Ключ"));
        pieData.add(new SliceValue((w7keysells), Color.parseColor("#00CED1")).setLabel("Win 7 Ключ"));
        pieData.add(new SliceValue((office), Color.parseColor("#7B68EE")).setLabel("Office"));
        pieData.add(new SliceValue((win10oem), Color.parseColor("#9370d8")).setLabel("Win 10 OEM-Pack"));
        pieData.add(new SliceValue((other), Color.parseColor("#191970")).setLabel("Другое"));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasCenterCircle(true).setHasLabels(true).setValueLabelTextSize(11);
        pieChartData.setHasLabelsOutside(true);
        pcw.setPieChartData(pieChartData);
    }
}
