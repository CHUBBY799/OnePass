package com.think.onepass.util;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

public class AppManager {
    public static List<Activity> activities=new LinkedList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static boolean containActivity(String activityName){
        for(Activity activity:activities){
            if(activity.getClass().getSimpleName().equals(activityName)){
                return true;
            }
        }
        return false;
    }
}
