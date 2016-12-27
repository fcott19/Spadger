package com.fcott.spadger.model.bean;

import java.util.ArrayList;

/**
 * Created by fcott on 2016/12/27.
 */

public class VedioListBean {
    private ArrayList<VedioListItemBean> vedioList = new ArrayList<>();

    public ArrayList<VedioListItemBean> getVedioList() {
        return vedioList;
    }

    public void setVedioList(ArrayList<VedioListItemBean> vedioList) {
        this.vedioList = vedioList;
    }
}
