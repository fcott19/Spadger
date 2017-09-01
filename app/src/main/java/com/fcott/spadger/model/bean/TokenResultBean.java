package com.fcott.spadger.model.bean;

/**
 * Created by Administrator on 2017/9/1.
 */

public class TokenResultBean {

    /**
     * Result : 1
     * Message : {"StartTime":"2017/09/01 11:57:06","EndTime":"2017/09/01 23:57:06","Token":"794B4AB93BFB46F2A554FADFE446EDAF"}
     * Code :
     */

    private int Result;
    private MessageBean Message;
    private String Code;

    public int getResult() {
        return Result;
    }

    public void setResult(int Result) {
        this.Result = Result;
    }

    public MessageBean getMessage() {
        return Message;
    }

    public void setMessage(MessageBean Message) {
        this.Message = Message;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public static class MessageBean {
        /**
         * StartTime : 2017/09/01 11:57:06
         * EndTime : 2017/09/01 23:57:06
         * Token : 794B4AB93BFB46F2A554FADFE446EDAF
         */

        private String StartTime;
        private String EndTime;
        private String Token;

        public String getStartTime() {
            return StartTime;
        }

        public void setStartTime(String StartTime) {
            this.StartTime = StartTime;
        }

        public String getEndTime() {
            return EndTime;
        }

        public void setEndTime(String EndTime) {
            this.EndTime = EndTime;
        }

        public String getToken() {
            return Token;
        }

        public void setToken(String Token) {
            this.Token = Token;
        }
    }
}
