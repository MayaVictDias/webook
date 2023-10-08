package com.dias.mayara.webook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.dias.mayara.webook.R;

public class EventosUsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos_usuario);

        // Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Eventos");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Configurar botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_branco);
    }

    public boolean onSupportNavigateUp() {

        finish();
        return false;
    }
}