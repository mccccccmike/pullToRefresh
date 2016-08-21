package com.sudichina.test;

/**
 * Created by mike on 2016/8/21.
 */
public class Bean {
    private String tv1;
    private String tv2;
    private int iv;

    public Bean(String tv1, String tv2, int iv) {
        this.tv1 = tv1;
        this.tv2 = tv2;
        this.iv = iv;
    }

    public String getTv1() {
        return tv1;
    }

    public void setTv1(String tv1) {
        this.tv1 = tv1;
    }

    public String getTv2() {
        return tv2;
    }

    public void setTv2(String tv2) {
        this.tv2 = tv2;
    }

    public int getIv() {
        return iv;
    }

    public void setIv(int iv) {
        this.iv = iv;
    }
}
