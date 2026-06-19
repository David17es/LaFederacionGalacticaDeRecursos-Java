package Modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Contenedor global del estado de la simulacion galactica
 * Contiene:
 *   - Zonas del sistema (planetas, depositos, base, hangar, etc.)
 *   - Listas de actores activos (delegados, patrullas, saqueadores)
 *   - Generadores de IDs unicos
 */
public class EstadoGalaxia {

    // --- Instancia Singleton ---

    private static EstadoGalaxia instancia;

    public static synchronized EstadoGalaxia getInstancia() {
        if (instancia == null) {
            instancia = new EstadoGalaxia();
        }
        return instancia;
    }

    // =========================================================
    // ZONAS
    // =========================================================

    private final CentroCoordinacionFederal centroCoordinacion;
    private final HangarPatrullas hangarPatrullas;
    private final BaseSaqueadores baseSaqueadores;
    private final ZonaRecuperacion zonaRecuperacion;

    // Planetas mineros
    private final PlanetaMinero planetaCryon;
    private final PlanetaMinero planetaVelora;
    private final PlanetaMinero planetaFerrum;
    private final PlanetaMinero planetaDrax;
    private final PlanetaMinero planetaIgnis;

    // Lista unificada de planetas
    private final List<PlanetaMinero> planetas;

    // Depositos orbitales
    private final DepositoOrbital depositoCristal;
    private final DepositoOrbital depositoMineral;
    private final DepositoOrbital depositoPlasma;

    // Lista unificada de depositos
    private final List<DepositoOrbital> depositos;

    // =========================================================
    // ACTORES ACTIVOS
    // =========================================================

    private final List<DelegadoComercial> delegados;
    private final List<PatrullaFederal> patrullas;
    private final List<Saqueador> saqueadores;

    // =========================================================
    // GENERADORES DE IDs UNICOS (thread-safe con AtomicInteger)
    // =========================================================

    private final AtomicInteger contadorDelegados;
    private final AtomicInteger contadorPatrullas;
    private final AtomicInteger contadorSaqueadores;

    // =========================================================
    // CONSTRUCTOR PRIVADO
    // =========================================================

    private EstadoGalaxia() {

        // --- Zonas generales ---
        centroCoordinacion = new CentroCoordinacionFederal();
        hangarPatrullas = new HangarPatrullas();
        baseSaqueadores = new BaseSaqueadores();
        zonaRecuperacion = new ZonaRecuperacion();

        // --- Planetas mineros ---
        planetaCryon = new PlanetaMinero("Cryon",  TipoRecurso.CRISTAL);
        planetaVelora = new PlanetaMinero("Velora", TipoRecurso.CRISTAL);
        planetaFerrum = new PlanetaMinero("Ferrum", TipoRecurso.MINERAL);
        planetaDrax = new PlanetaMinero("Drax",   TipoRecurso.MINERAL);
        planetaIgnis = new PlanetaMinero("Ignis",  TipoRecurso.PLASMA);

        planetas = new ArrayList<>();
        planetas.add(planetaCryon);
        planetas.add(planetaVelora);
        planetas.add(planetaFerrum);
        planetas.add(planetaDrax);
        planetas.add(planetaIgnis);

        // --- Depositos orbitales (nombre, recurso, capacidad max., unidades iniciales) ---
        depositoCristal = new DepositoOrbital("Depósito de Cristal", TipoRecurso.CRISTAL, 250, 100);
        depositoMineral = new DepositoOrbital("Depósito de Mineral", TipoRecurso.MINERAL, 200, 80);
        depositoPlasma = new DepositoOrbital("Depósito de Plasma", TipoRecurso.PLASMA,  150, 60);

        depositos = new ArrayList<>();
        depositos.add(depositoCristal);
        depositos.add(depositoMineral);
        depositos.add(depositoPlasma);

        // --- Listas de actores ---
        delegados = new ArrayList<>();
        patrullas = new ArrayList<>();
        saqueadores = new ArrayList<>();

        // --- Contadores de IDs ---
        contadorDelegados = new AtomicInteger(0);
        contadorPatrullas = new AtomicInteger(0);
        contadorSaqueadores = new AtomicInteger(0);
    }

