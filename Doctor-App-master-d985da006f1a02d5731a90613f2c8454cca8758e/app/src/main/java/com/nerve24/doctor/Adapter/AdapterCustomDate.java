package com.nerve24.doctor.Adapter;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nerve24.doctor.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import pl.rspective.pagerdatepicker.adapter.AbsDateAdapter;
import pl.rspective.pagerdatepicker.adapter.AbsDateItemHolder;
import pl.rspective.pagerdatepicker.model.DateItem;

public class AdapterCustomDate extends AbsDateAdapter<AdapterCustomDate.CustomDateViewHolder> {



    public AdapterCustomDate(Date start, Date end) {
        this(start, end, null);
    }

    public AdapterCustomDate(Date start, Date end, Date defaultSelectedDate) {
        super(start, end, defaultSelectedDate);
    }

    @Override
    protected void onDateItemHolderClick(CustomDateViewHolder itemHolder) {
        if (onDateItemListener != null) {
            onDateItemListener.onDateItemClick(getItem(itemHolder.getPosition()), itemHolder.getPosition());
        }

        if (selectedDate != -1 && selectedDateView != null) {
            selectedDateView.changeDateIndicatorColor(false);
            selectedDateView.changeTextColor(false);
        }

        selectedDateView = itemHolder;
        selectedDate = dateItems.get(itemHolder.getPosition()).getDate().getTime();

        selectedDateView.changeDateIndicatorColor(true);
        selectedDateView.changeTextColor(true);
    }

    @Override
    public CustomDateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.item_view_custom_date, parent, false);

        return new CustomDateViewHolder(root, this);
    }

    @Override
    public void onBindViewHolder(CustomDateViewHolder dateItemHolder, int position) {
        DateItem dateItem = dateItems.get(position);

        dateItemHolder.setDay(dateItem.getDate());
        dateItemHolder.setMonthName(dateItem.getDate());
        dateItemHolder.setDayName(dateItem.getDate());

        dateItemHolder.itemView.setSelected(true);

        if (isDateSelected(dateItem)) {
            dateItemHolder.updateDateItemView(true);
            selectedDateView = dateItemHolder;
        } else {
            dateItemHolder.updateDateItemView(false);
        }
    }

    static class CustomDateViewHolder extends AbsDateItemHolder {

        TextView tvDay;

        Resources resources;

        public CustomDateViewHolder(View itemView, AbsDateAdapter dateAdapter) {
            super(itemView, dateAdapter);

            this.resources = itemView.getResources();

            tvDay = (TextView) itemView.findViewById(R.id.tv_date_picker_day);
            tvDay.setTypeface(null,Typeface.NORMAL);
        }

        @Override
        public void changeTextColor(boolean isSelected) {
            if (isSelected) {

            } else {

            }
        }

        @Override
        public void setDay(Date date) {
            SimpleDateFormat formatDate = new SimpleDateFormat("EEE dd MMMM");
            tvDay.setText(formatDate.format(date));
        }

        @Override
        public void setMonthName(Date date) {
        }

        @Override
        public void setDayName(Date date) {
        }

        @Override
        public void changeDateIndicatorColor(boolean isSelected) {

        }

    }

}

