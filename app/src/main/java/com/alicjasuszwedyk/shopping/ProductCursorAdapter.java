package com.alicjasuszwedyk.shopping;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.alicjasuszwedyk.shopping.data.ProductContract;

import static com.alicjasuszwedyk.shopping.BasicActivity.*;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextview = (TextView) view.findViewById(R.id.summary);

        String productName = cursor.getString(
                cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)
        );

        Float productPrice = cursor.getFloat(
            cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)
        );

        Integer productQuantity = cursor.getInt(
            cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)
        );

        Integer productIsBought = cursor.getInt(
                cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IS_BOUGHT)
        );

        if (productIsBought == 1) {
          productName =  "\u2713" + productName;
        }

        nameTextView.setText(productName);
        summaryTextview.setText("Price: " + productPrice + " PLN, " + " Quantity: " + productQuantity);

        //change color
        if(colorFromPreferences == firstColorId) {
            nameTextView.setTextColor(Color.BLACK);
        } else if (colorFromPreferences == secondColorId) {
            nameTextView.setTextColor(Color.rgb(255, 102, 153));
        } else if (colorFromPreferences == thirdColorId) {
            nameTextView.setTextColor(Color.rgb(95, 104, 206));
        }

        //change size
        if(sizeFromPreferences == 0) {
            nameTextView.setTextSize(14);
        } else if (sizeFromPreferences ==1){
            nameTextView.setTextSize(20);
        }
    }
}
