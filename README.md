# 🌌 La Federación Galáctica de Recursos — Simulación Concurrente y Distribuida

Proyecto de programación avanzada en Java que simula un sistema de explotación y gestión de recursos naturales a escala intergaláctica. Desarrollado como práctica de la asignatura, abarca dos ámbitos principales: **programación concurrente** (hilos, semáforos, locks y conditions) y **programación distribuida** (Java RMI).

---

## 📖 Descripción general

La simulación recrea un escenario postbélico en el que la Federación Unida de Planetas restablece el comercio entre cinco planetas mineros y las diez razas de la alianza. Tres tipos de actores actúan en paralelo sobre un estado compartido de zonas y recursos:

- **Delegados comerciales (DXXX):** recogen recursos de planetas y los depositan en depósitos orbitales.
- **Patrullas federales (PXXX):** defienden zonas frente a los ataques de saqueadores.
- **Saqueadores espaciales (SXXX):** atacan planetas y depósitos para robar recursos o expulsar delegados.

El sistema incluye una **interfaz gráfica Swing** para el servidor y un **cliente remoto** independiente que permite monitorizar y controlar la simulación a distancia mediante RMI.

---

## 🗂️ Estructura del proyecto

```
LaFederacionGalacticaDeRecursos-Java/
├── src/main/java/
│   ├── Modelo/           # Clases de dominio: actores, zonas, depósitos, estado global
│   ├── Concurrencia/     # Hilos activos y clases de sincronización
│   ├── GUI/              # Interfaz gráfica Swing del servidor
│   ├── Log/              # Sistema de registro centralizado (federacion_galactica.txt)
│   ├── Remoto/           # Interfaz RMI, implementación remota y DTOs
│   ├── InterfazCliente/  # Cliente remoto con su propia ventana Swing
│   └── Main/             # Punto de entrada y configuración inicial
├── federacion_galactica.txt   # Log de eventos generado en ejecución
├── pom.xml                    # Configuración Maven
└── MemoriaExplicativaFederacionGalacticaDeRecursosSimulacion.pdf
```

---

## 🧩 Paquetes principales

### `Modelo`
Clases de dominio puras, sin lógica concurrente. Contiene:
- `Zona` (abstracta) → `PlanetaMinero`, `DepositoOrbital`, `CentroCoordinacionFederal`, `HangarPatrullas`, `BaseSaqueadores`, `ZonaRecuperacion`
- `Actor` (abstracta) → `DelegadoComercial`, `PatrullaFederal`, `Saqueador`
- `EstadoGalaxia` — Singleton que centraliza todas las zonas, actores y generadores de IDs únicos
- `TipoRecurso` — Enumerado con los tres tipos: `CRISTAL`, `MINERAL`, `PLASMA`

### `Concurrencia`
Hilos activos y sincronización:
- `HiloDelegado`, `HiloPatrulla`, `HiloSaqueador` — ciclo de vida completo de cada actor
- `GeneradorDelegados`, `GeneradorPatrullas`, `GeneradorSaqueadores` — generan actores periódicamente respetando límites y umbrales
- `ControlSimulacion` — pausa/reanudación centralizada con `ReentrantLock` + `Condition`
- `SincronizacionZonas` — acceso por aforo mediante `Semaphore` justo (fair)
- `SincronizacionRecursos` — gestión thread-safe de depósitos con `ReentrantLock` + `Condition`
- `SincronizacionSaqueadores` — coordinación de ataques con flags protegidos por lock

### `GUI`
Interfaz gráfica Swing del servidor. Muestra en tiempo real el estado de zonas, recursos y actores activos.

### `Log`
- `LoggerFederacion` (interfaz) + `LoggerFederacionImpl` — registro centralizado de eventos con `ReentrantLock`. Escribe en `federacion_galactica.txt` de forma atómica. Implementa el patrón Singleton.

### `Remoto`
Módulo distribuido basado en Java RMI:
- `InterfazMonitorRemoto` — interfaz remota que extiende `java.rmi.Remote`
- `MonitorRemotoImpl` — implementación que extiende `UnicastRemoteObject`
- `EstadoRecursosDTO`, `EstadoZonasDTO` — objetos serializables para la transferencia de datos (patrón DTO)

