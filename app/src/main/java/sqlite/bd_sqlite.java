package sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import technow.com.vision.Imagen;

/**
 * Created by Tautvydas on 05/04/2016.
 */
public class bd_sqlite extends SQLiteOpenHelper{

    private static String TABLA_BD="create table if not exists vision (id integer primary key AUTOINCREMENT,descripcion text,path text,fecha text ) ";

    /**
     * Constructor
     * @param context contexto en el que trabaja
     * @param name nombre
     * @param version version BD
     */
    public bd_sqlite(Context context,String name,int version) {
        super(context,name, null, version);
    }

    /**
     * Se ejecuta solo una vez cuando se instala la aplicación
     * @param db base de datos
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_BD);
    }

    /**
     * Método cuando se debe de actualizar la base de datos
     * @param db base de datos
     * @param oldVersion versión vieja
     * @param newVersion nueva versión
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Método que inserta imagen
     * @param context contexto con el que se trabaja
     * @param descripcion descripcion de la imagen
     * @param path dirección donde se almacena la imagen
     * @param fecha fecha de la creación de la imagen
     */
    public void insertarImagen(Context context,String descripcion,String path,String fecha){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into vision (descripcion,path,fecha) values ('"+descripcion+"','"+path+"','"+fecha+"')");
        db.close();
    }

    /**
     * Carga la lista en caso de que haya alguna imagen almacenada
     * @param context contexto
     * @param lista lista con todas las imagenes
     * @return lista cargada con las imagenes
     */
    public ArrayList<Imagen> obtenerImagenes(Context context, ArrayList<Imagen> lista){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select descripcion,path,fecha from vision",null);
        while(cursor.moveToNext()){
            Imagen imagen = new Imagen(cursor.getString(0),cursor.getString(1),cursor.getString(2));
            lista.add(imagen);
        }
        return lista;
    }

    /**
     * Eliminamos la imagen de la base de datos
     * @param path ruta de la imagen a eliminar
     */
    public void eliminarImagen(String path){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from vision where path='"+path+"'");
    }
}
