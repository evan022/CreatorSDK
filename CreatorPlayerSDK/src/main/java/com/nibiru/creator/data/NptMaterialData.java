package com.nibiru.creator.data;

import java.util.Map;

public class NptMaterialData {
    private int resCode;
    private String resMsg;
    private ResData resData;

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public ResData getResData() {
        return resData;
    }

    public void setResData(ResData resData) {
        this.resData = resData;
    }

    public static class ResData {
        private String codeFolder;
        private MaterialBean material;
        private Map<String,String> fileNameMap;

        public String getCodeFolder() {
            return codeFolder;
        }

        public void setCodeFolder(String codeFolder) {
            this.codeFolder = codeFolder;
        }

        public MaterialBean getMaterial() {
            return material;
        }

        public void setMaterial(MaterialBean material) {
            this.material = material;
        }

        public Map<String, String> getFileNameMap() {
            return fileNameMap;
        }

        public void setFileNameMap(Map<String, String> fileNameMap) {
            this.fileNameMap = fileNameMap;
        }
    }

    @Override
    public String toString() {
        return "NptMaterialData{" +
                "resCode=" + resCode +
                ", resMsg='" + resMsg + '\'' +
                ", resData=" + resData +
                '}';
    }
}
