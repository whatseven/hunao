package com.example.hw9.ui.home;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.collection.LruCache;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<NewsData> mNewsInfo;
    private List<String> weatherResult;
    private MutableLiveData<List<String>> mWeatherInfo;

//    public HomeViewModel() {
//        mText = new MutableLiveData<>();
//        mText.setValue("This is home fragment");
//    }
    private Context context;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<NewsData> getNewsList(){
        if(mNewsInfo == null){
            mNewsInfo = new MutableLiveData<NewsData>();
            loadNewsData();
        }else{
            loadNewsData();
        }
        return mNewsInfo;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadNewsData(){
        final NewsData results = new NewsData();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://10.0.2.2:3000/news/home";                         ///////////// Fix after deployment /////////////
        results.setImageLoader(queue,  new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray titles = response.getJSONArray("titles");
                            JSONArray ids = response.getJSONArray("ids");
                            JSONArray sections = response.getJSONArray("sections");
                            JSONArray times = response.getJSONArray("times");
                            JSONArray images = response.getJSONArray("images");

                            int length = titles.length();
                            for(int i = 0; i < length; ++i){
                                results.appendTitle((String) titles.get(i));
                                results.appendTime((String) times.get(i));
                                results.appendIds((String) ids.get(i));
                                results.appendImage((String) images.get(i));
                                results.appendSection((String) sections.get(i));
                            }
                            mNewsInfo.setValue(results);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println("Backend: " + error);
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public LiveData<List<String>> getWeatherInfo(String city,String state){

        if(mWeatherInfo == null){
            mWeatherInfo = new MutableLiveData<>();
            loadWeatherData(city, state);
        }
        return mWeatherInfo;
    }

    private void loadWeatherData(final String city, final String state){
        weatherResult = new ArrayList<String>();
        final String KEY = "eb36b3d40f82ad92fe78f48dc8bbe27e";
        // Instantiate the RequestQueue.
        String url="https://api.openweathermap.org/data/2.5/weather?q="+ city + "&units=metric&appid=" + KEY;
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String temp = Integer.toString((int)Math.round(response.getJSONObject("main").getDouble("temp"))) ;
                            String weather = response.getJSONArray("weather").getJSONObject(0).getString("main");
                            weatherResult.add(city);
                            weatherResult.add(state);
                            weatherResult.add(temp);
                            weatherResult.add(weather);
                            mWeatherInfo.setValue(weatherResult);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println("Cannot get response from openWeather");
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}