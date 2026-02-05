package org.example;

class MonThread extends Thread {
    @Override
    public void run() {
        System.out.println("Exécution dans le thread : " + Thread.currentThread().getName());
    }
}

public class TestThread {
    public static void main(String[] args) {
        MonThread t1 = new MonThread();
        t1.start(); //  démarre un nouveau thread
        System.out.println("Thread principal : " + Thread.currentThread().getName());
    }
}