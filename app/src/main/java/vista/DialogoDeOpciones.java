package vista;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import eventos.TouchEvent;
import technow.com.vision.MainActivity;

/**
 * Created by Tautvydas on 22/04/2016.
 */
public class DialogoDeOpciones extends DialogFragment  {

    private Context context;
    private FragmentManager fragmentManager;
    private RecyclerView recyclerView;

    public DialogoDeOpciones(Context context, FragmentManager fragmentManager,RecyclerView recyclerView) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.recyclerView=recyclerView;
    }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Desea eliminar la imagen seleccionada?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.imagens.remove(TouchEvent.posicionSeleccionada);
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            return builder.create();
        }




}
