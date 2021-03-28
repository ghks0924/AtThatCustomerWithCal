package com.example.atthatcustomerwithcal;

public class Customers {
    String OWNER_NAME,CUSTOMER_NAME,NUMBER,GRADE,RECOMMEND,POINT,VISIT,MEMO,SAVEDATE;
    int NOSHOWCOUNT;

    public Customers(String OWNER_NAME, String CUSTOMER_NAME, String NUMBER, String GRADE
            , String RECOMMEND, String POINT, String VISIT, String MEMO
            , String SAVEDATE, int NOSHOWCOUNT) {
        this.OWNER_NAME = OWNER_NAME;
        this.CUSTOMER_NAME = CUSTOMER_NAME;
        this.NUMBER = NUMBER;
        this.GRADE = GRADE;
        this.RECOMMEND = RECOMMEND;
        this.POINT = POINT;
        this.VISIT = VISIT;
        this.MEMO = MEMO;
        this.SAVEDATE = SAVEDATE;
        this.NOSHOWCOUNT = NOSHOWCOUNT;
    }


}
