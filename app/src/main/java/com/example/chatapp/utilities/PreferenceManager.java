package com.example.chatapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * A class which manages getting and setting user data
 */
public class PreferenceManager {

    private final SharedPreferences sharedPreferences;


    /**
     * Constructor
     * @param context Context object used to set sharedPreferences
     */
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }


    /**
     * Used for setting user boolean data
     * @param key Boolean data key
     * @param value Boolean data new value
     */
    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    /**
     * Used for getting user boolean data
     * @param key Boolean data key
     * @return The boolean value
     */
    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }


    /**
     * Used for setting user string data
     * @param key String data key
     * @param value String data new value
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    /**
     * Used for getting user string data
     * @param key String data key
     * @return The string value
     */
    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }


    /**
     * clears data
     */
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
