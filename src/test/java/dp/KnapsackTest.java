package dp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de pruebas JUnit 5 para las variantes de Knapsack.
 * Cubre solve01, solveUnbounded, solveMemOpt y reconstruct.
 *
 * @author Toscano
 */
@DisplayName("Knapsack Tests")
class KnapsackTest {

    // =========================================================================
    // Knapsack 0/1
    // =========================================================================

    @Nested
    @DisplayName("solve01 — Knapsack 0/1 clásico")
    class Solve01Tests {

        @Test
        @DisplayName("(a) W=0 → resultado es 0")
        void capacidadCero() {
            int[] weights = {1, 2, 3};
            int[] values = {10, 20, 30};
            assertEquals(0, Knapsack.solve01(weights, values, 0));
        }

        @Test
        @DisplayName("(b) Ningún objeto cabe en la mochila")
        void ningunObjetoCabe() {
            int[] weights = {5, 10, 15};
            int[] values = {100, 200, 300};
            assertEquals(0, Knapsack.solve01(weights, values, 4));
        }

        @Test
        @DisplayName("(c) Todos los objetos caben en la mochila")
        void todosLoObjetosCaben() {
            int[] weights = {1, 2, 3};
            int[] values = {10, 20, 30};
            assertEquals(60, Knapsack.solve01(weights, values, 10));
        }

        @Test
        @DisplayName("(d) Solución óptima no incluye el objeto de mayor valor")
        void optimoSinMayorValor() {
            // Objeto de mayor valor pesa 8 (valor 50), pero seleccionar los otros
            // dos (peso 3+4=7, valor 30+25=55) es mejor con W=7
            int[] weights = {3, 4, 8};
            int[] values = {30, 25, 50};
            assertEquals(55, Knapsack.solve01(weights, values, 7));
        }

        @Test
        @DisplayName("Sin objetos → resultado es 0")
        void sinObjetos() {
            assertEquals(0, Knapsack.solve01(new int[]{}, new int[]{}, 10));
        }

        @Test
        @DisplayName("Caso clásico del libro de texto")
        void casoClasico() {
            int[] weights = {2, 3, 4, 5};
            int[] values = {3, 4, 5, 6};
            // W=5: mejor es tomar objetos de peso 2+3 = valor 7
            assertEquals(7, Knapsack.solve01(weights, values, 5));
        }
    }

    // =========================================================================
    // solveMemOpt
    // =========================================================================

    @Nested
    @DisplayName("solveMemOpt — Knapsack 0/1 con O(W) espacio")
    class MemOptTests {

        @Test
        @DisplayName("(e) solveMemOpt retorna el mismo resultado que solve01")
        void mismoResultadoQueSolve01() {
            int[] weights = {3, 4, 8};
            int[] values = {30, 25, 50};
            for (int W = 0; W <= 20; W++) {
                assertEquals(
                        Knapsack.solve01(weights, values, W),
                        Knapsack.solveMemOpt(weights, values, W),
                        "Resultado difiere para W=" + W);
            }
        }

        @Test
        @DisplayName("solveMemOpt con W=0")
        void capacidadCero() {
            assertEquals(0, Knapsack.solveMemOpt(new int[]{1, 2}, new int[]{10, 20}, 0));
        }

        @Test
        @DisplayName("solveMemOpt con múltiples objetos iguales (no debe reusar)")
        void noReusaObjetos() {
            // Un solo objeto de peso 1 y valor 100, W=5
            // En 0/1 el resultado debe ser 100 (no 500)
            int[] weights = {1};
            int[] values = {100};
            assertEquals(100, Knapsack.solveMemOpt(weights, values, 5));
        }

        @Test
        @DisplayName("solveMemOpt coincide con solve01 para caso grande")
        void coincideCasoGrande() {
            int[] weights = {1, 3, 4, 5, 2, 7, 6};
            int[] values = {1, 4, 5, 7, 3, 10, 8};
            int W = 15;
            assertEquals(
                    Knapsack.solve01(weights, values, W),
                    Knapsack.solveMemOpt(weights, values, W));
        }
    }

