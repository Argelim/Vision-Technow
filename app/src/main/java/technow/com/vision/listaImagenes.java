package technow.com.vision;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;;
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

import java.io.File;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static android.support.v7.widget.helper.ItemTouchHelper.*;


public class listaImagenes extends RecyclerView.Adapter<listaImagenes.ViewHolder> {

    private RecyclerView recyclerView;
    private Context context;
    private static int PULSADO = 0;
    private static int SOLTADO = 1;
    private static int SCROLL = 3;
    private static int posicion;


    public listaImagenes() {
    }


    public listaImagenes(RecyclerView recyclerView, Context context) {
        this.recyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista,parent,false);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("PASAR", String.valueOf(event.getAction()));
                if (event.getAction() == PULSADO) {
                    v.setBackgroundResource(R.drawable.borde_encima);
                } else if (event.getAction() == SOLTADO) {
                    v.setBackgroundResource(R.drawable.borde);
                } else if (event.getAction() == SCROLL) {
                    v.setBackgroundResource(R.drawable.borde);
                }
                return true;
            }
        });

        Log.d("DEBUG", "Se ha infado el layout");
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.textView.setText(MainActivity.imagens.get(position).getDescripcion());
        Log.d("PRUEBA", String.valueOf(position));
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



    public static class ViewHolder extends RecyclerView.ViewHolder {

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
