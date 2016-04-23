package technow.com.vision;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;;
import android.support.v7.widget.RecyclerView.SimpleOnItemTouchListener;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.util.Base64;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;


public class listaImagenes extends RecyclerView.Adapter<listaImagenes.ViewHolder> {

    private RecyclerView recyclerView;
    private Context context;
    private FragmentManager fragmentManager;


    public listaImagenes() {
    }


    public listaImagenes(RecyclerView recyclerView, Context context,FragmentManager fragmentManager) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.fragmentManager=fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView.setText(MainActivity.imagens.get(position).getDescripcion());
        MainActivity.imagens.get(position).getRequestCreator().into(holder.imageView);
        holder.fecha.setText(MainActivity.imagens.get(position).getFecha());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri imagen = Uri.fromFile(new File(MainActivity.imagens.get(position).getPath()));
                i.setDataAndType(imagen, "image/*");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("DEBUG", String.valueOf(MainActivity.imagens.size()));
        return MainActivity.imagens.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView,fecha;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView2);
            textView=(TextView) itemView.findViewById(R.id.textViewDescripcion);
            fecha = (TextView) itemView.findViewById(R.id.textViewFecha);
        }

    }



    public void addItem(Imagen imagen){
        MainActivity.imagens.add(imagen);
        notifyItemInserted(MainActivity.imagens.size()-1);
    }

    public void removeItem(int pos){
        MainActivity.imagens.remove(MainActivity.imagens.size()-1);
        notifyItemRemoved(MainActivity.imagens.size()-1);
    }


}
