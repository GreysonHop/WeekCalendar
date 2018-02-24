package com.example.greyson.weekcalendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Greyson on 2018/2/19.
 */

public class WeekCalendarView extends ViewFlipper {
    private final String TAG = "grey_weekCalendarView";
    /**
     * 代表当前显示的周
     */
    private Calendar mCalendar;
    private Date mCurrentDate;

    private Calendar mMinDate;
    private Calendar mMaxDate;

    private DateAdapter mDateAdapter;

    public WeekCalendarView(Context context) {
        super(context);
    }

    public WeekCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化当初日期
        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCurrentDate = new Date();

        mDateAdapter = new DateAdapter(getContext(), null);
        mDateAdapter.setDateList(getWeekDays(mCalendar));
        mDateAdapter.setSelection(mCalendar.get(Calendar.DAY_OF_WEEK) - 1);
        addView(getWeekGridView());

    }

    private GridView getWeekGridView() {
        GridView gridView = new GridView(getContext());
        gridView.setNumColumns(7);
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        gridView.setLayoutParams(params);
        gridView.setAdapter(mDateAdapter);
        return gridView;
    }

    /**
     * 获取某天所在这一周的7天的数据
     *
     * @return
     */
    private List<DateBean> getWeekDays(Calendar calendar) {
        ArrayList<DateBean> list = new ArrayList<>();
        Calendar calendarTemp = (Calendar) calendar.clone();
        int dayOfWeek = calendarTemp.get(Calendar.DAY_OF_WEEK);
        calendarTemp.add(Calendar.DAY_OF_YEAR, -dayOfWeek);

        for (int i = 0; i < 7; i++) {
            calendarTemp.add(Calendar.DAY_OF_YEAR, 1);
            DateBean dateBean = new DateBean();
            dateBean.setDay(calendarTemp.get(Calendar.DAY_OF_MONTH));
            dateBean.setMonth(calendarTemp.get(Calendar.MONTH) + 1);
            dateBean.setYear(calendarTemp.get(Calendar.YEAR));
            dateBean.setDate(calendarTemp.getTime());

            dateBean.setSelectable(true);
            if ((mMaxDate != null) && calendarTemp.after(mMaxDate)) {
                dateBean.setSelectable(false);
            }
            if ((mMinDate != null) && calendarTemp.before(mMinDate)) {
                dateBean.setSelectable(false);
            }
            //将是否可选的标志放在DateBean中并在这里判断而不是在Adapter中判断
            // ，是为了解决切换下一页数据时被选中的位置是不可选的状态！
            //这样可以在View这里先做判断然后重新设置选中的位置！

            list.add(dateBean);
        }
        return list;
    }

    public void setRange(Calendar minDate, Calendar maxDate) {
        if (minDate != null && maxDate != null && minDate.after(maxDate)) {
            throw new IllegalArgumentException("日期范围参数有错！");
        }

        boolean isRangeExist = false;
        if (minDate != null) {
            mMinDate = minDate;
            isRangeExist = true;
            mMinDate.set(Calendar.HOUR_OF_DAY, 0);
            mMinDate.set(Calendar.MINUTE, 0);
            mMinDate.set(Calendar.SECOND, 0);
            mMinDate.set(Calendar.MILLISECOND, 0);
            mDateAdapter.setMinMonthAndDay(mMinDate.getTime());
        }
        if (maxDate != null) {
            mMaxDate = maxDate;
            isRangeExist = true;
            mMaxDate.set(Calendar.HOUR_OF_DAY, 0);
            mMaxDate.set(Calendar.MINUTE, 0);
            mMaxDate.set(Calendar.SECOND, 0);
            mMaxDate.set(Calendar.MILLISECOND, 0);
            mDateAdapter.setMaxMonthAndDay(mMaxDate.getTime());
        }

        if (isRangeExist) {
            mDateAdapter.setDateList(getWeekDays(mCalendar));
            mDateAdapter.notifyDataSetChanged();
        }
    }

    public Calendar getMinDate() {
        return mMinDate;
    }

    public Calendar getMaxDate() {
        return mMaxDate;
    }

    public Date getSelectedDate() {
        DateBean dateBean = mDateAdapter.getSelectedDateBean();
        if (dateBean == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(dateBean.getYear(), dateBean.getMonth() - 1, dateBean.getDay());
        return calendar.getTime();
    }

    float firstTouchX;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        System.out.println("onInterceptTouchEvent = " + ev.getAction());
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            firstTouchX = ev.getX();
        }
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (Math.abs(ev.getX() - firstTouchX) > 15) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("onTouchEvent = " + event.getAction());
        Calendar tempCalendar = (Calendar) mCalendar.clone();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() - firstTouchX < -60) {//向左划
                tempCalendar.add(Calendar.DATE, 7);
                List<DateBean> list = getWeekDays(tempCalendar);
                if (!list.get(0).isSelectable()) {
                    return true;
                }

                mCalendar.add(Calendar.DATE, 7);
                mDateAdapter.setDateList(list);

                if (!mDateAdapter.getSelectedDateBean().isSelectable()) {
                    mDateAdapter.setSelection(0);
                }

                addView(getWeekGridView());
                setInAnimation(getTranslateAnim(true, true));
                setOutAnimation(getTranslateAnim(true, false));
                showNext();
                removeViewAt(0);
            } else if (event.getX() - firstTouchX > 60) {
                tempCalendar.add(Calendar.DATE, -7);
                List<DateBean> list = getWeekDays(tempCalendar);
                if (!list.get(list.size() - 1).isSelectable()) {
                    return true;
                }

                mCalendar.add(Calendar.DATE, -7);
                mDateAdapter.setDateList(list);

                if (!mDateAdapter.getSelectedDateBean().isSelectable()) {
                    mDateAdapter.setSelection(list.size() - 1);
                }

                addView(getWeekGridView());
                setInAnimation(getTranslateAnim(false, true));
                setOutAnimation(getTranslateAnim(false, false));
                showNext();
                removeViewAt(0);
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * @param translateToLeft 是否为向左运行的动画
     * @param isShowIn        是否为进入屏幕的动画
     * @return
     */
    private TranslateAnimation getTranslateAnim(boolean translateToLeft, boolean isShowIn) {
        int start, end;
        if (translateToLeft) {//向左运动
            if (isShowIn) {
                start = 1;
                end = 0;
            } else {
                start = 0;
                end = -1;
            }
        } else {
            if (isShowIn) {
                start = -1;
                end = 0;
            } else {//移出屏幕
                start = 0;
                end = 1;
            }
        }

        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        anim.setDuration(400);
        return anim;
    }


}
