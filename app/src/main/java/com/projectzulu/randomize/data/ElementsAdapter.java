package com.projectzulu.randomize.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectzulu.randomize.R;

/**
 * Created by gianmarco on 02/02/16.
 */
public class ElementsAdapter extends RecyclerView.Adapter<ElementsAdapter.ViewHolder> {

    private Context mContext;
    private boolean mEditing;
    private ElementsAdapterCallbacks mCallbacks;
    private Cursor mCursor;

    private boolean mDataValid;
    private int mRowIDColumn;
//    private DataSetObserver mDataSetObserver;

    private static final String COLUMN_NAME = DbOpenHelper.ElementsTable.COLUMN_NAME;
    private static final String COLUMN_ENABLED = DbOpenHelper.ElementsTable.COLUMN_ENABLED;

    public ElementsAdapter(Context context, Cursor c, ElementsAdapterCallbacks callbacks) {
        mContext = context;
        mCursor = c;
        mCallbacks = callbacks;

        boolean cursorPresent = c != null;
        mDataValid = cursorPresent;
        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
//        mDataSetObserver = new MyDataSetObserver();
//
//        if (cursorPresent) {
//            c.registerDataSetObserver(mDataSetObserver);
//        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(mContext).inflate(R.layout.element_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cursor cursor = getItem(position);
        final long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));

        // Element name
        holder.Text.setText(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));

        // Icon
        holder.Image.setVisibility(mEditing ? View.VISIBLE : View.GONE);
        holder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onDeleteButtonClick(id);
            }
        });

        // Checkbox state
        holder.CheckBox.setChecked(cursor.getInt(cursor.getColumnIndex(COLUMN_ENABLED)) == 1);
        holder.CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onCheckBoxClick(id, ((CheckBox) v).isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public int getCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    public Cursor getItem(int position) {
        if (mDataValid && mCursor != null) {
            mCursor.moveToPosition(position);
            return mCursor;
        } else {
            return null;
        }
    }

    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
//            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
//            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    public boolean isElementEnabled(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return cursor.getInt(cursor.getColumnIndex(COLUMN_ENABLED)) == 1;
    }

    public String getElementName(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
    }

    public int getEnabledCount() {
        int count = 0;
        for (int i = 0; i < getCount(); i++) {
            if (isElementEnabled(i)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public void onEditing(boolean editing) {
        mEditing = editing;
//        notifyDataSetChanged();
    }

    public interface ElementsAdapterCallbacks {
        void onDeleteButtonClick(long id);
        void onCheckBoxClick(long id, boolean checked);
    }

//    private class MyDataSetObserver extends DataSetObserver {
//        @Override
//        public void onChanged() {
//            super.onChanged();
//            mDataValid = true;
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public void onInvalidated() {
//            super.onInvalidated();
//            mDataValid = false;
//            notifyDataSetChanged();
//        }
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView Text;
        public ImageView Image;
        public CheckBox CheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            Text = (TextView) itemView.findViewById(R.id.element_item_text);
            Image = (ImageView) itemView.findViewById(R.id.element_item_delete);
            CheckBox = (CheckBox) itemView.findViewById(R.id.element_item_checkbox);
        }
    }
}
