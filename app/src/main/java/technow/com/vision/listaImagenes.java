package technow.com.vision;

import android.content.Context;
import android.support.v7.widget.RecyclerView;;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class listaImagenes extends RecyclerView.Adapter<listaImagenes.ViewHolder>  {

    private RecyclerView recyclerView;
    private Context context;
    private static int PULSADO = 0;
    private static int SOLTADO = 1;
    private static int SCROLL = 3;

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
        Log.d("DEBUG","Se ha infado el layout");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(MainActivity.imagens.get(position).getDescripcion());
        Log.d("DEBUG",MainActivity.imagens.get(position).getDescripcion());
    }

    @Override
    public int getItemCount() {
        Log.d("DEBUG",String.valueOf(MainActivity.imagens.size()));
        return MainActivity.imagens.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            //imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView=(TextView) itemView.findViewById(R.id.textView2);
            Log.d("DEBUG","Se le referenciado");
        }
    }
}
