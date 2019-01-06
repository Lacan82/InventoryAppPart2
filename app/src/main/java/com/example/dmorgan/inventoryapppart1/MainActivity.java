package com.example.dmorgan.inventoryapppart1;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.Toast;
import com.example.dmorgan.inventoryapppart1.data.InventoryContract.InventoryEntry;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 100;

    InventoryCursorAdapter CursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView inventoryList = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        inventoryList.setEmptyView(emptyView);

        CursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryList.setAdapter(CursorAdapter);

        inventoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                Uri currentInventoryURI = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                intent.setData(currentInventoryURI);

                startActivity(intent);
            }



        });



        getLoaderManager().initLoader(INVENTORY_LOADER, null,  this);
    }


    private void insertProduct() {

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Paper Towels");
        values.put(InventoryEntry.COLUMN_PRICE, 3.99);
        values.put(InventoryEntry.COLUMN_QUANTITY, 5);
        values.put(InventoryEntry.COLUMN_SUPPLIER, 0);
        values.put(InventoryEntry.COLUMN_PHONE, "555-555-5555");

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            Toast.makeText(this, "Saved" + newUri, Toast.LENGTH_SHORT).show();

    }

    private void deleteAll() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.dummy_data:
                insertProduct();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.delete_all:
                deleteAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER,
                InventoryEntry.COLUMN_PHONE
        };

        return new CursorLoader(this, InventoryEntry.CONTENT_URI, projection, null, null, null);
        }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        CursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        CursorAdapter.swapCursor(null);
    }

    public void sale(int id, int quantity) {

        if (quantity == 0) {
            Toast.makeText(this, getString(R.string.quantity_error), Toast.LENGTH_SHORT).show();
        } else {
            quantity--;

            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_QUANTITY, quantity);
            Uri currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
            int rowAffected = getContentResolver().update(currentUri, values, null, null);
            if (rowAffected == -1) {
                Toast.makeText(this, getString(R.string.sale_error), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.save), Toast.LENGTH_SHORT).show();
            }

        }

        CursorAdapter.notifyDataSetChanged();


    }


}

