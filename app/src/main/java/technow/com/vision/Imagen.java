package technow.com.vision;

import android.widget.ImageView;

/**
 * Created by Tautvydas on 04/04/2016.
 */
public class Imagen {

    private ImageView imageView;
    private String descripcion,titulo;
    private int id;

    public Imagen(String descripcion, String titulo, int id) {
        this.descripcion = descripcion;
        this.titulo = titulo;
        this.id = id;
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}