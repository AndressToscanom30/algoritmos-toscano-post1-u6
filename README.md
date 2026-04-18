# Toscano — Post-Contenido 1 — Unidad 6

## Descripción

Este laboratorio implementa en Java 17+ tres algoritmos clásicos de programación dinámica para problemas de optimización:

- **Knapsack 0/1** (`solve01`): selecciona un subconjunto de objetos que maximiza el valor total sin exceder la capacidad, usando tabla DP bidimensional O(n·W).
- **Knapsack 0/1 optimizado en memoria** (`solveMemOpt`): misma lógica con un solo arreglo 1D de tamaño O(W), iterando de derecha a izquierda para preservar la propiedad 0/1.
- **Unbounded Knapsack** (`solveUnbounded`): variante con repetición ilimitada de objetos, arreglo 1D iterando de izquierda a derecha.
- **Weighted Interval Scheduling** (`solve`, `reconstructJobs`): selecciona trabajos no solapados que maximicen el valor total, con cálculo de p(j) mediante búsqueda binaria y reconstrucción de la solución.

Cada algoritmo incluye Javadoc completo con descripción, `@param`, `@return` y complejidad O().

---

## Estructura del Proyecto

```
toscano-post1-u6/
├── pom.xml
├── README.md
└── src/
    ├── main/java/dp/
    │   ├── Knapsack.java               # solve01, solveUnbounded, solveMemOpt, reconstruct
    │   ├── WeightedScheduling.java      # solve, reconstructJobs, computeP
    │   └── bench/
    │       ├── KnapsackBench.java       # Benchmarks JMH (solve01 vs solveMemOpt)
    │       └── ManualBenchmark.java     # Benchmark manual alternativo
    └── test/java/dp/
        ├── KnapsackTest.java            # 18 tests (solve01, solveUnbounded, solveMemOpt, reconstruct)
        └── SchedulingTest.java          # 10 tests (solve, reconstructJobs, casos borde)
```

---

## Componentes Implementados

### `Knapsack.java`
| Método | Descripción | Tiempo | Espacio |
|--------|-------------|--------|---------|
| `solve01` | Knapsack 0/1 con tabla 2D | O(n·W) | O(n·W) |
| `reconstruct` | Backtracking sobre tabla 2D para obtener objetos seleccionados | O(n·W) | O(n·W) |
| `solveUnbounded` | Mochila con repetición ilimitada, arreglo 1D izquierda→derecha | O(n·W) | O(W) |
| `solveMemOpt` | Knapsack 0/1 con arreglo 1D, iteración derecha→izquierda | O(n·W) | O(W) |

### `WeightedScheduling.java`
| Método | Descripción | Tiempo | Espacio |
|--------|-------------|--------|---------|
| `solve` | Valor máximo de trabajos compatibles | O(n log n) | O(n) |
| `reconstructJobs` | Lista de trabajos en la solución óptima | O(n log n) | O(n) |
| `computeP` | Cálculo de p(j) con búsqueda binaria | O(n log n) | O(n) |

---

## Instrucciones de Compilación y Ejecución

### Requisitos
- Java 17+ (`java --version`)
- Maven 3.9+ (`mvn --version`)

### Compilar
```bash
mvn clean compile
```

### Ejecutar tests
```bash
mvn test
```

### Ejecutar benchmarks JMH
```bash
mvn clean package -DskipTests
java -jar target/benchmarks.jar
```

O con el benchmark manual:
```bash
mvn compile
mvn exec:java -Dexec.mainClass=dp.bench.ManualBenchmark
```

---

## Resultados del Benchmark JMH

Comparación de `solve01` (tabla 2D, O(n·W) espacio) vs `solveMemOpt` (arreglo 1D, O(W) espacio).  
Datos generados con semilla fija `Random(42)`, pesos en `[1, W/4]`, valores en `[1, 100]`.

