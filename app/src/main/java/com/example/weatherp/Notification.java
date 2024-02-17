package com.example.weatherp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notification {

    public static final String CHANNEL_ID = "10001";
    public static final String NOTIFICATION_ID = "1";

    public static String generateWeatherSuggestions(double temperature, double windSpeed, double humidity) {
        double highTemperatureThreshold = 30.0;
        double lowTemperatureThreshold = 10.0;
        double highWindThreshold = 30.0;
        double highHumidityThreshold = 80.0;

        StringBuilder suggestions = new StringBuilder();

        if (temperature > highTemperatureThreshold) {
            suggestions.append("It's quite warm. Consider wearing light and breathable clothes. ");
        } else if (temperature < lowTemperatureThreshold) {
            suggestions.append("It's a bit chilly. Don't forget to wear a jacket or layers. ");
        } else {
            suggestions.append("The temperature is moderate. Choose comfortable clothing for the day. ");
        }


        if (windSpeed > highWindThreshold) {
            suggestions.append("Expect windy conditions. You may want to secure loose items and consider a windbreaker. ");
        }

        if (humidity > highHumidityThreshold) {
            suggestions.append("High humidity today. Stay hydrated and consider lighter clothing. ");
        }

        suggestions.append("Plan your day accordingly and stay comfortable!");

        return suggestions.toString();
    }

    public static void createNotificationChannel(Context context,String temperature,String windSpeed , String humidity) {

        double temperatureValue = Double.parseDouble(temperature);
        double windSpeedValue = Double.parseDouble(windSpeed);
        double humidityValue = Double.parseDouble(humidity);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification_Channel";
            String description = "A Description of the Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Handle the case where the user doesn't have the necessary permission.
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.img)
                .setContentTitle("Recommendation")
                .setContentText(generateWeatherSuggestions(temperatureValue,windSpeedValue,
                        humidityValue))
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(generateWeatherSuggestions(temperatureValue,windSpeedValue,
                        humidityValue)))

                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // notificationId is a unique int for each notification that you must define.
        NotificationManagerCompat.from(context).notify(Integer.parseInt(NOTIFICATION_ID), builder.build());
    }
}
