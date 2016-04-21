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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.protocol.ClientContext;
import cz.msebera.android.httpclient.client.protocol.HttpClientContext;
import cz.msebera.android.httpclient.conn.HttpHostConnectException;
import cz.msebera.android.httpclient.cookie.ClientCookie;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;
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
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost("http://8.35.192.144:8000/api-auth/login/");

                HttpContext httpContext = new BasicHttpContext();
                CookieStore cookieStore = new BasicCookieStore();
                httpContext.setAttribute(HttpClientContext.COOKIE_STORE,cookieStore);

                Usuario usuario = new Usuario(nombre,"",pass);
                String contenido = new Gson().toJson(usuario);

                try {
                    StringEntity stringEntity = new StringEntity(contenido);
                    httppost.addHeader("content-type","application/json");
                    httppost.setEntity(stringEntity);
                    HttpResponse httpResponse = httpclient.execute(httppost,httpContext);
                    Log.d("LOGIN",String.valueOf(httpResponse.getStatusLine().getStatusCode()));
                    if (httpResponse.getStatusLine().getStatusCode()==200){

                        List<Cookie> cookieList = cookieStore.getCookies();

                        usuario.setHeader(httpResponse.getHeaders("Set-Cookie"));
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
                   // Toast.makeText(getApplicationContext(),"Error de conexión",Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return null;
            }
        }.execute();
    }
}
