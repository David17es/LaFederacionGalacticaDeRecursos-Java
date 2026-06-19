package Log;

//Define el contrato minimo que debe cumplir cualquier implementacion del logger del sistema
public interface LoggerFederacion {
    
    //Registra un evento en el log
    void registrarEvento(String descripcion);
}