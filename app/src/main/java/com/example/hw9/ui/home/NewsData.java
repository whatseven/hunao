package com.example.hw9.ui.home;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewsData {
    public static List<String> titles;
    public static List<String> imgPaths;
    public static List<String> times;
    public static List<String> ids;
    public static List<String> sections;
    public static ImageLoader mImageLoader;
    private static ZonedDateTime zdt_currentTime;

    @RequiresApi(api = Build.VERSION_CODES.O)
    NewsData(){
        titles = new ArrayList<String>();
        times = new ArrayList<String>();
        imgPaths = new ArrayList<String>();
        ids = new ArrayList<String>();
        sections = new ArrayList<String>();
        // Initial Current Time
        zdt_currentTime = ZonedDateTime.now( ZoneId.of("America/Los_Angeles"));
    }
    public void appendTitle(String title){
        titles.add(title);
    }
    public void appendImage(String image){
        imgPaths.add(image);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void appendTime(String time){
        times.add(this.getTimeDifference(time));
    }
    public void appendIds(String id){
        ids.add(id);
    }
    public void appendSection(String section) {
        sections.add(section);
    }

    public void setImageLoader(RequestQueue queue, ImageLoader.ImageCache cache){
        mImageLoader = new ImageLoader(queue, cache);
    }

    public ImageLoader getImageLoader(){
        return mImageLoader;
    }

    public int getLength(){
        return times.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getTimeDifference(String time){
        LocalDateTime thisTime = LocalDateTime.parse(time);
        ZonedDateTime zdt_thisTime_ = ZonedDateTime.of(thisTime, ZoneId.of("UTC+0"));
        ZonedDateTime zdt_thisTime =zdt_thisTime_.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
        Duration d = Duration.between(zdt_thisTime, zdt_currentTime);
        long second =  d.getSeconds();
        if(second < 60){
            return String.valueOf(second) + "s ago";
        }else{
            long minute = d.toMinutes();
            if(minute < 60){
                return String.valueOf(minute) + "m ago";
            }else{
                long hour = d.toHours();
                return String.valueOf(hour) + "h ago";
            }
        }
    }
}
