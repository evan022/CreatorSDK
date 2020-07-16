package com.nibiru.creator.data;

import java.util.List;

public class ResUrlData {
    private int recode;
    private String msg;
    private Data data;

    public ResUrlData() {
    }

    public int getRecode() {
        return recode;
    }

    public void setRecode(int recode) {
        this.recode = recode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private List<Items> items;

        public Data() {
        }

        public List<Items> getItems() {
            return items;
        }

        public void setItems(List<Items> items) {
            this.items = items;
        }

        public static class Items {
            private String inputUrl;
            private String playUrl;

            public Items() {
            }

            public String getInputUrl() {
                return inputUrl;
            }

            public void setInputUrl(String inputUrl) {
                this.inputUrl = inputUrl;
            }

            public String getPlayUrl() {
                return playUrl;
            }

            public void setPlayUrl(String playUrl) {
                this.playUrl = playUrl;
            }
        }
    }
}
