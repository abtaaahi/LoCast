package com.abtahiapp.locast_aweatherapp;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.speech.RecognizerIntent;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {
    private BiometricPrompt.PromptInfo promptInfo;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int SPEECH_REQUEST_CODE = 100;
    private TextView locationTextView,weatherTextView,timeTextView,dateTextView,weatherDescriptionText;
    private SearchView searchView; private ImageButton getLocationButton,fingerbutton,shareButton;
    private ImageView feel,hum,pre,windp,cloud,sunrise,sunset,uv,pressurep, weatherphoto;
    private TextView feelT,humT,preT,windT,cloudT,sunriseT,sunsetT,uvT,pressureT; private TextView feelD,humD,preD,windD,cloudD,sunriseD,sunsetD,uvD,pressureD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fingerbutton=findViewById(R.id.fingerButton);
        locationTextView = findViewById(R.id.location_text);
        shareButton = findViewById(R.id.shareButton);
        weatherTextView = findViewById(R.id.weatherTextView);
        getLocationButton = findViewById(R.id.get_location_button);
        searchView = findViewById(R.id.searchView);
        timeTextView = findViewById(R.id.timeTextView);
        dateTextView = findViewById(R.id.dateTextView);
        weatherDescriptionText = findViewById(R.id.weatherDescription);

        feel = findViewById(R.id.feelPic);
        hum = findViewById(R.id.humPic);
        pre = findViewById(R.id.precipapic);
        windp = findViewById(R.id.windpic);
        cloud = findViewById(R.id.cloudpic);
        sunrise = findViewById(R.id.sunrisepic);
        sunset = findViewById(R.id.sunsetpic);
        uv = findViewById(R.id.uvpic);
        pressurep = findViewById(R.id.pressurepic);
        weatherphoto = findViewById(R.id.weatherImage);

        feelT = findViewById(R.id.feelsliket);
        humT = findViewById(R.id.humidity);
        preT = findViewById(R.id.preci);
        windT = findViewById(R.id.wind);
        cloudT = findViewById(R.id.cloud);
        sunriseT = findViewById(R.id.sunrise);
        sunsetT = findViewById(R.id.sunset);
        uvT = findViewById(R.id.uvindex);
        pressureT = findViewById(R.id.pressure);

        feelD = findViewById(R.id.feeldet);
        humD = findViewById(R.id.humdet);
        preD = findViewById(R.id.precidet);
        windD = findViewById(R.id.windet);
        cloudD = findViewById(R.id.clooddet);
        sunriseD = findViewById(R.id.sunrisedet);
        sunsetD = findViewById(R.id.sunsetdata);
        uvD = findViewById(R.id.uvdata);
        pressureD = findViewById(R.id.pressuredet);
        ImageView micIcon = findViewById(R.id.micIcon);

        micIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "Voice Search", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        getLocationButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "Current Location Weather", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        fingerbutton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "Scan for Current Location Weather", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        shareButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "Share Weather", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        getLocation(); // Call getLocation() when authentication is successful
                    }
                });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authenticate to get location")
                .setSubtitle("Place your finger on the sensor")
                .setNegativeButtonText("Cancel")
                .build();
        // Get current date and day of the week
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault());
        String currentDateAndDay = dateFormat.format(calendar.getTime());
        dateTextView.setText(currentDateAndDay); // Set the current date and day to the TextView

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateCurrentTime();
            }
        }, 0, 1000);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCity(query); // Perform search
                searchView.clearFocus(); // Clear focus from SearchView
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search as the text changes (optional)
                return false;
            }
        });
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        fingerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWeatherInfo();
            }
        });
    }
    public void errorHandling(){
        weatherTextView.setText("");
        weatherDescriptionText.setText("");
        weatherphoto.setImageResource(R.drawable.blank);
        feel.setImageResource(R.drawable.blank);
        hum.setImageResource(R.drawable.blank);
        pre.setImageResource(R.drawable.blank);
        windp.setImageResource(R.drawable.blank);
        cloud.setImageResource(R.drawable.blank);
        sunrise.setImageResource(R.drawable.blank);
        sunset.setImageResource(R.drawable.blank);
        uv.setImageResource(R.drawable.blank);
        pressurep.setImageResource(R.drawable.blank);
        feelT.setText("");
        humT.setText("");
        preT.setText("");
        windT.setText("");
        cloudT.setText("");
        sunriseT.setText("");
        sunsetT.setText("");
        uvT.setText("");
        pressureT.setText("");
        feelD.setText("");
        humD.setText("");
        preD.setText("");
        windD.setText("");
        cloudD.setText("");
        sunriseD.setText("");
        sunsetD.setText("");
        uvD.setText("");
        pressureD.setText("");
    }
    public void onVoiceSearchClick(View view) {
        startVoiceInput();
    }
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search...");
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && result.size() > 0) {
                String spokenText = result.get(0);
                // Use the spokenText for search or any other purpose
                searchView.setQuery(spokenText, true); // Set the spoken text as the search query
            }
        }
    }
    private void showShareButton() {
        shareButton.setVisibility(View.VISIBLE);
    }
    private void updateCurrentTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Get current time
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
                String currentTime = sdf.format(calendar.getTime());
                timeTextView.setText(currentTime);
            }
        });
    }
    private void searchCity(String cityName) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(cityName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();
                // Update location TextView
                locationTextView.setText("Hello "+cityName+"!");
                Animation fadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                locationTextView.startAnimation(fadeInAnimation);
                // Fetch weather data for the city
                fetchWeatherData(latitude, longitude);
            } else {
                locationTextView.setText("City not found");
                errorHandling();
            }
        } catch (IOException e) {
            e.printStackTrace();
            locationTextView.setText("Check your Internet Connection!");
            errorHandling();
        }
    }
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission has been granted, proceed to get location
            // Initialize location service
            LocationProvider locationProvider = new LocationProvider(this);
            // Get the current location
            Location currentLocation = locationProvider.getCurrentLocation();
            if (currentLocation != null) {
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();
                // Initialize Geocoder
                fetchWeatherData(latitude, longitude);
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    // Get address from coordinates
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        // Get the first address
                        Address address = addresses.get(0);
                        // Extract city name
                        String cityName = address.getLocality();
                        // Update UI with city name
                        if (cityName != null) {
                            locationTextView.setText("Hello "+cityName+"!");
                            Animation fadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                            locationTextView.startAnimation(fadeInAnimation);
                        } else {
                            locationTextView.setText("City name not found");
                            errorHandling();
                        }
                    } else {
                        locationTextView.setText("Location not found");
                        errorHandling();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    locationTextView.setText("Check your Internet Connection!");
                    errorHandling();
                }
            } else {
                locationTextView.setText("Turn on your GPS!");
                errorHandling();
            }
        }
    }
    private void fetchWeatherData(double latitude, double longitude) {
        String apiKey = "80034495************75354755f6e6"; // OpenWeatherMap API key
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey + "&units=metric";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse weather data
                            JSONObject main = response.getJSONObject("main");
                            double temperature = main.getDouble("temp");
                            JSONArray weatherArray = response.getJSONArray("weather");
                            JSONObject weatherObject = weatherArray.getJSONObject(0);
                            String description = weatherObject.getString("description");
                            description = capitalizeFirstLetter(description);

                            if (description.contains("Sunny")|| description.contains("Clear")) {
                                weatherphoto.setImageResource(R.drawable.clearsky);
                            } else if (description.contains("Haze")|| description.contains("Overcast")) {
                                weatherphoto.setImageResource(R.drawable.haze);
                            } else if (description.contains("Rain") || description.contains("Rainy") || description.contains("Shower") || description.contains("Drizzle")) {
                                weatherphoto.setImageResource(R.drawable.rain);
                            } else if (description.contains("Thunderstorm") ) {
                                weatherphoto.setImageResource(R.drawable.storm);
                            } else if (description.contains("Snow") ) {
                                weatherphoto.setImageResource(R.drawable.snow);
                            } else if (description.contains("Mist") || description.contains("Fog") ) {
                                weatherphoto.setImageResource(R.drawable.fog);
                            } else if (description.contains("Scattered") || description.contains("Broken") ) {
                                weatherphoto.setImageResource(R.drawable.cloud);
                            } else {
                                weatherphoto.setImageResource(R.drawable.defaultclouds); // Setting a default image if none of the conditions match
                            }
                            showShareButton();
                            double feelsLike = main.getDouble("feels_like");
                            int humidity = main.getInt("humidity");
                            double precipitation = 0.0; // Precipitation data not available in current weather API
                            JSONObject wind = response.getJSONObject("wind");
                            double windSpeed = wind.getDouble("speed");
                            int cloudiness = response.getJSONObject("clouds").getInt("all");
                            long sunriseTimestamp = response.getJSONObject("sys").getLong("sunrise") * 1000;
                            long sunsetTimestamp = response.getJSONObject("sys").getLong("sunset") * 1000;
                            double uvIndex = 0.0; // UV index data not available in current weather API
                            int pressure = main.getInt("pressure");

                            String feelsLikeString = Double.toString(feelsLike)+ "°C";
                            String humidityString = Integer.toString(humidity) + "%";
                            String precipitationString = "0.0"; // Indicate missing data
                            String windSpeedString = Double.toString(windSpeed) + " m/s"; // Add unit
                            String cloudinessString = Integer.toString(cloudiness) + "%"; // Add unit
                            String sunriseTimeString = new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date(sunriseTimestamp));
                            String sunsetTimeString = new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date(sunsetTimestamp));
                            String uvIndexString = "0.0"; // Indicate missing data
                            String airPressureString = Integer.toString(pressure) + " hPa"; // Add unit
                            // Update weather TextView
                            feel.setImageResource(R.drawable.feelslike);
                            hum.setImageResource(R.drawable.humidity);
                            pre.setImageResource(R.drawable.precipitation);
                            windp.setImageResource(R.drawable.wind);
                            cloud.setImageResource(R.drawable.cloudcover);
                            sunrise.setImageResource(R.drawable.sunrise);
                            sunset.setImageResource(R.drawable.sunset);
                            uv.setImageResource(R.drawable.uvindex);
                            pressurep.setImageResource(R.drawable.pressure);

                            feelT.setText("Feels Like");
                            humT.setText("Humidity");
                            preT.setText("Cloud Burst");
                            windT.setText("Wind Speed");
                            cloudT.setText("Cloud Cover");
                            sunriseT.setText("Sunrise");
                            sunsetT.setText("Sunset");
                            uvT.setText("UV Index");
                            pressureT.setText("Air Pressure");

                            feelD.setText(feelsLikeString);
                            humD.setText(humidityString);
                            preD.setText(precipitationString);
                            windD.setText(windSpeedString);
                            cloudD.setText(cloudinessString);
                            sunriseD.setText(sunriseTimeString);
                            sunsetD.setText(sunsetTimeString);
                            uvD.setText(uvIndexString);
                            pressureD.setText(airPressureString);
                            // Update weather TextView
                            String weatherInfo = temperature + "°C";
                            weatherTextView.setText(weatherInfo);
                            weatherDescriptionText.setText(description);
                            // Load the animation
                            Animation fadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                            // Apply the animation to the ImageView
                            weatherphoto.startAnimation(fadeInAnimation);
                            shareButton.startAnimation(fadeInAnimation);
                            // Apply the animation to all the views
                            feel.startAnimation(fadeInAnimation);
                            hum.startAnimation(fadeInAnimation);
                            pre.startAnimation(fadeInAnimation);
                            windp.startAnimation(fadeInAnimation);
                            cloud.startAnimation(fadeInAnimation);
                            sunrise.startAnimation(fadeInAnimation);
                            sunset.startAnimation(fadeInAnimation);
                            uv.startAnimation(fadeInAnimation);
                            pressurep.startAnimation(fadeInAnimation);

                            feelT.startAnimation(fadeInAnimation);
                            humT.startAnimation(fadeInAnimation);
                            preT.startAnimation(fadeInAnimation);
                            windT.startAnimation(fadeInAnimation);
                            cloudT.startAnimation(fadeInAnimation);
                            sunriseT.startAnimation(fadeInAnimation);
                            sunsetT.startAnimation(fadeInAnimation);
                            uvT.startAnimation(fadeInAnimation);
                            pressureT.startAnimation(fadeInAnimation);

                            feelD.startAnimation(fadeInAnimation);
                            humD.startAnimation(fadeInAnimation);
                            preD.startAnimation(fadeInAnimation);
                            windD.startAnimation(fadeInAnimation);
                            cloudD.startAnimation(fadeInAnimation);
                            sunriseD.startAnimation(fadeInAnimation);
                            sunsetD.startAnimation(fadeInAnimation);
                            uvD.startAnimation(fadeInAnimation);
                            pressureD.startAnimation(fadeInAnimation);

                            weatherTextView.startAnimation(fadeInAnimation);
                            weatherDescriptionText.startAnimation(fadeInAnimation);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorHandling();
                            locationTextView.setText("Check your Internet Connection!");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        errorHandling();
                        locationTextView.setText("Check your Internet Connection!");
                    }
                });
        // Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        // Split the text into words
        String[] words = text.split(" ");
        StringBuilder builder = new StringBuilder();
        // Capitalize the first letter of each word
        for (String word : words) {
            if (!word.isEmpty()) {
                builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        // Remove extra space at the end and return the result
        return builder.toString().trim();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                locationTextView.setText("Location permission denied.");
            }
        }
    }
    private void shareWeatherInfo() {
        String cityName= locationTextView.getText().toString();
        String temperatureInfo = weatherTextView.getText().toString();
        String descriptionInfo = weatherDescriptionText.getText().toString();
        String shareText=cityName+"\n"+temperatureInfo + "\n" + descriptionInfo;
        Intent i=new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT,shareText);
        i.setType("image/*");
        startActivity(i.createChooser(i,"Share Via"));
    }
}
