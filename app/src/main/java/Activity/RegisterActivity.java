package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import Config.FirebaseConfig;
import Helper.Base64Custom;
import Helper.FirebaseUser;
import Model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPass;
    private Button btRegister;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etNameR);
        etEmail = findViewById(R.id.etEmailR);
        etPass = findViewById(R.id.etPassR);
        btRegister = findViewById(R.id.btRegisterR);
    }

    public void validateUserRegister(View view){

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if(!name.isEmpty()){

            if(!email.isEmpty()){

                if(!pass.isEmpty()){

                    User user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPass(pass);
                    registerAndSaveUser(user);

                }else{

                    Toast.makeText(RegisterActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();

                }
            }else{

                Toast.makeText(RegisterActivity.this, "Preencha o e-mail!", Toast.LENGTH_SHORT).show();

            }
        }else{

            Toast.makeText(RegisterActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();

        }
    }

    public void registerAndSaveUser(User user){

        auth = FirebaseConfig.getFirebaseAuth();
        auth = FirebaseConfig.getFirebaseAuth();
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPass()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Toast.makeText(RegisterActivity.this, "Sucesso ao cadastrar usuário!", Toast.LENGTH_SHORT).show();
                    FirebaseUser.updateUserName(user.getName());
                    finish();

                    try{

                        String userID = Base64Custom.encode(user.getEmail());
                        user.setUserID(userID);
                        user.save();

                    }catch (Exception e){

                        e.printStackTrace();
                    }

                    openMainScreen();
                    finish();

                }else{

                    String exception = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){

                        exception = "Digite uma senha mais forte!";

                    }catch (FirebaseAuthInvalidCredentialsException e){

                        exception = "Por favor, digite um E-mail válido!";
                    }catch (FirebaseAuthUserCollisionException e){

                        exception = "Já existe uma conta com esse E-mail";
                    }
                    catch (Exception e){

                        exception = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(RegisterActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void openMainScreen(){

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }
}