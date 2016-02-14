package com.projectzulu.randomize.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectzulu.randomize.R;

/**
 * Created by gianmarco on 14/02/16.
 */
public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ViewHolder> {

    private Context mContext;
    private boolean mEditing;
    private ListsAdapterCallbacks mCallbacks;
    private Cursor mCursor;

    private boolean mDataValid;
    private int mRowIDColumn;

    private static final String COLUMN_NAME = DbOpenHelper.ListsTable.COLUMN_NAME;

    public ListsAdapter(Context context, Cursor c, ListsAdapterCallbacks callbacks) {
        mContext = context;
        mCursor = c;
        mCallbacks = callbacks;

        boolean cursorPresent = c != null;
        mDataValid = cursorPresent;
        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cursor cursor = getItem(position);
        final long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        final String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

        // Item click listener
        holder.View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onItemClick(id, name);
            }
        });

        // List name
        holder.Text.setText(name);

        // Icon
        //holder.Image.setVisibility(mEditing ? View.VISIBLE : View.GONE);
        holder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onDeleteButtonClick(id);
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

    public String getListName(int position) {
        Cursor cursor = (Cursor) getItem(position);
        return cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

//    public void onEditing(boolean editing) {
//        mEditing = editing;
////        notifyDataSetChanged();
//    }

    public interface ListsAdapterCallbacks {
        void onDeleteButtonClick(long id);
        void onItemClick(long id, String name);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View View;
        public TextView Text;
        public ImageView Image;

        public ViewHolder(View itemView) {
            super(itemView);
            View = itemView;
            Text = (TextView) itemView.findViewById(R.id.list_item_text);
            Image = (ImageView) itemView.findViewById(R.id.list_item_delete);
        }
    }
}
