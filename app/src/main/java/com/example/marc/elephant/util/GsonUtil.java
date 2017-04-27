package com.example.marc.elephant.util;

import com.google.gson.Gson;
import com.example.marc.elephant.gson.Content;
import com.example.marc.elephant.gson.GuoKeList;
import com.example.marc.elephant.gson.YigeList;
import com.example.marc.elephant.gson.Zhihu2List;
import com.example.marc.elephant.gson.ZhihuList;

/**
 * Created by marc on 17-4-20.
 */

public class GsonUtil {
    public static ZhihuList TodayUtil(String json){
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, ZhihuList.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static Content ZhihuContentUtil(String json){
        try {
            Gson gson=new Gson();
            return gson.fromJson(json,Content.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static GuoKeList GuokeUtil(String json){
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, GuoKeList.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static YigeList yigeUtil(String json){
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, YigeList.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static Zhihu2List zhihu2Util(String json){
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, Zhihu2List.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