    // =========================================================
    // GENERADORES DE IDs
    // =========================================================

    //Genera el siguiente ID unico para delegado
    public String generarIdDelegado() {
        return String.format("D%03d", contadorDelegados.incrementAndGet());
    }

    //Genera el siguiente ID unico para patrulla
    public String generarIdPatrulla() {
        return String.format("P%03d", contadorPatrullas.incrementAndGet());
    }

    //Genera el siguiente ID único para saqueador
    public String generarIdSaqueador() {
        return String.format("S%03d", contadorSaqueadores.incrementAndGet());
    }

    // =========================================================
    // GETTERS DE ZONAS
    // =========================================================

    public CentroCoordinacionFederal getCentroCoordinacion() {
        return centroCoordinacion;
    }

    public HangarPatrullas getHangarPatrullas() {
        return hangarPatrullas;
    }

    public BaseSaqueadores getBaseSaqueadores() {
        return baseSaqueadores;
    }

    public ZonaRecuperacion getZonaRecuperacion() {
        return zonaRecuperacion;
    }

    public PlanetaMinero getPlanetaCryon() {return planetaCryon;}
    public PlanetaMinero getPlanetaVelora() {return planetaVelora;}
    public PlanetaMinero getPlanetaFerrum() {return planetaFerrum;}
    public PlanetaMinero getPlanetaDrax() {return planetaDrax;}
    public PlanetaMinero getPlanetaIgnis() {return planetaIgnis;}

    public List<PlanetaMinero> getPlanetas() {
        return planetas;
    }

    public DepositoOrbital getDepositoCristal() {return depositoCristal;}
    public DepositoOrbital getDepositoMineral() {return depositoMineral;}
    public DepositoOrbital getDepositoPlasma() {return depositoPlasma;}

    public List<DepositoOrbital> getDepositos() {
        return depositos;
    }
    
    //Devuelve todas las zonas del sistema en orden fijo para el mapa
    public List<Zona> getTodasLasZonas() {
        List<Zona> zonas = new ArrayList<>();
        zonas.add(centroCoordinacion);
        zonas.addAll(planetas);   // lista de 3 planetas
        zonas.addAll(depositos); // lista de 3 depósitos
        zonas.add(hangarPatrullas);
        zonas.add(baseSaqueadores);
        zonas.add(zonaRecuperacion);
        return Collections.unmodifiableList(zonas);
    }

    // =========================================================
    // GETTERS Y METODOS DE ACTORES
    // =========================================================

    public synchronized void agregarDelegado(DelegadoComercial delegado) {
        delegados.add(delegado);
    }

    public synchronized void eliminarDelegado(DelegadoComercial delegado) {
        delegados.remove(delegado);
    }

    public synchronized List<DelegadoComercial> getDelegados() {
        return new ArrayList<>(delegados);
    }

    public synchronized int getNumeroDelegados() {
        return delegados.size();
    }

    public synchronized int getNumeroDelegadosActivos() {
        int activos = 0;
        for (DelegadoComercial d : delegados) {
            if (!d.isEnRecuperacion()) {
                activos++;
            }
        }
        return activos;
    }

    public synchronized void agregarPatrulla(PatrullaFederal patrulla) {
        patrullas.add(patrulla);
    }

    public synchronized void eliminarPatrulla(PatrullaFederal patrulla) {
        patrullas.remove(patrulla);
    }

    public synchronized List<PatrullaFederal> getPatrullas() {
        return new ArrayList<>(patrullas);
    }

    public synchronized int getNumeroPatrullas() {
        return patrullas.size();
    }

    public synchronized void agregarSaqueador(Saqueador saqueador) {
        saqueadores.add(saqueador);
    }

    public synchronized void eliminarSaqueador(Saqueador saqueador) {
        saqueadores.remove(saqueador);
    }

    public synchronized List<Saqueador> getSaqueadores() {
        return new ArrayList<>(saqueadores);
    }

    public synchronized int getNumeroSaqueadores() {
        return saqueadores.size();
    }
}