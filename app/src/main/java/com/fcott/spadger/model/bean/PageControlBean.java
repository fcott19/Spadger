package com.fcott.spadger.model.bean;

/**
 * Created by Administrator on 2016/12/29.
 */

public class PageControlBean{
    private String currentPage = "";//当前页
    private String firstPageUrl = "";//第一页
    private String lastPageUrl = "";//最后一页
    private String nextPageUrl = "";//下一页
    private String prePageUrl = "";//上一页
    private String jumpUrl = "";//跳转url
    private int totalPage = 1;

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public PageControlBean(String currentPage, String firstPageUrl, String lastPageUrl, String nextPageUrl, String prePageUrl, String jumpUrl, String totalPage){
        this.currentPage = currentPage;
        this.firstPageUrl = firstPageUrl;
        this.lastPageUrl = lastPageUrl;
        this.nextPageUrl =nextPageUrl;
        this.prePageUrl = prePageUrl;
        this.jumpUrl = jumpUrl;
        try {
            this.totalPage = Integer.valueOf(totalPage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getTotalPage() {
        return totalPage;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getFirstPageUrl() {
        return firstPageUrl;
    }

    public void setFirstPageUrl(String firstPageUrl) {
        this.firstPageUrl = firstPageUrl;
    }

    public String getLastPageUrl() {
        return lastPageUrl;
    }

    public void setLastPageUrl(String lastPageUrl) {
        this.lastPageUrl = lastPageUrl;
    }

    public String getNextPage() {
        return nextPageUrl;
    }

    public void setNextPage(String nextPage) {
        this.nextPageUrl = nextPage;
    }

    public String getPrePage() {
        return prePageUrl;
    }

    public void setPrePage(String prePage) {
        this.prePageUrl = prePage;
    }
}
