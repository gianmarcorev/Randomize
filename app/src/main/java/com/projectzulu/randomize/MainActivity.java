package com.projectzulu.randomize;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ListsFragment.ListsFragmentCallbacks {

    private static final String FRAGMENT_LISTS_TAG = "ListsFragment";
    private static final String FRAGMENT_ELEMENTS_TAG = "ElementsFragment";
    public static final int FAB_ICON_TRANSITION_DURATION = 200;

    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListsFragment lf = new ListsFragment();
        lf.setActivityCallbacks(this);
        getFragmentManager().beginTransaction().replace(R.id.main_container, lf, FRAGMENT_LISTS_TAG)
                .commit();

        mFab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_about:
                showAboutDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            // Restore the add icon
            // Until we have only two fragments this will be always correct
            TransitionDrawable td = (TransitionDrawable) mFab.getDrawable();
            td.setCrossFadeEnabled(true);
            td.reverseTransition(FAB_ICON_TRANSITION_DURATION);
        } else {
            super.onBackPressed();
        }
    }

    public FloatingActionButton getFab() {
        return mFab;
    }

    @Override
    public void openList(long id, String name) {
        ElementsFragment ef = ElementsFragment.newInstance(id, name);

        TransitionDrawable td = (TransitionDrawable) mFab.getDrawable();
        td.setCrossFadeEnabled(true);
        td.startTransition(FAB_ICON_TRANSITION_DURATION);

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.elements_fragment_slide_in,
                        R.animator.lists_fragment_slide_out,
                        R.animator.lists_fragment_slide_in,
                        R.animator.elements_fragment_slide_out)
                .replace(R.id.main_container, ef, FRAGMENT_ELEMENTS_TAG)
                .addToBackStack(null).commit();
    }

    private void showAboutDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_about, null, false))
                .create();
        dialog.show();
    }
}
