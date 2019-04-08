package datalayer.model;

/**
 * Created by Samir KHan on 6/21/2016.
 */
// Food TABLE MODEL..
public class FoodTable {

    public static final String TABLE_NAME = "food";

    public static String getCreateQuery() {
        String query;
        query = "CREATE TABLE   '" + TABLE_NAME + "' ( 'id' INTEGER,'name' TEXT,'price' REAL,'descrip' TEXT," +
                " 'flavours' TEXT, 'min_time' TEXT, 'available' INTEGER, 'img' TEXT,  'rest_id' INTEGER," +
                " 'cat_id' INTEGER, 'created_date' TEXT, 'modified_date' TEXT, PRIMARY KEY(id));";
        return query;
    }
}
