package com.jaewon.project1;

/**
 * Created by won on 2015-08-20.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class SuperAwesomeCardFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static String location;
    ArrayList<ForecastItem> forecastItem = new ArrayList<ForecastItem>();
    ForecastItem item;
    private RecyclerView recyclerView;
    private myRecycleAdapter mAdapter;
    private MyPagerAdapter adapter;
    private int position;

    public static SuperAwesomeCardFragment newInstance(int position){
        SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                .getDisplayMetrics());

        recyclerView = new RecyclerView(getActivity());

        adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        params.setMargins(margin, margin, margin, margin);

        initData();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemclickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ForecastItem forecast = mAdapter.getItem(forecastItem ,position);
                        Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecast.getText());
                        startActivity(intent);
                    }
                })
        );

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);
        fl.addView(recyclerView);
        updateWeatherb(adapter.returnString(position));
        return fl;
    }

    private void initData() {

        for(int i = 0 ; i < 7; i++) {
            item = new ForecastItem();
            item.setText_forecast_date("date");
            item.setText_forecast_temp("temp");
            item.setImg(R.mipmap.ic_launcher);
            forecastItem.add(item);
        }
        mAdapter = new myRecycleAdapter(forecastItem,R.layout.list_item_forecast);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void updateWeatherb(String i) {
        FetchweatherTask weatherTask = new FetchweatherTask();
                /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String location = prefs.getString(getString(R.string.pref_location_key),
                        getString(R.string.pref_location_default));*/
        weatherTask.execute(i);
    }

    private void updateWeather() {
        FetchweatherTask weatherTask = new FetchweatherTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        weatherTask.execute(location);
    }

    public class FetchweatherTask extends AsyncTask<String, Void, String[]> {


        private final String LOG_TAG = FetchweatherTask.class.getSimpleName();
        private ArrayList<String> str_weather = new ArrayList<>();
        private ArrayList<String> str_date = new ArrayList<>();
        private ArrayList<String> str_high_temp = new ArrayList<>();
        private ArrayList<String> str_low_temp = new ArrayList<>();


        private String getReadableDateString(long time) {
            SimpleDateFormat shortenedDeateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDeateFormat.format(time);
        }

        private String formatHighLows(double high, double low, String unitType) {

            if (unitType.equals(getString(R.string.pref_units_imperial))) {
                high = (high * 1.8) + 32;
                low = (low * 1.8) + 32;

            } else if (!unitType.equals(getString(R.string.pref_units_metric))) {
                Log.d(LOG_TAG, "Unit type not found : " + unitType);
            }

            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highText = roundedHigh + "";
            String lowText = roundedLow + "";
            str_high_temp.add(highText);
            str_low_temp.add(lowText);

            String highLowstr = roundedHigh + "/" + roundedLow;
            return highLowstr;
        }

        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            Time dayTime = new Time();
            dayTime.setToNow();

            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            dayTime = new Time();

            String[] resultStrs = new String[numDays];

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String unitType = sharedPrefs.getString(
                    getString(R.string.pref_units_key),
                    getString(R.string.pref_units_metric));

            for (int i = 0; i < weatherArray.length(); i++) {
                String day;
                String highAndLow;
                String description;

                JSONObject dayForecast = weatherArray.getJSONObject(i);

                long dateTime;

                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                JSONObject temperatureObjet = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObjet.getDouble(OWM_MAX);
                double low = temperatureObjet.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low, unitType);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
                str_weather.add(description);
                str_date.add(day);
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry : " + s);
            }
            return resultStrs;
        }


        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String format = "json";
            String units = "metric";
            int numDays = 7;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=Incheon&mode=xml&units=metric&cnt=7");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast string " + forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                forecastItem.clear();
                for (int i = 0; result.length > i; i++) {
                    item = new ForecastItem();
                    mAdapter.additems(forecastItem, item, result[i], str_weather, str_date, str_high_temp,str_low_temp, i);
                }
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }
        }
    }
}