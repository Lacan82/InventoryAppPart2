package com.example.dmorgan.inventoryapppart1;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmorgan.inventoryapppart1.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    private Uri currentUri;

    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameText = view.findViewById(R.id.product);
        TextView supplierText = view.findViewById(R.id.list_price);
        TextView quantityText = view.findViewById(R.id.quantity_number);
        Button sale = view.findViewById(R.id.sale);

        final int id = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);


        final String productName = cursor.getString(nameColumnIndex);
        final double price = cursor.getDouble(priceColumnIndex);
        final int quantityAsInt = cursor.getInt(quantityColumnIndex);
        final String quantityAmount = cursor.getString(quantityColumnIndex);


        nameText.setText(productName);
        supplierText.setText("$" + Double.toString(price));
        quantityText.setText(quantityAmount);

        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) context;
                activity.sale(id, quantityAsInt);



            }
        });

    }


}
