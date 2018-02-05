package com.example.aimi.marsweather;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Aimi on 12/05/2016.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    SharedPreferences preferences;
    int day, month, year;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);


    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.day = dayOfMonth;
        this.month = monthOfYear;
        this.year = year;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        preferences.edit().putInt("day", day).putInt("month", month).putInt("year", year).commit();
        Intent i = new Intent(getActivity(), ArchiveActivity.class);
        startActivity(i);
        super.onDismiss(dialog);
    }
}
