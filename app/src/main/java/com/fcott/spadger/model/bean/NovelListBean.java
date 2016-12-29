package com.fcott.spadger.model.bean;

import java.util.ArrayList;

/**
 * Created by fcott on 2016/12/27.
 */

public class NovelListBean {
    private ArrayList<NovelListItemBean> novelList = new ArrayList<>();
    private PageControlBean pageControlBean;

    public ArrayList<NovelListItemBean> getNovelList() {
        return novelList;
    }

    public void setNovelList(ArrayList<NovelListItemBean> novelList) {
        this.novelList = novelList;
    }

    public PageControlBean getPageControlBean() {
        return pageControlBean;
    }

    public void setPageControlBean(PageControlBean pageControlBean) {
        this.pageControlBean = pageControlBean;
    }
}
