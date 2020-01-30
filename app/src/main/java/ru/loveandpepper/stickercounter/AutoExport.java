package ru.loveandpepper.stickercounter;

import android.content.Context;
import android.content.SharedPreferences;

public class AutoExport {
    public static final String STORAGE_NAME = "dateSettings";

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    private static Context context;

    public static void init( Context cntxt ){
        context = cntxt;
    }

    private static void init(){
        settings = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public static void addProperty( String name, String value ){
        if( settings == null ){
            init();
        }
        editor.putString( name, value );
        editor.commit();
    }

    public static String getProperty( String name ){
        if( settings == null ){
            init();
        }
        return settings.getString( name, null );
    }
}
