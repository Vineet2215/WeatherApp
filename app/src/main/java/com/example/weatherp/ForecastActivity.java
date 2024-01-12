package com.example.weatherp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


class ForecastItems {
    private String day,temperature,uv,wind,icon;



    public ForecastItems(String day, String temperature,String uv,String wind , String icon) {
        this.day = day;
        this.temperature = temperature;
        this.uv = uv;
        this.wind = wind;
        this.icon = icon;
    }

    public String getDay() {
        return day;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getUv(){
        return uv;
    }

     public String getWind() {
         return wind;
     }

    public String getIcon() {
        return icon;
    }
};




public class ForecastActivity extends RecyclerView.Adapter<ForecastActivity.ViewHolder> {

    private Context context;
    private ArrayList<ForecastItems> forecastItemsArrayList;

    public ForecastActivity(Context context, ArrayList<ForecastItems> forecastItemsArrayList) {
        this.context = context;
        this.forecastItemsArrayList = forecastItemsArrayList;
    }


    @NonNull
    @Override
    public ForecastActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seven_days_item,parent,false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ForecastActivity.ViewHolder holder, int position) {

        ForecastItems modal = forecastItemsArrayList.get(position);

        holder.temperatureSevenTV.setText(modal.getTemperature()+"°C");
        holder.windSevenTV.setText(modal.getWind() + " Km/h");
        holder.uvSevenTV.setText(modal.getUv());

        Picasso.get().load("https:".concat(modal.getIcon())).into(holder.conditionSevenIV);  // image


        // Time
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");

        try{
            Date d = input.parse(modal.getDay());
            holder.dateSevenTV.setText(output.format(d));

        } catch (ParseException e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return forecastItemsArrayList.size();
    }



    // Intialize and do stuff on the items of recycler view
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView windSevenTV,temperatureSevenTV,dateSevenTV,uvSevenTV;
        private ImageView conditionSevenIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windSevenTV=itemView.findViewById(R.id.IDTVSeven_wind);
            temperatureSevenTV=itemView.findViewById(R.id.IDTVSeven_temp);
            dateSevenTV=itemView.findViewById(R.id.IDTVSeven_date);
            conditionSevenIV=itemView.findViewById(R.id.IDIVSeven_icon);
            uvSevenTV = itemView.findViewById(R.id.IDTVSeven_uv);

            // Always Check ?

        }
    }
}
