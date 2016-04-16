package technow.com.vision;

import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.RequestCreator;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by Tautvydas on 04/04/2016.
 */
public class Imagen {

    private String descripcion,fecha,path;
    private int id;
    private RequestCreator requestCreator;
    private String Base64Imagen;

    public String getBase64Imagen() {
        return Base64Imagen;
    }

    public void setBase64Imagen(String base64Imagen) {
        Base64Imagen = base64Imagen;
    }

    public Imagen(String descripcion, String path, String fecha) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.path = path;
    }

    public Imagen() {
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RequestCreator getRequestCreator() {
        return requestCreator;
    }

    public void setRequestCreator(RequestCreator requestCreator) {
        this.requestCreator = requestCreator;
    }

}
