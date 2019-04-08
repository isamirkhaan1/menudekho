package datalayer.model;

/**
 * Created by Samir KHan on 6/20/2016.
 */
// CITY TABLE MODEL..
public class CityTable {

    public static final String TABLE_NAME = "city";

    public static String getCreateQuery() {
        String query;
        query = "CREATE TABLE   '" + TABLE_NAME + "' ( 'id' INTEGER PRIMARY KEY, 'name' TEXT, 'state_id' INTEGER, 'img' BLOB, created_date TEXT, modified_date TEXT);";
        return query;
    }
}
