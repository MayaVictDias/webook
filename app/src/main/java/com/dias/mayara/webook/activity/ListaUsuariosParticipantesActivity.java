package com.dias.mayara.webook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.adapter.PesquisaAdapter;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.RecyclerItemClickListener;
import com.dias.mayara.webook.model.Evento;
import com.dias.mayara.webook.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaUsuariosParticipantesActivity extends AppCompatActivity {

    private PesquisaAdapter pesquisaAdapter;
    private RecyclerView recyclerView;
    private DatabaseReference presencasConfirmadasRef;
    private List<Usuario> listaUsuarios;
    private Evento evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios_participantes);

        // Recupere o objeto Evento passado pelo adapter
        evento = (Evento) getIntent().getSerializableExtra("evento");

        // Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Visualizar participantes");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Configurar botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_branco);

        // Inicializar componentes
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaUsuarios = new ArrayList<>();
        presencasConfirmadasRef = ConfiguracaoFirebase.getFirebase()
                .child("presencasConfirmadas")
                .child(evento.getId());

        pesquisaAdapter = new PesquisaAdapter(listaUsuarios, this);
        recyclerView.setAdapter(pesquisaAdapter);

        DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");
        DatabaseReference usuario;

        // Recuperar usuarios
        presencasConfirmadasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String userId = ds.getKey(); // Obter a chave (ID) do usuário

                    // Usar o ID do usuário para obter os detalhes do usuário
                    DatabaseReference usuarioRef = usuariosRef.child(userId);

                    usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Verificar se o snapshot contém dados antes de prosseguir
                            if (dataSnapshot.exists()) {
                                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                                listaUsuarios.add(usuario);
                                pesquisaAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Lidar com erros de leitura do Firebase Database (se houver)
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Lidar com erros de leitura do Firebase Database (se houver)
            }
        });




        // Configuração do evento de clique
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Usuario usuarioSelecionado = listaUsuarios.get(position);
                        Intent i = new Intent(ListaUsuariosParticipantesActivity.this, PerfilAmigoActivity.class);

                        i.putExtra("usuarioSelecionado", usuarioSelecionado);
                        startActivity(i);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
    }

    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}