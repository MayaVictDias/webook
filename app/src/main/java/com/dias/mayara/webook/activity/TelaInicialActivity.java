package com.dias.mayara.webook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dias.mayara.webook.R;

public class TelaInicialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
    }

    public void abrirTelaLogin(View view) {
        Intent i = new Intent(TelaInicialActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void abrirTelaCadastro(View view) {
        Intent i = new Intent(TelaInicialActivity.this, CadastrarNovoUsuarioActivity.class);
        startActivity(i);
    }
}