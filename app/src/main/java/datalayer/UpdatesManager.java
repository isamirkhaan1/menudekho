package datalayer;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import custom.ExceptionHandling;

/**
 * Created by Samir KHan on 9/5/2016.
 */
public class UpdatesManager {
    Context context;
    SQLiteDatabase database;

    public UpdatesManager(Context context) {
        this.context = context;
        this.database = new DBHelper(context).getWritableDatabase();
    }

    public void country(BufferedReader reader) {
        String data = BufferedReaderToString.convert(reader);
        try {
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.length()<3)
                    return;
                if (i == 0) {
                    String response = jsonObject.getString("response");
                    if (response.equals("error")) {
                        throw new Exception(ExceptionHandling.SERVER_RESPONSE_EXCEPTION);
                    }
                }
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String desc = jsonObject.getString("descrip");
                String createdDate = jsonObject.getString("created_date");
                String modifiedDate = jsonObject.getString("modified_date");

                String query = "INSERT OR REPLACE INTO country VALUES(" + id + ", '" + name + "', '" +
                        desc + "', '" + createdDate + "', '" + modifiedDate + "' );";
                database.execSQL(query);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("UpdatesManager", "JSON error while updating country " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("UpdatesManager", "While updating country " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void state(BufferedReader reader) {
        String data = BufferedReaderToString.convert(reader);
        try {
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.length()<3)
                    return;
                if (i == 0) {
                    String response = jsonObject.getString("response");
                    if (response.equals("error")) {
                        throw new Exception(ExceptionHandling.SERVER_RESPONSE_EXCEPTION);
                    }
                }
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String desc = jsonObject.getString("desc");
                String country_id = jsonObject.getString("country_id");
                String createdDate = jsonObject.getString("created_date");
                String modifiedDate = jsonObject.getString("modified_date");

                String query = "INSERT OR REPLACE INTO state VALUES(" + id + ", '" + name + "', '" +
                        desc + "', " + country_id + ", '" + createdDate + "', '" + modifiedDate + "' );";

                database.execSQL(query);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("UpdatesManager", "JSON error while updating state " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("UpdatesManager", "While updating state " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void city(BufferedReader reader) {
        String data = BufferedReaderToString.convert(reader);
        try {
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.length()<3)
                    return;
                if (i == 0) {
                    String response = jsonObject.getString("response");
                    if (response.equals("error")) {
                        throw new Exception(ExceptionHandling.SERVER_RESPONSE_EXCEPTION);
                    }
                }
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String stateId = jsonObject.getString("state_id");
                String img =  jsonObject.getString("img");
                String createdDate = jsonObject.getString("created_date");
                String modifiedDate = jsonObject.getString("modified_date");

                String query = "INSERT OR REPLACE INTO city VALUES(" + id + ", '" + name + "', " +
                        stateId + ", '" + img + "', '" + createdDate + "', '" + modifiedDate + "' );";

                database.execSQL(query);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("UpdatesManager", "JSON error while updating city " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("UpdatesManager", "While updating city " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void restaurant(BufferedReader reader) {
        String data = BufferedReaderToString.convert(reader);
        try {
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.length()<3)
                    return;
                if (i == 0) {
                    String response = jsonObject.getString("response");
                    if (response.equals("error")) {
                        throw new Exception(ExceptionHandling.SERVER_RESPONSE_EXCEPTION);
                    }
                }
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String subTitle = jsonObject.getString("sub_title");
                String streetAdd = jsonObject.getString("street_address");
                String cityId = jsonObject.getString("city_id");
                String phone = jsonObject.getString("phone_no");
                String mobile = jsonObject.getString("mobile_no");
                String openingTime = jsonObject.getString("opening_time");
                String closingTime = jsonObject.getString("closing_time");
                String selfDelivery = jsonObject.getString("self_delivery");
                String darewroPartner = jsonObject.getString("darewro_partner");
                String website = jsonObject.getString("website");
                String priority = jsonObject.getString("priority");
                String img = jsonObject.getString("img");
                String createdDate = jsonObject.getString("created_date");
                String modifiedDate = jsonObject.getString("modified_date");

                String query = "INSERT OR REPLACE INTO restaurant VALUES(" + id + ", '" + name + "','" + subTitle + "', '" +
                        streetAdd + "', " + cityId + ", '" + phone + "', '" + mobile + "', '" + openingTime + "', '" +
                        closingTime + "', " + selfDelivery + ", " + darewroPartner + ", '" + website + "', " + priority +
                        ", '" + img + "', '" + createdDate + "', '" + modifiedDate + "' );";
                database.execSQL(query);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("UpdatesManager", "JSON error while updating restaurant " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("UpdatesManager", "While updating restaurant " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void category(BufferedReader reader) {
        String data = BufferedReaderToString.convert(reader);
        try {
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.length()<3)
                    return;
                if (i == 0) {
                    String response = jsonObject.getString("response");
                    if (response.equals("error")) {
                        throw new Exception(ExceptionHandling.SERVER_RESPONSE_EXCEPTION);
                    }
                }
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String desc = jsonObject.getString("descrip");
                String rest_id = jsonObject.getString("rest_id");
                String createdDate = jsonObject.getString("created_date");
                String modifiedDate = jsonObject.getString("modified_date");

                String query = "INSERT OR REPLACE INTO category VALUES(" + id + ", '" + name + "', '" +
                        desc + "', " + rest_id + ", '" + createdDate + "', '" + modifiedDate + "' );";
                database.execSQL(query);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("UpdatesManager", "JSON error while updating category " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("UpdatesManager", "While updating category " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void food(BufferedReader reader) {
        String data = BufferedReaderToString.convert(reader);
        try {
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.length()<3)
                    return;
                if (i == 0) {
                    String response = jsonObject.getString("response");
                    if (response.equals("error")) {
                        throw new Exception(ExceptionHandling.SERVER_RESPONSE_EXCEPTION);
                    }
                }
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String price = jsonObject.getString("price");
                String desc = jsonObject.getString("descrip");
                String flavours = jsonObject.getString("flavours");
                String minTime = jsonObject.getString("min_time");
                String avail = jsonObject.getString("available");
                String img = jsonObject.getString("img");
                String rest_id = jsonObject.getString("rest_id");
                String cat_id = jsonObject.getString("cat_id");
                String createdDate = jsonObject.getString("created_date");
                String modifiedDate = jsonObject.getString("modified_date");

                String query = "INSERT OR REPLACE INTO food VALUES(" + id + ", '" + name + "', " + price + ", '" +
                        desc + "', '" + flavours + "', " + minTime + ", " + avail + ", '" + img + "'," + rest_id +
                        ", " + cat_id + ", '" + createdDate + "', '" + modifiedDate + "' );";
                database.execSQL(query);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("UpdatesManager", "JSON error while updating food " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("UpdatesManager", "While updating food " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void review(BufferedReader reader) {
        String data = BufferedReaderToString.convert(reader);
        try {
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.length()<3)
                    return;
                if (i == 0) {
                    String response = jsonObject.getString("response");
                    if (response.equals("error")) {
                        throw new Exception(ExceptionHandling.SERVER_RESPONSE_EXCEPTION);
                    }
                }
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String contact = jsonObject.getString("contact_no");
                String remarks = jsonObject.getString("remarks");
                String stars = jsonObject.getString("stars");
                String rest_id = jsonObject.getString("rest_id");
                String date = jsonObject.getString("date");

                String query = "INSERT OR REPLACE INTO review VALUES(" + id + ", '" + name + "', '" +
                        contact + "', '" + remarks + "', " + stars + ", " + rest_id + ", '" + date + "' );";
                database.execSQL(query);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("UpdatesManager", "JSON error while updating reviews " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("UpdatesManager", "While updating reviews " + e.getMessage());
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Error while retrieving data..", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
