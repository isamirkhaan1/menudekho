package datalayer.model;

/**
 * Created by Samir KHan on 6/21/2016.
 */

// Restaurant Table Model..
public class RestaurantTable {

    public static final String TABLE_NAME = "restaurant";

    public static String getCreateQuery() {
        // column names: id, name, sub_title, street_address, city_id, state_id, country_id, phone_no, mobile_no
        // opening_time, closing_time, self_delivery, darewro_partner, website, current_version, priority, ic_logo,
        // created_date, modified_date
        String query;
        query = "CREATE TABLE '" + TABLE_NAME + "' (  'id' INTEGER,'name' TEXT, 'sub_title' TEXT, 'street_address' TEXT," +
                " 'city_id' INTEGER, 'phone_no' TEXT, 'mobile_no' TEXT," +
                " 'opening_time' TEXT,'closing_time' TEXT, 'self_delivery' INTEGER,  'darewro_partner' INTEGER," +
                " 'website' TEXT,  'priority' INTEGER, 'img' BLOB,  'created_date' TEXT, " +
                " 'modified_date' TEXT, PRIMARY KEY(id) );";
        return query;
    }
}
