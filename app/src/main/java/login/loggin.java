package login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.HttpHostConnectException;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import technow.com.vision.MainActivity;
import technow.com.vision.R;

public class loggin extends AppCompatActivity {

    private EditText usuario;
    private EditText contrasenia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);

        usuario = (EditText) findViewById(R.id.editTextUsuarioLogin);
        contrasenia = (EditText) findViewById(R.id.editTextContraseniaLogin);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getTitle().equals("Crear cuenta")){
            Intent intent = new Intent(getApplicationContext(),Registro.class);
            startActivity(intent);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login,menu);
        return true;
    }

    public void login(View view){

        if(usuario.getText().toString().equalsIgnoreCase("")){
            usuario.setError("Debes introducir el usuario");
        }else if(contrasenia.getText().toString().equalsIgnoreCase("")){
            contrasenia.setError("Debes introducir una contraseña");
        }else{
            if(usuario.getText().toString().length()<4 || usuario.getText().toString().length()>30 ) {
                usuario.setError("Nombre de usuario debe contener como mínimo 5 caracteres y como máximo 30");
            }else if(contrasenia.getText().toString().length()<5 || contrasenia.getText().toString().length()>10 ){
                contrasenia.setError("Contraseña debe tener como mínimo 5 caracteres y como máximo 10");
            }else{
                loggin(usuario.getText().toString(),contrasenia.getText().toString());
            }
        }
    }

    private void loggin(final String nombre, final String pass){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000)
                                                                    .setConnectTimeout(5000)
                                                                    .setConnectionRequestTimeout(5000)
                                                                    .build();

                CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
                HttpPost httppost = new HttpPost("http://104.197.94.177/api-token-auth/");
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

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("user",usuario);
                        startActivity(intent);

                    }else{
                        Snackbar.make(getCurrentFocus(),"Usuario o contraseña incorrectos",Snackbar.LENGTH_LONG).show();
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
        }.execute();
    }
}
