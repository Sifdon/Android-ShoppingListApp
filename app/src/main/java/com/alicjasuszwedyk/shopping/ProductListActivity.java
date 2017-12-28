package com.alicjasuszwedyk.shopping;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.alicjasuszwedyk.shopping.data.ProductContract;

public class ProductListActivity extends BasicActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    ProductCursorAdapter productCursorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        setTitle("Shopping List");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(
                (view) -> {
                    Intent intent = new Intent(ProductListActivity.this, ProductEditorActivity.class);
                    startActivity(intent);
                }
        );

        ListView productsListView = (ListView) findViewById(R.id.product_list);
        productsListView.setEmptyView(
                findViewById(R.id.empty_view)
        );

        productCursorAdapter = new ProductCursorAdapter(this, null);
        productsListView.setAdapter(productCursorAdapter);

        productsListView.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent intent = new Intent(ProductListActivity.this, ProductEditorActivity.class);
            Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);
            intent.setData(currentProductUri);
            startActivity(intent);
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
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

        return new CursorLoader(this, ProductContract.ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        productCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_preferences:
                goToPreferences(item.getActionView());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToPreferences(View view){
        Intent intent1 = new Intent(this, PreferencesActivity.class);
        startActivity(intent1);
    }


}
