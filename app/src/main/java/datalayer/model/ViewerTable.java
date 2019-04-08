package datalayer.model;

/**
 * Created by Samir KHan on 9/25/2016.
 */
public class ViewerTable {

    public static final String TABLE_NAME = "viewer";
    public static String getCreateQuery(){
        String query;
        query = "CREATE TABLE '"+ TABLE_NAME +"' ('mac_address' TEXT, 'platform' INTEGER, 'action_date' TEXT, " +
                "'upload_date' TEXT); ";
        return query;
    }
}
