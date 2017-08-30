package com.fcott.spadger.utils;

import android.util.Log;

import com.fcott.spadger.model.bean.ItemBean;
import com.fcott.spadger.model.bean.MenuBean;
import com.fcott.spadger.model.bean.NovelListBean;
import com.fcott.spadger.model.bean.NovelListItemBean;
import com.fcott.spadger.model.bean.PageControlBean;
import com.fcott.spadger.model.bean.VedioListBean;
import com.fcott.spadger.model.bean.VedioListItemBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/27.
 */
public class JsoupUtil {

    public static MenuBean parseYirenMenu(String response){
        MenuBean menuBean = new MenuBean();
        Document document = Jsoup.parse(response);
        Elements elements = document.select(".menu").select(".mt5");
        for(Element element:elements){
            Elements elements1 = element.select("a");
            String type = elements1.get(0).text();
            elements1.remove(0);
            if(type.equals("图片")){
                for(Element ele:elements1){
                    menuBean.getPicList().add(new ItemBean( ele.text(),ele.attr("href")));
                }
            }else if(type.equals("小说")){
                for(Element ele:elements1){
                    menuBean.getNovelList().add(new ItemBean( ele.text(),ele.attr("href")));
                }
            }else if(type.equals("电影")){
                for(Element ele:elements1){
                    menuBean.getVedioList().add(new ItemBean( ele.text(),ele.attr("href")));
                }
            }
        }
        return menuBean;
    }

    public static NovelListBean parseYirenList(String response){
        NovelListBean novelListBean = new NovelListBean();
        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByClass("textList").select("a");
        for(Element element:elements){
            LogUtil.log(element.attr("href")+"--"+element.attr("title")+"--"+element.text());
            novelListBean.getNovelList().add(new NovelListItemBean(element.attr("title"),element.attr("href"),""));
        }
        Element pageElement = document.getElementsByClass("pages").get(0);
        String totalPage = pageElement.select("strong").get(0).text();

        novelListBean.setPageControlBean(new PageControlBean(null,
                null,
                null,
                null,
                null,
                null,totalPage));
        return novelListBean;
    }

    public static ArrayList<String> parseYirenPic(String response){
        ArrayList<String> arrayList = new ArrayList<>();
        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByClass("novelContent").select("img");
        for(Element element:elements){
            arrayList.add(element.attr("src"));
        }
        return arrayList;
    }

    public static String parseYirenNovelDetial(String response){

        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByClass("novelContent").select("td");

        return elements.get(0).text();
    }

    public static VedioListBean parseYirenVideo(String response){
        VedioListBean vedioListBean = new VedioListBean();
        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByClass("movieList").select("li");
        for(Element element:elements){
            LogUtil.log(element.attr("href")+"--"+element.attr("title")+"--"+element.text());
            vedioListBean.getVedioList().add(new VedioListItemBean(element.select("h3").text(),element.select("a").attr("href"),element.select("img").attr("src"),element.select("span").text()));
        }

        Elements pageElement = document.getElementsByClass("pages");
        String totalPage;
        if(pageElement.size() == 0)
            totalPage = "1";
        else
            totalPage = pageElement.get(0).select("strong").get(0).text();
        vedioListBean.setPageControlBean(new PageControlBean(null,
                null,
                null,
                null,
                null,
                null,totalPage));
        return vedioListBean;
    }
    public static String parseYirenVideoUrl(String response){

        Document document = Jsoup.parse(response);
//        Element element = document.getElementById("videobox");
//        LogUtil.log(element.attr("classid") +"--" +element.attr("xxid"));

//        String title = document.getElementsByTag("title").text();
        String[] scripts = document.getElementsByTag("script").toString().split("http");
        if(scripts == null || scripts.length < 2)
            return null;
        return "http"+scripts[1].split("mp4")[0]+"mp4";
    }

    public static VedioListBean parseYirenSearch(String response){
        VedioListBean vedioListBean = new VedioListBean();
        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByClass("textList").select("li");
        for(Element element:elements){
            LogUtil.log(element.attr("href")+"--"+element.attr("title")+"--"+element.text());
            vedioListBean.getVedioList().add(new VedioListItemBean(element.select("h3").text(),element.select("a").attr("href"),element.select("img").attr("src"),element.select("span").text()));
        }
        Elements pageElement = document.getElementsByClass("pages");
        String totalPage;
        String jumpUrl = null;
        if(pageElement.size() == 0)
            totalPage = "1";
        else{
            totalPage = pageElement.get(0).select("strong").get(0).text();
            jumpUrl = document.getElementsByClass("pageList").toString().split("searchid=")[1].split("\"")[0];
        }
        vedioListBean.setPageControlBean(new PageControlBean(null,
                null,
                null,
                null,
                null,
                jumpUrl,totalPage));
        return vedioListBean;
    }
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
                menuBean.getPicList().add(new ItemBean( e.select("span").text(),e.select("a").attr("href") ));
            }
        }
        //小说
        for(Element e : elementsXs){
            if(!e.select("a").attr("href").isEmpty() && !e.select("span").text().isEmpty()){
                menuBean.getNovelList().add(new ItemBean( e.select("span").text(),e.select("a").attr("href") ));
            }
        }
        //电影
        for(Element e : elementsDy){
            if(!e.select("a").attr("href").isEmpty() && !e.select("span").text().isEmpty()){
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
            vedioListBean.getVedioList().add(new VedioListItemBean(e.select("a").attr("title"),e.select("a").attr("href")
                    ,e.select("img").attr("src"),e.select("span").text()));
        }

        Element page = document.getElementsByClass("page").get(0);
        String s = page.select("input ").attr("onclick").replace("pagego('","").replace(")","");
        String info[] = s.split("',");
        vedioListBean.setPageControlBean(new PageControlBean(page.select("span").text(),
                page.getElementsContainingText("首页").attr("href"),
                page.getElementsContainingText("尾页").attr("href"),
                page.getElementsContainingText("下一页").attr("href"),
                page.getElementsContainingText("上一页").attr("href"),
                info[0],info[1]));
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
            realUrl = ParseUtil.parseUrl(NativeUtil.ascii2Native(string.substring(index1,index2)+"m3u8").replace("\\",""));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LogUtil.log(realUrl);
        return realUrl;
    }

    public static NovelListBean parseNovelList(String response){
        NovelListBean novelListBean = new NovelListBean();

        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByClass("artlist").select("ul");
        for(Element element:elements){
            novelListBean.getNovelList().add(new NovelListItemBean(element.select("a").attr("title"),element.select("a").attr("href"),element.select("font").text()));
        }

        Element page = document.getElementsByClass("page").get(0);
        String s = page.select("input ").attr("onclick").replace("pagego('","").replace(")","");
        String info[] = s.split("',");
        novelListBean.setPageControlBean(new PageControlBean(page.select("span").text(),
                page.getElementsContainingText("首页").attr("href"),
                page.getElementsContainingText("尾页").attr("href"),
                page.getElementsContainingText("下一页").attr("href"),
                page.getElementsContainingText("上一页").attr("href"),
                info[0],info[1]));

        return novelListBean;
    }

    public static String parseNovelDetial(String response){

        Document document = Jsoup.parse(response);
        Elements elements = document.getElementsByClass("cont");

        return elements.text();
    }

    public static ArrayList<String> parsePictureDetial(String response){
        ArrayList<String> arrayList = new ArrayList<>();
        Document document = Jsoup.parse(response);
        Elements elements = document.getElementById("postmessage").select("img");
        for (Element element:elements){
            arrayList.add(element.attr("src"));
        }
        return arrayList;
    }


}
