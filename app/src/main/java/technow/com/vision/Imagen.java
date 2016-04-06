package technow.com.vision;

import android.widget.ImageView;

import com.squareup.picasso.RequestCreator;

/**
 * Created by Tautvydas on 04/04/2016.
 */
public class Imagen {

    private ImageView imageView;
    private String descripcion,fecha,path;
    private int id;
    private RequestCreator requestCreator;

    public RequestCreator getRequestCreator() {
        return requestCreator;
    }

    public void setRequestCreator(RequestCreator requestCreator) {
        this.requestCreator = requestCreator;
    }

    public String getFecha() {
        return fecha;
    }

    public String getPath() {
        return path;
    }

    public Imagen(String descripcion, String path, String fecha) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.path = path;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
