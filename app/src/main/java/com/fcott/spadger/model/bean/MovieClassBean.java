package com.fcott.spadger.model.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/14.
 */

public class MovieClassBean {

    /**
     * Result : 1
     * Message : {"Total":298,"PageCount":5,"Data":[{"ID":"2","Name":"巨乳"},{"ID":"3","Name":"人妻"},{"ID":"4","Name":"內射"},{"ID":"5","Name":"褲襪"},{"ID":"6","Name":"美少女"},{"ID":"7","Name":"熟女"},{"ID":"8","Name":"企劃"},{"ID":"9","Name":"出軌"},{"ID":"10","Name":"大姊姊"},{"ID":"11","Name":"激烈淫亂"},{"ID":"12","Name":"潮吹"},{"ID":"13","Name":"各種職業"},{"ID":"14","Name":"窈窕系"},{"ID":"16","Name":"美臀癖"},{"ID":"17","Name":"偷拍實錄 偷窺"},{"ID":"18","Name":"女教師"},{"ID":"19","Name":"洋妞"},{"ID":"20","Name":"素人演出"},{"ID":"21","Name":"痴女"},{"ID":"22","Name":"姊妹"},{"ID":"23","Name":"修長"},{"ID":"24","Name":"蘿莉系"},{"ID":"25","Name":"劇情"},{"ID":"26","Name":"雜交"},{"ID":"27","Name":"蕾絲邊"},{"ID":"28","Name":"上班族"},{"ID":"29","Name":"立即性交"},{"ID":"30","Name":"凌辱"},{"ID":"31","Name":"cosplay"},{"ID":"32","Name":"按摩"},{"ID":"33","Name":"痴漢"},{"ID":"34","Name":"調教,奴隸"},{"ID":"35","Name":"輪姦"},{"ID":"36","Name":"母乳"},{"ID":"37","Name":"戀腳癖"},{"ID":"38","Name":"家庭教師"},{"ID":"39","Name":"四小時以上作品"},{"ID":"40","Name":"女子校生"},{"ID":"41","Name":"白虎"},{"ID":"42","Name":"酒店小姐"},{"ID":"43","Name":"騎乘位"},{"ID":"44","Name":"野外露出"},{"ID":"45","Name":"內衣"},{"ID":"46","Name":"3P・4P"},{"ID":"47","Name":"主觀視野"},{"ID":"48","Name":"搭訕"},{"ID":"49","Name":"紀錄影片"},{"ID":"50","Name":"黑人男優"},{"ID":"51","Name":"空姐"},{"ID":"52","Name":"新娘・嫩妻"},{"ID":"53","Name":"羞恥"},{"ID":"54","Name":"乳交"},{"ID":"55","Name":"出道作品"},{"ID":"56","Name":"淫語"},{"ID":"57","Name":"戀足 足交"},{"ID":"58","Name":"第一人稱視野"},{"ID":"59","Name":"千金,大小姐"},{"ID":"60","Name":"巨乳癖"},{"ID":"61","Name":"辣妹"},{"ID":"62","Name":"美乳"}]}
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
         * Total : 298
         * PageCount : 5
         * Data : [{"ID":"2","Name":"巨乳"},{"ID":"3","Name":"人妻"},{"ID":"4","Name":"內射"},{"ID":"5","Name":"褲襪"},{"ID":"6","Name":"美少女"},{"ID":"7","Name":"熟女"},{"ID":"8","Name":"企劃"},{"ID":"9","Name":"出軌"},{"ID":"10","Name":"大姊姊"},{"ID":"11","Name":"激烈淫亂"},{"ID":"12","Name":"潮吹"},{"ID":"13","Name":"各種職業"},{"ID":"14","Name":"窈窕系"},{"ID":"16","Name":"美臀癖"},{"ID":"17","Name":"偷拍實錄 偷窺"},{"ID":"18","Name":"女教師"},{"ID":"19","Name":"洋妞"},{"ID":"20","Name":"素人演出"},{"ID":"21","Name":"痴女"},{"ID":"22","Name":"姊妹"},{"ID":"23","Name":"修長"},{"ID":"24","Name":"蘿莉系"},{"ID":"25","Name":"劇情"},{"ID":"26","Name":"雜交"},{"ID":"27","Name":"蕾絲邊"},{"ID":"28","Name":"上班族"},{"ID":"29","Name":"立即性交"},{"ID":"30","Name":"凌辱"},{"ID":"31","Name":"cosplay"},{"ID":"32","Name":"按摩"},{"ID":"33","Name":"痴漢"},{"ID":"34","Name":"調教,奴隸"},{"ID":"35","Name":"輪姦"},{"ID":"36","Name":"母乳"},{"ID":"37","Name":"戀腳癖"},{"ID":"38","Name":"家庭教師"},{"ID":"39","Name":"四小時以上作品"},{"ID":"40","Name":"女子校生"},{"ID":"41","Name":"白虎"},{"ID":"42","Name":"酒店小姐"},{"ID":"43","Name":"騎乘位"},{"ID":"44","Name":"野外露出"},{"ID":"45","Name":"內衣"},{"ID":"46","Name":"3P・4P"},{"ID":"47","Name":"主觀視野"},{"ID":"48","Name":"搭訕"},{"ID":"49","Name":"紀錄影片"},{"ID":"50","Name":"黑人男優"},{"ID":"51","Name":"空姐"},{"ID":"52","Name":"新娘・嫩妻"},{"ID":"53","Name":"羞恥"},{"ID":"54","Name":"乳交"},{"ID":"55","Name":"出道作品"},{"ID":"56","Name":"淫語"},{"ID":"57","Name":"戀足 足交"},{"ID":"58","Name":"第一人稱視野"},{"ID":"59","Name":"千金,大小姐"},{"ID":"60","Name":"巨乳癖"},{"ID":"61","Name":"辣妹"},{"ID":"62","Name":"美乳"}]
         */

        private int Total;
        private int PageCount;
        private List<DataBean> Data;

        public int getTotal() {
            return Total;
        }

        public void setTotal(int Total) {
            this.Total = Total;
        }

        public int getPageCount() {
            return PageCount;
        }

        public void setPageCount(int PageCount) {
            this.PageCount = PageCount;
        }

        public List<DataBean> getData() {
            return Data;
        }

        public void setData(List<DataBean> Data) {
            this.Data = Data;
        }

        public static class DataBean {
            /**
             * ID : 2
             * Name : 巨乳
             */

            private String ID;
            private String Name;

            public String getID() {
                return ID;
            }

            public void setID(String ID) {
                this.ID = ID;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }
        }
    }
}
