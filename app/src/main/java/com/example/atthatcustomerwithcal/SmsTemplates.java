package com.example.atthatcustomerwithcal;

public class SmsTemplates {

    String TITLE, CONTENTS;

    public SmsTemplates(String TITLE, String CONTENTS) {
        this.TITLE = TITLE;
        this.CONTENTS = CONTENTS;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getCONTENTS() {
        return CONTENTS;
    }

    public void setCONTENTS(String CONTENTS) {
        this.CONTENTS = CONTENTS;
    }
}
