package com.jaewon.project1;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
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
import java.util.List;
/**
 * Created by won on 2015-07-10.
 */
public class ForecastFragment extends Fragment {

    private static String LOG = ForecastFragment.class.getSimpleName();
    private static Boolean DEBUG = true;
    View rootView;
    List<ForecastItem> forecastItemList = new ArrayList<ForecastItem>();
    //private  ArrayAdapter<String> mForecastAdapter;
    RecyclerView recyclerView;
    RecyclerItemClickListener recyclerItemClickListener;
    ImageView imgview1;
    private String unitType;
    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Util.getInstance().printLog(DEBUG,LOG, "OnCreate!");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void updateWeather() {

        FetchWeatherTast weatherTast = new FetchWeatherTast();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        unitType = prefs.getString(getString(R.string.pref_units_key),
                getString(R.string.pref_units_metric));
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        weatherTast.execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main,container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.myRecyclerView);

        final myRecycleAdapter myrecycleadapter = new myRecycleAdapter(forecastItemList, 7);
        imgview1 = (ImageView) rootView.findViewById(R.id.imgView);
        recyclerView.setAdapter(new myRecycleAdapter(forecastItemList, R.layout.list_item_forecast));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, forecastItemList.get(position).getText());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(getActivity().getApplicationContext(), "Long Click~~!", Toast.LENGTH_SHORT).show();

            }
        }));

        return rootView;
    }
    public class FetchWeatherTast extends AsyncTask<String, Void, String[]> {

        private String description;
        private final String LOG_TAG = FetchWeatherTast.class.getSimpleName();

        private String getReadableDateString(long time) {
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        private String formatHighLows(double high, double low, String unitType) {
            if(unitType.equals(getString(R.string.pref_units_imperial))) {
                high = (high * 1.8) + 32;
                low = (low * 1.8) + 32;
            } else if ( !unitType.equals(getString(R.string.pref_units_metric))) {
                Log.d(LOG_TAG,"Unit type not found : " + unitType);
            }
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

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
            for (int i = 0; i < weatherArray.length(); i++) {
                String day;
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

                highAndLow = formatHighLows(high, low, unitType);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Foreast entry : " + s);
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
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
                //mForecastAdapter.clear();
                forecastItemList.clear();
                for(int i= 0; i< result.length; i++) {
                    ForecastItem item = new ForecastItem();
                    switch (description) {
                        case "Rain" :
                            item.setImg(R.drawable.d);
                            break;
                        case "Clouds" :
                            item.setImg(R.drawable.b);
                            break;
                        case "snow" :
                            item.setImg(R.drawable.i);
                            break;
                        case "Thunderstorm" :
                            item.setImg(R.drawable.j);
                            break;
                        case "mist" :
                            item.setImg(R.drawable.a);
                            break;
                        case "scatter clouds" :
                            item.setImg(R.drawable.h);
                            break;
                        default:
                                break;
                    }
                    item.setText(result[i]);
                    forecastItemList.add(item);
                }
                recyclerView.setAdapter(new myRecycleAdapter(forecastItemList,R.layout.list_item_forecast));
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());

            }
        }
    }
}
