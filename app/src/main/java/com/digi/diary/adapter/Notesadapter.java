package com.digi.diary.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digi.diary.R;
import com.digi.diary.WriteNoteActivity;
import com.digi.diary.model.NotesModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by anupama.sinha on 25-10-2016.
 */
public class Notesadapter extends RecyclerView.Adapter<Notesadapter.MyViewHolder> implements Filterable {
    public ArrayList<NotesModel> mNotesModels;
    private Context mContext;
    ArrayList<NotesModel> mUnfilteredData;
    private SimpleFilter mFilter;
    public ArrayList<NotesModel> selected_usersList=new ArrayList<>();
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.otes_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public Notesadapter( Context context) {

        this.mContext = context;
    }
public void setList(ArrayList<NotesModel> list,ArrayList<NotesModel> selectedlist)
{
    this.mNotesModels = list;
    this.selected_usersList=selectedlist;
}
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotesModel notesModel = mNotesModels.get(position);
//        holder.title.setText(notesModel.getmTitle());
        holder.bind(notesModel);
    }

    @Override
    public int getItemCount() {
        return mNotesModels.size();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new SimpleFilter();
        }
        return mFilter;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, tvDate, content, tvMonth, year;
        public LinearLayout linearLayout;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            tvDate = (TextView) view.findViewById(R.id.date);
            year = (TextView) view.findViewById(R.id.year);
            tvMonth = (TextView) view.findViewById(R.id.month);
            content = (TextView) view.findViewById(R.id.content);
            linearLayout=(LinearLayout)view.findViewById(R.id.ll1);
        }

        private Date getDate(long time) {

            Calendar calendar = Calendar.getInstance();
//            TimeZone tz1 = TimeZone.getDefault();
//            calendar.add(Calendar.MILLISECOND, tz1.getOffset(calendar.getTimeInMillis()));
//            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            java.util.Date currenTimeZone=new java.util.Date((long)
//                    System.currentTimeMillis()*1000);
//            Toast.makeText(mContext, sdf1.format(currenTimeZone), Toast.LENGTH_SHORT).show();


            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();//get your local time zone.
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            sdf.setTimeZone(tz);//set time zone.
            String localTime = sdf.format(new Date(time * 1000));
            Date date = new Date();
            try {
                date = sdf.parse(localTime);//get local date
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date;
        }

        public void bind(final NotesModel item) {
            if(selected_usersList.contains(item))
                linearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_selected_state));
            else
                linearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_normal_state));


            title.setText(item.getmTitle());

            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(mContext);
            String themeName = pref.getString("theme", "Theme1");
            if (themeName.equals("Theme2")) {
//                mContext.setTheme(R.style.AppTheme);
                tvMonth.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark_Pink));
            }
            else if (themeName.equals("Theme1")) {
//                Toast.makeText(mContext, "set theme", Toast.LENGTH_SHORT).show();
//                mContext.setTheme(R.style.AppTheme1);
                tvMonth.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            }

            Log.d("dateval", " with db notesModel.getmDate() " + item.getmDate());
            Date date = new Date(item.getmDate() * 1000);
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(date);
            Log.d("dateval", "miliisec val " + item.getmDate() + " date " + date + " year " + date.getYear());
            tvMonth.setText(android.text.format.DateFormat.format("MMM", date));
            year.setText(android.text.format.DateFormat.format("yyyy", date));
            tvDate.setText(android.text.format.DateFormat.format("dd", date));
            String str = item.getmContents();
            int newLineIndex = str.indexOf("\n");
            if (newLineIndex>0) {
                String firstline = str.substring(0, newLineIndex);
                content.setText(firstline + "...");
            } else
                content.setText(item.getmContents());
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WriteNoteActivity.class);
                    intent.putExtra("val", item);
                    mContext.startActivity(intent);
//                    listener.onItemClick(item);

                }

            });

        }

    }

    private void timecheck(long currentDateTime) {
        //Converting milliseconds to Date using java.util.Date
        //current time in milliseconds
//        long currentDateTime = System.currentTimeMillis();

        //creating Date from millisecond
        Date currentDate = new Date(currentDateTime);

        //printing value of Date
        System.out.println("current Date: " + currentDate);

        DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");

        //formatted value of current Date
        System.out.println("Milliseconds to Date: " + df.format(currentDate));

        //Converting milliseconds to Date using Calendar
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentDateTime);
        System.out.println("Milliseconds to Date using Calendar:"
                + df.format(cal.getTime()));

        //copying one Date's value into another Date in Java
        Date now = new Date();
        Date copiedDate = new Date(now.getTime());

        System.out.println("original Date: " + df.format(now));
        System.out.println("copied Date: " + df.format(copiedDate));


    }

    private class SimpleFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<NotesModel>(mNotesModels);
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<NotesModel> list = (ArrayList<NotesModel>) mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<NotesModel> unfilteredValues = (ArrayList<NotesModel>) mUnfilteredData;
                int count = unfilteredValues.size();
                ArrayList<NotesModel> newValues = new ArrayList<NotesModel>(count);

                for (int i = 0; i < count; i++) {
                    NotesModel h = unfilteredValues.get(i);
                    if (h != null) {
                        String str = (String) h.getmTitle();
                        if (str.toLowerCase().contains(prefixString)) {
                            newValues.add(h);
                        } else {
                            String[] words = str.split(" ");
                            int wordCount = words.length;
                            for (int k = 0; k < wordCount; k++) {
                                String word = words[k];
                                if (word.toLowerCase().startsWith(prefixString)) {
                                    newValues.add(h);
                                    break;
                                }
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mNotesModels = (ArrayList<NotesModel>) results.values;
//            savedlist.clear();
//            savedlist = contactdb.getAllRecordings();
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
//                notifyDataSetInvalidated();
            }

        }
    }
}
