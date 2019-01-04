package com.example.dmorgan.inventoryapppart1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dmorgan.inventoryapppart1.data.InventoryContract.InventoryEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {


    private static String DATABASE = "inventory.db";

    private static final  int VERSION = 1;

    public InventoryDbHelper(Context context) { super(context, DATABASE, null, VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
            + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
            + InventoryEntry.COLUMN_PRICE + " REAL NOT NULL, "
            + InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
            + InventoryEntry.COLUMN_SUPPLIER + " INTEGER NOT NULL, "
            + InventoryEntry.COLUMN_PHONE + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
