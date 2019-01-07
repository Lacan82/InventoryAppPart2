package com.example.dmorgan.inventoryapppart1;

import android.app.AlertDialog;
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
        Quantity.setText("0");
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

    public void OrderItem(View view) {
        String phone = Phone.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.order_phone), Toast.LENGTH_SHORT).show();
        }


    }

    private void saveProduct() {
        String nameString = ProductName.getText().toString().trim();
        String priceString = Price.getText().toString().trim();
        String quantityString = Quantity.getText().toString().trim();
        String phoneString = Phone.getText().toString().trim();



        if (currentUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(phoneString) && Supplier == InventoryEntry.SUPPLIER_WALMART) {
            return;
        }

        //set Price and Quantity to zero just in case user removes, or forgets values.

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.required_name),Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.required_price),Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.required_quantity),Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phoneString)) {
            Toast.makeText(this, getString(R.string.required_phone),Toast.LENGTH_SHORT).show();
            return;
        }

        //initalized the variables before parsing string so we can make sure they did enter an integer!

        double price = 0.00;
        int quantity = 0;

        try {
            price = Double.parseDouble(priceString);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.required_error), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            quantity = Integer.parseInt(quantityString);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.required_error), Toast.LENGTH_SHORT).show();
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
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                DeleteConfirmation();
                return true;
            case android.R.id.home:
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
        finish();
    }

    private void DeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirm);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
