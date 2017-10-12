package pricemypiece.Objects;

import android.content.Context;

/**
 * Created by Mendes on 05/01/2016.
 */
public class SharedPrefs {
    private static final String PREFERENCES_FILE = "pricemypiece_settings";
    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        android.content.SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        android.content.SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }
}
