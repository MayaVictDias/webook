package com.dias.mayara.webook.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dias.mayara.webook.R;
import com.dias.mayara.webook.activity.EdicaoPerfilActivity;
import com.dias.mayara.webook.adapter.EventosAdapter;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Evento;
import com.dias.mayara.webook.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private CircleImageView imageViewFotoUsuario;
    private TextView textViewNomeUsuario;
    private TextView textViewQuantidadeEventosUsuario;
    private TextView textViewQuantidadeFavoritos;
    private TextView textViewQuantidadeSeguidores;
    private TextView textViewQuantidadeSeguindo;
    private Button buttonAcaoPerfil;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private ValueEventListener valueEventListenerPerfil;

    private Usuario usuarioLogado;
    private FirebaseUser usuarioPerfil;




    private RecyclerView recyclerView;
    private EventosAdapter eventosAdapter;
    private List<Evento> listaEventos = new ArrayList<>();
    private ValueEventListener valueEventListenerEventos;
    private DatabaseReference eventosRef;
    private UsuarioFirebase usuarioFirebase;
    private String idUsuarioLogado;





    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        idUsuarioLogado = usuarioFirebase.getIdentificadorUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        eventosRef = ConfiguracaoFirebase.getFirebase().child("eventos").child(idUsuarioLogado);

        inicializarComponentes(view);

        // Recuperar dados do usuário
        usuarioPerfil = UsuarioFirebase.getUsuarioAtual();

        // Exibir foto do usuario, caso ele tenha setado uma
        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null) {
            Glide.with(PerfilFragment.this).load(url).into(imageViewFotoUsuario);
        } else {
            imageViewFotoUsuario.setImageResource(R.drawable.icone_account_circle);
        }

        // Abre activity de editar perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EdicaoPerfilActivity.class);
                startActivity(i);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventosAdapter = new EventosAdapter(listaEventos, getActivity());
        recyclerView.setAdapter(eventosAdapter);

        return view;
    }

    private void inicializarComponentes(View view) {

        // Configuração dos componentes
        imageViewFotoUsuario = view.findViewById(R.id.imageViewFotoUsuario);
        textViewNomeUsuario = view.findViewById(R.id.textViewNomeUsuario);
        textViewQuantidadeEventosUsuario = view.findViewById(R.id.textViewQuantidadeEventosUsuario);
        textViewQuantidadeSeguidores = view.findViewById(R.id.textViewQuantidadeSeguidoresUsuario);
        textViewQuantidadeSeguindo = view.findViewById(R.id.textViewQuantidadeSeguindoUsuario);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcao);

        recyclerView = view.findViewById(R.id.recyclerViewPerfil);
    }

    private void recuperarDadosUsuarioLogado(){

        usuarioLogadoRef = usuariosRef.child( usuarioLogado.getId() );
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario usuario = snapshot.getValue( Usuario.class );

                textViewNomeUsuario.setText(usuario.getNomeUsuario());

                String numeroEventos = String.valueOf( usuario.getNumeroEventos() );
                String numeroSeguindo = String.valueOf( usuario.getNumeroSeguindo() );
                String numeroSeguidores = String.valueOf( usuario.getNumeroSeguidores() );

                // Configura valores recuperados
                textViewQuantidadeEventosUsuario.setText( numeroEventos );
                textViewQuantidadeSeguidores.setText( numeroSeguidores );
                textViewQuantidadeSeguindo.setText( numeroSeguindo );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void listarEventos() {

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

    @Override
    public void onStart() {
        super.onStart();

        // Recuperar dados do usuario logado
        recuperarDadosUsuarioLogado();

        listarEventos();

    }

    @Override
    public void onStop() {
        super.onStop();
        eventosRef.removeEventListener(valueEventListenerEventos);
    }
}