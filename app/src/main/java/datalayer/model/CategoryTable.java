package datalayer.model;

/**
 * Created by Samir KHan on 6/21/2016.
 */
// CATEGORY TABLE MODEL
public class CategoryTable {

    public static final String TABLE_NAME = "category";

    public static String getCreateQuery() {
        String query;
        query = "CREATE TABLE '" + TABLE_NAME + "' ('id' INTEGER, 'name' TEXT, 'descrip'  TEXT, " +
                " 'rest_id' INTEGER, 'created_date' TEXT, 'modified_date' TEXT, PRIMARY KEY(id) );";
        return query;
    }
}
