package custom;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Samir KHan on 8/18/2016.
 */
public class PreferencesFile {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    final public static String FILE_NAME = "com.samirkhan.apps.citymenu.file";
    final public static String DEFAULT_CITY = "defaultCity";
    final public static String FIRST_SYNCED = "firstSynced";
    final public static String LAST_UPDATES = "lastUpdates";

    final public static String REST_USERNAME = "restUsername";
    final public static String REST_PASSWORD = "restPassword";
    final public static String REST_ID = "restId";
    final public static String EMPTY = null;
    final public int DEFAULT_REST_ID = 0;

    public PreferencesFile(Context context) {
        //this.context = context;
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String fileName() {
        return FILE_NAME;
    }

    public int restId() {
        int restId = sharedPreferences.getInt(REST_ID, DEFAULT_REST_ID);
        return restId;
    }

    public void setRestId(int restId) {
        editor.putInt(REST_ID, restId);
        editor.apply();
    }

    public void removeRestId() {
        editor.remove(REST_ID);
        editor.apply();
    }

    public String restUsername() {
        String restUsername = sharedPreferences.getString(REST_USERNAME, EMPTY);
        return restUsername;
    }

    public void setRestUsername(String restUsername) {
        editor.putString(REST_USERNAME, restUsername);
        editor.apply();
    }

    public void removeRestUsername() {
        editor.remove(REST_USERNAME);
        editor.apply();
    }

    public String restPassword() {
        String password = sharedPreferences.getString(REST_PASSWORD, EMPTY);
        return password;
    }

    public void setRestPassword(String password) {
        editor.putString(REST_PASSWORD, password);
        editor.apply();
    }

    public void removeRestPassword() {
        editor.remove(REST_PASSWORD);
        editor.apply();
    }


    public int defaultCityId() {
        int defaultCityId = sharedPreferences.getInt(DEFAULT_CITY, -1);
        return defaultCityId;
    }

    public void setDefaultCityId(int defaultCityId) {
        editor.putInt(DEFAULT_CITY, defaultCityId);
        editor.apply();
    }

    public boolean firstTime() {
        boolean firstSynced = sharedPreferences.getBoolean(FIRST_SYNCED, true);
        return firstSynced;
    }

    public void setFirstTime(boolean defaultCity) {
        editor.putBoolean(FIRST_SYNCED, defaultCity);
        editor.apply();
    }

    public String lastUpdates() {

        String lastDate = sharedPreferences.getString(LAST_UPDATES, null);
        return lastDate;
    }

    public void setLastUpdates(String updatesAvailable) {
        editor.putString(LAST_UPDATES, updatesAvailable);
        editor.apply();
    }


}
