package com.dias.mayara.webook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class TelaInicialActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        verificarUsuarioLogado();
    }

    public void abrirTelaLogin(View view) {
        Intent i = new Intent(TelaInicialActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void abrirTelaCadastro(View view) {
        Intent i = new Intent(TelaInicialActivity.this, CadastrarNovoUsuarioActivity.class);
        startActivity(i);
    }

    /*
     * Metodo que verifica se o usuario já está logado, para que ele não precise fazer login de novo
     *
     * TODO ALTERAR ESSE METODO PARA QUE ELE FUNCIONE APENAS SE A CHECKBOX DA TELA DE LOGIN TIVER SIDO MARCADA
     *
     */
    public void verificarUsuarioLogado() {

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        if(autenticacao.getCurrentUser() != null) {

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }
    }
}