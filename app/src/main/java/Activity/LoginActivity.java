package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import Config.FirebaseConfig;
import Model.User;

public class LoginActivity extends AppCompatActivity {

    private AppCompatEditText etEmail, etPass;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmailL);
        etPass = findViewById(R.id.etPassL);
        auth = FirebaseConfig.getFirebaseAuth();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){

            openMainScreen();
        }
    }

    public void openRegisterScreen(View view){

        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public void openMainScreen(){

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void validateUserLogin(View view){

        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if(!email.isEmpty()){

            if(!pass.isEmpty()){

                User user = new User();
                user.setEmail(email);
                user.setPass(pass);

                userLogin(user);

            }else{

                Toast.makeText(LoginActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();

            }
        }else{

            Toast.makeText(LoginActivity.this, "Preencha o e-mail!", Toast.LENGTH_SHORT).show();

        }

    }

    public void userLogin(User user){

        auth.signInWithEmailAndPassword(user.getEmail(), user.getPass()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    openMainScreen();
                }else{

                    String exception = "";
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        exception = "Usuário não está cadastrado.";
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        exception = "E-mail ou senha incorretos.";
                    }catch(Exception e){
                        exception = "Erro ao cadastrar usuário:" + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}