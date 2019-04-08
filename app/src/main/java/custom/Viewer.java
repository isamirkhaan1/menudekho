package custom;

import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import datalayer.DBHelper;
import datalayer.LocalDataManager;

/**
 * Created by Samir KHan on 9/27/2016.
 */
public class Viewer {
    Context mContext;
    private String mMacAddress;
    private final String mPlatform = "0";

    public Viewer(Context context) {
        this.mContext = context;
        this.mMacAddress = new UserInformation(mContext).macAddress();
    }

    public void add() {
        String date = getDateAndTime();
        String[] data = {mMacAddress, mPlatform, date};
        new LocalDataManager(mContext).addViewer(data);
    }

    public void addRestViewer(int restId) {

        String date = getDateAndTime();
        String[] data = {mMacAddress, mPlatform, date, restId + ""};
        new LocalDataManager(mContext).addRestViewer(data);
    }

    public void deleteViewers() {
        new LocalDataManager(mContext).deleteViewers();
    }

    public void deleteRestViewers() {
        new LocalDataManager(mContext).deleteRestViewers();
    }

    public String uploadViewer() {
        JSONArray jsonArray = new JSONArray();

        String query = "SELECT * FROM viewer";
        Cursor cursor = new DBHelper(mContext).execRAW(query);
        try {
            if (cursor.moveToFirst())
                do {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("mac_address", cursor.getString(0));
                    jsonObject.put("platform", cursor.getString(1));
                    jsonObject.put("action_date", cursor.getString(2));
                    jsonArray.put(jsonObject);
                } while (cursor.moveToNext());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
        return jsonArray.toString();
    }

    public String uploadRestViewer() {
        JSONArray jsonArray = new JSONArray();

        String query = "SELECT * FROM rest_viewer";
        Cursor cursor = new DBHelper(mContext).execRAW(query);
        try {
            if (cursor.moveToFirst())
                do {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("mac_address", cursor.getString(0));
                    jsonObject.put("platform", cursor.getString(1));
                    jsonObject.put("action_date", cursor.getString(2));
                    jsonObject.put("upload_date", getDateAndTime());
                    jsonObject.put("rest_id", cursor.getString(3));
                    jsonArray.put(jsonObject);
                } while (cursor.moveToNext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    private String getDateAndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");
        String date = sdf.format(new Date());
        return date;
    }

}
