package com.baidu.global;

public class T {
    public static int a =0;
    private int b =1;
    public void add(){
        synchronized (this){
           a++;
        }
    }
}
