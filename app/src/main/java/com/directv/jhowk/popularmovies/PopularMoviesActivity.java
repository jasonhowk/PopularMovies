package com.directv.jhowk.popularmovies;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class PopularMoviesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = PopularMoviesActivity.class.getSimpleName();
    private TypedArray mSectionsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Supressing inspection as we're explicitly setting above.
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Section spinner.
        mSectionsArray = getResources().obtainTypedArray(R.array.sections_rid);
        Spinner spinner = (Spinner)findViewById(R.id.nav_spinner);
        assert spinner != null;
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.sections_rid,R.layout.nav_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSectionsArray.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Spinner Callbacks
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int resId = mSectionsArray.getResourceId(position,-1);
        Log.d(LOG_TAG, "onItemSelected: selected resid:" + resId);
        switch (resId) {
            case R.string.popular:
                Log.d(LOG_TAG, "onItemSelected: Popular selected.");
                break;
            case R.string.topRated:
                Log.d(LOG_TAG, "onItemSelected: Top Rated selected.");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(LOG_TAG, "onNothingSelected.");
    }
}
