package ru.loveandpepper.stickercounter;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastMaker {
    public void showToast(Context context, String phrase) {
        Toast toast = Toast.makeText(context.getApplicationContext(),
                phrase,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }




}
