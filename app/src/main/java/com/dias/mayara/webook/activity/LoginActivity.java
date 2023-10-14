package com.dias.mayara.webook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editTextLoginEmail, editTextLoginSenha;
    private Button buttonLogin;
    private ProgressBar progressBar2;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private CheckBox checkBoxManterLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Login");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Configurar botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_branco);

        // Fazer login do usuário
        progressBar2.setVisibility(View.GONE);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoEmail = editTextLoginEmail.getText().toString();
                String textoSenha = editTextLoginSenha.getText().toString();

                // Se algum campo não tiver sido preenchido, envia uma mensagem de erro pro usuário
                if (!textoEmail.isEmpty()) {
                    if (!textoSenha.isEmpty()) {

                        usuario = new Usuario();
                        usuario.setEmailUsuario(textoEmail);
                        usuario.setSenhaUsuario(textoSenha);

                        validarLogin(usuario);

                    } else {
                        Toast.makeText(LoginActivity.this, "Preencha o campo Senha!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Preencha o campo Email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void inicializarComponentes() {

        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginSenha = findViewById(R.id.editTextLoginSenha);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar2 = findViewById(R.id.progressBar2);
    }

    public void validarLogin(Usuario usuario) {

        progressBar2.setVisibility(View.VISIBLE);

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmailUsuario(),
                usuario.getSenhaUsuario()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    progressBar2.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {

                    Toast.makeText(LoginActivity.this, "Erro ao fazer login",
                            Toast.LENGTH_SHORT).show();
                    progressBar2.setVisibility(View.GONE);
                }
            }
        });
    }
}