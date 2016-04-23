package eventos;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import technow.com.vision.R;
import vista.DialogoDeOpciones;

/**
 * Created by Tautvydas on 22/04/2016.
 */
public class GestureDetectorEvent implements android.view.GestureDetector.OnGestureListener, android.view.GestureDetector.OnDoubleTapListener {

    private FragmentManager fragmentManager;
    private Context context;
    private RecyclerView recyclerView;

    public GestureDetectorEvent(FragmentManager fragmentManager, Context context,RecyclerView recyclerView) {
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        DialogoDeOpciones dialogoDeOpciones = new DialogoDeOpciones(context,fragmentManager,recyclerView);
        dialogoDeOpciones.show(fragmentManager,"dialogo");
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }


}
