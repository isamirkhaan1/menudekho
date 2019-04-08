package datalayer.model;

/**
 * Created by Samir KHan on 6/21/2016.
 */
// COUNTRY TABLE MODEL..
public class CountryTable {

    public static final String TABLE_NAME = "country";

    public static String getCreateQuery() {
        String query;
        query = "CREATE TABLE '" + TABLE_NAME + "'( 'id' INTEGER PRIMARY KEY, 'name' TEXT, 'desc' TEXT, created_date TEXT, modified_date TEXT);";
        return query;
    }
}
