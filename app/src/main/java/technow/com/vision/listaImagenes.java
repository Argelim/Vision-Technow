package technow.com.vision;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;


public class listaImagenes extends RecyclerView.Adapter<listaImagenes.ViewHolder>  {

    private RecyclerView recyclerView;
    private Context context;
    private static int PULSADO = 0;
    private static int SOLTADO = 1;
    private static int SCROLL = 3;


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
                Log.d("PASAR",String.valueOf(event.getAction()));
                if (event.getAction()==PULSADO){
                    v.setBackgroundResource(R.drawable.borde_encima);
                }else if(event.getAction()==SOLTADO){
                    v.setBackgroundResource(R.drawable.borde);
                }else if(event.getAction()==SCROLL){
                    v.setBackgroundResource(R.drawable.borde);
                }
                return true;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildLayoutPosition(v);
                Uri imagen = Uri.fromFile(new File(MainActivity.imagens.get(pos).getPath()));
                Intent i = new Intent(Intent.ACTION_VIEW, imagen);
                i.setType("image/*");
                context.startActivity(i);
            }
        });
        Log.d("DEBUG","Se ha infado el layout");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(MainActivity.imagens.get(position).getDescripcion());
        MainActivity.imagens.get(position).getRequestCreator().into(holder.imageView);
        holder.fecha.setText(MainActivity.imagens.get(position).getFecha());
        Log.d("DEBUG",MainActivity.imagens.get(position).getDescripcion());
    }

    @Override
    public int getItemCount() {
        Log.d("DEBUG",String.valueOf(MainActivity.imagens.size()));
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
            Log.d("DEBUG","Se le referenciado");
        }
    }

    public void addItem(Imagen imagen){
        MainActivity.imagens.add(0,imagen);
        notifyItemInserted(0);
    }

    public void removeItem(int pos){
        MainActivity.imagens.remove(pos);
        notifyItemRemoved(pos);
    }
}
