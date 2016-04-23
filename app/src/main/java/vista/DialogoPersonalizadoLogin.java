package vista;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.HttpHostConnectException;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import login.Token;
import login.Usuario;
import sqlite.bd_sqlite;
import technow.com.vision.MainActivity;
import technow.com.vision.R;

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

    public DialogoPersonalizadoLogin() {
    }

    public DialogoPersonalizadoLogin(Context context, bd_sqlite bd_sqlite) {
        this.context = context;
        this.bd_sqlite=bd_sqlite;
    }

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

        userBD = new UserBD();
        bd_sqlite.datosUsuario(userBD);

        if (userBD.getUser()!=null && userBD.getPass()!=null){
            usuario.setText(userBD.getUser());
            contrasenia.setText(userBD.getPass());
            checkBox.setChecked(true);
            login();
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
                        userBD.setPass(usuario.getText().toString());
                        bd_sqlite.insertarUsuario(userBD);
                    }
                }else{
                    bd_sqlite.EliminaUsuario(new UserBD(usuario.getText().toString(),contrasenia.getText().toString()));
                }
                loggin(usuario.getText().toString(),contrasenia.getText().toString());
            }
        }
    }

    private void loggin(final String nombre, final String pass){
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                usuario.setEnabled(false);
                contrasenia.setEnabled(false);
                button.setEnabled(false);
                checkBox.setEnabled(false);
            }

            @Override
            protected Void doInBackground(Void... params) {

                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost("http://8.35.192.144:8000/api-token-auth/");


                Usuario usuario = new Usuario(nombre,"",pass);
                String contenido = new Gson().toJson(usuario);

                try {
                    StringEntity stringEntity = new StringEntity(contenido);
                    httppost.addHeader("content-type","application/json");
                    httppost.setEntity(stringEntity);
                    HttpResponse httpResponse = httpclient.execute(httppost);
                    Log.d("LOGIN",String.valueOf(httpResponse.getStatusLine().getStatusCode()));
                    if (httpResponse.getStatusLine().getStatusCode()==200){
                        String token = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
                        Gson gson = new Gson();
                        Token token1 = gson.fromJson(token,Token.class);
                        usuario.setTokenKey(token1);

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("user",usuario);
                        startActivity(intent);

                    }else{
                        Snackbar.make(getActivity().getCurrentFocus(),"Usuario o contraseña incorrectos",Snackbar.LENGTH_LONG).show();
                    }

                    Log.d("LOGIN",contenido);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (HttpHostConnectException e){
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(),"Error de conexión",Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progressBar.setVisibility(View.INVISIBLE);
                usuario.setEnabled(true);
                contrasenia.setEnabled(true);
                button.setEnabled(true);
                checkBox.setEnabled(true);
            }
        }.execute();
    }
}
