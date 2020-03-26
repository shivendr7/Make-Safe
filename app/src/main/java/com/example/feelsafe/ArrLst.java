package com.example.feelsafe;

import java.util.ArrayList;

public class ArrLst {

    public ArrayList<String> arr;

    ArrLst() {
        arr=new ArrayList<>();
    }
    /*public void ADD(String s) {
        this.arr.add(s);
    }*/
    public ArrayList<String> getList() {
        return arr;
    }
    public void delElement(String s) {
        arr.remove(s);
    }

}
