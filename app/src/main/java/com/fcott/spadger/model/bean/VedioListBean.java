package com.fcott.spadger.model.bean;

import java.util.ArrayList;

/**
 * Created by fcott on 2016/12/27.
 */

public class VedioListBean {
    private ArrayList<VedioListItemBean> vedioList = new ArrayList<>();
    private PageControlBean pageControlBean;

    public ArrayList<VedioListItemBean> getVedioList() {
        return vedioList;
    }

    public void setVedioList(ArrayList<VedioListItemBean> vedioList) {
        this.vedioList = vedioList;
    }

    public PageControlBean getPageControlBean() {
        return pageControlBean;
    }

    public void setPageControlBean(PageControlBean pageControlBean) {
        this.pageControlBean = pageControlBean;
    }

}
