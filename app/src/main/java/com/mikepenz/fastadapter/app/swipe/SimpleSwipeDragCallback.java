package com.mikepenz.fastadapter.app.swipe;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.mikepenz.fastadapter.drag.ItemTouchCallback;
import com.mikepenz.fastadapter.drag.SimpleDragCallback;

/**
 * Created by Mattias on 2016-02-13.
 */
public class SimpleSwipeDragCallback extends SimpleDragCallback {

    private final SimpleSwipeCallback simpleSwipeCallback;

    public SimpleSwipeDragCallback(ItemTouchCallback itemTouchCallback, SimpleSwipeCallback.ItemSwipeCallback itemSwipeCallback) {
        this(itemTouchCallback, itemSwipeCallback, ItemTouchHelper.LEFT);
    }

    public SimpleSwipeDragCallback(ItemTouchCallback itemTouchCallback, SimpleSwipeCallback.ItemSwipeCallback itemSwipeCallback, int swipeDirs) {
        this(itemTouchCallback, itemSwipeCallback, swipeDirs, Color.RED);
    }

    public SimpleSwipeDragCallback(ItemTouchCallback itemTouchCallback, SimpleSwipeCallback.ItemSwipeCallback itemSwipeCallback, int swipeDirs, @ColorInt int bgColor) {
        super(itemTouchCallback);
        setDefaultSwipeDirs(swipeDirs);
        simpleSwipeCallback = new SimpleSwipeCallback(itemSwipeCallback, swipeDirs, bgColor);
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        simpleSwipeCallback.onSwiped(viewHolder, direction);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        simpleSwipeCallback.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        //Happen to know that our direct parent class doesn't (currently) draw anything...
        //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}