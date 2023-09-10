package com.dias.mayara.webook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.activity.EdicaoPerfilActivity;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.google.firebase.auth.FirebaseUser;

public class PerfilFragment extends Fragment {

    private ImageView imageViewFotoUsuario;
    private TextView textViewNomeUsuario;
    private TextView textViewQuantidadeEventosUsuario;
    private TextView textViewQuantidadeFavoritos;
    private TextView textViewQuantidadeSeguidores;
    private TextView textViewQuantidadeSeguindo;
    private Button buttonEditarPerfil;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Configuração dos componentes
        imageViewFotoUsuario = view.findViewById(R.id.imageViewFotoUsuario);
        textViewNomeUsuario = view.findViewById(R.id.textViewNomeUsuario);
        textViewQuantidadeEventosUsuario = view.findViewById(R.id.textViewQuantidadeEventosUsuario);
        textViewQuantidadeFavoritos = view.findViewById(R.id.textViewQuantidadeFavoritos);
        textViewQuantidadeSeguidores = view.findViewById(R.id.textViewQuantidadeSeguidores);
        textViewQuantidadeSeguindo = view.findViewById(R.id.textViewQuantidadeSeguindo);
        buttonEditarPerfil = view.findViewById(R.id.buttonEditarPerfil);

        // Recuperar dados do usuário
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        textViewNomeUsuario.setText(usuarioPerfil.getDisplayName());

        // Abre activity de editar perfil
        buttonEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EdicaoPerfilActivity.class);
                startActivity(i);
            }
        });

        return view;
    }
}