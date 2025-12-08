import java.util.concurrent.*;

class Tache implements Runnable {
    private int id;

    public Tache(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Tâche " + id + " exécutée par " + Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class TestExecutor {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(5); // 3 threads

        for (int i = 1; i <= 7; i++) {
            pool.execute(new Tache(i));
        }

        pool.shutdown();

        try {
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("Pool did not terminate");
            } else {
                System.out.println("Pool terminated");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
} 