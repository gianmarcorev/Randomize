package com.projectzulu.randomize;


import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.projectzulu.randomize.data.DbOpenHelper;
import com.projectzulu.randomize.data.ElementsAdapter;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ElementsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ElementsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ElementsAdapter.ElementsAdapterCallbacks, NewElementDialog.NewElementDialogListener {

    //TODO: create and edit; when editing, the fab becomes the add button and the bins appear

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PARAM_LIST_ID = "list_id";
    private static final String PARAM_LIST_NAME = "list_name";

    private static final String ELEMENTS_SELECTION = DbOpenHelper.ElementsTable.COLUMN_LIST_ID + "=?";

    private Context mContext;
    private long mListId;
    private String mListName;
    private ElementsAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private FloatingActionButton mFab;
    private float mFabDefaultY;

    private boolean mEditing;
    private Snackbar mSnackBar;

    public ElementsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param listId The list id used to retrieve the elements.
     * @param listName The list name.
     * @return A new instance of fragment ElementsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ElementsFragment newInstance(long listId, String listName) {
        ElementsFragment fragment = new ElementsFragment();
        Bundle args = new Bundle();
        args.putLong(PARAM_LIST_ID, listId);
        args.putString(PARAM_LIST_NAME, listName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mEditing = false;
        if (getArguments() != null) {
            mListId = getArguments().getLong(PARAM_LIST_ID);
            mListName = getArguments().getString(PARAM_LIST_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_elements, container, false);

        // Main list
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view);
        //mRecyclerView.setHasFixedSize(true);
        mAdapter = new ElementsAdapter(mContext, null, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mEditing) {
                    if (dy > 0 && mFab.getTranslationY() == 0) {
                        mFab.animate().translationY(mFab.getHeight() + Utility.dpToPx(16))
                                .setDuration(200).start();
                    } else if (dy < 0 && mFab.getTranslationY() > 0) {
                        mFab.animate().translationY(0).setDuration(200).start();
                    }
                }
            }
        });

        // Change appbar title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mListName);

        // FAB related things
        mFab = ((MainActivity) getActivity()).getFab();
        mFabDefaultY = mFab.getY();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabClicked();
            }
        });

        // Start the loader manager
        getLoaderManager().initLoader(0, null, this);

        // This fragment has options
        setHasOptionsMenu(true);

        return rootView;
    }

    private String[] getSelectionArgsForQuery() {
        return new String[]{ Long.toString(mListId) };
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), null, null, ELEMENTS_SELECTION,
                getSelectionArgsForQuery(), null) {
            @Override
            public Cursor loadInBackground() {
                DbOpenHelper helper = new DbOpenHelper(getContext());
                return helper.getReadableDatabase().query(DbOpenHelper.ElementsTable.TABLE_NAME,
                        getProjection(), getSelection(), getSelectionArgs(), null, null, null);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_elements, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                onEditing(!mEditing);
                return true;
            case R.id.action_select_all:
                selectAll(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onEditing(boolean editing) {
        mEditing = editing;

        // Transition to add icon and snack bar
        TransitionDrawable td = (TransitionDrawable) mFab.getDrawable();
        td.setCrossFadeEnabled(true);

        // Notify data adapter
        mAdapter.onEditing(editing);

        // Show or hide delete buttons
        showDeleteButtons(editing);

        // TODO: the snack bar overlaps the last item of the list! :(
        if (editing) {
//            mSnackBar = Snackbar.make(getView(), R.string.snackbar_edit_mode, Snackbar.LENGTH_INDEFINITE);
//            mSnackBar.show();
            td.reverseTransition(MainActivity.FAB_ICON_TRANSITION_DURATION);
        } else {
//            if (mSnackBar != null && mSnackBar.isShown()) {
//                mSnackBar.dismiss();
//            }
            td.startTransition(MainActivity.FAB_ICON_TRANSITION_DURATION);
        }

        // Show FAB whenever the user toggles edit mode
        if (!mFab.isShown()) {
            mFab.show();
        }
    }

    private void showDeleteButtons(boolean show) {
        // Here we handle only the list's children that are visible, not visible are updated by
        // the adapter when needed
        int childCount = mRecyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            // Get the child view
            View child = mRecyclerView.getChildAt(i);
            ElementsAdapter.ViewHolder vh = (ElementsAdapter.ViewHolder) mRecyclerView.getChildViewHolder(child);

            // Little hack: when the view is created its visibility is GONE, the first animation
            // makes it visible and then translate it out of the screen when needed
            vh.Image.setVisibility(View.VISIBLE);

            // Set up the animation
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            float pixels = (16+24) * metrics.density;
            ObjectAnimator anim;
            if (show) {
                anim = ObjectAnimator.ofFloat(vh.Image, "x", metrics.widthPixels, metrics.widthPixels - pixels);
            } else {
                anim = ObjectAnimator.ofFloat(vh.Image, "x", metrics.widthPixels - pixels, metrics.widthPixels);
            }
            anim.setDuration(300);
            anim.start();
        }

        // Wait for the animation to finish and then adjust the list view
        Timer timer = new Timer("timer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.getRecycledViewPool().clear();
                        mAdapter.notifyDataSetChanged();
                        //Log.d("TAG", "Timer gone");
                    }
                });
            }
        }, 300);

    }

    private void onFabClicked() {
        if (mEditing) {
            NewElementDialog dialog = new NewElementDialog();
            dialog.setListener(this);
            dialog.show(getFragmentManager(), null);
        } else {
            if (mAdapter.getEnabledCount() < 2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.dialog_more_elements)
                        .setPositiveButton(R.string.dialog_close, null)
                        .create().show();
                return;
            }

            // Randomize
            Random random = new Random();
            int number;
            do {
                number = random.nextInt(mAdapter.getCount());
            } while (!mAdapter.isElementEnabled(number));

            String element = mAdapter.getElementName(number);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(element)
                    .setPositiveButton(R.string.dialog_close, null)
                    .create().show();
        }
    }

    @Override
    public void onDeleteButtonClick(final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_erase_element)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteElement(id);
                    }
                });
        builder.create().show();
    }

    @Override
    public void onDialogPositiveClick(String name) {
        createNewElement(name);
    }

    private void createNewElement(String name) {
        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.ElementsTable.COLUMN_NAME, name);
        values.put(DbOpenHelper.ElementsTable.COLUMN_LIST_ID, mListId);
        values.put(DbOpenHelper.ElementsTable.COLUMN_ENABLED, 1);
        DbOpenHelper helper = new DbOpenHelper(getActivity());
        helper.getWritableDatabase().insert(DbOpenHelper.ElementsTable.TABLE_NAME, null, values);
        getLoaderManager().restartLoader(0, null, this);
    }

    private void deleteElement(long id) {
        DbOpenHelper helper = new DbOpenHelper(getActivity());
        helper.getWritableDatabase().delete(DbOpenHelper.ElementsTable.TABLE_NAME,
                DbOpenHelper.ElementsTable._ID + "=?", new String[]{Long.toString(id)});
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onCheckBoxClick(long id, boolean checked) {
        DbOpenHelper helper = new DbOpenHelper(getActivity());

        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.ElementsTable.COLUMN_ENABLED, checked ? 1 : 0);

        helper.getWritableDatabase().update(DbOpenHelper.ElementsTable.TABLE_NAME,
                values, DbOpenHelper.ElementsTable._ID + "=?", new String[]{Long.toString(id)});
        getLoaderManager().restartLoader(0, null, this);
    }

    private void selectAll(boolean select) {
        for (int i = 0; i <mAdapter.getCount(); i++) {
            onCheckBoxClick(mAdapter.getItemId(i), select);
        }
    }
}
