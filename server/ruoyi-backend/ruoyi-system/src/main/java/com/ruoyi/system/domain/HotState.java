package com.ruoyi.system.domain;





public class HotState {
    private int id;
    private int status1001;
    private int status1002;
    private int status1003;
    private int status1004;
    private int status1005;
    private int status1006;
    private int total;

    @Override
    public String toString() {
        return "HotState{" +
                "id=" + id +
                ", status1001=" + status1001 +
                ", status1002=" + status1002 +
                ", status1003=" + status1003 +
                ", status1004=" + status1004 +
                ", status1005=" + status1005 +
                ", status1006=" + status1006 +
                ", total=" + total +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus1001() {
        return status1001;
    }

    public void setStatus1001(int status1001) {
        this.status1001 = status1001;
    }

    public int getStatus1002() {
        return status1002;
    }

    public void setStatus1002(int status1002) {
        this.status1002 = status1002;
    }

    public int getStatus1003() {
        return status1003;
    }

    public void setStatus1003(int status1003) {
        this.status1003 = status1003;
    }

    public int getStatus1004() {
        return status1004;
    }

    public void setStatus1004(int status1004) {
        this.status1004 = status1004;
    }

    public int getStatus1005() {
        return status1005;
    }

    public void setStatus1005(int status1005) {
        this.status1005 = status1005;
    }

    public int getStatus1006() {
        return status1006;
    }

    public void setStatus1006(int status1006) {
        this.status1006 = status1006;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}