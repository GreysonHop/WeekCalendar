package com.example.greyson.weekcalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private WeekCalendarView weekCalendarView;
    private TextView dateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weekCalendarView = (WeekCalendarView) findViewById(R.id.weekCalendarView);
        dateTV = (TextView) findViewById(R.id.dateTV);


        Calendar minCal = Calendar.getInstance();
        minCal.add(Calendar.DATE, -7);
        Calendar maxCal = Calendar.getInstance();
        maxCal.add(Calendar.DATE, 3);
        weekCalendarView.setRange(minCal, maxCal);
    }

    public void onClick(View view) {
        Date date = weekCalendarView.getSelectedDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        dateTV.setText(format.format(date));
        System.out.println(date.getYear() + " - " + date.getMonth() + " - " + date.getDate());

    }
}
