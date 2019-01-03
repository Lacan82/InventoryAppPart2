package com.example.dmorgan.inventoryapppart1.data;

import android.provider.BaseColumns;

public class InventoryContract {

    private InventoryContract() {}

    public static final class InventoryEntry implements BaseColumns {

        public final static String TABLE_NAME = "inventory";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_NAME = "Product";

        public final static String COLUMN_PRICE = "Price";

        public final static String COLUMN_QUANTITY = "Quantity";

        public final static String COLUMN_SUPPLIER = "Supplier";

        public final static String COLUMN_PHONE = "Phone";

        public static final int SUPPLIER_WALMART = 0;
        public static final int SUPPLIER_COSTCO = 1;
        public static final int SUPPLIER_ULINE = 2;

    }
}
