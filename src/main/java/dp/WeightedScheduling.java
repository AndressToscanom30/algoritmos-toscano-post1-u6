package dp;

import java.util.*;

/**
 * Implementación del algoritmo Weighted Interval Scheduling mediante programación dinámica.
 * <p>
 * Dado un conjunto de trabajos con tiempos de inicio, fin y valor asociado,
 * selecciona un subconjunto de trabajos mutuamente compatibles (sin solapamiento)
 * que maximice el valor total.
 * </p>
 * <p>
 * El algoritmo ordena por tiempo de finalización, calcula p(j) con búsqueda binaria,
 * llena la tabla DP y opcionalmente reconstruye la solución óptima.
 * </p>
 *
 * @author Toscano
 */
public class WeightedScheduling {

    private WeightedScheduling() {
        // Clase utilitaria, no instanciable
    }

    /**
     * Registro inmutable que representa un trabajo con tiempo de inicio, fin y valor.
     *
     * @param start  tiempo de inicio del trabajo (inclusive)
     * @param finish tiempo de finalización del trabajo (inclusive)
     * @param value  valor/ganancia asociada al trabajo
     */
    public record Job(int start, int finish, int value) {}

    /**
     * Calcula el valor máximo de un conjunto de trabajos compatibles.
     * <p>
     * Ordena los trabajos por tiempo de finalización, calcula el arreglo p(j)
     * y aplica la recurrencia: {@code dp[j] = max(dp[j-1], value_j + dp[p[j]])}.
     * </p>
     *
     * @param jobs lista de trabajos a considerar (puede estar desordenada)
     * @return valor máximo alcanzable seleccionando trabajos sin solapamiento
     * @implNote Complejidad temporal: O(n log n) por ordenamiento y búsqueda binaria.
     *           Complejidad espacial: O(n).
     */
    public static int solve(List<Job> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return 0;
        }

        List<Job> sorted = new ArrayList<>(jobs);
        sorted.sort(Comparator.comparingInt(Job::finish));

        int n = sorted.size();
        int[] p = computeP(sorted);
        int[] dp = new int[n + 1];

        for (int j = 1; j <= n; j++) {
            int include = sorted.get(j - 1).value() + dp[p[j]];
            int exclude = dp[j - 1];
            dp[j] = Math.max(include, exclude);
        }
        return dp[n];
    }

    /**
     * Reconstruye la lista de trabajos seleccionados en la solución óptima.
     * <p>
     * Recorre la tabla DP de atrás hacia adelante, determinando en cada posición
     * si el trabajo fue incluido (comparando include vs exclude).
     * </p>
     *
     * @param jobs lista de trabajos a considerar
     * @return lista de trabajos seleccionados en el óptimo, ordenados por tiempo de fin
     * @implNote Complejidad temporal: O(n log n). Complejidad espacial: O(n).
     */
    public static List<Job> reconstructJobs(List<Job> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Job> sorted = new ArrayList<>(jobs);
        sorted.sort(Comparator.comparingInt(Job::finish));

        int n = sorted.size();
        int[] p = computeP(sorted);
        int[] dp = new int[n + 1];

        for (int j = 1; j <= n; j++) {
            int include = sorted.get(j - 1).value() + dp[p[j]];
            int exclude = dp[j - 1];
            dp[j] = Math.max(include, exclude);
        }

        // Backtracking para reconstruir la solución
        List<Job> selected = new ArrayList<>();
        int j = n;
        while (j > 0) {
            int include = sorted.get(j - 1).value() + dp[p[j]];
            if (include >= dp[j - 1]) {
                selected.add(sorted.get(j - 1));
                j = p[j];
            } else {
                j--;
            }
        }
        Collections.reverse(selected);
        return selected;
    }

    /**
     * Calcula p[j] para cada trabajo j: el índice del último trabajo compatible
     * cuyo tiempo de finalización es {@code <= start_j}.
     * <p>
     * Usa búsqueda binaria sobre los tiempos de finalización (ya ordenados)
     * para encontrar eficientemente el último trabajo que no solapa con j.
     * </p>
     *
     * @param jobs lista de trabajos ya ordenada por tiempo de finalización
     * @return arreglo p donde p[j] es el índice (1-based) del último trabajo
     *         compatible con j, o 0 si ninguno es compatible
     * @implNote Complejidad temporal: O(n log n). Complejidad espacial: O(n).
     */
    static int[] computeP(List<Job> jobs) {
        int n = jobs.size();
        int[] finishes = jobs.stream().mapToInt(Job::finish).toArray();
        int[] p = new int[n + 1];

        for (int j = 1; j <= n; j++) {
            int s = jobs.get(j - 1).start();
            int lo = 0;
            int hi = j - 1;
            while (lo < hi) {
                int mid = (lo + hi + 1) >>> 1;
                if (finishes[mid - 1] <= s) {
                    lo = mid;
                } else {
                    hi = mid - 1;
                }
            }
            p[j] = lo;
        }
        return p;
    }
}
