package com.dias.mayara.webook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dias.mayara.webook.R;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class        PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private TextView textViewNomeUsuario;
    private Button buttonAcaoPerfil;
    private ImageView imageViewFotoUsuario;

    private TextView textViewQuantidadeEventosUsuario,
            textViewQuantidadeSeguidoresUsuario, textViewQuantidadeSeguindoUsuario;

    private String idUsuarioLogado;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference usuarioLogadoRef;

    private ValueEventListener valueEventListenerPerfilAmigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        // Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

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

        textViewQuantidadeEventosUsuario = findViewById(R.id.textViewQuantidadeEventosUsuario);
        textViewQuantidadeSeguidoresUsuario = findViewById(R.id.textViewQuantidadeSeguidoresUsuario);
        textViewQuantidadeSeguindoUsuario = findViewById(R.id.textViewQuantidadeSeguindoUsuario);

        buttonAcaoPerfil.setText("Carregando");

        // Recuperar usuário selecionado
        Bundle bundle = getIntent().getExtras();

        if( bundle != null ){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            // Recuperar foto do usuário
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if( caminhoFoto != null ){
                Uri url = Uri.parse( caminhoFoto );
                Glide.with(PerfilAmigoActivity.this)
                        .load( url )
                        .into( imageViewFotoUsuario );
            }

        }
    }

    private void recuperarDadosUsuarioLogado() {
        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        usuarioLogado = snapshot.getValue(Usuario.class);

                        verificaSeSegueUsuarioAmigo();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    // Metodo responsavel por verificar se um usuário já segue outro qualquer
    private void verificaSeSegueUsuarioAmigo() {

        DatabaseReference seguidorRef = seguidoresRef
                .child( idUsuarioLogado )
                .child( usuarioSelecionado.getId() );

        seguidorRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if( dataSnapshot.exists() ){
                            // Já está seguindo
                            Log.i("dadosUsuario", ": Seguindo" );
                            habilitarBotaoSeguirUsuario( true );
                        }else {
                            // Ainda não está seguindo
                            Log.i("dadosUsuario", ": Seguir" );
                            habilitarBotaoSeguirUsuario( false );
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    // Seguir usuário
    private void habilitarBotaoSeguirUsuario(boolean seSegueUsuario) {

        if ( seSegueUsuario ){
            buttonAcaoPerfil.setText("Seguindo");
        } else {

            buttonAcaoPerfil.setText("Seguir");

            // Adiciona evento para seguir usuário
            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Salvar seguidor
                    salvarSeguidor(usuarioLogado, usuarioSelecionado);
                }
            });

        }


    }

    private void salvarSeguidor(Usuario usuarioLogado, Usuario usuarioAmigo) {

        HashMap<String, Object> dadosAmigo = new HashMap<>();
        dadosAmigo.put("nomeUsuario", usuarioAmigo.getNomeUsuario() );
        dadosAmigo.put("caminhoFoto", usuarioAmigo.getCaminhoFoto() );
        DatabaseReference seguidorRef = seguidoresRef
                .child( usuarioLogado.getId() )
                .child( usuarioAmigo.getId() );
        seguidorRef.setValue( dadosAmigo );

        // Alterar botao acao para seguindo
        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);

        // Incrementar seguindo do usuário logado
        int numeroSeguindo = usuarioLogado.getNumeroSeguindo() + 1;

        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("numeroSeguindo", numeroSeguindo );

        DatabaseReference usuarioSeguindo = usuariosRef
                .child( usuarioLogado.getId() );
        usuarioSeguindo.updateChildren( dadosSeguindo );

        // Incrementar seguidores do amigo
        int numeroSeguidores = usuarioAmigo.getNumeroSeguidores() + 1;

        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("numeroSeguidores", numeroSeguidores );

        DatabaseReference usuarioSeguidores = usuariosRef
                .child( usuarioAmigo.getId() );
        usuarioSeguidores.updateChildren( dadosSeguidores );

    }

    // Metodo que é chamado logo após o CreateView
    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPerfilAmigo();

        recuperarDadosUsuarioLogado();
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

                        String nomeUsuario = usuario.getNomeUsuario();
                        String numeroEventos = String.valueOf(usuario.getNumeroEventos());
                        String numeroSeguidores = String.valueOf(usuario.getNumeroSeguidores());
                        String numeroSeguindo = String.valueOf(usuario.getNumeroSeguindo());

                        // Configura valores recuperados
                        textViewNomeUsuario.setText(nomeUsuario);
                        textViewQuantidadeEventosUsuario.setText(numeroEventos);
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