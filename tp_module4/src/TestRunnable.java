class TacheRunnable implements Runnable {
    private String nom;

    public TacheRunnable(String nom) {
        this.nom = nom;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 3; i++) {
            System.out.println("Exécution " + nom + " - étape " + i);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class TestRunnable {
    public static void main(String[] args) {
        Thread t1 = new Thread(new TacheRunnable("T1"));
        Thread t2 = new Thread(new TacheRunnable("T2"));
        Thread t3 = new Thread(new TacheRunnable("T3"));
        Thread t4 = new Thread(new TacheRunnable("T4"));
        Thread t5 = new Thread(new TacheRunnable("T5"));


        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        System.out.println("Le thread principal continue ...");
    }
}