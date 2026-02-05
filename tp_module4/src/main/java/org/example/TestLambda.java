package org.example;

public class TestLambda {
    public static void main(String[] args) {
        Thread t = new Thread(() -> System.out.println("Bonjour du thread " + Thread.currentThread().getName()));
        t.start();
    }
}