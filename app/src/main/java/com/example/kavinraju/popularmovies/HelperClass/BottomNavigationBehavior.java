package com.example.kavinraju.popularmovies.HelperClass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BottomNavigationBehavior extends CoordinatorLayout.Behavior<BottomNavigationView> {

    private int height;
    private String className = BottomNavigationBehavior.class.getSimpleName();

    public BottomNavigationBehavior(){
        super();
    }
    public BottomNavigationBehavior(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, BottomNavigationView child, View dependency) {
        height = child.getHeight();
        return dependency instanceof CoordinatorLayout;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        Log.d(className , "onStartNestedScroll");
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }


    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        if ( dyConsumed > 0 ){
            child.clearAnimation();
            child.animate().translationY(height).setDuration(200);
            Log.d(className , "onNestedPreScroll: dy < 0" + " Type" + String.valueOf(type));
        }else if ( dyConsumed < 0 ){
            child.clearAnimation();
            child.animate().translationY(0).setDuration(200);
            Log.d(className , "onNestedPreScroll: dy > 0" + " Type" + String.valueOf(type));
        }
    }
}

