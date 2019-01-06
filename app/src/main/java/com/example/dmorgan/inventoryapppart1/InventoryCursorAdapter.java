package com.example.dmorgan.inventoryapppart1;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.dmorgan.inventoryapppart1.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameText = view.findViewById(R.id.product);
        TextView supplierText = view.findViewById(R.id.list_price);
        TextView quantityText = view.findViewById(R.id.quantity_number);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        String quantityAmount = cursor.getString(quantityColumnIndex);



        nameText.setText(productName);
        supplierText.setText("$" + Double.toString(price));
        quantityText.setText(quantityAmount);

    }
}
