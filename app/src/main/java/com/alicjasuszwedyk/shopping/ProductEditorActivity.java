package com.alicjasuszwedyk.shopping;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.alicjasuszwedyk.shopping.data.ProductContract;

public class ProductEditorActivity extends BasicActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri currentProductUri;
    private EditText nameEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private CheckBox isBoughtCheckBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_editor);

        nameEditText = (EditText) findViewById(R.id.edit_product_name);
        priceEditText = (EditText) findViewById(R.id.edit_product_price);
        quantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        isBoughtCheckBox = (CheckBox) findViewById(R.id.edit_checkbox_is_bought);

        Intent intent = getIntent();
        currentProductUri = intent.getData();

        if (currentProductUri == null) {
            setTitle("Add new product");
        } else {
            setTitle("Edit product");
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        //changing color
        changeFont();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_IS_BOUGHT
        };

        return new CursorLoader(this, currentProductUri, projection, null, null,  null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            nameEditText.setText(
                    cursor.getString(
                            cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)
                    )
            );
            priceEditText.setText(Float.toString(
                    cursor.getFloat(
                            cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)
                    )
                    )
            );
            quantityEditText.setText(Integer.toString(
                    cursor.getInt(
                            cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)
                    )
                )
            );

            Integer isBought = cursor.getInt(
                    cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IS_BOUGHT)
            );
            if (isBought == 1) {
                isBoughtCheckBox.setChecked(true);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        isBoughtCheckBox.setChecked(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentProductUri == null) {
            menu.findItem(R.id.action_delete).setVisible(false);
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
                deleteProduct();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {
        String productName = nameEditText.getText().toString().trim();
        String productPrice = priceEditText.getText().toString().trim();
        String productQuantity = quantityEditText.getText().toString().trim();

        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPrice) || TextUtils.isEmpty(productQuantity)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IS_BOUGHT, isBoughtCheckBox.isChecked() ? 1 : 0);

        if (currentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteProduct() {
        if (currentProductUri == null) {
            return;
        }

        int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