### `InterfazCliente`
Cliente remoto independiente con ventana Swing propia. Se conecta al registro RMI del servidor, actualiza la interfaz automáticamente de forma periódica y permite vaciar depósitos de forma remota.

### `Main`
Punto de entrada de la aplicación. Inicializa el estado de la galaxia, lanza los generadores, arranca el servidor RMI y abre la interfaz gráfica.

---

## ⚙️ Mecanismos de sincronización

| Mecanismo | Uso |
|---|---|
| `Semaphore` (fair) | Control de aforo en zonas (planetas: máx. 4, depósitos: máx. 3) |
| `ReentrantLock` + `Condition` | Gestión de depósitos orbitales (depositar/saquear/refuerzos) |
| `ReentrantLock` | Protección del log y de los flags de ataque de saqueadores |
| `volatile boolean` | Flag de pausa en `ControlSimulacion` |
| `AtomicInteger` | Generación de IDs únicos en `EstadoGalaxia` |
| Singleton | `EstadoGalaxia` y `LoggerFederacionImpl` |

---

## 🌐 Módulo distribuido (RMI)

El servidor expone el objeto remoto `MonitorRemotoImpl` en el registro RMI local. El cliente puede:
- Consultar el número de actores en cada zona en tiempo real
- Ver el estado de los tres depósitos orbitales
- Vaciar de forma independiente cada depósito

Los datos se transfieren mediante DTOs serializables (`EstadoRecursosDTO`, `EstadoZonasDTO`), evitando que el cliente acceda directamente a los objetos de dominio del servidor.

---

## 🚀 Ejecución

### Requisitos
- Java 17 o superior
- Maven 3.6+
- IDE recomendado: NetBeans o IntelliJ IDEA

### Pasos

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/David17es/LaFederacionGalacticaDeRecursos-Java.git
   cd LaFederacionGalacticaDeRecursos-Java
   ```

2. Compilar con Maven:
   ```bash
   mvn clean compile
   ```

3. Arrancar el servidor (simulación principal):
   ```bash
   mvn exec:java -Dexec.mainClass="Main.Main"
   ```

4. En otra terminal, arrancar el cliente remoto:
   ```bash
   mvn exec:java -Dexec.mainClass="InterfazCliente.ClienteRemotoMain"
   ```

> El servidor debe estar en ejecución antes de lanzar el cliente. El registro RMI se inicia automáticamente en el puerto por defecto (1099).

---

## 📋 Parámetros de la simulación

| Parámetro | Valor |
|---|---|
| Máximo de delegados activos | 20 |
| Intervalo de generación de delegados | 2 segundos |
| Máximo de patrullas activas | 20 |
| Umbral de refuerzo (cristal / mineral / plasma) | 150 / 100 / 75 |
| Máximo de saqueadores activos | 40 |
| Intervalo de aparición de saqueadores | 10–20 segundos |
| Aforo máximo por planeta | 4 delegados |
| Aforo máximo por depósito | 3 delegados |
| Probabilidad de objetivo (depósito / planeta) | 70% / 30% |
| Probabilidad de victoria en combate | 50% cada parte |
| Tiempo de recuperación de delegado expulsado | 10–15 segundos |
| Tiempo de espera del saqueador derrotado | 20 segundos |
| Umbral de alerta de depósito bajo | 10% de capacidad |

---

## 📄 Documentación

La memoria explicativa completa del proyecto está disponible en el repositorio:  
[`MemoriaExplicativaFederacionGalacticaDeRecursosSimulacion.pdf`](./MemoriaExplicativaFederacionGalacticaDeRecursosSimulacion.pdf)

Incluye análisis de alto nivel, diseño del sistema, descripción de clases, diagramas de clases y consideraciones de diseño.

---

## 🛠️ Tecnologías utilizadas

- **Java 17**
- **Java Concurrency API** — `Thread`, `Semaphore`, `ReentrantLock`, `Condition`, `AtomicInteger`
- **Java RMI** — `Remote`, `UnicastRemoteObject`, `Registry`
- **Java Swing** — interfaz gráfica del servidor y del cliente remoto
- **Maven** — gestión de dependencias y compilación
