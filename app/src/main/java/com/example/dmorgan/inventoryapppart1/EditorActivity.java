package com.example.dmorgan.inventoryapppart1;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.dmorgan.inventoryapppart1.data.InventoryContract.InventoryEntry;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_LOADER = 100;

    private Uri currentUri;

    private EditText ProductName;
    private EditText Price;
    private EditText Quantity;
    private Spinner SupplierSpinner;
    private EditText Phone;

    private int Supplier = InventoryEntry.SUPPLIER_WALMART;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);

        Intent intent = getIntent();
        currentUri = intent.getData();

        if (currentUri == null) {
            setTitle(getString(R.string.editor_title));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_item_title));

            getLoaderManager().initLoader(EXISTING_LOADER, null, this);
        }



        ProductName = findViewById(R.id.edit_product_name);
        Price = findViewById(R.id.edit_price);
        Quantity = findViewById(R.id.edit_quantity);
        Phone = findViewById(R.id.edit_phone);
        SupplierSpinner = findViewById(R.id.supplier_spinner);

        setupSpinner();
    }

    private void setupSpinner() {

        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_suppliers, android.R.layout.simple_dropdown_item_1line);

        SupplierSpinner.setAdapter(supplierSpinnerAdapter);

        SupplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection))  {
                    if (selection.equals(getString(R.string.SupplierWalmart))) {
                        Supplier = InventoryEntry.SUPPLIER_WALMART;
                    } else if (selection.equals(getString(R.string.SupplierCostco))) {
                        Supplier = InventoryEntry.SUPPLIER_COSTCO;
                    } else {
                        Supplier = InventoryEntry.SUPPLIER_ULINE;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Supplier = InventoryEntry.SUPPLIER_WALMART;
            }
        });
    }

    public void minusQuantity(View view) {
        String quantityString = Quantity.getText().toString().trim();

        if (Integer.parseInt(quantityString) == 0) {
            Toast.makeText(this, getString(R.string.quantity_error), Toast.LENGTH_SHORT).show();
        } else {
            int quantityvalue = (Integer.parseInt(quantityString) - 1);
            quantityString = Integer.toString(quantityvalue);
            Quantity.setText(quantityString);
        }

    }

    public void plusQuantity(View view) {
        String quantityString = Quantity.getText().toString().trim();
        int quantityvalue = (Integer.parseInt(quantityString) + 1);
        quantityString = Integer.toString(quantityvalue);
        Quantity.setText(quantityString);
    }

    private void saveProduct() {
        String nameString = ProductName.getText().toString().trim();
        String priceString = Price.getText().toString().trim();
        String quantityString = Quantity.getText().toString().trim();
        String phoneString = Phone.getText().toString().trim();

        Double price = Double.parseDouble(priceString);
        int quantity = Integer.parseInt(quantityString);

        if (currentUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(phoneString) && Supplier == InventoryEntry.SUPPLIER_WALMART) {
            return;
        }


        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRICE, price);
        values.put(InventoryEntry.COLUMN_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_SUPPLIER, Supplier);
        values.put(InventoryEntry.COLUMN_PHONE, phoneString);

        if (currentUri == null) {

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {

                Toast.makeText(this, getString(R.string.save_error), Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.save_item), Toast.LENGTH_LONG).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this,getString(R.string.update_error), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_item), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (currentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                deleteItem();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
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

        return new CursorLoader(this, currentUri, projection, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PHONE);


            String productName = cursor.getString(nameColumnIndex);
            double productPrice = cursor.getDouble(priceColumnIndex);
            int supplierName = cursor.getInt(supplierColumnIndex);
            int quantityAmount = cursor.getInt(quantityColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);

            ProductName.setText(productName);
            Price.setText(Double.toString(productPrice));
            Quantity.setText(Integer.toString(quantityAmount));

            switch (supplierName) {
                case InventoryEntry.SUPPLIER_WALMART:
                    SupplierSpinner.setSelection(0);
                    break;
                case InventoryEntry.SUPPLIER_COSTCO:
                    SupplierSpinner.setSelection(1);
                    break;
                case InventoryEntry.SUPPLIER_ULINE:
                    SupplierSpinner.setSelection(2);
                    break;
            }

            Phone.setText(phone);



        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ProductName.setText("");
        Price.setText("");
        Quantity.setText("");
        SupplierSpinner.setSelection(0);
        Phone.setText("");

    }

    private void deleteItem() {
        if (currentUri != null) {
            int rowsDeleted = getContentResolver().delete(currentUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_error), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_item), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
