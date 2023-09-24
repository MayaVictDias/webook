package com.dias.mayara.webook.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dias.mayara.webook.R;
import com.dias.mayara.webook.activity.EdicaoPerfilActivity;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private CircleImageView imageViewFotoUsuario;
    private TextView textViewNomeUsuario;
    private TextView textViewQuantidadePostagensUsuario;
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

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //Configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");

        inicializarComponentes(view);

        // Recuperar dados do usuário
        usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        textViewNomeUsuario.setText(usuarioPerfil.getDisplayName());

        // Abre activity de editar perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EdicaoPerfilActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    private void inicializarComponentes(View view) {

        // Configuração dos componentes
        imageViewFotoUsuario = view.findViewById(R.id.imageViewFotoUsuario);
        textViewNomeUsuario = view.findViewById(R.id.textViewNomeUsuario);
        textViewQuantidadePostagensUsuario = view.findViewById(R.id.textViewQuantidadePostagensUsuario);
        textViewQuantidadeFavoritos = view.findViewById(R.id.textViewQuantidadeFavoritosUsuario);
        textViewQuantidadeSeguidores = view.findViewById(R.id.textViewQuantidadeSeguidoresUsuario);
        textViewQuantidadeSeguindo = view.findViewById(R.id.textViewQuantidadeSeguindoUsuario);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcao);
    }

    private void recuperarDadosUsuarioLogado(){

        usuarioLogadoRef = usuariosRef.child( usuarioLogado.getId() );
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario usuario = snapshot.getValue( Usuario.class );

                // Exibir foto do usuario, caso ele tenha setado uma
                Uri url = usuarioPerfil.getPhotoUrl();
                if(url != null) {
                    Glide.with(PerfilFragment.this).load(url).into(imageViewFotoUsuario);
                } else {
                    imageViewFotoUsuario.setImageResource(R.drawable.icone_account_circle);
                }

                String numeroPostagens = String.valueOf( usuario.getNumeroPostagens() );
                String numeroFavoritos = String.valueOf(usuario.getNumeroFavoritos());
                String numeroSeguindo = String.valueOf( usuario.getNumeroSeguindo() );
                String numeroSeguidores = String.valueOf( usuario.getNumeroSeguidores() );

                // Configura valores recuperados
                textViewQuantidadePostagensUsuario.setText( numeroPostagens );
                textViewQuantidadeFavoritos.setText( numeroFavoritos );
                textViewQuantidadeSeguidores.setText( numeroSeguindo );
                textViewQuantidadeSeguindo.setText( numeroSeguidores );

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

    }
}