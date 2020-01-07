package com.casstime.net.example;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CTResponse implements Serializable {


    /**
     * errorCode : 0
     * data : {"appConfig":[]}
     */

    private int errorCode;
    private DataBean data;
    private List<DataBean.Config.Info> list;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public List<DataBean.Config.Info> getList() {
        return list;
    }

    public void setList(List<DataBean.Config.Info> list) {
        this.list = list;
    }

    public static class DataBean implements Serializable {
        private Config config;

        private List<LinkedList<Weather<Rain>>> appConfigs;

        public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        public static class Config implements Serializable {

            private String name;

            private int id;

            private DataBean bean;

            private Map<String, Date> daily;

            private Map<Date, Weather> history;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public static class Info implements Serializable {

                private String message;

                public String getMessage() {
                    return message;
                }

                public void setMessage(String message) {
                    this.message = message;
                }
            }
        }

    }

    public static class Weather<T> implements Serializable {

    }


    public static class Rain implements Serializable {

    }

    public static class Date implements Serializable {

        int date;

        @Override
        public boolean equals(Object o) {
            if (this == o) {

                return true;
            }
            if (o == null || getClass() != o.getClass()) {

                return false;
            }

            Date date1 = (Date) o;

            return date == date1.date;
        }

        @Override
        public int hashCode() {
            return date;
        }
    }

}

