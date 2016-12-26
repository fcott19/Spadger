package com.fcott.spadger;

import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/27.
 */
public class JsoupUtil {

    public static void parseImagsTitle(String response) {
        Log.w("aaa","begin");
        Document document = Jsoup.parse(response);
        Elements elementsTp = document.getElementsByClass("tp").get(0).select("li");
        Elements elementsXs = document.getElementsByClass("xs").get(0).select("li");
        Elements elementsDy = document.getElementsByClass("dy").get(0).select("li");
        Elements newInfo = document.getElementsByClass("indexvod");

        //最新信息 0：电影 1：图片 2：小说
        for(int i = 0;i < newInfo.size();i++){
            Log.w("aaa",newInfo.get(i).select("a").toString());
            Log.w("aaa","------------");
        }
        //图片
        for(Element e : elementsTp){
            if(!e.select("a").attr("href").isEmpty() && !e.select("span").text().isEmpty()){
                Log.w("aaa",e.select("a").attr("href")+"-"+e.select("span").text());
            }
        }
        Log.w("aaa","------------");
        //小说
        for(Element e : elementsXs){
            if(!e.select("a").attr("href").isEmpty() && !e.select("span").text().isEmpty()){
                Log.w("aaa",e.select("a").attr("href")+"-"+e.select("span").text());
            }
        }
        Log.w("aaa","------------");
        //电影
        for(Element e : elementsDy){
            if(!e.select("a").attr("href").isEmpty() && !e.select("span").text().isEmpty()){
                Log.w("aaa",e.select("a").attr("href")+"-"+e.select("span").text());
            }
        }

    }

    public static void parseVideoList(String response){
        Log.w("aaa","begin");

        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByClass("indexvod").select("li");

        for(Element e:elements){
            Log.w("aaa",e.select("a").attr("href")+"--"+e.select("a").attr("title")+"--"+e.select("img").attr("src")+"--"+e.select("span").text());
        }
    }

    public static void parseVideoDetial(String response){
        Log.w("aaa","begin");

        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByClass("player").select("script");

        Log.w("aaa",elements.get(0).data());
    }


}
