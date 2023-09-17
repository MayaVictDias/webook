package com.dias.mayara.webook.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dias.mayara.webook.R;
import com.dias.mayara.webook.activity.EdicaoPerfilActivity;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private CircleImageView imageViewFotoUsuario;
    private TextView textViewNomeUsuario;
    private TextView textViewQuantidadeEventosUsuario;
    private TextView textViewQuantidadeFavoritos;
    private TextView textViewQuantidadeSeguidores;
    private TextView textViewQuantidadeSeguindo;
    private Button buttonAcaoPerfil;

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
        textViewQuantidadeFavoritos = view.findViewById(R.id.textViewQuantidadeFavoritosUsuario);
        textViewQuantidadeSeguidores = view.findViewById(R.id.textViewQuantidadeSeguidoresUsuario);
        textViewQuantidadeSeguindo = view.findViewById(R.id.textViewQuantidadeSeguindoUsuario);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcao);

        // Recuperar dados do usuário
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        textViewNomeUsuario.setText(usuarioPerfil.getDisplayName());

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

        return view;
    }
}