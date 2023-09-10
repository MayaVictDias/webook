package com.dias.mayara.webook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class EdicaoPerfilActivity extends AppCompatActivity {

    private CircleImageView ImageViewFotoPerfilUsuario;
    private TextView textViewAlterarFoto;
    private TextInputEditText editTextEditarNomeUsuario;
    private Button buttonSalvarAlteracoes;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicao_perfil);

        inicializarComponentes();

        // Configuração do usuário
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar perfil");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Configurar botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_branco);

        // Recuperar dados do usuário
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editTextEditarNomeUsuario.setText(usuarioPerfil.getDisplayName());

    }

    public void inicializarComponentes() {

        ImageViewFotoPerfilUsuario = findViewById(R.id.ImageViewFotoPerfilUsuario);
        textViewAlterarFoto = findViewById(R.id.textViewAlterarFoto);
        editTextEditarNomeUsuario = findViewById(R.id.editTextEditarNomeUsuario);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);

        // Salvar alterações no nome
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nomeAtualizado = editTextEditarNomeUsuario.getText().toString();

                // Atualizar nome no firebase
                UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                // Atualizar nome dentro do banco de dados
                usuarioLogado.setNomeUsuario(nomeAtualizado);
                usuarioLogado.atualizar();

                Toast.makeText(EdicaoPerfilActivity.this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    /*
     * Método que ajusta o botão de voltar para que, ao invés de levar para a MainActivity com
     * o PublicacoesFragment configurado, configure o PerfilFragment
     */
    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;
    }
}