package com.example.aimi.marsweather;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    final static String RECENT_API_ENDPOINT = "http://marsweather.ingenology.com/v1/latest/";
    final static String FLICKR_API_KEY = "eb828d01a8c2fc3a2d0e52eb0d0b25d5";
    final static String IMAGES_API_ENDPOINT = "https://api.flickr.com/services/rest/?format=json&nojsoncallback=1&sort=random&method=flickr.photos.search&" +
            "tags=mars,planet,rover&tag_mode=all&api_key=";
    final static String SHARED_PREFS_IMG_KEY = "img";
    final static String SHARED_PREFS_DAY_KEY = "day";
    final static String PHOTO_LINK_BASE = "https://www.flickr.com/photos/";
    final String welcomeScreenShownPref = "welcomeScreenShown";
    final String SUMMARY_STRING_METRIC = "Maximum temperature:\n" +
            "%s°C \n" +
            "Minimum temperature:\n" +
            "%s°C\n" +
            "Pressure:\n" +
            "%s mb\n" +
            "Sunrise:\n" +
            "%s \n" +
            "Sunset:\n" +
            "%s";
    final String SUMMARY_STRING_IMPERIAL = "Maximum temperature:\n" +
            "%s°F \n" +
            "Minimum temperature:\n" +
            "%s°F\n" +
            "Pressure:\n" +
            "%s mb\n" +
            "Sunrise:\n" +
            "%s \n" +
            "Sunset:\n" +
            "%s";
    TextView avgTempTV, conditionTV, summaryTV;
    ImageView imageView;
    MarsWeather helper = MarsWeather.getInstance();
    SharedPreferences preferences;
    int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    int mainColor = Color.parseColor("#FF5722");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean welcomeScreenShown = preferences.getBoolean(welcomeScreenShownPref, false);
        if (!welcomeScreenShown) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.title_welcome))
                    .setMessage(getResources().getString(R.string.welcome_summary))
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

            preferences.edit().putBoolean(welcomeScreenShownPref, true).commit();
        }


        avgTempTV = (TextView) findViewById(R.id.degrees);
        conditionTV = (TextView) findViewById(R.id.weather);
        imageView = (ImageView) findViewById(R.id.main_bg);
        summaryTV = (TextView) findViewById(R.id.summary);


        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(preferences.getString("individualphotolink", "")));
                startActivity(intent);
                return false;
            }
        });


        if (preferences.getInt(SHARED_PREFS_DAY_KEY, 0) != today) {
            try {
                searchRandomImage();
            } catch (Exception e) {
                imageError(e);
            }
        } else {
            loadImg(preferences.getString(SHARED_PREFS_IMG_KEY, ""));
        }
        loadWeatherData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWeatherData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_new_picture) {
            try {
                searchRandomImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (id == R.id.action_refresh) {
            loadWeatherData();
        }

        if (id == R.id.action_archive) {
            showDatePickerDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadWeatherData() {
        CustomJsonRequest request = new CustomJsonRequest
                (Request.Method.GET, RECENT_API_ENDPOINT, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            int minTemp;
                            int maxTemp;
                            String atmo;
                            String pressure;
                            String sunrise;
                            String sunset;
                            int avgTemp;

                            response = response.getJSONObject("report");

                            minTemp = response.getInt("min_temp");
                            maxTemp = response.getInt("max_temp");
                            avgTemp = minTemp + maxTemp / 2;

                            atmo = response.getString("atmo_opacity");
                            pressure = String.valueOf((int) response.getDouble("pressure"));
                            sunrise = response.getString("sunrise").substring(11, 16);
                            sunset = response.getString("sunset").substring(11, 16);

                            if (preferences.getString("units", "metric").equals("metric")) {
                                avgTempTV.setText(avgTemp + "° C");
                                conditionTV.setText(atmo.toLowerCase() + ".");
                                summaryTV.setText(String.format(SUMMARY_STRING_METRIC, maxTemp, minTemp, pressure, sunrise, sunset));
                            } else {
                                avgTempTV.setText(convertCtoF(avgTemp) + "° F");
                                conditionTV.setText(atmo + ".");

                                summaryTV.setText(String.format(SUMMARY_STRING_IMPERIAL, convertCtoF(maxTemp), convertCtoF(minTemp), pressure, sunrise, sunset));
                            }
                        } catch (Exception e) {
                            txtError(e);
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

    private void searchRandomImage() throws Exception {
        if (FLICKR_API_KEY.equals(""))
            throw new Exception("You didn't provide a working Flickr API!");

        CustomJsonRequest request = new CustomJsonRequest
                (Request.Method.GET, IMAGES_API_ENDPOINT + FLICKR_API_KEY, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray images = response.getJSONObject("photos").getJSONArray("photo");
                            int index = new Random().nextInt(images.length());

                            JSONObject imageItem = images.getJSONObject(index);

                            String imageUrl = "http://farm" + imageItem.getString("farm") +
                                    ".static.flickr.com/" + imageItem.getString("server") + "/" +
                                    imageItem.getString("id") + "_" + imageItem.getString("secret") + "_" + "c.jpg";
                            String imgLinkUrl = PHOTO_LINK_BASE + imageItem.getString("owner") + "/" + imageItem.getString("id");

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt(SHARED_PREFS_DAY_KEY, today);
                            editor.putString(SHARED_PREFS_IMG_KEY, imageUrl);
                            editor.putString("individualphotolink", imgLinkUrl);
                            editor.commit();

                            loadImg(imageUrl);

                        } catch (Exception e) {
                            imageError(e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        imageError(error);
                    }
                });
        request.setPriority(Request.Priority.LOW);
        helper.add(request);
    }

    private void imageError(Exception e) {
        imageView.setBackgroundColor(mainColor);
        e.printStackTrace();
    }

    private void loadImg(String imageUrl) {
        ImageRequest request = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        imageError(error);
                    }
                });

        helper.add(request);
    }

    public int convertCtoF(double v) {
        return (int) (v * 1.8000 + 32.00);
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
}
