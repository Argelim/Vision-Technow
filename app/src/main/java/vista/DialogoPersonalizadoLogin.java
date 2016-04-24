package vista;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import login.Token;
import login.Usuario;
import sqlite.bd_sqlite;
import technow.com.vision.R;
import technow.com.vision.estados;


/**
 * Created by Tautvydas on 23/04/2016.
 */
public class DialogoPersonalizadoLogin extends DialogFragment {

    private Context context;
    private EditText usuario;
    private EditText contrasenia;
    private Button button;
    private CheckBox checkBox;
    private bd_sqlite bd_sqlite;
    private ProgressBar progressBar;
    private UserBD userBD;
    private TextView error;
    private OnHeadlineSelectedListener mCallback;
    private CloseableHttpClient httpclient;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(Usuario usuario);
    }

    /**
     * Constructor por defecto
     */
    public DialogoPersonalizadoLogin() {
    }

    /**
     * Constructor que recibe por parámetro
     * @param context contexto en el que se encuetra
     * @param bd_sqlite base de datos para poder recuperar contraseña almacenadas
     */
    public DialogoPersonalizadoLogin(Context context, bd_sqlite bd_sqlite) {
        this.context = context;
        this.bd_sqlite=bd_sqlite;
    }


    /**
     * Creamos el dialogo
     * @param savedInstanceState por si tenemos datos que recuperar
     * @return dialogo personalizado
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.loggin_personalizado,null);
        builder.setTitle("Necesita inicar sesión");
        usuario = (EditText) view.findViewById(R.id.editTextUsuarioLogin2);
        contrasenia = (EditText) view.findViewById(R.id.editTextContraseniaLogin2);
        button = (Button) view.findViewById(R.id.buttonLogin);
        checkBox = (CheckBox)view.findViewById(R.id.checkBoxUser);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
        error = (TextView) view.findViewById(R.id.textViewError);

        userBD = new UserBD();
        bd_sqlite.datosUsuario(userBD);
        //si el usuario y la contraseña no es nula, almacena los datos en los campos
        if (userBD.getUser()!=null && userBD.getPass()!=null){
            usuario.setText(userBD.getUser());
            contrasenia.setText(userBD.getPass());
            checkBox.setChecked(true);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    /**
     * Método que comprueba de que los datos introducidos sean
     * válidos y pueda realizar la comunicación con el servidor
     * correctamente
     */
    public void login(){
        //primero comprobamos de que los campos no esten vacios
        if(usuario.getText().toString().equalsIgnoreCase("")){
            usuario.setError("Debes introducir el usuario");
        }else if(contrasenia.getText().toString().equalsIgnoreCase("")){
            contrasenia.setError("Debes introducir una contraseña");
        }else{
            //si no estan vacios comprobamos de que todo cumpla con unas ciertas condiciones de logeo
            if(usuario.getText().toString().length()<4 || usuario.getText().toString().length()>30 ) {
                usuario.setError("Nombre de usuario debe contener como mínimo 5 caracteres y como máximo 30");
            }else if(contrasenia.getText().toString().length()<5 || contrasenia.getText().toString().length()>10 ){
                contrasenia.setError("Contraseña debe tener como mínimo 5 caracteres y como máximo 10");
            }else{

                if (checkBox.isChecked()){
                    if(!usuario.getText().toString().equals(userBD.getUser())){
                        userBD.setUser(usuario.getText().toString());
                        userBD.setPass(contrasenia.getText().toString());
                        //lanzamos un hilo para que guarde las credenciales
                        //del usuario
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                bd_sqlite.insertarUsuario(userBD);
                            }
                        }).start();

                    }
                }else{
                    bd_sqlite.EliminaUsuario(new UserBD(usuario.getText().toString(),contrasenia.getText().toString()));
                }
                loggin(usuario.getText().toString(),contrasenia.getText().toString(),this);
            }
        }
    }

    /**
     * Hilo que realiza la comunicacion con el servidor para autentificarse para poder
     * enviar imagenes y recibir la descripción de la imagen
     * @param nombre nombre del usuario
     * @param pass contraseña del usuario
     * @param dialogFragment dialogo de login
     */
    private void loggin(final String nombre, final String pass, final DialogFragment dialogFragment){
        new AsyncTask<Void,Integer,Boolean>(){

            private HttpResponse httpResponse;

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                usuario.setEnabled(false);
                contrasenia.setEnabled(false);
                button.setEnabled(false);
                checkBox.setEnabled(false);
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                Usuario usuario = new Usuario(nombre,"",pass);
                String contenido = new Gson().toJson(usuario);
                httpResponse = conectar(contenido);
                boolean bandera=false;
                try {
                    //comprobamos de qu se pudo realizar la conexión y ha devuelto un estado
                    if (httpResponse!=null){
                        switch (httpResponse.getStatusLine().getStatusCode()){
                            case estados.HTTP_OK:
                                String token = EntityUtils.toString(httpResponse.getEntity());
                                Gson gson = new Gson();
                                Token token1 = gson.fromJson(token,Token.class);
                                Log.d("TOKEN",token1.getToken());
                                usuario.setTokenKey(token1);
                                mCallback.onArticleSelected(usuario);
                                bandera=true;
                                break;
                            case estados.HTTP_BAD_REQUEST:
                                publishProgress(estados.HTTP_BAD_REQUEST);
                                break;
                            case estados.HTTP_FORBIDDEN:
                                publishProgress(estados.HTTP_FORBIDDEN);
                                break;

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return bandera;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                switch (values[0]){
                    case estados.HTTP_BAD_REQUEST:
                        error.setText("Revise los datos de usuario");
                        error.requestFocus();
                        break;
                    case estados.HTTP_FORBIDDEN:
                        error.setText("No se pudo conectar");
                        error.requestFocus();
                        break;
                }
            }

            @Override
            protected void onPostExecute(Boolean b) {
                    progressBar.setVisibility(View.INVISIBLE);
                    usuario.setEnabled(true);
                    contrasenia.setEnabled(true);
                    button.setEnabled(true);
                    checkBox.setEnabled(true);
                //si el login ha sido correcto podemos cerrar el dialogo
                if (b){
                    dialogFragment.dismiss();
                }

                if(httpclient!=null){
                    try {
                        httpclient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.execute();
    }

    /**
     * en caso de que el dialogo se cierre debemos de
     * controlarlo y cerrarlo manualmete para evitar
     * errores cuando vuelva realizar el createDialog()
     */
    @Override
    public void onPause() {
        super.onPause();
        this.dismiss();
    }

    /**
     * Método que realiza la comunicación con el servidor
     * @param contenido fichero JSON con los datos de login
     * @return respuesta del servidor
     */
    private HttpResponse conectar(String contenido){

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        HttpPost httppost = new HttpPost(estados.HTTP_POST_LOGIN_TOKEN);
        StringEntity stringEntity=null;
        HttpResponse httpResponse=null;
        try {
            stringEntity = new StringEntity(contenido);
            httppost.addHeader("content-type","application/json");
            httppost.setEntity(stringEntity);
            httpResponse = httpclient.execute(httppost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpResponse;
    }

    /**
     * Método que será llamado cuando la actividad sobre la que esta el fragment se ha
     * cargado y así podremos comunicarnos con ella mediante la interfaz OnHeadlineSelectedListener
     * obligatoriamente dicha interfaz debe estar implementada en la actividad
     * @param activity actividad sobre la que esta el fragment cargado
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (OnHeadlineSelectedListener) activity;

    }
}
