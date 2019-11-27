package com.deeba.deebadriver;

public class TaskClass {
    private  String startloc;
    private String endloc;
    private String date;
    private  String status;
    private  String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



//    public Void changecolor(String background){
//    }

    public String getStartloc() {
        return startloc;
    }

    public void setStartloc(String startloc) {
        this.startloc = startloc;
    }

    public String getEndloc() {
        return endloc;
    }

    public void setEndloc(String endloc) {
        this.endloc = endloc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TaskClass(String startloc, String endloc, String date, String status, String time) {
        this.startloc = startloc;
        this.endloc = endloc;
        this.date = date;
        this.status = status;
        this.time = time;
    }

//    public TaskClass(String startloc, String endloc, String date,String status,String time) {
//        this.startloc = startloc;
//        this.endloc = endloc;
//        this.date = date;
//        this.status=status;
//        this.time=time;
//
//    }
}
