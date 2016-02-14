package com.mikepenz.fastadapter.app;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.app.adapter.FastScrollIndicatorAdapter;
import com.mikepenz.fastadapter.app.items.SampleItem;
import com.mikepenz.fastadapter.app.swipe.SimpleSwipeCallback;
import com.mikepenz.fastadapter.app.swipe.SimpleSwipeDragCallback;
import com.mikepenz.fastadapter.drag.ItemTouchCallback;
import com.mikepenz.fastadapter.drag.SimpleDragCallback;
import com.mikepenz.fastadapter.helpers.UndoHelper;
import com.mikepenz.materialize.MaterializeBuilder;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SwipeListActivity extends AppCompatActivity implements ItemTouchCallback, SimpleSwipeCallback.ItemSwipeCallback {
    private static final String[] ALPHABET = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    //save our FastAdapter
    private FastItemAdapter<SampleItem> fastItemAdapter;

    //drag & drop
    private SimpleDragCallback touchCallback;
    private ItemTouchHelper touchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //create our FastAdapter which will manage everything
        fastItemAdapter = new FastItemAdapter<>();

        //configure our fastAdapter
        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener<SampleItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SampleItem> adapter, SampleItem item, int position) {
                Toast.makeText(v.getContext(), (item).name.getText(v.getContext()), Toast.LENGTH_LONG).show();
                return false;
            }
        });

        //configure the itemAdapter
        fastItemAdapter.withFilterPredicate(new IItemAdapter.Predicate<SampleItem>() {
            @Override
            public boolean filter(SampleItem item, CharSequence constraint) {
                //return true if we should filter it out
                //return false to keep it
                return !item.name.getText().toLowerCase().contains(constraint.toString().toLowerCase());
            }
        });

        //get our recyclerView and do basic setup
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastItemAdapter);

        //fill with some sample data
        int x = 0;
        List<SampleItem> items = new ArrayList<>();
        for (String s : ALPHABET) {
            int count = new Random().nextInt(20);
            for (int i = 1; i <= count; i++) {
                items.add(new SampleItem().withName(s + " Test " + x).withIdentifier(100 + x));
                x++;
            }
        }
        fastItemAdapter.add(items);


        //add drag and drop for item
        //and add swipe as well
        touchCallback = new SimpleSwipeDragCallback(this, this, ItemTouchHelper.LEFT, Color.GREEN);
        touchHelper = new ItemTouchHelper(touchCallback); // Create ItemTouchHelper and pass with parameter the SimpleDragCallback
        touchHelper.attachToRecyclerView(recyclerView); // Attach ItemTouchHelper to RecyclerView

        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = fastItemAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    touchCallback.setIsDragEnabled(false);
                    fastItemAdapter.filter(s);
                    return true;
                }


                @Override
                public boolean onQueryTextChange(String s) {
                    fastItemAdapter.filter(s);
                    touchCallback.setIsDragEnabled(TextUtils.isEmpty(s));
                    return true;
                }
            });
        } else {
            menu.findItem(R.id.search).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean itemTouchOnMove(int oldPosition, int newPosition) {
        Collections.swap(fastItemAdapter.getAdapterItems(), oldPosition, newPosition); // change position
        fastItemAdapter.notifyAdapterItemMoved(oldPosition, newPosition);
        return true;
    }

    @Override
    public void itemSwiped(final int position, int direction) {
        // -- Option 1: Direct action --
        //do something when swiped such as: remove, select, ...
        //fastItemAdapter.select(position);
        //fastItemAdapter.remove(position);

        // -- Option 2: Delayed action --
        //Currently just showing example of modifying current item, could swap it out, or
        //set part of the layout as GONE and another as VISIBLE
        //Possibly make a general case of this?
        fastItemAdapter.getItem(position).withDescription("").withName("Swiped, removing...");
        fastItemAdapter.notifyItemChanged(position);

        Runnable removeRunnable = new Runnable() {
            @Override
            public void run() {
                fastItemAdapter.remove(position);
            }
        };
        View rv = findViewById(R.id.rv);
        rv.postDelayed(removeRunnable, 3000);

        //for undo, add an onClickListener to something (button, image, text) which removes this
        //runnable from the message queue (reset the ui as well!)
        //rv.removeCallbacks(removeRunnable);
        //fastItemAdapter.notifyItemChanged(position);
    }

}
