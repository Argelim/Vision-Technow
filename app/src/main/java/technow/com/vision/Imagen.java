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

    private ImageView imageView;
    private String descripcion,fecha,path;
    private int id;
    private RequestCreator requestCreator;
    private RecyclerView.ViewHolder viewHolder;
    private Button bar;
    private ProgressBar progressBar;


    public Imagen(String descripcion, String path, String fecha) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.path = path;
    }

    public Imagen() {
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

    public RecyclerView.ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(RecyclerView.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public Button getButton() {
        return bar;
    }

    public void setButton(Button bar) {
        this.bar = bar;
    }
}
