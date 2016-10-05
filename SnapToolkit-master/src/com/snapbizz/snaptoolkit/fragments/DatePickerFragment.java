package com.snapbizz.snaptoolkit.fragments;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements
DatePickerDialog.OnDateSetListener {

	private OnDateSelectedListener onDateSelectedListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(onDateSelectedListener == null) {
			throw new ClassCastException(activity.toString() + "must implement OnDateSelectedListener");
		}
	}

	public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
		this.onDateSelectedListener = onDateSelectedListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		if(view.isShown())
			onDateSelectedListener.onDateSelected(new Date(year - 1900, month, day));
	}

	public interface OnDateSelectedListener {
		public void onDateSelected(Date selectedDate);
	}	
}
