package com.example.dmorgan.inventoryapppart1;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.dmorgan.inventoryapppart1.data.InventoryDbHelper;

public class EditorActivity extends AppCompatActivity {

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

    private void insertProduct() {
        String nameString = ProductName.getText().toString().trim();
        String priceString = Price.getText().toString().trim();
        String quantityString = Quantity.getText().toString().trim();
        String phoneString = Phone.getText().toString().trim();

        Double price = Double.parseDouble(priceString);
        int quantity = Integer.parseInt(quantityString);

        InventoryDbHelper DbHelper = new InventoryDbHelper(this);

        SQLiteDatabase db = DbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRICE, price);
        values.put(InventoryEntry.COLUMN_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_SUPPLIER, Supplier);
        values.put(InventoryEntry.COLUMN_PHONE, phoneString);

        long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error with saving", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                insertProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                Toast.makeText(this, "Function not implemented yet!", Toast.LENGTH_SHORT).show();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
