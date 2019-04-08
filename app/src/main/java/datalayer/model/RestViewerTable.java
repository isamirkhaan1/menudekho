package datalayer.model;

/**
 * Created by Samir KHan on 9/25/2016.
 */
public class RestViewerTable {
    public static final String TABLE_NAME = "rest_viewer";
    public static String getCreateQuery(){
        String query;
        query = "CREATE TABLE '"+ TABLE_NAME +"' ('id' INTEGER PRIMARY KEY, 'mac_address' TEXT, 'platform' INTEGER, 'action_date' TEXT, " +
                "'upload_date' TEXT, 'rest_id' INTEGER); ";
        return query;
    }
}
