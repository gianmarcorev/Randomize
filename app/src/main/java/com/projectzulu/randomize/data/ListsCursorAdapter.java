package com.projectzulu.randomize.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectzulu.randomize.R;


/**
 * Created by gianmarco on 27/01/16.
 */
public class ListsCursorAdapter extends CursorAdapter {

    private static final String COLUMN_NAME = DbOpenHelper.ListsTable.COLUMN_NAME;

    private ListsAdapterCallbacks mCallbacks;

    public ListsCursorAdapter(Context context, Cursor c, ListsAdapterCallbacks callbacks) {
        super(context, c, 0);
        mCallbacks = callbacks;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.Text = (TextView) view.findViewById(R.id.list_item_text);
        viewHolder.Image = (ImageView) view.findViewById(R.id.list_item_delete);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // List name
        viewHolder.Text.setText(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));

        // Icon is black, change the color
        viewHolder.Image.getDrawable().setColorFilter(context.getResources()
                .getColor(R.color.color_delete_button), PorterDuff.Mode.SRC_ATOP);
        viewHolder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onDeleteButtonClick(id);
            }
        });
    }

    public interface ListsAdapterCallbacks {
        public void onDeleteButtonClick(long id);
    }

    public class ViewHolder {
        public TextView Text;
        public ImageView Image;
    }
}
