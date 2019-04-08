package datalayer;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Samir KHan on 6/23/2016.
 */
// *************** IT'S ALL ABOUT GETTING DATA FROM DATABASE ****************************
public class LocalDataManager {

    Context context;
    String query;
    ArrayList<String[]> data;
    DBHelper helper;

    public LocalDataManager(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        this.helper = new DBHelper(context);
    }

    /* CITY TABLE */
    public ArrayList<Object[]> getCity(String whereClause, boolean forList) {
        if (whereClause == null)
            whereClause = "";
        ArrayList<Object[]> data = new ArrayList<>();
        if (forList) {
            query = "SELECT cit.id, cit.name, st.name, count.name, cit.img, (SELECT IFNULL(count(r1.name),0) FROM restaurant AS r1 " +
                    " WHERE cit.id = r1.city_id) AS total_rest FROM city AS cit INNER JOIN state AS st " +
                    " ON st.id = cit.state_id INNER JOIN country AS count ON count.id = st.country_id " + whereClause +
                    " ORDER BY cit.name";
        } else {
            query = "SELECT cit.id, cit.name, st.name, count.name, cit.img FROM city AS cit INNER JOIN state AS st " +
                    " ON st.id = cit.state_id INNER JOIN country AS count ON count.id = st.country_id " + whereClause +
                    " ORDER BY cit.name";
        }
        Cursor cursor = helper.execRAW(query);
        if (cursor.moveToFirst()) {
            data.clear();
            do {
                String cityId = cursor.getString(0);
                String cityName = cursor.getString(1);
                String details = cursor.getString(2) + ", " + cursor.getString(3);
                StringBuilder img;
                img = new StringBuilder(cursor.getString(4));
                String total_rest = "";
                if (forList) {
                    total_rest = cursor.getString(5);
                }
                data.add(new Object[]{cityId, cityName, details, img, total_rest});
            } while (cursor.moveToNext());
        }
        return data;
    }

    public ArrayList<Object[]> getRestaurant(String whereClause) {
        if (whereClause == null)
            whereClause = "";

        ArrayList<Object[]> data = new ArrayList<>();
        query = "SELECT r.id, r.name, r.sub_title, r.street_address, c.name, r.phone_no, r.mobile_no, " +
                " (SELECT round(IFNULL(AVG(IFNULL(rev.stars,0)), 0), 1) FROM REVIEW AS rev where rev.rest_id " +
                " = r.id) AS stars, (SELECT COUNT(IFNULL(rev.stars, 0)) FROM review AS rev WHERE rev.rest_id = r.id)" +
                " AS reviews, r.img, r.opening_time, r.closing_time, r.self_delivery, r.darewro_partner, r.website, " +
                " r.modified_date FROM restaurant AS r INNER JOIN city AS c ON c.id = r.city_id " + whereClause +
                " ORDER BY 7, r.priority, r.name ";
        Cursor cursor = new DBHelper(context).execRAW(query);
        if (cursor.moveToFirst()) {
            data.clear();
            do {
                String restId = cursor.getString(0);
                String restName = cursor.getString(1);
                String sub_title = cursor.getString(2);
                String address = cursor.getString(3) + ", " + cursor.getString(4);
                String phone = cursor.getString(5);
                String mobile = cursor.getString(6);
                String stars = cursor.getString(7);
                String reviews = cursor.getString(8);
                StringBuilder img;
                img = new StringBuilder(cursor.getString(9));
                String openingTime = cursor.getString(10);
                String closingTime = cursor.getString(11);
                String delivery = cursor.getString(12);
                String darewro = cursor.getString(13);
                String website = cursor.getString(14);
                String lastModified = cursor.getString(15);

                data.add(new Object[]{restId, restName, sub_title, address, phone, mobile, stars, reviews, img, openingTime, closingTime, delivery, darewro, website, lastModified});
            } while (cursor.moveToNext());
        }
        return data;
    }

    public ArrayList<String[]> getCategory(String whereClause) {
        if (whereClause == null)
            whereClause = "";

        query = "SELECT c.id, c.name, c.descrip, c.rest_id, c.created_date, c.modified_date FROM category AS c " + whereClause;
        Cursor cursor = new DBHelper(context).execRAW(query);
        if (cursor.moveToFirst()) {
            data.clear();
            do {
                String categoryId = cursor.getString(0);
                String categoryName = cursor.getString(1);
                String desc = cursor.getString(2);
                String restId = cursor.getString(3);
                String createdDate = cursor.getString(4);
                String modifiedDate = cursor.getString(5);
                data.add(new String[]{categoryId, categoryName, desc, restId, createdDate, modifiedDate});
            } while (cursor.moveToNext());
        }
        return data;

    }

