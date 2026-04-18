package dp;

import dp.WeightedScheduling.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de pruebas JUnit 5 para Weighted Interval Scheduling.
 * Valida solve y reconstructJobs con diversos escenarios.
 *
 * @author Toscano
 */
@DisplayName("Weighted Interval Scheduling Tests")
class SchedulingTest {

    @Test
    @DisplayName("(a) Caso con un solo trabajo")
    void unSoloTrabajo() {
        List<Job> jobs = List.of(new Job(0, 5, 10));
        assertEquals(10, WeightedScheduling.solve(jobs));

        List<Job> selected = WeightedScheduling.reconstructJobs(jobs);
        assertEquals(1, selected.size());
        assertEquals(10, selected.get(0).value());
    }

    @Test
    @DisplayName("(b) Todos los trabajos son compatibles entre sí")
    void todosCompatibles() {
        List<Job> jobs = List.of(
                new Job(0, 2, 5),
                new Job(3, 5, 10),
                new Job(6, 8, 15)
        );
        // Todos compatibles, se seleccionan todos → valor = 30
        assertEquals(30, WeightedScheduling.solve(jobs));

        List<Job> selected = WeightedScheduling.reconstructJobs(jobs);
        assertEquals(3, selected.size());
        int totalValue = selected.stream().mapToInt(Job::value).sum();
        assertEquals(30, totalValue);
    }

    @Test
    @DisplayName("(c) Greedy EDF daría resultado subóptimo")
    void greedyEdfSuboptimo() {
        // Un trabajo de alto valor [0,6) valor=100 solapa con tres de bajo valor
        // Greedy EDF seleccionaría por orden de fin: [0,2)=10, [2,4)=10, [4,6)=10 → 30
        // DP selecciona el trabajo grande → 100
        List<Job> jobs = List.of(
                new Job(0, 2, 10),
                new Job(2, 4, 10),
                new Job(4, 6, 10),
                new Job(0, 6, 100)
        );
        assertEquals(100, WeightedScheduling.solve(jobs));
    }

    @Test
    @DisplayName("(d) reconstructJobs retorna trabajos cuya suma de valores es el óptimo")
    void reconstructCoincideConSolve() {
        List<Job> jobs = List.of(
                new Job(0, 2, 10),
                new Job(2, 4, 10),
                new Job(4, 6, 10),
                new Job(0, 6, 100)
        );
        int optimo = WeightedScheduling.solve(jobs);
        List<Job> selected = WeightedScheduling.reconstructJobs(jobs);

        int totalValue = selected.stream().mapToInt(Job::value).sum();
        assertEquals(optimo, totalValue,
                "La suma de valores de los trabajos reconstruidos debe ser igual al óptimo");
    }

    @Test
    @DisplayName("Lista vacía → valor 0")
    void listaVacia() {
        assertEquals(0, WeightedScheduling.solve(Collections.emptyList()));
        assertTrue(WeightedScheduling.reconstructJobs(Collections.emptyList()).isEmpty());
    }

    @Test
    @DisplayName("Lista null → valor 0")
    void listaNula() {
        assertEquals(0, WeightedScheduling.solve(null));
        assertTrue(WeightedScheduling.reconstructJobs(null).isEmpty());
    }

    @Test
    @DisplayName("Trabajos completamente solapados — se elige el de mayor valor")
    void todosSolapados() {
        List<Job> jobs = List.of(
                new Job(0, 10, 5),
                new Job(0, 10, 20),
                new Job(0, 10, 15)
        );
        assertEquals(20, WeightedScheduling.solve(jobs));

        List<Job> selected = WeightedScheduling.reconstructJobs(jobs);
        assertEquals(1, selected.size());
        assertEquals(20, selected.get(0).value());
    }

    @Test
    @DisplayName("Caso mixto: combinar trabajos no solapados es mejor que uno grande")
    void combinacionMejorQueUnoGrande() {
        // Trabajo grande [0,10) valor=50
        // Dos trabajos pequeños [0,3) valor=30 + [5,8) valor=30 = 60
        List<Job> jobs = List.of(
                new Job(0, 3, 30),
                new Job(5, 8, 30),
                new Job(0, 10, 50)
        );
        assertEquals(60, WeightedScheduling.solve(jobs));

        List<Job> selected = WeightedScheduling.reconstructJobs(jobs);
        assertEquals(2, selected.size());
        int totalValue = selected.stream().mapToInt(Job::value).sum();
        assertEquals(60, totalValue);
    }

    @Test
    @DisplayName("Trabajos con solapamiento parcial — selección óptima")
    void solapamientoParcial() {
        List<Job> jobs = List.of(
                new Job(1, 4, 5),
                new Job(3, 6, 6),
                new Job(5, 8, 8),
                new Job(7, 10, 4)
        );
        // Opciones: {1-4, 5-8} = 13, {1-4, 7-10} = 9, {3-6, 7-10} = 10
        assertEquals(13, WeightedScheduling.solve(jobs));
    }

    @Test
    @DisplayName("Los trabajos reconstruidos no se solapan entre sí")
    void reconstruccionSinSolapamiento() {
        List<Job> jobs = List.of(
                new Job(1, 4, 5),
                new Job(3, 6, 6),
                new Job(5, 8, 8),
                new Job(7, 10, 4),
                new Job(0, 12, 20)
        );
        List<Job> selected = WeightedScheduling.reconstructJobs(jobs);

        // Verificar que ningún par de trabajos seleccionados se solapa
        for (int i = 0; i < selected.size(); i++) {
            for (int j = i + 1; j < selected.size(); j++) {
                Job a = selected.get(i);
                Job b = selected.get(j);
                assertTrue(a.finish() <= b.start() || b.finish() <= a.start(),
                        "Trabajos seleccionados no deben solaparse: " + a + " y " + b);
            }
        }
    }
}
