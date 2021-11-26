package dadm.scaffold.database;

import android.content.Context;
import android.content.SharedPreferences;

import dadm.scaffold.R;

public class Preferences {
    private static final String PREFERENCES = "preferences";

    public static void SetStringValue(Context context, String key, String save) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, save);
        editor.apply();
    }

    public static void SetBooleanValue(Context context, String key, boolean save) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, save);
        editor.apply();
    }

    public static String GetStringValue(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES, 0);
        return sharedPref.getString(key, null);
    }

    public static boolean GetBooleanValue(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES, 0);
        return sharedPref.getBoolean(key, true);
    }

    public static int GetShipValue(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES, 0);
        return sharedPref.getInt(key, R.drawable.ship_a);
    }

    public static void SetShipValue(Context context, String key, int save) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, save);
        editor.apply();
    }
}
