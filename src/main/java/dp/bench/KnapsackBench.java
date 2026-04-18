package dp.bench;

import dp.Knapsack;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks JMH para comparar las variantes de Knapsack.
 * <p>
 * Compara {@code solve01} (tabla 2D, O(n·W) espacio) contra
 * {@code solveMemOpt} (arreglo 1D, O(W) espacio) para tres tamaños
 * de problema: n=100/W=1000, n=500/W=5000 y n=1000/W=10000.
 * </p>
 *
 * @author Toscano
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Benchmark)
public class KnapsackBench {

    private static final int RANDOM_SEED = 42;
    private static final int MAX_VALUE = 100;

    @Param({"100", "500", "1000"})
    public int n;

    @Param({"1000", "5000", "10000"})
    public int W;

    private int[] weights;
    private int[] values;

    /**
     * Genera datos aleatorios con semilla fija para reproducibilidad.
     * Los pesos se distribuyen en [1, W/4] y los valores en [1, 100].
     */
    @Setup
    public void setup() {
        Random rng = new Random(RANDOM_SEED);
        weights = new int[n];
        values = new int[n];
        for (int i = 0; i < n; i++) {
            weights[i] = rng.nextInt(W / 4) + 1;
            values[i] = rng.nextInt(MAX_VALUE) + 1;
        }
    }

    /**
     * Benchmark de Knapsack 0/1 con tabla 2D — O(n·W) espacio.
     *
     * @return valor óptimo (consumido por JMH para evitar dead-code elimination)
     */
    @Benchmark
    public int bench01() {
        return Knapsack.solve01(weights, values, W);
    }

    /**
     * Benchmark de Knapsack 0/1 optimizado en memoria — O(W) espacio.
     *
     * @return valor óptimo (consumido por JMH para evitar dead-code elimination)
     */
    @Benchmark
    public int benchMemOpt() {
        return Knapsack.solveMemOpt(weights, values, W);
    }
}
