package com.nibiru.creator.data;

public class LoginResultData {
    private int resCode;
    private String resMsg;
    private ResData resData;

    public LoginResultData() {
    }

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

    @Override
    public String toString() {
        return "LoginResultData{" +
                "resCode=" + resCode +
                ", resMsg='" + resMsg + '\'' +
                ", resData=" + resData +
                '}';
    }

    public static class ResData {
        private UserData user;

        public ResData() {
        }

        public UserData getUser() {
            return user;
        }

        public void setUser(UserData user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "ResData{" +
                    "user=" + user +
                    '}';
        }
    }
}
