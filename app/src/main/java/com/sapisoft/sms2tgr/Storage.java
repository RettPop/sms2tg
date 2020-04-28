package com.sapisoft.sms2tgr;


import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

/**
 * Created by smj on 12/9/17.
 * Storage
 */

public class Storage {
    private final String STORAGE = Constants.pkg+"Storage";
    private SharedPreferences preferences;
    private Context context;

    public Storage(Context context) {
        this.context = context;
    }

    public void write(String key,String data){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, data);
        editor.apply();
    }
    public String read(String key, String defValue){
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getString(key,defValue);
    }

    public void writeSet(String key,Set<String> list){
        preferences  = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // Save the list.
        editor.putStringSet(key, list);
        editor.apply();
    }
    public void appendSet(String key,String value){
        preferences  = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // Add the new value.
        Set<String> myStrings = readSet(key);
        myStrings.add(value);
        editor.apply();
    }
    public Set<String> readSet(String key){
        // Get the current list.
        preferences  = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return  preferences.getStringSet(key, null);
    }
}
