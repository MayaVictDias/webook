package com.dias.mayara.webook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dias.mayara.webook.R;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private TextView textViewNomeUsuario;
    private Button buttonAcaoPerfil;
    private ImageView imageViewFotoUsuario;

    private TextView textViewQuantidadePostagensUsuario, textViewQuantidadeFavoritosUsuario,
            textViewQuantidadeSeguidoresUsuario, textViewQuantidadeSeguindoUsuario;

    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private ValueEventListener valueEventListenerPerfilAmigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        // Configurações iniciais
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");

        // Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Configurar botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_branco);

        // Iniciando os componentes
        textViewNomeUsuario = findViewById(R.id.textViewNomeUsuario);
        buttonAcaoPerfil = findViewById(R.id.buttonAcao);
        imageViewFotoUsuario = findViewById(R.id.imageViewFotoUsuario);

        textViewQuantidadePostagensUsuario = findViewById(R.id.textViewQuantidadePostagensUsuario);
        textViewQuantidadeFavoritosUsuario = findViewById(R.id.textViewQuantidadeFavoritosUsuario);
        textViewQuantidadeSeguidoresUsuario = findViewById(R.id.textViewQuantidadeSeguidoresUsuario);
        textViewQuantidadeSeguindoUsuario = findViewById(R.id.textViewQuantidadeSeguindoUsuario);

        buttonAcaoPerfil.setText("Seguir");

        // Recuperar usuário selecionado
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {

            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            // Configura nome de usuario
            textViewNomeUsuario.setText(usuarioSelecionado.getNomeUsuario());

            // Exibir foto do usuario, caso ele tenha setado uma
            Uri url = Uri.parse(usuarioSelecionado.getCaminhoFoto());
            if(url != null) {
                Glide.with(PerfilAmigoActivity.this).load(url).into(imageViewFotoUsuario);
            } else {
                imageViewFotoUsuario.setImageResource(R.drawable.icone_account_circle);
            }
        }
    }

    // Metodo que é chamado logo após o CreateView
    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPerfilAmigo();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Remove o listener que está recuperando dados de um amigo especifico
        usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);
    }

    // Metodo responsavel por recuperar os dados do usuario que está sendo pesquisado
    private void recuperarDadosPerfilAmigo() {

        usuarioAmigoRef = usuariosRef.child(usuarioSelecionado.getId());

        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Usuario usuario = snapshot.getValue(Usuario.class);

                        String numeroPostagens = String.valueOf(usuario.getNumeroPostagens());
                        String numeroFavoritos = String.valueOf(usuario.getNumeroFavoritos());
                        String numeroSeguidores = String.valueOf(usuario.getNumeroSeguidores());
                        String numeroSeguindo = String.valueOf(usuario.getNumeroSeguindo());

                        // Configura valores recuperados
                        textViewQuantidadePostagensUsuario.setText(numeroPostagens);
                        textViewQuantidadeFavoritosUsuario.setText(numeroFavoritos);
                        textViewQuantidadeSeguidoresUsuario.setText(numeroSeguidores);
                        textViewQuantidadeSeguindoUsuario.setText(numeroSeguindo);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}