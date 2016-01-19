package uuxia.het.com.library.utils;

import java.io.Serializable;

/**
 * Created by Android Studio.
 * Author: uuxia
 * Date: 2016-01-19 18:22
 * Description:
 */
/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: DaemonModel.java
 * Create: 2016/1/19 18:22
 */
public class DaemonModel implements Serializable{
    /**
     * default:"restart"
     */
    public String code = "restart";
    /**
     * DaemonService
     */
    public String daemonClasz;
    /**
     * AppService
     */
    public String destClasz;
    public String daseAppPakage;
    /**
     * AppService's Action
     */
    public String destAction;
    /**
     * daemon process interval
     */
    public int interval = 5;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDaemonClasz() {
        return daemonClasz;
    }

    public void setDaemonClasz(String daemonClasz) {
        this.daemonClasz = daemonClasz;
    }

    public String getDestClasz() {
        return destClasz;
    }

    public void setDestClasz(String destClasz) {
        this.destClasz = destClasz;
    }

    public String getDestAction() {
        return destAction;
    }

    public void setDestAction(String destAction) {
        this.destAction = destAction;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getDaseAppPakage() {
        return daseAppPakage;
    }

    public void setDaseAppPakage(String daseAppPakage) {
        this.daseAppPakage = daseAppPakage;
    }

    @Override
    public String toString() {
        return "DaemonModel{" +
                "code='" + code + '\'' +
                ", daemonClasz='" + daemonClasz + '\'' +
                ", destClasz='" + destClasz + '\'' +
                ", daseAppPakage='" + daseAppPakage + '\'' +
                ", destAction='" + destAction + '\'' +
                ", interval=" + interval +
                '}';
    }
}
