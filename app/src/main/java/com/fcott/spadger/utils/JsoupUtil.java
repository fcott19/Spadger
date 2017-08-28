package com.fcott.spadger.utils;

import android.util.Log;

import com.fcott.spadger.model.bean.ItemBean;
import com.fcott.spadger.model.bean.MenuBean;
import com.fcott.spadger.model.bean.NovelListBean;
import com.fcott.spadger.model.bean.NovelListItemBean;
import com.fcott.spadger.model.bean.PageControlBean;
import com.fcott.spadger.model.bean.VedioListBean;
import com.fcott.spadger.model.bean.VedioListItemBean;
import com.fcott.spadger.ui.activity.TestActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.tencent.tinker.android.dex.util.FileUtils.readStream;

/**
 * Created by Administrator on 2016/9/27.
 */
public class JsoupUtil {

    public static void parseWangxiao(String response, TestActivity.loadCallBack loadCallBack){
        Document document = Jsoup.parse(response);
        Elements elements = document.getElementById("ClassHoursList").getElementsByClass("con-box");
        String title = null;
        for(Element e:elements){
            if(e.getElementsByClass("con-box").size() != 1){
                title = e.getElementsByClass("row").get(0).select("span").get(0).text();
            }else {
                Elements elements1 = e.getElementsByClass("car");
                LogUtil.log("小标题", e.getElementsByClass("row").get(0).select("span").get(0).text());
                LogUtil.log("小地址",elements1.select("a").attr("href"));

                try {
                    requestImageGet(title.replace(" ","_").trim(),e.getElementsByClass("row").get(0).select("span").get(0).text().replace(" ","_").trim(),elements1.select("a").attr("href").replace(" ","_").trim(),loadCallBack);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    // Get方式请求
    public static void requestImageGet(String a,String b,String u,TestActivity.loadCallBack loadCallBack) throws Exception {
        String path = "http://wap.wangxiao.cn"+u;
        // 新建一个URL对象
        URL url = new URL(path);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        // 设置连接超时时间
        urlConn.setConnectTimeout(5 * 1000);
        // 开始连接
        urlConn.connect();
        // 判断请求是否成功
        if (urlConn.getResponseCode() == 200) {
            // 获取返回的数据
            byte[] data = readStream(urlConn.getInputStream());
            String s =  new String(data, "UTF-8");
            Document document = Jsoup.parse(s);
            Elements elements = document.getElementById("divImg").select("img");
            int i = 0;
            for(Element element:elements){
                i++;
                LogUtil.log(element.attr("src"));
//                SDFileHelper helper = new SDFileHelper(App.getInstance());
//                helper.savePicture(a,b,i+"handout.jpg",element.attr("src"));
                loadCallBack.success(a,b,element.attr("src"),i+"handout.jpg");
            }
        }
        // 关闭连接
        urlConn.disconnect();
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
