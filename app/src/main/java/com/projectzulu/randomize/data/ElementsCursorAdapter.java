package com.projectzulu.randomize.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectzulu.randomize.R;


/**
 * Created by gianmarco on 27/01/16.
 */
public class ElementsCursorAdapter extends CursorAdapter {

    private boolean mEditing;
    private ElementsAdapterCallbacks mCallbacks;

    private static final String COLUMN_NAME = DbOpenHelper.ElementsTable.COLUMN_NAME;
    private static final String COLUMN_ENABLED = DbOpenHelper.ElementsTable.COLUMN_ENABLED;

    public ElementsCursorAdapter(Context context, Cursor c, ElementsAdapterCallbacks callbacks) {
        super(context, c, 0);
        mCallbacks = callbacks;
        mEditing = false;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.element_item, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.Text = (TextView) view.findViewById(R.id.element_item_text);
        viewHolder.Image = (ImageView) view.findViewById(R.id.element_item_delete);
        viewHolder.CheckBox = (CheckBox) view.findViewById(R.id.element_item_checkbox);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Element name
        viewHolder.Text.setText(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));

        // Icon is black, change the color
        viewHolder.Image.setVisibility(mEditing ? View.VISIBLE : View.GONE);
        viewHolder.Image.getDrawable().setColorFilter(context.getResources()
                .getColor(R.color.color_delete_button), PorterDuff.Mode.SRC_ATOP);
        viewHolder.Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onDeleteButtonClick(id);
            }
        });

        // Checkbox state
        viewHolder.CheckBox.setChecked(cursor.getInt(cursor.getColumnIndex(COLUMN_ENABLED)) == 1);
        viewHolder.CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onCheckBoxClick(id, ((CheckBox) v).isChecked());
            }
        });
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

    public void onEditing(boolean editing) {
        mEditing = editing;
    }

    public interface ElementsAdapterCallbacks {
        public void onDeleteButtonClick(long id);
        public void onCheckBoxClick(long id, boolean checked);
    }

    public class ViewHolder {
        public TextView Text;
        public ImageView Image;
        public CheckBox CheckBox;
    }
}
