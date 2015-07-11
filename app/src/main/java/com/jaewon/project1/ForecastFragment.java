package com.jaewon.project1;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by won on 2015-07-10.
 */
public class ForecastFragment extends Fragment {

    private  ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh) {
            FetchWeatherTast weatherTast = new FetchWeatherTast();
            weatherTast.execute("Bucheon");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        ArrayList<String> weekForecast = new ArrayList<String>();

        weekForecast.add("Today - Sunny - 88 / 63");
        weekForecast.add("Tommorrow - Foggy - 70 / 46");
        weekForecast.add("Weds - cloudy - 72 / 63");
        weekForecast.add("Thurs - Rainy - 64 / 51");
        weekForecast.add("Fri - Foggy - 70 / 46");
        weekForecast.add("Sat - Sunny - 76 / 68");

        mForecastAdapter =  new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,weekForecast);

        View rootView = inflater.inflate(R.layout.fragment_main,container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);


        return rootView;
    }
    public class FetchWeatherTast extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTast.class.getSimpleName();

        private String getReadableDateString(long time) {
            SimpleDateFormat shortenedDeateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDeateFormat.format(time);
        }

        private  String formatHighLows(double high, double low) {
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowstr = roundedHigh + "/" + roundedLow;
            return highLowstr;
        }
        private String [] getWeatherDataFromJson(String forecastJsonStr, int numDays)
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

            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(),dayTime.gmtoff);

            dayTime = new Time();

            String [] resultStrs = new String[numDays];
            for (int i = 0; i< weatherArray.length();i++) {
                String day;
                String description;
                String highAndLow;

                JSONObject dayForecast = weatherArray.getJSONObject(i);

                long dateTime;

                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                JSONObject temperatureObjet = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObjet.getDouble(OWM_MAX);
                double low = temperatureObjet.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
             }

            for(String s : resultStrs) {
                Log.v(LOG_TAG,"Foreast entry : "+ s);
            }
            return resultStrs;

        }
        @Override
        protected String[] doInBackground(String... params) {

            if(params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {

                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
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

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast string : " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
            finally {
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
                return getWeatherDataFromJson(forecastJsonStr,numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String[] result) {
            if(result != null) {
                mForecastAdapter.clear();
                for(String dayForecastStr : result) {
                    mForecastAdapter.add(dayForecastStr);
                }
            }
        }
    }
}