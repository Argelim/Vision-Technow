package sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.ArrayList;

import technow.com.vision.CircleTransform;
import technow.com.vision.Imagen;
import technow.com.vision.listaImagenes;
import vista.UserBD;

/**
 * Created by Tautvydas on 05/04/2016.
 */
public class bd_sqlite extends SQLiteOpenHelper{

    private static String TABLA_VISION_BD="create table if not exists vision " +
            "(id integer primary key AUTOINCREMENT,descripcion text,path text,fecha text ) ";
    private static String TABLA_USUARIOS_BD ="create table if not exists usuario (id integer primary key AUTOINCREMENT,usuario text,passwd text )";

    /**
     * Constructor
     * @param context contexto en el que trabaja
     * @param name nombre
     * @param version version BD
     */
    public bd_sqlite(Context context,String name,int version) {
        super(context, name, null, version);
    }

    /**
     * Se ejecuta solo una vez cuando se instala la aplicación
     * @param db base de datos
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_VISION_BD);
        db.execSQL(TABLA_USUARIOS_BD);
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
     * @return lista cargada con las imagenes
     */
    public void obtenerImagenes(final Context context, final listaImagenes listaImagenes){

        new AsyncTask<Void,Imagen,SQLiteDatabase>(){

            @Override
            protected SQLiteDatabase doInBackground(Void... params) {
                SQLiteDatabase db = getReadableDatabase();
                Cursor cursor = db.rawQuery("select descripcion,path,fecha from vision",null);
                while(cursor.moveToNext()){
                    Imagen imagen = new Imagen(cursor.getString(0),cursor.getString(1),cursor.getString(2));
                    File file = new File(imagen.getPath());
                    RequestCreator requestCreator = Picasso.with(context).load(file).resize(50,50).transform(new CircleTransform());
                    imagen.setRequestCreator(requestCreator);

                    publishProgress(imagen);
                }
                return db;
            }

            @Override
            protected void onProgressUpdate(Imagen... values) {
                listaImagenes.addItem(values[0]);
            }

            @Override
            protected void onPostExecute(SQLiteDatabase aVoid) {
                aVoid.close();
            }
        }.execute();

    }

    /**
     * Eliminamos la imagen de la base de datos
     * @param path ruta de la imagen a eliminar
     */
    public void eliminarImagen(String path){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from vision where path='"+path+"'");
        db.close();
    }


    public void insertarUsuario(UserBD user){
        SQLiteDatabase db = getWritableDatabase();
        //eliminamos el usuario actual para almacenar el nuevo
        db.execSQL("delete from usuario");
        //insertamos el nuevo usuario
        db.execSQL("insert into usuario (usuario,passwd) values ('"+user.getUser()+"','"+user.getPass()+"')");
        db.close();
    }

    public void EliminaUsuario(UserBD user){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from usuario where usuario = '"+user.getUser()+"'");
        db.close();
    }

    public UserBD datosUsuario (UserBD s){

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select id,usuario,passwd from usuario",null);

        while (cursor.moveToNext()){
            s.setUser(cursor.getString(1));
            s.setPass(cursor.getString(2));
        }
        db.close();

        return s;
    }
}
