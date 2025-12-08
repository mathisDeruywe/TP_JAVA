class MonThread extends Thread {
    private String nom;
    private int time;

    public MonThread(String nom, int time) {
        this.nom = nom;
        this.time = time;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(nom + " - Compteur : " + i);
            try {
                Thread.sleep(time); // Pause 0,5 sec
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class TestThread {
    public static void main(String[] args) {
        MonThread t1 = new MonThread("Thread A", 500);
        MonThread t2 = new MonThread("Thread B", 800);
        MonThread t3 = new MonThread("Thread c", 300);

        t1.start();
        t2.start();
    }
}