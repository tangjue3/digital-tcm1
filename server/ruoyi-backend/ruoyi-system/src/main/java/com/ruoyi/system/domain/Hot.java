package com.ruoyi.system.domain;





public class Hot {
    private int id;
    private String name;
    private HotState state;
    private int total;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HotState getState() {
        return state;
    }

    public void setState(HotState state) {
        this.state = state;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Hot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", total=" + total +
                '}';
    }
}
