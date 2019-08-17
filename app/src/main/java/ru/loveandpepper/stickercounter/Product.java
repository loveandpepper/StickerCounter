package ru.loveandpepper.stickercounter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Product {
    private int id;
    private String name;
    private int price;
    private int quantity;
    private Date date;
    private int total;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Product(int id, String name, int price, int quantity, Date date, int total) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.date = date;
        this.total = total;
    }
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdfDate = new SimpleDateFormat("d MMMM yyyy", MainActivity.myDateFormatSymbols);
    @Override
    public String toString() {
        return "id " + id +
                " | " + name  +
                " | " + price + " руб." +
                " | " + quantity +  " шт." +
                " | \n" + sdfDate.format(date) +
                " | Итог: " + total +
                " руб.";
    }
}
