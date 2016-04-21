package login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.validation.Validator;

import technow.com.vision.R;

public class Registro extends AppCompatActivity {

    private Validator validator;
    private EditText usuario,email,passwd;
    private CheckBox checkBox;
    private Button registrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usuario = (EditText) findViewById(R.id.editTextUsuario);
        email = (EditText) findViewById(R.id.editTextEmail);
        passwd =  (EditText) findViewById(R.id.editTextPasswd);
        checkBox = (CheckBox) findViewById(R.id.checkBoxCheck);
        registrar = (Button) findViewById(R.id.buttonRegistrar);


    }

    public void registrar(View view){

        if(usuario.getText().toString().equalsIgnoreCase("")){
            usuario.setError("Debes introducir el usuario");
        }else if(email.getText().toString().equalsIgnoreCase("")){
            email.setError("Debes introducir un email");
        }else if(passwd.getText().toString().equalsIgnoreCase("")){
            passwd.setError("Debes introducir un usuario");
        }else{
            Pattern pattern = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher matcher = pattern.matcher(email.getText().toString());
            //si el nombre de usuario es menor que 5 o mayor que 30 salta un error
            if(usuario.getText().toString().length()<5 || usuario.getText().toString().length()>30 ){
                usuario.setError("Nombre de usuario debe contener como mínimo 5 caracteres y como máximo 30");
            }else if(!matcher.find()){
                email.setError("Email no válido ejemplo: ejemplo@gmail.com");
            }else if(passwd.getText().toString().length()<5 || passwd.getText().toString().length()>10){
                passwd.setError("Contraseña debe tener como mínimo 5 caracteres y como máximo 10");
                //En caso de que los datos sean correctos
            }else{

            }

        }



    }

}
