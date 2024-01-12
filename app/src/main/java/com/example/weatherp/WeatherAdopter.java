package com.example.weatherp;


//
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdopter extends RecyclerView.Adapter<WeatherAdopter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherModal> weatherModalArrayList;

    public WeatherAdopter(Context context, ArrayList<WeatherModal> weatherModalArrayList) {
        this.context = context;
        this.weatherModalArrayList = weatherModalArrayList;
    }

    @NonNull
    @Override
    public WeatherAdopter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item,parent,false);
        return new ViewHolder(view);
    }


    // Main function for binding
    @Override
    public void onBindViewHolder(@NonNull WeatherAdopter.ViewHolder holder, int position) {

        WeatherModal modal = weatherModalArrayList.get(position);

        holder.temperatureTV.setText(modal.getTemperature() +"°C");
        holder.windTV.setText(modal.getWindspeed() + " Km/h");

        Picasso.get().load("https:".concat(modal.getIcon())).into(holder.conditionIV);  // image


        // Time
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy hh:mm");

        try{
            Date d = input.parse(modal.getTime());
            holder.timeTV.setText(output.format(d));

        } catch (ParseException e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return weatherModalArrayList.size();
    }



    // Intialize and do stuff on the items of recycler view
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView windTV,temperatureTV,timeTV;
        private ImageView conditionIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV=itemView.findViewById(R.id.idCARDTVCondition);
            temperatureTV=itemView.findViewById(R.id.idCARDTVTemperature);
            timeTV=itemView.findViewById(R.id.idCARDTVTime);
            conditionIV=itemView.findViewById(R.id.idCARDIVCondition);


            // check always all these correct or not ????
        }
    }
}