| Benchmark     |     n |      W | Mode | Cnt | Score (μs/op) |
| ------------- | ----: | -----: | ---- | --: | ------------: |
| `bench01`     |   100 |  1 000 | avgt |   5 |       103.147 |
| `bench01`     |   500 |  1 000 | avgt |   5 |       826.515 |
| `bench01`     | 1 000 |  1 000 | avgt |   5 |     2 483.062 |
| `bench01`     |   100 |  5 000 | avgt |   5 |       822.135 |
| `bench01`     |   500 |  5 000 | avgt |   5 |     4 404.879 |
| `bench01`     | 1 000 |  5 000 | avgt |   5 |     8 784.684 |
| `bench01`     |   100 | 10 000 | avgt |   5 |     1 390.247 |
| `bench01`     |   500 | 10 000 | avgt |   5 |     8 140.165 |
| `bench01`     | 1 000 | 10 000 | avgt |   5 |    15 705.083 |
| `benchMemOpt` |   100 |  1 000 | avgt |   5 |        34.702 |
| `benchMemOpt` |   500 |  1 000 | avgt |   5 |       140.805 |
| `benchMemOpt` | 1 000 |  1 000 | avgt |   5 |       377.193 |
| `benchMemOpt` |   100 |  5 000 | avgt |   5 |       279.945 |
| `benchMemOpt` |   500 |  5 000 | avgt |   5 |       738.755 |
| `benchMemOpt` | 1 000 |  5 000 | avgt |   5 |     1 959.264 |
| `benchMemOpt` |   100 | 10 000 | avgt |   5 |       553.335 |
| `benchMemOpt` |   500 | 10 000 | avgt |   5 |     1 690.710 |
| `benchMemOpt` | 1 000 | 10 000 | avgt |   5 |     3 291.951 |

---

## Análisis de Resultados (Trade-off Espacio/Tiempo)

Los resultados del benchmark confirman que ambas variantes siguen la complejidad teórica O(n·W), pero la versión optimizada en memoria (solveMemOpt) es consistentemente más rápida en la práctica. Para el caso más pequeño (n=100, W=1000), solveMemOpt resulta aproximadamente 2.97 veces más rápida que solve01 (34.7 μs vs 103.1 μs). A medida que el problema crece a n=500/W=5000, la ventaja se mantiene en un factor de 5.96× (738.8 μs vs 4404.9 μs), y para n=1000/W=10000 aumenta ligeramente a 4.77× (3292.0 μs vs 15705.1 μs).

Esta diferencia se explica principalmente por el patrón de acceso a memoria. La tabla 2D de solve01 requiere n×W posiciones (por ejemplo, 10 millones de enteros para n=1000, W=10000, equivalentes a ~38 MB), lo que provoca frecuentes fallos de caché L2/L3 al recorrer filas completas. En contraste, solveMemOpt trabaja con un solo arreglo de W+1 posiciones (~39 KB para W=10000), que cabe completamente en la caché L1, mejorando significativamente la localidad espacial y reduciendo latencias.

La escalabilidad de ambos métodos es coherente con O(n·W): al multiplicar n×W por 25 (de 100×1000 a 500×5000), solve01 escala aproximadamente ×42.7 (103.1 → 4404.9 μs) mientras que solveMemOpt escala ×21.3 (34.7 → 738.8 μs). Al multiplicar por 100 (de 100×1000 a 1000×10000), solve01 escala ×152.3 (103.1 → 15705.1 μs) y solveMemOpt ×94.8 (34.7 → 3292.0 μs). Estas desviaciones frente al factor teórico ideal se deben principalmente a efectos de jerarquía de memoria, caché y posibles costos asociados al manejo de grandes estructuras (como arreglos bidimensionales) en solve01.

En cuanto a las implicaciones prácticas, solveMemOpt debe preferirse siempre que no se requiera reconstruir la solución, ya que ofrece menor tiempo de ejecución y un consumo de memoria drásticamente inferior. Si se necesita conocer qué objetos fueron seleccionados, es necesario usar solve01 con la tabla 2D completa, ya que solveMemOpt descarta la información histórica necesaria para el backtracking. Este trade-off es clave: elegir entre eficiencia (1D) y capacidad de reconstrucción (2D) dependiendo de los requerimientos del problema.

---

## Casos de Prueba

### KnapsackTest (18 tests)
- **solve01:** W=0, ningún objeto cabe, todos caben, óptimo sin el de mayor valor, sin objetos, caso clásico
- **solveMemOpt:** equivalencia con solve01 para múltiples valores de W, W=0, no reutilización de objetos, caso grande
- **solveUnbounded:** W=0, repetición, ninguno cabe, mejor ratio, unbounded ≥ 0/1
- **reconstruct:** suma coincide, W=0 no selecciona, objetos correctos

### SchedulingTest (10 tests)
- Un solo trabajo, todos compatibles, greedy EDF subóptimo, reconstructJobs = solve, lista vacía, null, todos solapados, combinación mejor que uno grande, solapamiento parcial, no-solapamiento en reconstrucción
