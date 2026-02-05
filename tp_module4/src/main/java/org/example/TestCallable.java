package org.example;

import java.util.concurrent.*;

public class TestCallable {
    public static void main(String[] args) throws Exception {
        Callable<Integer> somme = () -> {
            Thread.sleep(1000);
            return 10 + 20;
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> futur = executor.submit(somme);

        System.out.println("Résultat en attente...");
        System.out.println("Résultat : " + futur.get()); // 🔸 bloque jusqu'à résultat
        executor.shutdown();
    }
}0