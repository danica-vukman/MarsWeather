package com.example.aimi.marsweather;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

public class ArchiveActivity extends AppCompatActivity {
    SharedPreferences preferences;
    int day;
    int month;
    int year;
    String ARCHIVE_API_ENDPOINT = "http://marsweather.ingenology.com/v1/archive/?terrestrial_date=";
    String query;
    MarsWeather helper = MarsWeather.getInstance();
    TextView avgTempTV, condTV, dateTV, minTempTV, maxTempTV, pressureTv, sunsetTv, sunriseTv, noDataTV, text1, text2, text3, text4, text5, text6, text7, text8;
    Button newDatebtn;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        day = preferences.getInt("day", 1);
        month = preferences.getInt("month", 1);
        year = preferences.getInt("year", 2013);
        query = year + "-" + (month + 1) + "-" + day;
        avgTempTV = (TextView) findViewById(R.id.avgTempTV);
        condTV = (TextView) findViewById(R.id.conditionTV);
        dateTV = (TextView) findViewById(R.id.dateTV);
        minTempTV = (TextView) findViewById(R.id.minTempTV);
        maxTempTV = (TextView) findViewById(R.id.maxTempTV);
        pressureTv = (TextView) findViewById(R.id.pressureTV);
        sunsetTv = (TextView) findViewById(R.id.sunsetTV);
        sunriseTv = (TextView) findViewById(R.id.sunriseTV);
        noDataTV = (TextView) findViewById(R.id.noDataTV);
        newDatebtn = (Button) findViewById(R.id.newDateBTN);
        text1 = (TextView) findViewById(R.id.tv1);
        text2 = (TextView) findViewById(R.id.tv2);
        text3 = (TextView) findViewById(R.id.tv3);
        text4 = (TextView) findViewById(R.id.tv4);
        text5 = (TextView) findViewById(R.id.tv5);
        text6 = (TextView) findViewById(R.id.tv6);
        text7 = (TextView) findViewById(R.id.tv7);
        text8 = (TextView) findViewById(R.id.tv8);
        Log.v("tag", query);
        loadWeatherData();
        newDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

    }

    private void loadWeatherData() {
        CustomJsonRequest request = new CustomJsonRequest
                (Request.Method.GET, ARCHIVE_API_ENDPOINT + query, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.v("tag", String.valueOf(response));
                            int minTemp;
                            int maxTemp;
                            String atmo;
                            String pressure;
                            String sunrise;
                            String sunset;
                            int avgTemp;

                            JSONArray responseArray = response.getJSONArray("results");
                            JSONObject dayarray = responseArray.getJSONObject(0);

                            minTemp = dayarray.getInt("min_temp");
                            maxTemp = dayarray.getInt("max_temp");
                            avgTemp = minTemp + maxTemp / 2;

                            atmo = dayarray.getString("atmo_opacity");
                            pressure = String.valueOf((int) dayarray.getDouble("pressure"));
                            sunrise = dayarray.getString("sunrise").substring(11, 16);
                            sunset = dayarray.getString("sunset").substring(11, 16);

                            avgTempTV.setVisibility(View.VISIBLE);
                            condTV.setVisibility(View.VISIBLE);
                            dateTV.setVisibility(View.VISIBLE);
                            minTempTV.setVisibility(View.VISIBLE);
                            maxTempTV.setVisibility(View.VISIBLE);
                            pressureTv.setVisibility(View.VISIBLE);
                            sunsetTv.setVisibility(View.VISIBLE);
                            sunriseTv.setVisibility(View.VISIBLE);
                            text1.setVisibility(View.VISIBLE);
                            text2.setVisibility(View.VISIBLE);
                            text3.setVisibility(View.VISIBLE);
                            text4.setVisibility(View.VISIBLE);
                            text5.setVisibility(View.VISIBLE);
                            text6.setVisibility(View.VISIBLE);
                            text7.setVisibility(View.VISIBLE);
                            text8.setVisibility(View.VISIBLE);
                            newDatebtn.setVisibility(View.VISIBLE);


                            dateTV.setText(day + "." + month + "." + year + ".");
                            condTV.setText(atmo.toLowerCase() + ".");
                            pressureTv.setText(pressure + " mb.");

                            sunriseTv.setText(sunrise + " ");
                            sunsetTv.setText(sunset + ".");

                            if (preferences.getString("units", "metric").equals("metric")) {
                                avgTempTV.setText(avgTemp + "° C");
                                minTempTV.setText(minTemp + "° C");
                                maxTempTV.setText(maxTemp + "° C" + ".");
                            } else {
                                avgTempTV.setText(convertCtoF(avgTemp) + "° F");
                                minTempTV.setText(convertCtoF(minTemp) + "° F");
                                maxTempTV.setText(convertCtoF(maxTemp) + "° F" + ".");
                            }


                        } catch (Exception e) {
                            txtError(e);
                            avgTempTV.setVisibility(View.INVISIBLE);
                            condTV.setVisibility(View.INVISIBLE);
                            dateTV.setVisibility(View.INVISIBLE);
                            minTempTV.setVisibility(View.INVISIBLE);
                            maxTempTV.setVisibility(View.INVISIBLE);
                            pressureTv.setVisibility(View.INVISIBLE);
                            sunsetTv.setVisibility(View.INVISIBLE);
                            sunriseTv.setVisibility(View.INVISIBLE);
                            text1.setVisibility(View.INVISIBLE);
                            text2.setVisibility(View.INVISIBLE);
                            text3.setVisibility(View.INVISIBLE);
                            text4.setVisibility(View.INVISIBLE);
                            text5.setVisibility(View.INVISIBLE);
                            text6.setVisibility(View.INVISIBLE);
                            text7.setVisibility(View.INVISIBLE);
                            text8.setVisibility(View.INVISIBLE);
                            noDataTV.setVisibility(View.VISIBLE);
                            newDatebtn.setVisibility(View.VISIBLE);

                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txtError(error);
                    }
                });

        request.setPriority(Request.Priority.HIGH);
        helper.add(request);

    }

    private void txtError(Exception e) {
        e.printStackTrace();
    }

    public int convertCtoF(double v) {
        return (int) (v * 1.8000 + 32.00);
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
    }
}
