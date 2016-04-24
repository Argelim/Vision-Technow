package technow.com.vision;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import eventos.AbrirImagen;

/**
 * Adaptador del recyclerView, almacena todos los layouts que se van
 * generando
 */
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
        holder.imageView.setOnClickListener(new AbrirImagen(context,position));
        Log.d("VISTA","SE HA CREADO LA VISTA");
    }

    @Override
    public int getItemCount() {
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
