package com.dias.mayara.webook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.activity.CriarPublicacaoActivity;
import com.dias.mayara.webook.activity.PerfilAmigoActivity;
import com.dias.mayara.webook.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PublicacoesFragment extends Fragment {

    private FloatingActionButton fabCriarPublicacao;

    public PublicacoesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publicacoes, container, false);

        // Inicialização dos componentes
        fabCriarPublicacao = view.findViewById(R.id.floatingActionButtonCriarPublicacao);

        // Evento de click no floating action button
        fabCriarPublicacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), CriarPublicacaoActivity.class);
                startActivity(i);

            }
        });

        return view;
    }
}