package com.fcott.spadger.utils;

import android.text.TextUtils;
import android.util.Log;

import com.fcott.spadger.model.bean.ItemBean;
import com.fcott.spadger.model.bean.MenuBean;
import com.fcott.spadger.model.bean.VedioListBean;
import com.fcott.spadger.model.bean.VedioListItemBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/27.
 */
public class JsoupUtil {

    /**
     * 解析菜单（导航）
     * @param response
     */
    public static MenuBean parseMenu(String response) {
        MenuBean menuBean = new MenuBean();
        Document document = Jsoup.parse(response);
        Elements elementsTp = document.getElementsByClass("tp").get(0).select("li");
        Elements elementsXs = document.getElementsByClass("xs").get(0).select("li");
        Elements elementsDy = document.getElementsByClass("dy").get(0).select("li");
        Elements newInfo = document.getElementsByClass("indexvod");

        //最新信息 0：电影 1：图片 2：小说
        for(int i = 0;i < newInfo.size();i++){
//            Log.w("aaa",newInfo.get(i).select("a").toString());
            Log.w("aaa","------------");
            Elements e = newInfo.get(i).select("a");
            switch (i){
                case 0:
                    for(Element element:e){
                        menuBean.getNewVedioList().add(new ItemBean(element.attr("title"),element.attr("href")));
                    }
                    break;
                case 1:
                    for(Element element:e){
                        menuBean.getNewpicList().add(new ItemBean(element.attr("title"),element.attr("href")));
                    }
                    break;
                case 2:
                    for(Element element:e){
                        menuBean.getNewNovelList().add(new ItemBean(element.attr("title"),element.attr("href")));
                    }
                    break;
            }
        }
        //图片
        for(Element e : elementsTp){
            if(!e.select("a").attr("href").isEmpty() && !e.select("span").text().isEmpty()){
//                Log.w("aaa",e.select("a").attr("href")+"-"+e.select("span").text());
                menuBean.getPicList().add(new ItemBean( e.select("span").text(),e.select("a").attr("href") ));
            }
        }
        Log.w("aaa","------------");
        //小说
        for(Element e : elementsXs){
            if(!e.select("a").attr("href").isEmpty() && !e.select("span").text().isEmpty()){
//                Log.w("aaa",e.select("a").attr("href")+"-"+e.select("span").text());
                menuBean.getNovelList().add(new ItemBean( e.select("span").text(),e.select("a").attr("href") ));
            }
        }
        Log.w("aaa","------------");
        //电影
        for(Element e : elementsDy){
            if(!e.select("a").attr("href").isEmpty() && !e.select("span").text().isEmpty()){
//                Log.w("aaa",e.select("a").attr("href")+"-"+e.select("span").text());
                menuBean.getVedioList().add(new ItemBean( e.select("span").text(),e.select("a").attr("href") ));
            }
        }

        return menuBean;
    }

    public static VedioListBean parseVideoList(String response){
        VedioListBean vedioListBean = new VedioListBean();

        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByClass("indexvod").select("li");

        for(Element e:elements){
            Log.w("aaa",e.select("a").attr("href")+"--"+e.select("a").attr("title")+"--"+e.select("img").attr("src")+"--"+e.select("span").text());
            vedioListBean.getVedioList().add(new VedioListItemBean(e.select("a").attr("title"),e.select("a").attr("href")
                    ,e.select("img").attr("src"),e.select("span").text()));
        }
        return vedioListBean;
    }

    public static String parseVideoDetial(String response){
        Log.w("aaa","begin");

        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByClass("player").select("script");
        String string = elements.get(0).data();
        int index1 = string.indexOf("http");
        int index2 = string.indexOf("m3u8");

        String realUrl = "";
        try {
            realUrl = ParseUtil.parseUrl(Native.ascii2Native(string.substring(index1,index2)+"m3u8").replace("\\",""));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtil.log(realUrl);
        return realUrl;
    }


}
