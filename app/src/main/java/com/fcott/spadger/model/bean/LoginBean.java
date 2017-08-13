package com.fcott.spadger.model.bean;

/**
 * Created by fcott on 2017/8/13.
 */

public class LoginBean {

    /**
     * Result : 1
     * Message : null
     * Code :
     */

    private int Result;
    private Object Message;
    private String Code;

    public int getResult() {
        return Result;
    }

    public void setResult(int Result) {
        this.Result = Result;
    }

    public Object getMessage() {
        return Message;
    }

    public void setMessage(Object Message) {
        this.Message = Message;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }
}