    // =========================================================================
    // solveUnbounded
    // =========================================================================

    @Nested
    @DisplayName("solveUnbounded — Knapsack con repetición ilimitada")
    class UnboundedTests {

        @Test
        @DisplayName("W=0 → resultado es 0")
        void capacidadCero() {
            assertEquals(0, Knapsack.solveUnbounded(new int[]{1, 2}, new int[]{10, 20}, 0));
        }

        @Test
        @DisplayName("Repetición produce mejor resultado que 0/1")
        void repeticionMejorQue01() {
            // Objeto peso=1, valor=100 con W=5 → puede repetirse 5 veces = 500
            int[] weights = {1};
            int[] values = {100};
            assertEquals(500, Knapsack.solveUnbounded(weights, values, 5));
        }

        @Test
        @DisplayName("Ningún objeto cabe")
        void ningunObjetoCabe() {
            int[] weights = {10, 20};
            int[] values = {100, 200};
            assertEquals(0, Knapsack.solveUnbounded(weights, values, 5));
        }

        @Test
        @DisplayName("Caso con mejor ratio valor/peso")
        void mejorRatio() {
            // peso 3 valor 4, peso 4 valor 5
            // W=12: con peso 3 → 4 copias → valor 16
            //        con peso 4 → 3 copias → valor 15
            int[] weights = {3, 4};
            int[] values = {4, 5};
            assertEquals(16, Knapsack.solveUnbounded(weights, values, 12));
        }

        @Test
        @DisplayName("Unbounded >= 0/1 para mismos datos")
        void unboundedMayorIgualQue01() {
            int[] weights = {2, 3, 5};
            int[] values = {6, 8, 12};
            int W = 10;
            assertTrue(Knapsack.solveUnbounded(weights, values, W)
                    >= Knapsack.solve01(weights, values, W));
        }
    }

    // =========================================================================
    // Reconstrucción
    // =========================================================================

    @Nested
    @DisplayName("reconstruct — Reconstrucción de la solución")
    class ReconstructTests {

        @Test
        @DisplayName("La suma de valores seleccionados coincide con solve01")
        void sumaCoincide() {
            int[] weights = {2, 3, 4, 5};
            int[] values = {3, 4, 5, 6};
            int W = 5;

            boolean[] sel = Knapsack.reconstruct(weights, values, W);
            int totalValue = 0;
            int totalWeight = 0;
            for (int i = 0; i < sel.length; i++) {
                if (sel[i]) {
                    totalValue += values[i];
                    totalWeight += weights[i];
                }
            }
            assertEquals(Knapsack.solve01(weights, values, W), totalValue);
            assertTrue(totalWeight <= W);
        }

        @Test
        @DisplayName("Reconstrucción con W=0 no selecciona nada")
        void wCeroNoSelecciona() {
            boolean[] sel = Knapsack.reconstruct(new int[]{1, 2}, new int[]{10, 20}, 0);
            for (boolean s : sel) {
                assertFalse(s);
            }
        }

        @Test
        @DisplayName("Reconstrucción selecciona objetos correctos")
        void seleccionCorrecta() {
            // Objetos: peso={3,4,8}, valor={30,25,50}, W=7
            // Óptimo: objetos 0 y 1 (peso 3+4=7, valor 55)
            int[] weights = {3, 4, 8};
            int[] values = {30, 25, 50};
            boolean[] sel = Knapsack.reconstruct(weights, values, 7);
            assertTrue(sel[0], "Objeto 0 debería estar seleccionado");
            assertTrue(sel[1], "Objeto 1 debería estar seleccionado");
            assertFalse(sel[2], "Objeto 2 no debería estar seleccionado");
        }
    }
}
