package ru.loveandpepper.stickercounter.callback_dialogs;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import ru.loveandpepper.stickercounter.DataBaseOperations;
import ru.loveandpepper.stickercounter.Export_activity;
import ru.loveandpepper.stickercounter.R;
import ru.loveandpepper.stickercounter.ToastMaker;

public class AddCallbackDialog extends AppCompatDialogFragment {
    private EditText number;
    private EditText name;
    private EditText comment;
    private ImageButton imageButton;
    private Export_activity ex;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_callback_layout, null);
        number = view.findViewById(R.id.edit_phone);
        name = view.findViewById(R.id.editName);
        comment = view.findViewById(R.id.editComment);
        imageButton = view.findViewById(R.id.imageButtonFromCalls);
        ex = new Export_activity();
        if (ex.getPhoneNum() != null) {
            number.setText(ex.getPhoneNum());}
        if (ex.getNameForNum() != null) {
            name.setText(ex.getNameForNum());}
        imageButton.setOnClickListener(view1 -> {
                    Intent callLogIntent = new Intent(getActivity(), CallLogClass.class);
                    startActivity(callLogIntent);
                    getActivity().finish();
                });
                builder.setView(view)
                        .setIcon(R.drawable.ic_add_callback)
                        .setTitle("Добавление нового CallBack")
                        .setNegativeButton("Отмена", (dialogInterface, i) -> {
                            cleanUp();
                        })
                        .setPositiveButton("Добавить", (dialogInterface, i) -> {
                            if (!number.getText().toString().isEmpty() && !name.getText().toString().isEmpty() && !comment.getText().toString().isEmpty()) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("phone", number.getText().toString());
                                contentValues.put("name", name.getText().toString());
                                contentValues.put("comment", comment.getText().toString());
                                Export_activity.database.insert(DataBaseOperations.CALLBACK_TABLE, null, contentValues);
                                cleanUp();
                                Intent intentForFinish = new Intent(getActivity(), Export_activity.class);
                                startActivity(intentForFinish);
                                getActivity().finish();
                                new ToastMaker().showToast(getActivity(), "Успешно добавлено!");
                            } else {
                                new ToastMaker().showToast(getActivity(), "Одно из полей пустое!");
                                new AddCallbackDialog().show(getFragmentManager(), "recreation");
                            }
                        });

                return builder.create();
            }

            public void cleanUp(){
                ex.setPhoneNum(null);
                ex.setNameForNum(null);
            }


        }