    public ArrayList<String[]> getFood(String whereClause) {
        if (whereClause == null)
            whereClause = "";

        query = "SELECT f.id, f.name, f.price, f.descrip, f.flavours, f.min_time, f.available," +
                " f.img, f.rest_id, f.cat_id, f.created_date, f.modified_date FROM food AS f " +
                " INNER JOIN category AS c ON c.id = f.cat_id INNER JOIN restaurant AS r ON " +
                " r.id = c.rest_id " + whereClause +
                " ORDER BY f.name";

        Cursor cursor = new DBHelper(context).execRAW(query);
        if (cursor.moveToFirst()) {
            data.clear();

            do {
                String foodId = cursor.getString(0);
                String foodName = cursor.getString(1);
                String foodPrice = cursor.getString(2);
                String desc = cursor.getString(3);
                String flavours = cursor.getString(4);
                String minTime = cursor.getString(5);
                String available = ((Integer.parseInt(cursor.getString(6)) > 0) ? "YES" : "NO");
                String img = cursor.getString(7);
                String restId = cursor.getString(8);
                String catId = cursor.getString(9);
                String createdDate = cursor.getString(10);
                String modifiedDate = cursor.getString(11);
                data.add(new String[]{foodId, foodName, foodPrice, desc, flavours, minTime,
                        available, img, restId, catId, createdDate, modifiedDate});
            }
            while (cursor.moveToNext());
        }
        return data;
    }

    public ArrayList<String[]> getReview(String whereClause) {
        if (whereClause == null)
            whereClause = "";

        query = "SELECT rev.id, rev.name, rev.contact_no, rev.remarks, ROUND(rev.stars, 1), rev.date " +
                " FROM review AS rev INNER JOIN" +
                " restaurant AS r ON r.id = rev.rest_id " + whereClause +
                " ORDER BY rev.id desc;";

        Cursor cursor = helper.execRAW(query);
        if (cursor.moveToFirst()) {
            data.clear();
            do {
                String revId = cursor.getString(0);
                String revUsername = cursor.getString(1);
                String revContact = cursor.getString(2);
                String revRemarks = cursor.getString(3);
                String revStars = cursor.getString(4);
                String revDate = cursor.getString(5);
                data.add(new String[]{revId, revUsername, revContact, revRemarks, revStars, revDate});
            } while (cursor.moveToNext());
        }
        return data;
    }

    public Boolean updateRow(String query) {

        try {
            Cursor cursor = new DBHelper(context).execRAW(query);
            cursor.moveToFirst();
            return true;
        } catch (Exception exp) {
            return false;
        }
    }

    public ArrayList<String> getSingleColumn(String query) {
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor = new DBHelper(context).execRAW(query);
        if (cursor.moveToFirst())
            do {
                arrayList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        return arrayList;
    }

    public String modifiedDate(String tableName, String colName) {
        query = "SELECT IFNULL(MAX(" + colName + "),'2000:01:01') FROM " + tableName;
        Cursor cursor = new DBHelper(context).execRAW(query);
        if (cursor == null)
            return "2000-00-00";
        else if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        return "2000-00-00";
    }

    public void addViewer(String[] data) {
        query = "INSERT INTO viewer VALUES ( '" + data[0] +
                "', " + data[1] + ", '" + data[2] + "', 'nill')";
        Cursor cursor = new DBHelper(context).execRAW(query);
        if(cursor.moveToNext())
        {

        }
    }

    public void addRestViewer(String[] data) {
        query = "INSERT INTO rest_viewer VALUES ( '" + data[0] +
                "', " + data[1] + ", '" + data[2] + "', 'nill', " + data[3] + ")";
        new DBHelper(context).execRAW(query);

    }

    public void deleteViewers(){
        query = "DELETE FROM viewer";
        new DBHelper(context).execRAW(query);
    }

    public void deleteRestViewers(){

        query = "DELETE FROM rest_viewer";
        new DBHelper(context).execRAW(query);
    }


}
