package com.fcott.spadger.model.bean;

/**
 * Created by fcott on 2017/8/13.
 */

public class MoviePlayBean {

    /**
     * Result : 1
     * Message : http://lb2.iimovie.cc:1935/cmvod/_definst_/mp4:2017/3/0612/MDTM061/MDTM061-300k.mp4/playlist.m3u8?data=601A074340B7A1A6CE23F6D2DD5C133309CAA9FE4D92336049B51295739BAD869AD4AAEB522CC8ACDE7F22ED74E6F62CA51D32A5123A4581BE6FD17EED3AE4C27B610F822757BF4C3641B4787AAF8F19DE8602E3F221E729BC134F1A85A219E0&movieid=5037666
     * Code :
     */

    private int Result;
    private String Message;
    private String Code;

    public int getResult() {
        return Result;
    }

    public void setResult(int Result) {
        this.Result = Result;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }
}
