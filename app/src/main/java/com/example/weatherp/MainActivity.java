package com.example.weatherp;

import java.io.IOException;
import java.util.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LocationListener
{

    private int isDay=1;
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV,temperatureTV,conditionTV ,updated_at_TV,feelsLikeTV,humidityTV,windTV,airPressureTV,windSevenTV, temperatureSevenTV,daySevenTV,uvSevenTV;
    private RecyclerView weatherRV;


    private EditText cityEdt;
    private ImageView backIV,iconIV,searchIV,micSearchIV,nextIV,locIV;

    private ArrayList <WeatherModal>weatherModalArrayList;
    private WeatherAdopter weatherAdopter;
    private LocationManager locationManager;
    private  int PERMISSION_CODE = 1;
    private final int REQUEST_CODE_EXTRA_INPUT = 101;
    private String cityName,temperature;
    private CardView feelsLikeCV,humidityCV,windCV,pressureCV;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);  // for full screen display
        setContentView(R.layout.activity_main);


// ---------------------------------------------------------------------------------------------------------------------------------------------
        homeRL= findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        updated_at_TV = findViewById(R.id.IDTVUpdated_At);

        nextIV = findViewById(R.id.IDIVNext);

        // Weather Details
        feelsLikeTV = findViewById(R.id.IDTVFeelsLike);
        humidityTV = findViewById(R.id.IDTVHumidity);
        airPressureTV = findViewById(R.id.IDTVAirPressure);
        windTV = findViewById(R.id.IDTVWind);

        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        micSearchIV=findViewById(R.id.idIVmicSearch);
        locIV = findViewById(R.id.idIVLoc);

        // Card View
        feelsLikeCV = findViewById(R.id.idCVFeelsLike);
        humidityCV = findViewById(R.id.idCVHumidity);
        windCV = findViewById(R.id.idCVWind);
        pressureCV = findViewById(R.id.idCVPressure);



        weatherModalArrayList = new ArrayList<>();

        weatherAdopter = new WeatherAdopter(this,weatherModalArrayList);
        weatherRV.setAdapter(weatherAdopter);

// ------------------------------------------------------------------------------------------------------------

        nextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity2();
            }
        });
// ----------------------------------------------------------------------------------------------------------------
        locIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the location updates are already requested
                if (locationManager != null) {
                    locationManager.removeUpdates(MainActivity.this);
                }
                // Request location updates again
                getLocation();
            }
        });


//-----------------------------------------------------------------------------------------------------------------------------------------------------------
        // for permission ->
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION },PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null)
        {
            cityName = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(cityName);
        }

        // --------------------------------------------------------------------------------------------------------------------------------------


        // For Search Icon
        searchIV.setOnClickListener(view -> {
            String city;
            city = cityEdt.getText().toString();

            if(city.isEmpty()){
                Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
            }else {
                cityNameTV.setText(cityName);
                getWeatherInfo(city);
            }
        });


        // For Mic Search
        micSearchIV.setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the city name"); // Provide a suitable prompt

            try {
                // Start the voice recognition activity
                startActivityForResult(intent, REQUEST_CODE_EXTRA_INPUT);
            } catch (Exception e) {
                Log.e("Error Voice", "Mic Error: " + e);
            }
        });

    }

    //---------------------------------------------
    // Next 3 Days Button
    public  void openMainActivity2()
    {
        Intent intent = new Intent(MainActivity.this,MainActivity2.class);
        intent.putExtra("city",cityEdt.getText().toString());
        intent.putExtra("isDay",isDay);
        intent.putExtra("temp",temperature);
        startActivity(intent);
    }

    //==============================================================



    //----------------------------------
    // For MIC
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EXTRA_INPUT && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String recognizedSpeech = result.get(0); // Get the first result

            // Now, set the recognized speech as the city name in your EditText
            cityEdt.setText(recognizedSpeech);

            // Trigger the weather query with the recognized city name
            getWeatherInfo(recognizedSpeech);
        }
    }

