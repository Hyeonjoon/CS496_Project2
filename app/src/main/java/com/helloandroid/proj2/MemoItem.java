package com.helloandroid.proj2;

public class MemoItem {

    String memo;
    String num;

    public MemoItem(String memo, String num) {
        this.memo = memo;
        this.num = num;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String memo) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "";
    }


}
