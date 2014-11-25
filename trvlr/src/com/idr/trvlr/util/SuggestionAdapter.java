package com.idr.trvlr.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.idr.trvlr.R;
import com.idr.trvlr.sqlite.TrainDbOpenHelper;

/**
 * Created by hadoop on 6/23/14.
 */
public class SuggestionAdapter extends CursorAdapter
{
    private TrainDbOpenHelper mDb;

    public SuggestionAdapter(Context context, Cursor c,TrainDbOpenHelper db)
    {
        super(context, c);
        this.mDb = db;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        int columnIndex = cursor.getColumnIndexOrThrow("stationName");
        
        TextView textview = (TextView) view.findViewById(R.id.dropdown_station_name);
        textview.setText(cursor.getString(columnIndex));
      //  ((TextView) view).setText(cursor.getString(columnIndex));
    }

    @Override
    public String convertToString(Cursor cursor)
    {
        int columnIndex = cursor.getColumnIndexOrThrow("stationName");
        return cursor.getString(columnIndex);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = (View) inflater.inflate(R.layout.station_dropdown_line, parent, false);
        int columnIndex = cursor.getColumnIndexOrThrow("stationName");
        TextView textview = (TextView) view.findViewById(R.id.dropdown_station_name);
        textview.setText(cursor.getString(columnIndex));
        return view;
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint)
    {
        mDb.openDataBase();
        String pattern = "";
        if( constraint != null){
            pattern = constraint.toString();
        }
        Cursor stationCursor = mDb.getDb().rawQuery("SELECT _id,stationCode,stationName FROM station_table where stationName like ?",new String[]{"%"+pattern+"%"});
        //mDb.close();

        return stationCursor;
    }
}