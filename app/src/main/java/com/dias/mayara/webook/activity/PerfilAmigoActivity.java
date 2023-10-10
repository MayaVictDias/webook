package com.dias.mayara.webook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dias.mayara.webook.R;
import com.dias.mayara.webook.adapter.EventosAdapter;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Evento;
import com.dias.mayara.webook.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class        PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private TextView textViewNomeUsuario;
    private ImageView imageViewFotoUsuario;

    private TextView textViewQuantidadeEventosUsuario,
            textViewQuantidadeSeguidoresUsuario, textViewQuantidadeSeguindoUsuario;
    private RecyclerView recyclerViewPerfil;

    private String idUsuarioLogado;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference seguindoRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference eventosRef;
    private Button buttonPerfilAmigo;

    private ValueEventListener valueEventListenerPerfilAmigo;
    private ValueEventListener valueEventListenerEventos;
    private EventosAdapter eventosAdapter;
    private List<Evento> listaEventos = new ArrayList<>();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        // Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        seguindoRef = firebaseRef.child("seguindo");
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
        imageViewFotoUsuario = findViewById(R.id.imageViewFotoUsuario);
        buttonPerfilAmigo = findViewById(R.id.buttonAcao);
        buttonPerfilAmigo.setVisibility(View.GONE);

        recyclerViewPerfil = findViewById(R.id.recyclerViewPerfil);

        recyclerViewPerfil.setHasFixedSize(true);
        recyclerViewPerfil.setLayoutManager(new LinearLayoutManager(this));
        eventosAdapter = new EventosAdapter(listaEventos, this);
        recyclerViewPerfil.setAdapter(eventosAdapter);


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



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    // Metodo responsavel por verificar se um usuário já segue outro qualquer
    private void verificaSeSegueUsuarioAmigo() {

        DatabaseReference seguidorRef = seguindoRef
                .child( idUsuarioLogado )
                .child( usuarioSelecionado.getId() );

        /*seguidorRef.addListenerForSingleValueEvent(
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

         */

    }

    /*
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

     */

    private void abrirDialogCarregamento(String titulo) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false); // Impede que o usuário cancele a tela de carregamento
        alert.setView(R.layout.dialog_carregamento);

        dialog = alert.create();
        dialog.show();

    }

    private void salvarSeguidor(Usuario usuarioLogado, Usuario usuarioAmigo) {

        abrirDialogCarregamento("Salvando seguidor");

        HashMap<String, Object> dadosAmigo = new HashMap<>();
        dadosAmigo.put("nomeUsuario", usuarioAmigo.getNomeUsuario() );
        dadosAmigo.put("caminhoFoto", usuarioAmigo.getCaminhoFoto() );
        DatabaseReference seguidorRef = seguindoRef
                .child( usuarioLogado.getId() )
                .child( usuarioAmigo.getId() );
        seguidorRef.setValue( dadosAmigo );


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

        dialog.cancel();

    }

    // Metodo que é chamado logo após o CreateView
    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPerfilAmigo();

        recuperarDadosUsuarioLogado();

        listarEventos();
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

                        // Configura valores recuperados
                        textViewNomeUsuario.setText(nomeUsuario);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    private void listarEventos() {

        eventosRef = ConfiguracaoFirebase.getFirebase().child("eventos").child(usuarioSelecionado.getId());

        valueEventListenerEventos = eventosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren() ){
                    listaEventos.add(ds.getValue(Evento.class));
                }
                eventosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}