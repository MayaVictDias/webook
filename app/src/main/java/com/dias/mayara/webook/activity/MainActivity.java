package com.dias.mayara.webook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("WEbook");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Configuracao do firebase
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
    }

    // Metodo que cria os itens do menu
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Metodo que atribui ação aos itens do menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_sair) {
            deslogarUsuario();
            startActivity(new Intent(getApplicationContext(), TelaInicialActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario() {

        try {
            autenticacao.signOut();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}