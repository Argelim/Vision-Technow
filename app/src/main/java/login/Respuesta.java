package login;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tautvydas on 22/04/2016.
 */
public class Respuesta {


    private String id;
    private String fecha_creacion;
    private String imagen;
    private String descripcion;
    private String restantes;
    private String owner;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRestantes() {
        return restantes;
    }

    public void setRestantes(String restantes) {
        this.restantes = restantes;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