//------------------------------------------------------------------------------------------------------------------------------------------------
    // On request Permission ..
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length >0 && grantResults[0]==PackageManager.PERMISSION_GRANTED ){
                Toast.makeText(this, "Permission Granted ..", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Please provide the permissions ..", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // To get the city Name -->

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not found";

        Geocoder gcd = new Geocoder(getBaseContext(),Locale.getDefault());

        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);


            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();

                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Log.d("TAG", "City Not Found");
                        Toast.makeText(this, "City not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return cityName;
    }


    private  void getWeatherInfo(String cityName)
    {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=f7255112c26e4734808160748231809&q=" + cityName+ "&days=3&aqi=no&alerts=no";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue  = Volley.newRequestQueue(MainActivity.this);
        Log.i("test1", cityName);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                // inside the on response method.
                // we are hiding our progress bar.
                loadingPB.setVisibility(View.GONE);

                // in below line we are making our card
                // view visible after we get all the data.
               homeRL.setVisibility(View.VISIBLE);

//                forecastItemsArrayList.clear();
                 weatherModalArrayList.clear();
                try {
                    // now we get our response from API in json object format.
                    // in below line we are extracting a string with its key
                    // value from our json object.
                    // similarly we are extracting all the strings from our json object.

                    temperature = response.getJSONObject("current").getString("temp_c");
                    String updated_at = response.getJSONObject("current").getString("last_updated");
                    isDay = response.getJSONObject("current").getInt("is_day");

                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");

                    // Weather Details
                    String feels_like = response.getJSONObject("current").getString("feelslike_c");
                    String humidity = response.getJSONObject("current").getString("humidity");
                    String wind_1 = response.getJSONObject("current").getString("wind_kph");
                    String air_pressure = response.getJSONObject("current").getString("pressure_mb");

                    // From Notification Class
                    Notification notification = new Notification();
                    Log.d(temperature, "onResponse: " );
                    Notification.createNotificationChannel(MainActivity.this,temperature,wind_1,humidity);

                    // we are using picasso to load the image from url.
                    Picasso.get().load("https:".concat(conditionIcon)).into(iconIV);  // Loading our image

                    // after extracting all the data we are
                    // setting that data to all our views.
                    temperatureTV.setText(temperature+"°C");
                    conditionTV.setText(condition);
                    updated_at_TV.setText(updated_at);

                    // Setting weather details.
                    feelsLikeTV.setText(feels_like+"°C");
                    humidityTV.setText(humidity+"%");
                    windTV.setText(wind_1+" km/h");
                    airPressureTV.setText(air_pressure+" mb");


                    int drawableDayId = R.drawable.day;
                    int drawableNightId=R.drawable.night;
                    if(isDay==1)
                    {
                        // its day [ For Image ]
                        Picasso.get().load("android.resource://" + getPackageName() + "/" + drawableDayId).into(backIV);

                        // [ For Background Color of CardView]
                        int colorDay = getResources().getColor(R.color.cardDayColor);
                        feelsLikeCV.setCardBackgroundColor(colorDay);
                        humidityCV.setCardBackgroundColor(colorDay);
                        windCV.setCardBackgroundColor(colorDay);
                        pressureCV.setCardBackgroundColor(colorDay);


                    }
                    else
                    {
                        // its night
                        Picasso.get().load("android.resource://" + getPackageName() + "/" + drawableNightId).into(backIV);

                        // [ For Background Color of CardView]
                        int colorNight = getResources().getColor(R.color.cardNightColor);
                        feelsLikeCV.setCardBackgroundColor(colorNight);
                        humidityCV.setCardBackgroundColor(colorNight);
                        windCV.setCardBackgroundColor(colorNight);
                        pressureCV.setCardBackgroundColor(colorNight);
                    }


                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");

//                    for(int i=0 ; i< hourArray.length(); i++)
//                    {
//                        JSONObject hourObj = hourArray.getJSONObject(i);
//                        String time = hourObj.getString("time");
//                        String temper = hourObj.getString("temp_c");
//                        String img = hourObj.getJSONObject("condition").getString("icon");
//                        String wind = hourObj.getString("wind_kph");
//
//                        weatherModalArrayList.add(new WeatherModal(time,temper,img,wind));
//                    }
//----------------------------------------------------------------------------------------------------
                    // Get the current time
                    Calendar calendar = Calendar.getInstance();
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // Get the current hour in 24-hour format

                    // Iterate through the hourly forecast data
                    for (int i = 0; i < hourArray.length(); i++)
                    {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        int hour = Integer.parseInt(time.split(" ")[1].split(":")[0]); // Extract the hour from the time

                        // Check if the current hour is less than or equal to the hour in the forecast
                        if (currentHour <= hour) {
                            String temper = hourObj.getString("temp_c");
                            String img = hourObj.getJSONObject("condition").getString("icon");
                            String wind = hourObj.getString("wind_kph");

                            // Display the forecast data starting from the current hour
                            // You may break out of the loop if you only want to show a certain number of future hours.
                            weatherModalArrayList.add(new WeatherModal(time,temper,img,wind));
                        }

                    }

                    //--------------------------------------------------------------------------------------------

                    weatherAdopter.notifyDataSetChanged();

                } catch (JSONException e) {
                    // if we do not extract data from json object properly.
                    // below line of code is use to handle json exception
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            // this is the error listener method which
            // we will call if we get any error from API.
            @Override
            public void onErrorResponse(VolleyError error) {
                // below line is use to display a toast message along with our error.
                Log.i("Test2", error.toString());
                Toast.makeText(MainActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });
        // at last we are adding our json
        // object request to our request
        // queue to fetch all the json data.
        requestQueue.add(jsonObjectRequest);

    }




    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            // Check if GPS is enabled
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // GPS provider consumes more battery but gives more accuracy
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1000, this);
            } else {
                // If GPS is not enabled, prompt the user to enable it
                buildAlertMessageNoGps();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, "Current Co-Ordinate: "+location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            assert addresses != null;
            if (!addresses.isEmpty()) {
                String address = addresses.get(0).getLocality();
                getWeatherInfo(address);
            } else {
                // Handle case when locality is not available
                Toast.makeText(this, "Locality not found", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            // Log or display an error message if something goes wrong
            Log.e("LocationError", "Error getting locality: " + e.getMessage());
            Toast.makeText(this, "Error getting locality", Toast.LENGTH_SHORT).show();
        }
    }




    private void buildAlertMessageNoGps()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Location [GPS] seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        // Open the location settings to enable GPS
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


}