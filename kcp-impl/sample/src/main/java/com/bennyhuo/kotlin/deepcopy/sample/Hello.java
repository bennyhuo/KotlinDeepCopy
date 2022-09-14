package com.bennyhuo.kotlin.deepcopy.sample;

public class Hello {
    public static void main(String[] args) {
        System.out.println(new User(1, "").deepCopy());
    }
}
