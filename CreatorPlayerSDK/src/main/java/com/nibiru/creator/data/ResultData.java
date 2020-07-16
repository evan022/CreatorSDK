package com.nibiru.creator.data;

import java.util.List;

public class ResultData {
    private String reason;
    private List<CategoryData> result;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<CategoryData> getResult() {
        return result;
    }

    public void setResult(List<CategoryData> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResultData{" +
                "reason='" + reason + '\'' +
                ", result=" + result +
                '}';
    }

    public static class CategoryData{
        private String id;
        private String name;
        private String explain;
        private String require;
        private String common;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getExplain() {
            return explain;
        }

        public void setExplain(String explain) {
            this.explain = explain;
        }

        public String getRequire() {
            return require;
        }

        public void setRequire(String require) {
            this.require = require;
        }

        public String getCommon() {
            return common;
        }

        public void setCommon(String common) {
            this.common = common;
        }

        @Override
        public String toString() {
            return "CategoryData{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", explain='" + explain + '\'' +
                    ", require='" + require + '\'' +
                    ", common='" + common + '\'' +
                    '}';
        }
    }
}
