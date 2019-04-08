package datalayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import datalayer.model.CategoryTable;
import datalayer.model.CityTable;
import datalayer.model.CountryTable;
import datalayer.model.FoodTable;
import datalayer.model.RestViewerTable;
import datalayer.model.RestaurantTable;
import datalayer.model.ReviewTable;
import datalayer.model.StateTable;
import datalayer.model.ViewerTable;

/**
 * Created by Samir KHan on 6/20/2016.
 */
// DATABASE CLASS
public class DBHelper extends SQLiteOpenHelper {

    // data declaration..
    private static final String DATABASE = "citymenu_db.db";
    private static final int VERSION = 1;
    private String query;

    Context context;

    public DBHelper(Context context) {
        super(context, DATABASE, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create * tables

        db.beginTransaction();
        db.execSQL(CountryTable.getCreateQuery());
        db.execSQL(StateTable.getCreateQuery());
        db.execSQL(CityTable.getCreateQuery());
        db.execSQL(RestaurantTable.getCreateQuery());
        db.execSQL(CategoryTable.getCreateQuery());
        db.execSQL(FoodTable.getCreateQuery());
        db.execSQL(ReviewTable.getCreateQuery());
        db.execSQL(ViewerTable.getCreateQuery());
        db.execSQL(RestViewerTable.getCreateQuery());
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        query = "drop table if exists " + CountryTable.TABLE_NAME;
        db.execSQL(query);
        query = "drop table if exists " + StateTable.TABLE_NAME;
        db.execSQL(query);
        query = "drop table if exists " + CityTable.TABLE_NAME;
        db.execSQL(query);
        query = "drop table if exists " + RestaurantTable.TABLE_NAME;
        db.execSQL(query);
        query = "drop table if exists " + ReviewTable.TABLE_NAME;
        db.execSQL(query);
        query = "drop table if exists " + CategoryTable.TABLE_NAME;
        db.execSQL(query);
        query = "drop table if exists " + FoodTable.TABLE_NAME;
        db.execSQL(query);
        query = "drop table if exists " + ViewerTable.TABLE_NAME;
        db.execSQL(query);
        query = "drop table if exists " + RestViewerTable.TABLE_NAME;
        db.execSQL(query);
        db.setTransactionSuccessful();
        db.endTransaction();
        onCreate(db);
    }

    public Cursor execRAW(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
        }catch(Exception exp){

        }
        return cursor;
    }

}
