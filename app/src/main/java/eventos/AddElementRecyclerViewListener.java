package eventos;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.View;

import technow.com.vision.MainActivity;

/**
 * Created by Tautvydas on 22/04/2016.
 */
public class AddElementRecyclerViewListener implements RecyclerView.OnChildAttachStateChangeListener {


    private FragmentManager fragmentManager;
    private GestureDetector gestureDetector;
    private RecyclerView recyclerView;


    public AddElementRecyclerViewListener(FragmentManager fragmentManager, GestureDetector gestureDetector,RecyclerView recyclerView) {
        this.fragmentManager = fragmentManager;
        this.gestureDetector = gestureDetector;
        this.recyclerView=recyclerView;
    }

    @Override
    public void onChildViewAttachedToWindow(View view) {
        if (MainActivity.bandera){
            view.requestFocus();
        }
        view.setOnTouchListener(new TouchEvent(gestureDetector,recyclerView));
    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {

    }
}
