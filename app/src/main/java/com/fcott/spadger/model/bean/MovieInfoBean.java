package com.fcott.spadger.model.bean;

import java.util.List;

/**
 * Created by fcott on 2017/8/13.
 */

public class MovieInfoBean {

    /**
     * Result : 1
     * Message : {"MovieID":"5037666","Name":"女子校生緊縛懷孕餵養 上原亜衣","Description":"放學路上被導師埋伏的亞衣。以補習的名目被帶到老師家裡，而一點懷疑都沒有。從那裡開始女高中生的悲劇開始了卻一點也不知道...。由藥劑從昏睡到清醒後，就被緊縛奪走了自由。被盡情玩弄變成繩子的俘虜。連在學校惡作劇依舊持續...變成了校內男人們的奴隸。","CreateTime":"2017/06/12","Img":"http://hdimg.xxtvphoto.com/PIC/2017/3/0612/MDTM061/MDTM061.jpg","CoverImg":"http://hdimg.xxtvphoto.com/PIC/2017/3/0612/MDTM061/MDTM061m.jpg","CutPicName":"http://hdimg.xxtvphoto.com/PIC/2017/3/0612/MDTM061/MDTM061.mp4.jpg","Length":"","FileSize":"","Resolution":"","Channel":{"ID":"3","Name":"有碼"},"Class":[{"ID":4,"Name":"內射"},{"ID":6,"Name":"美少女"},{"ID":40,"Name":"女子校生"},{"ID":66,"Name":"綑綁"},{"ID":92,"Name":"綑綁．緊縛"}],"Supplier":{"ID":"33","Name":"sonyaibo"},"Series":{"ID":"","Name":""},"Actor":[{"ID":2,"Name":"上原亜衣","Pic":"2.jpg"}],"Point":"29","Download":"39"}
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
         * MovieID : 5037666
         * Name : 女子校生緊縛懷孕餵養 上原亜衣
         * Description : 放學路上被導師埋伏的亞衣。以補習的名目被帶到老師家裡，而一點懷疑都沒有。從那裡開始女高中生的悲劇開始了卻一點也不知道...。由藥劑從昏睡到清醒後，就被緊縛奪走了自由。被盡情玩弄變成繩子的俘虜。連在學校惡作劇依舊持續...變成了校內男人們的奴隸。
         * CreateTime : 2017/06/12
         * Img : http://hdimg.xxtvphoto.com/PIC/2017/3/0612/MDTM061/MDTM061.jpg
         * CoverImg : http://hdimg.xxtvphoto.com/PIC/2017/3/0612/MDTM061/MDTM061m.jpg
         * CutPicName : http://hdimg.xxtvphoto.com/PIC/2017/3/0612/MDTM061/MDTM061.mp4.jpg
         * Length :
         * FileSize :
         * Resolution :
         * Channel : {"ID":"3","Name":"有碼"}
         * Class : [{"ID":4,"Name":"內射"},{"ID":6,"Name":"美少女"},{"ID":40,"Name":"女子校生"},{"ID":66,"Name":"綑綁"},{"ID":92,"Name":"綑綁．緊縛"}]
         * Supplier : {"ID":"33","Name":"sonyaibo"}
         * Series : {"ID":"","Name":""}
         * Actor : [{"ID":2,"Name":"上原亜衣","Pic":"2.jpg"}]
         * Point : 29
         * Download : 39
         */

        private String MovieID;
        private String Name;
        private String Description;
        private String CreateTime;
        private String Img;
        private String CoverImg;
        private String CutPicName;
        private String Length;
        private String FileSize;
        private String Resolution;
        private ChannelBean Channel;
        private SupplierBean Supplier;
        private SeriesBean Series;
        private String Point;
        private String Download;
        private List<ClassBean> Class;
        private List<ActorBean> Actor;

        public String getMovieID() {
            return MovieID;
        }

        public void setMovieID(String MovieID) {
            this.MovieID = MovieID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String Description) {
            this.Description = Description;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public String getImg() {
            return Img;
        }

        public void setImg(String Img) {
            this.Img = Img;
        }

        public String getCoverImg() {
            return CoverImg;
        }

        public void setCoverImg(String CoverImg) {
            this.CoverImg = CoverImg;
        }

        public String getCutPicName() {
            return CutPicName;
        }

        public void setCutPicName(String CutPicName) {
            this.CutPicName = CutPicName;
        }

        public String getLength() {
            return Length;
        }

        public void setLength(String Length) {
            this.Length = Length;
        }

        public String getFileSize() {
            return FileSize;
        }

        public void setFileSize(String FileSize) {
            this.FileSize = FileSize;
        }

        public String getResolution() {
            return Resolution;
        }

        public void setResolution(String Resolution) {
            this.Resolution = Resolution;
        }

        public ChannelBean getChannel() {
            return Channel;
        }

        public void setChannel(ChannelBean Channel) {
            this.Channel = Channel;
        }

        public SupplierBean getSupplier() {
            return Supplier;
        }

        public void setSupplier(SupplierBean Supplier) {
            this.Supplier = Supplier;
        }

        public SeriesBean getSeries() {
            return Series;
        }

        public void setSeries(SeriesBean Series) {
            this.Series = Series;
        }

        public String getPoint() {
            return Point;
        }

        public void setPoint(String Point) {
            this.Point = Point;
        }

        public String getDownload() {
            return Download;
        }

        public void setDownload(String Download) {
            this.Download = Download;
        }

        public List<ClassBean> getClassBean() {
            return Class;
        }

        public void setClass(List<ClassBean> Class) {
            this.Class = Class;
        }

        public List<ActorBean> getActor() {
            return Actor;
        }

        public void setActor(List<ActorBean> Actor) {
            this.Actor = Actor;
        }

        public static class ChannelBean {
            /**
             * ID : 3
             * Name : 有碼
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

        public static class SupplierBean {
            /**
             * ID : 33
             * Name : sonyaibo
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

        public static class SeriesBean {
            /**
             * ID :
             * Name :
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

        public static class ClassBean {
            /**
             * ID : 4
             * Name : 內射
             */

            private int ID;
            private String Name;

            public int getID() {
                return ID;
            }

            public void setID(int ID) {
                this.ID = ID;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }
        }

        public static class ActorBean {
            /**
             * ID : 2
             * Name : 上原亜衣
             * Pic : 2.jpg
             */

            private int ID;
            private String Name;
            private String Pic;

            public int getID() {
                return ID;
            }

            public void setID(int ID) {
                this.ID = ID;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public String getPic() {
                return Pic;
            }

            public void setPic(String Pic) {
                this.Pic = Pic;
            }
        }
    }
}
