package com.fcott.spadger.model.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class ActorBean {

    /**
     * Result : 1
     * Message : {"Total":7350,"PageCount":368,"Data":[{"Pic":"http://hdimg.xxtvphoto.com/Actor/1.jpg","Count":"213","ID":"1","Name":"波多野結衣"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/2.jpg","Count":"114","ID":"2","Name":"上原亜衣"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/3.jpg","Count":"41","ID":"3","Name":"桜井あゆ"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/4.jpg","Count":"58","ID":"4","Name":"神波多一花"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/5.jpg","Count":"10","ID":"5","Name":"椎名ゆな"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/6.jpg","Count":"39","ID":"6","Name":"小早川怜子"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/7.jpg","Count":"5","ID":"7","Name":"风间ゆみ"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/8.jpg","Count":"87","ID":"8","Name":"浜崎真緒"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/9.jpg","Count":"106","ID":"9","Name":"大槻ひびき"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/10.jpg","Count":"14","ID":"10","Name":"結城みさ"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/11.jpg","Count":"16","ID":"11","Name":"筱田あゆみ"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/12.jpg","Count":"73","ID":"12","Name":"本田莉子"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/13.jpg","Count":"17","ID":"13","Name":"湊莉久"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/14.jpg","Count":"85","ID":"14","Name":"川上ゆう(森野雫)"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/15.jpg","Count":"0","ID":"15","Name":"佐藤遥希"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/16.jpg","Count":"54","ID":"16","Name":"北条麻妃"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/17.jpg","Count":"38","ID":"17","Name":"有村千佳"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/18.jpg","Count":"45","ID":"18","Name":"翔田千里"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/19.jpg","Count":"27","ID":"19","Name":"保坂えり"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/20.jpg","Count":"16","ID":"20","Name":"吉泽明步"}]}
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
         * Total : 7350
         * PageCount : 368
         * Data : [{"Pic":"http://hdimg.xxtvphoto.com/Actor/1.jpg","Count":"213","ID":"1","Name":"波多野結衣"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/2.jpg","Count":"114","ID":"2","Name":"上原亜衣"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/3.jpg","Count":"41","ID":"3","Name":"桜井あゆ"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/4.jpg","Count":"58","ID":"4","Name":"神波多一花"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/5.jpg","Count":"10","ID":"5","Name":"椎名ゆな"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/6.jpg","Count":"39","ID":"6","Name":"小早川怜子"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/7.jpg","Count":"5","ID":"7","Name":"风间ゆみ"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/8.jpg","Count":"87","ID":"8","Name":"浜崎真緒"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/9.jpg","Count":"106","ID":"9","Name":"大槻ひびき"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/10.jpg","Count":"14","ID":"10","Name":"結城みさ"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/11.jpg","Count":"16","ID":"11","Name":"筱田あゆみ"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/12.jpg","Count":"73","ID":"12","Name":"本田莉子"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/13.jpg","Count":"17","ID":"13","Name":"湊莉久"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/14.jpg","Count":"85","ID":"14","Name":"川上ゆう(森野雫)"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/15.jpg","Count":"0","ID":"15","Name":"佐藤遥希"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/16.jpg","Count":"54","ID":"16","Name":"北条麻妃"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/17.jpg","Count":"38","ID":"17","Name":"有村千佳"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/18.jpg","Count":"45","ID":"18","Name":"翔田千里"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/19.jpg","Count":"27","ID":"19","Name":"保坂えり"},{"Pic":"http://hdimg.xxtvphoto.com/Actor/20.jpg","Count":"16","ID":"20","Name":"吉泽明步"}]
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
             * Pic : http://hdimg.xxtvphoto.com/Actor/1.jpg
             * Count : 213
             * ID : 1
             * Name : 波多野結衣
             */

            private String Pic;
            private String Count;
            private String ID;
            private String Name;

            public String getPic() {
                return Pic;
            }

            public void setPic(String Pic) {
                this.Pic = Pic;
            }

            public String getCount() {
                return Count;
            }

            public void setCount(String Count) {
                this.Count = Count;
            }

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
