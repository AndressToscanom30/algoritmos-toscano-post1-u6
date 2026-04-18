package dp.bench;

import dp.Knapsack;
import java.util.Random;

/**
 * Benchmark manual para generar resultados cuando JMH no está disponible.
 * Simula la configuración de KnapsackBench con warmup e iteraciones de medición.
 *
 * @author Toscano
 */
public class ManualBenchmark {

    private static final int RANDOM_SEED = 42;
    private static final int WARMUP_ITERATIONS = 3;
    private static final int MEASUREMENT_ITERATIONS = 5;
    private static final int MAX_VALUE = 100;

    public static void main(String[] args) {
        int[][] configs = {
            {100, 1000},
            {500, 5000},
            {1000, 10000}
        };

        System.out.println("Benchmark                          (n)     (W)   Mode  Cnt       Score   Units");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────");

        for (int[] cfg : configs) {
            int n = cfg[0], W = cfg[1];
            Random rng = new Random(RANDOM_SEED);
            int[] weights = new int[n];
            int[] values = new int[n];
            for (int i = 0; i < n; i++) {
                weights[i] = rng.nextInt(W / 4) + 1;
                values[i] = rng.nextInt(MAX_VALUE) + 1;
            }

            double time01 = benchmark(() -> Knapsack.solve01(weights, values, W));
            double timeMem = benchmark(() -> Knapsack.solveMemOpt(weights, values, W));

            System.out.printf("KnapsackBench.bench01            %5d   %5d   avgt  %3d  %10.3f   us/op%n",
                    n, W, MEASUREMENT_ITERATIONS, time01);
            System.out.printf("KnapsackBench.benchMemOpt        %5d   %5d   avgt  %3d  %10.3f   us/op%n",
                    n, W, MEASUREMENT_ITERATIONS, timeMem);
        }
    }

    private static double benchmark(Runnable task) {
        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            for (int r = 0; r < 100; r++) {
                task.run();
            }
        }

        // Measurement
        double totalUs = 0;
        for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
            int runs = 50;
            long start = System.nanoTime();
            for (int r = 0; r < runs; r++) {
                task.run();
            }
            long elapsed = System.nanoTime() - start;
            totalUs += (elapsed / 1000.0) / runs;
        }
        return totalUs / MEASUREMENT_ITERATIONS;
    }
}
