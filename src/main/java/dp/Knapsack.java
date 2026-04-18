package dp;

/**
 * Implementaciones de variantes del problema Knapsack usando programación dinámica.
 * <p>
 * Incluye Knapsack 0/1 clásico, Unbounded Knapsack y una versión
 * optimizada en memoria del 0/1 que usa solo O(W) espacio.
 * </p>
 *
 * @author Toscano
 */
public class Knapsack {

    private Knapsack() {
        // Clase utilitaria, no instanciable
    }

    /**
     * Resuelve el problema Knapsack 0/1 usando una tabla DP bidimensional.
     * <p>
     * Cada objeto puede incluirse a lo sumo una vez. La recurrencia es:
     * {@code dp[i][w] = max(dp[i-1][w], v_i + dp[i-1][w - w_i])} si {@code w_i <= w}.
     * </p>
     *
     * @param weights arreglo de pesos de los n objetos (todos positivos)
     * @param values  arreglo de valores de los n objetos (todos no negativos)
     * @param W       capacidad máxima de la mochila (no negativo)
     * @return valor máximo alcanzable seleccionando un subconjunto de objetos
     *         cuyo peso total no exceda W
     * @throws IllegalArgumentException si los arreglos tienen longitudes distintas
     *         o si W es negativo
     * @implNote Complejidad temporal: O(n·W). Complejidad espacial: O(n·W).
     */
    public static int solve01(int[] weights, int[] values, int W) {
        validateInputs(weights, values, W);
        int n = weights.length;
        int[][] dp = new int[n + 1][W + 1];

        for (int i = 1; i <= n; i++) {
            int wi = weights[i - 1];
            int vi = values[i - 1];
            for (int w = 0; w <= W; w++) {
                dp[i][w] = dp[i - 1][w];
                if (wi <= w) {
                    dp[i][w] = Math.max(dp[i][w], vi + dp[i - 1][w - wi]);
                }
            }
        }
        return dp[n][W];
    }

    /**
     * Reconstruye la selección óptima de objetos para Knapsack 0/1.
     * <p>
     * Recorre la tabla DP de atrás hacia adelante para determinar qué objetos
     * fueron incluidos en la solución óptima.
     * </p>
     *
     * @param weights arreglo de pesos de los n objetos
     * @param values  arreglo de valores de los n objetos
     * @param W       capacidad máxima de la mochila
     * @return arreglo booleano donde {@code sel[i] == true} indica que el objeto i
     *         fue seleccionado en la solución óptima
     * @implNote Complejidad temporal: O(n·W) para llenar la tabla + O(n+W) para reconstruir.
     *           Complejidad espacial: O(n·W).
     */
    public static boolean[] reconstruct(int[] weights, int[] values, int W) {
        validateInputs(weights, values, W);
        int n = weights.length;
        int[][] dp = new int[n + 1][W + 1];

        for (int i = 1; i <= n; i++) {
            int wi = weights[i - 1];
            int vi = values[i - 1];
            for (int w = 0; w <= W; w++) {
                dp[i][w] = dp[i - 1][w];
                if (wi <= w) {
                    dp[i][w] = Math.max(dp[i][w], vi + dp[i - 1][w - wi]);
                }
            }
        }

        boolean[] sel = new boolean[n];
        int w = W;
        for (int i = n; i >= 1; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                sel[i - 1] = true;
                w -= weights[i - 1];
            }
        }
        return sel;
    }

    /**
     * Resuelve el problema Unbounded Knapsack (mochila con repetición ilimitada).
     * <p>
     * Cada objeto puede incluirse cualquier número de veces. Usa un arreglo 1D
     * iterando de izquierda a derecha para permitir reutilización de objetos.
     * La recurrencia es: {@code dp[w] = max(dp[w], v_i + dp[w - w_i])} para cada objeto i.
     * </p>
     *
     * @param weights arreglo de pesos de los n tipos de objeto
     * @param values  arreglo de valores de los n tipos de objeto
     * @param W       capacidad máxima de la mochila
     * @return valor máximo alcanzable permitiendo repetición ilimitada de objetos
     * @implNote Complejidad temporal: O(n·W). Complejidad espacial: O(W).
     */
    public static int solveUnbounded(int[] weights, int[] values, int W) {
        validateInputs(weights, values, W);
        int n = weights.length;
        int[] dp = new int[W + 1];

        for (int i = 0; i < n; i++) {
            int wi = weights[i];
            int vi = values[i];
            for (int w = wi; w <= W; w++) {
                dp[w] = Math.max(dp[w], vi + dp[w - wi]);
            }
        }
        return dp[W];
    }

    /**
     * Resuelve Knapsack 0/1 con O(W) espacio usando iteración de derecha a izquierda.
     * <p>
     * Al iterar w de W hacia 0, se garantiza que cada objeto se considere a lo sumo
     * una vez por fila, emulando la tabla 2D con un solo arreglo 1D.
     * Esta versión no permite reconstrucción directa de la solución.
     * </p>
     *
     * @param weights arreglo de pesos de los n objetos
     * @param values  arreglo de valores de los n objetos
     * @param W       capacidad máxima de la mochila
     * @return valor máximo alcanzable (idéntico a {@link #solve01})
     * @implNote Complejidad temporal: O(n·W). Complejidad espacial: O(W).
     */
    public static int solveMemOpt(int[] weights, int[] values, int W) {
        validateInputs(weights, values, W);
        int n = weights.length;
        int[] dp = new int[W + 1];

        for (int i = 0; i < n; i++) {
            int wi = weights[i];
            int vi = values[i];
            // Iteración de derecha a izquierda para mantener la propiedad 0/1
            for (int w = W; w >= wi; w--) {
                dp[w] = Math.max(dp[w], vi + dp[w - wi]);
            }
        }
        return dp[W];
    }

    /**
     * Valida que los arreglos de entrada sean consistentes y W no sea negativo.
     */
    private static void validateInputs(int[] weights, int[] values, int W) {
        if (weights.length != values.length) {
            throw new IllegalArgumentException(
                    "Los arreglos de pesos y valores deben tener la misma longitud");
        }
        if (W < 0) {
            throw new IllegalArgumentException("La capacidad W no puede ser negativa");
        }
    }
}
