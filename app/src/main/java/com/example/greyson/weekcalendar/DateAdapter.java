package com.example.greyson.weekcalendar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 每一周（7天）数据的Adapter
 */
public class DateAdapter extends BaseAdapter {
    private Context mContext;
    private List<DateBean> mDateList;
    private int mSelectedPosition = -1;

    private Date mMinDate;
    private Date mMaxDate;


    public DateAdapter(Context context, ArrayList<DateBean> dateList) {
        mContext = context;
        mDateList = dateList;
    }

    public void setDateList(List<DateBean> dateList) {
        mDateList = dateList;
    }

    public void setSelection(int position) {
        mSelectedPosition = position;
    }

    public void setMaxMonthAndDay(Date date) {
        mMaxDate = date;
    }

    public void setMinMonthAndDay(Date date) {
        mMinDate = date;
    }

    public DateBean getSelectedDateBean() {
        if (mSelectedPosition < 0 || mSelectedPosition > getCount() - 1) {
            return null;
        }
        return mDateList.get(mSelectedPosition);
    }

    @Override
    public int getCount() {
        if (mDateList == null) {
            return 0;
        }
        return mDateList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mDateList != null && mDateList.size() != 0) {
            return mDateList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.date_list_item_calendar, null);

            holder.containerLayout = convertView.findViewById(R.id.containerLayout);
            holder.bgLayout = convertView.findViewById(R.id.bgLayout);
            holder.dateTV = (TextView) convertView.findViewById(R.id.dateTV);
            //holder.monthTV = (TextView) convertView.findViewById(R.id.monthTV);
            holder.weekTV = (TextView) convertView.findViewById(R.id.weekTV);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        DateBean dateBean = mDateList.get(position);
        holder.dateTV.setText(String.valueOf(dateBean.getDay()));
        holder.weekTV.setText(getWeekStrForPosition(position));
        //holder.monthTV.setText(getCurrentMonth(position) + ".");


        holder.bgLayout.setBackgroundColor(Color.TRANSPARENT);
        holder.dateTV.setTextColor(Color.parseColor("#201D23"));
        holder.weekTV.setTextColor(Color.parseColor("#201D23"));


        if (dateBean.isSelectable()) {
            //可以被选的日期样式
            holder.dateTV.setAlpha(1f);
            holder.weekTV.setAlpha(0.3f);
            holder.containerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelection(position);
                    notifyDataSetChanged();
                }
            });
            if (mSelectedPosition == position) {
                holder.bgLayout.setBackgroundResource(R.drawable.bg_item_selected);
                holder.dateTV.setTextColor(Color.WHITE);
                holder.weekTV.setTextColor(Color.WHITE);
            }
        } else {
            holder.dateTV.setAlpha(0.1f);
            holder.weekTV.setAlpha(0.1f);
            holder.containerLayout.setOnClickListener(null);
        }


        return convertView;
    }


    private String getWeekStrForPosition(int position) {
        switch (position) {
            case 0:
                return "SUN";

            case 1:
                return "MON";

            case 2:
                return "TUE";

            case 3:
                return "WED";

            case 4:
                return "THU";

            case 5:
                return "FRI";

            case 6:
                return "SAT";

            default:
                return "";
        }
    }

    private class Holder {
        View containerLayout;
        View bgLayout;
        TextView weekTV;
        TextView dateTV;
        //TextView monthTV;
    }
}
