package com.dias.mayara.webook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.fragment.EventosFragment;
import com.dias.mayara.webook.fragment.PerfilFragment;
import com.dias.mayara.webook.fragment.PesquisarFragment;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("WEbook");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Configuracao do firebase
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        //Configurar bottom navigation view
        configurarBottomNavigation();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.viewPager, new EventosFragment()).commit();
    }

    private void configurarBottomNavigation() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Habilitar navegação
        habilitarNavegacao( bottomNavigationView );

        // Configuração do item que aparece como selecionado inicialmente (fragment de eventos)
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

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

    // Metodo que trata eventos de clique no bottom navigation
    private void habilitarNavegacao(BottomNavigationView bottomNavigationView) {

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Recuperar item do bottom navigation que foi selecionado

                if(item.getItemId() == R.id.ic_home) {
                    fragmentTransaction.replace(R.id.viewPager, new EventosFragment()).commit();
                    return true;
                } else if(item.getItemId() == R.id.ic_pesquisar) {
                    fragmentTransaction.replace(R.id.viewPager, new PesquisarFragment()).commit();
                    return true;
                } else if(item.getItemId() == R.id.ic_perfil) {
                    fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                    return true;
                }

                return false;
            }
        });
    }

    private void deslogarUsuario() {

        try {
            autenticacao.signOut();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}