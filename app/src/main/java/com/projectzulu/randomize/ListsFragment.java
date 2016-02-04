package com.projectzulu.randomize;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.projectzulu.randomize.data.DbOpenHelper;
import com.projectzulu.randomize.data.ListsCursorAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        NewListDialog.NewListDialogListener, ListsCursorAdapter.ListsAdapterCallbacks {

    private static final String LOG_TAG = ListsFragment.class.getSimpleName();

    private ListView mListView;
    private ListsCursorAdapter mAdapter;

    private FloatingActionButton mFab;

    public interface ListsFragmentCallbacks {
        void openList(long id, String name);
    }
    private ListsFragmentCallbacks mCallbacks;

    public ListsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mAdapter = new ListsCursorAdapter(getActivity(), null, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mAdapter.getCursor();
                cursor.moveToPosition(position);
                String listName = cursor.getString(cursor.getColumnIndex(DbOpenHelper.ListsTable.COLUMN_NAME));
                mCallbacks.openList(id, listName);
            }
        });

        mFab = ((MainActivity) getActivity()).getFab();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabClicked();
            }
        });

        getLoaderManager().initLoader(0, null, this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), null, null, null, null, null) {
            @Override
            public Cursor loadInBackground() {
                DbOpenHelper helper = new DbOpenHelper(getContext());
                return helper.getReadableDatabase().query(DbOpenHelper.ListsTable.TABLE_NAME,
                        getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder());
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, data.toString());
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void onFabClicked() {
        NewListDialog dialog = new NewListDialog();
        dialog.setListener(this);
        dialog.show(getFragmentManager(), null);
    }

    private void createNewList(String name) {
        ContentValues values = new ContentValues();
        values.put(DbOpenHelper.ListsTable.COLUMN_NAME, name);
        DbOpenHelper helper = new DbOpenHelper(getActivity());
        helper.getWritableDatabase().insert(DbOpenHelper.ListsTable.TABLE_NAME, null, values);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onDialogPositiveClick(String name) {
        createNewList(name);
    }

    private void deleteList(long id) {
        DbOpenHelper helper = new DbOpenHelper(getActivity());
        helper.getWritableDatabase().delete(DbOpenHelper.ListsTable.TABLE_NAME,
                DbOpenHelper.ListsTable._ID + "=?", new String[]{Long.toString(id)});
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onDeleteButtonClick(final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_erase_list)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteList(id);
                    }
                });
        builder.create().show();
    }

    public void setActivityCallbacks(ListsFragmentCallbacks callbacks) {
        mCallbacks = callbacks;
    }
}
