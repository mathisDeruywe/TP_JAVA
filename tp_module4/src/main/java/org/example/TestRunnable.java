package org.example;

class Tache implements Runnable {
    public void run() {
        System.out.println("Tâche exécutée par : " + Thread.currentThread().getName());
    }
}

public class TestRunnable {
    public static void main(String[] args) {
        Thread t = new Thread(new Tache());
        t.start();
    }
}