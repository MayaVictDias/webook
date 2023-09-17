package com.dias.mayara.webook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Usuario;
import com.google.firebase.auth.FirebaseUser;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private TextView textViewNomeUsuario;
    private Button buttonAcaoPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        // Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Configurar botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_branco);

        textViewNomeUsuario = findViewById(R.id.textViewNomeUsuario);
        buttonAcaoPerfil = findViewById(R.id.buttonAcao);

        buttonAcaoPerfil.setText("Seguir");

        // Recuperar usuário selecionado
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {

            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            // Configura nome de usuario
            textViewNomeUsuario.setText(usuarioSelecionado.getNomeUsuario());
        }
    }

    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}