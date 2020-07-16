package com.nibiru.creator.data;

import java.util.ArrayList;
import java.util.List;

public class NptResultData {
    private int resCode;
    private ResData resData;
    private String resMsg;

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public ResData getResData() {
        return resData;
    }

    public void setResData(ResData resData) {
        this.resData = resData;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public static class ResData {

        private PageInfo pageInfo;
        private List<NptData> rows = new ArrayList<NptData>();

        public PageInfo getPageInfo() {
            return pageInfo;
        }

        public void setPageInfo(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
        }

        public List<NptData> getRows() {
            return rows;
        }

        public void setRows(List<NptData> rows) {
            this.rows = rows;
        }

        public static class PageInfo {
            private int allPageNum;
            private int pageNo;
            private int pageRows;
            private int totalRows;

            @Override
            public String toString() {
                return "PageInfo{" +
                        "allPageNum=" + allPageNum +
                        ", pageNo=" + pageNo +
                        ", pageRows=" + pageRows +
                        ", totalRows=" + totalRows +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "ResData{" +
                    "pageInfo=" + pageInfo +
                    ", rows=" + rows +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NptResultData{" +
                "resCode=" + resCode +
                ", resData=" + resData +
                ", resMsg='" + resMsg + '\'' +
                '}';
    }
}
