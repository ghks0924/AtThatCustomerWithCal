package com.example.atthatcustomerwithcal;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Events {
    String EVENT,TIME,DATE,MONTH,YEAR, SHORTMEMO, NUMBER, RETOUCH, MENU, CARDCASH, MATERIALMEMO, CONTENTMEMO, CRTDATE;
    int PRICE;
    boolean COMPLETE, NOSHOW;



    public Events( String EVENT, String TIME, String DATE, String MONTH, String YEAR, String RETOUCH
            , int PRICE, boolean COMPLETE, String SHORTMEMO, String NUMBER, boolean NOSHOW, String MENU
            , String CARDCASH, String MATERIALMEMO, String CONTENTMEMO, String CRTDATE) {

        this.EVENT = EVENT;
        this.TIME = TIME;
        this.DATE = DATE;
        this.MONTH = MONTH;
        this.YEAR = YEAR;
        this.RETOUCH = RETOUCH;
        this.PRICE = PRICE;
        this.COMPLETE = COMPLETE;
        this.SHORTMEMO = SHORTMEMO;
        this.NUMBER = NUMBER;
        this.NOSHOW = NOSHOW;
        this.MENU = MENU;
        this.CARDCASH = CARDCASH;
        this.MATERIALMEMO = MATERIALMEMO;
        this.CONTENTMEMO = CONTENTMEMO;
        this.CRTDATE = CRTDATE;
    }

    public String getEVENT() {
        return EVENT;
    }

    public void setEVENT(String EVENT) {
        this.EVENT = EVENT;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getMONTH() {
        return MONTH;
    }

    public void setMONTH(String MONTH) {
        this.MONTH = MONTH;
    }

    public String getYEAR() {
        return YEAR;
    }

    public void setYEAR(String YEAR) {
        this.YEAR = YEAR;
    }

    public String getSHORTMEMO() {
        return SHORTMEMO;
    }

    public void setSHORTMEMO(String SHORTMEMO) {
        this.SHORTMEMO = SHORTMEMO;
    }

    public String getNUMBER() {
        return NUMBER;
    }

    public void setNUMBER(String NUMBER) {
        this.NUMBER = NUMBER;
    }

    public int getPRICE() {
        return PRICE;
    }

    public void setPRICE(int PRICE) {
        this.PRICE = PRICE;
    }

    public boolean isCOMPLETE() {
        return COMPLETE;
    }

    public void setCOMPLETE(boolean COMPLETE) {
        this.COMPLETE = COMPLETE;
    }

    public boolean isNOSHOW() {
        return NOSHOW;
    }

    public void setNOSHOW(boolean NOSHOW) {
        this.NOSHOW = NOSHOW;
    }

    public String getRETOUCH() {
        return RETOUCH;
    }

    public void setRETOUCH(String RETOUCH) {
        this.RETOUCH = RETOUCH;
    }

    public String getMENU() {
        return MENU;
    }

    public void setMENU(String MENU) {
        this.MENU = MENU;
    }

    public String getCARDCASH() {
        return CARDCASH;
    }

    public void setCARDCASH(String CARDCASH) {
        this.CARDCASH = CARDCASH;
    }

    public String getMATERIALMEMO() {
        return MATERIALMEMO;
    }

    public void setMATERIALMEMO(String MATERIALMEMO) {
        this.MATERIALMEMO = MATERIALMEMO;
    }

    public String getCONTENTMEMO() {
        return CONTENTMEMO;
    }

    public void setCONTENTMEMO(String CONTENTMEMO) {
        this.CONTENTMEMO = CONTENTMEMO;
    }
}
