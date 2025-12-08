import java.util.concurrent.*;

class Calcul implements Callable<Integer> {
    private int n;

    public Calcul(int n) {
        this.n = n;
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("Calcul de la somme jusqu'à " + n);

        int somme = 0;

        for (int i = 1; i <= n; i++) {
            somme += i;
            Thread.sleep(100);
        }
        return somme;
    }
}

public class TestCallable {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        var taches = java.util.List.of(
                new Calcul(5),
                new Calcul(10),
                new Calcul(15)
        );

        System.out.println("Calcule ...");
        var resultats = executor.invokeAll(taches);

        for (Future<Integer> f : resultats) {
            System.out.println("Résultat = " + f.get());
        }

        executor.shutdown();
    }
} 