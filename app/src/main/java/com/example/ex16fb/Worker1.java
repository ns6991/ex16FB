package com.example.ex16fb;

public class Worker1 {
    private String LNAME;
    private String FNAME;
    private String COMPANY;
    private String ID;
    private String PHONE;
    private String ACTIVE;

    public Worker1(String LNAME, String FNAME, String COMPANY, String ID, String PHONE, String ACTIVE) {
        this.LNAME = LNAME;
        this.FNAME = FNAME;
        this.COMPANY = COMPANY;
        this.ID = ID;
        this.PHONE = PHONE;
        this.ACTIVE = ACTIVE;
    }
    public Worker1(){}

    public Worker1(String user) {}

    public String getLNAME() {
        return LNAME;
    }

    public void setLNAME(String LNAME) {
        this.LNAME = LNAME;
    }

    public String getFNAME() {
        return FNAME;
    }

    public void setFNAME(String FNAME) {
        this.FNAME = FNAME;
    }

    public String getCOMPANY() {
        return COMPANY;
    }

    public void setCOMPANY(String COMPANY) {
        this.COMPANY = COMPANY;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getACTIVE() {
        return ACTIVE;
    }

    public void setACTIVE(String ACTIVE) {
        this.ACTIVE = ACTIVE;
    }
}
