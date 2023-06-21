package com.bixuebihui.test.business;


import java.util.ArrayList;
import java.util.List;

public class GenericTest {

    static class GrandParent{
        public void say(){
            System.out.println("GrandParent");
        }
    }
    static class Parent extends GrandParent{
        public void say(){
            System.out.println("Parent");
        }

        public void otherSay(){
            System.out.println("other from Parent");
        }
    }

    static class Child extends Parent{
        public void say(){
            System.out.println("Child");
        }
        public void otherSay(){
            System.out.println("other from Child");
        }

        public void childSay(){
            System.out.println("childSay");
        }
    }

    public static void say(List<? extends Parent> p){
        for(GrandParent g:p){
            g.say();
        }
        for(Parent g:p){
            g.otherSay();
        }
//        for(Child g:p){ // compile error
//            g.childSay();
//        }

//        p.add(new Child()); // 消费者不能添加元素
    }

    public static void add(List<? super Parent> p){
        p.add(new Child());
        p.add(new Parent());
        // p.add(new GrandParent()); // compile error

        // Parent c = p.get(0); // 生产者不能获取元素
    }

    public static void main(String[] args) {
        List<Parent> family = new ArrayList<>(); // 生产者同时也是消费者，则只能使用明确的类型
        add(family);
        say(family);

        List<Child> family2 = new ArrayList<>();
//        add(family2); // compile error
        say(family2);

        List<GrandParent> family3 = new ArrayList<>();
        add(family3);
//        say(family3); // compile error

        List<? extends Parent > family4 = new ArrayList<>();
//        add(family4); // compile error
        say(family4);

        List<? super Parent> family5 = new ArrayList<>();
        add(family5);
//        say(family5); // compile error

    }

}
