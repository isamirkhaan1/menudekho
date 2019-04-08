package datalayer.model;

/**
 * Created by Samir KHan on 6/21/2016.
 */
// Review Table Model..
public class ReviewTable {

    public static final String TABLE_NAME = "review";

    public static String getCreateQuery() {
        String query;
        query = "CREATE TABLE '" + TABLE_NAME + "' ( 'id'  INTEGER,  'name' TEXT, 'contact_no' TEXT, " +
                "'remarks' TEXT, 'stars' REAL, 'rest_id' INTEGER, 'date' TEXT,  PRIMARY KEY(id));";
        return query;
    }
}