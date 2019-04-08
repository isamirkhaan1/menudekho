package datalayer.model;

/**
 * Created by Samir KHan on 6/21/2016.
 */
public class StateTable {
    public static final String TABLE_NAME = "state";

    public static String getCreateQuery() {
        String query;
        query = "CREATE TABLE  '" + TABLE_NAME + "' ( 'id' INTEGER PRIMARY KEY, 'name' TEXT, 'description' TEXT, 'country_id' INTEGER, created_date TEXT, modified_date TEXT);";
        return query;
    }
}
