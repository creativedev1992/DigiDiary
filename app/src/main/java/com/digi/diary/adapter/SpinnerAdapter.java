package com.digi.diary.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.digi.diary.R;

import java.util.List;

/**
 * Created by anupama.sinha on 08-11-2016.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    private Activity context;
    private List<String> fontList;
    private int ilayout;

    public SpinnerAdapter(Activity context, int layout, List<String> list) {
        super(context, layout, list);
        this.context = context;
        this.fontList = list;
        this.ilayout = layout;
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        public TextView tvName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View rowView = convertView;
        if (rowView == null) {

            LayoutInflater layoutInflater = context.getLayoutInflater();
            rowView = layoutInflater.inflate(ilayout, null, true);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) rowView.findViewById(R.id.tv);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        if(position==0)//if 1 is for sunday
        {
            Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/GoodDog.otf");
            viewHolder.tvName.setTypeface(face);
        }
        else if(position==1)//if 1 is for monday
        {
            Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/KaushanScript-Regular.otf");
            viewHolder.tvName.setTypeface(face);

        }
        else if(position==2)//if 1 is for monday
        {
            Typeface face=Typeface.createFromAsset(context.getAssets(), "fonts/Pacifico.ttf");
            viewHolder.tvName.setTypeface(face);

        }
        viewHolder.tvName.setText(fontList.get(position));
        return rowView;
    }
}
