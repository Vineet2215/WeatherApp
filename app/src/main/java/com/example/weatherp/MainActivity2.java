package com.example.weatherp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    RecyclerView recyclerViewSeven;
   ForecastActivity forecastActivity;
    private LocationManager locationManager;
    private EditText cityEdt;
    private ImageView backIV2,previousIV,shareIV;
    private String cityName,temperature;
    private  int PERMISSION_CODE = 1;


    private ArrayList<ForecastItems> forecastItemsArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        recyclerViewSeven = findViewById(R.id.idRVNextSevenDays);
        backIV2 = findViewById(R.id.idIVBack2);
        previousIV = findViewById(R.id.idIVprevious);
        shareIV = findViewById(R.id.idIVShare);

        String s = getIntent().getStringExtra("city");
        temperature = getIntent().getStringExtra("temp");
        int isDay = getIntent().getIntExtra("isDay",1);
        Log.d("city",s);
        getWeatherInfo(s);

        if(isDay==1)
        {
            backIV2.setImageResource(R.drawable.day);
        }else {
            backIV2.setImageResource(R.drawable.night);
        }



        recyclerViewSeven.setLayoutManager(new LinearLayoutManager(this));
        forecastItemsArrayList = new ArrayList<>();
        forecastActivity = new ForecastActivity(this,forecastItemsArrayList);
        recyclerViewSeven.setAdapter(forecastActivity);



        previousIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity1();
            }
        });

        shareIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Current temparature in the "+ s + " is " + temperature + "°C");
                if(intent.resolveActivity( getPackageManager() ) != null){
                    startActivity(intent);
                }
            }
        });

        // Location

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity2.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION },PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null)
        {
            cityName = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(cityName);
        }
    }


    public void openMainActivity1()
    {
        Intent intent = new Intent(MainActivity2.this,MainActivity.class);
        startActivity(intent);
    }



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

    private String getCityName(double longitude, double latitude) {
        String cityName = "Not found";

        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);


            for (Address adr : addresses) {
                if (adr != null) {
                    String city = adr.getLocality();

                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Log.d("TAG", "City Not Found");
                        Toast.makeText(this, "User City not found ..", Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue  = Volley.newRequestQueue(MainActivity2.this);
        Log.i("test1", cityName);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                forecastItemsArrayList.clear();

                try {

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONArray forecastDayArray = forecastObj.getJSONArray("forecastday");

                    for(int i=0;i<forecastDayArray.length();i++)
                    {
                        JSONObject forecastDay = forecastDayArray.getJSONObject(i);
                        String dateSeven = forecastDay.getString("date");

                        JSONObject dayData = forecastDay.getJSONObject("day");

                        String avgTempSeven = dayData.getString("avgtemp_c");
                        String maxWindSeven = dayData.getString("maxwind_kph");
                        String UVSeven = dayData.getString("uv");

                        JSONObject condn = dayData.getJSONObject("condition");
                        String iconSeven = condn.getString("icon");

                        forecastItemsArrayList.add(new ForecastItems(dateSeven,avgTempSeven,UVSeven,maxWindSeven,iconSeven));
                    }

                    forecastActivity.notifyDataSetChanged();

                } catch (JSONException e) {
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
                Toast.makeText(MainActivity2.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

}