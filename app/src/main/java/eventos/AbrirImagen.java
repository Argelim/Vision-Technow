package eventos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import java.io.File;

import technow.com.vision.MainActivity;

/**
 * Created by Tautvydas on 23/04/2016.
 */
public class AbrirImagen implements View.OnClickListener {

    private Context context;
    private int position;

    public AbrirImagen(Context context,int position) {
        this.context = context;
        this.position= position;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        Uri imagen = Uri.fromFile(new File(MainActivity.imagens.get(position).getPath()));
        i.setDataAndType(imagen, "image/*");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
