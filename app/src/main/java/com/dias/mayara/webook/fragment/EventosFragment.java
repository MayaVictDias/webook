package com.dias.mayara.webook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.activity.CriarEventoActivity;
import com.dias.mayara.webook.activity.CriarPublicacaoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EventosFragment extends Fragment {

    private FloatingActionButton floatingActionButtonCriarPublicacao;

    public EventosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eventos, container, false);

        floatingActionButtonCriarPublicacao = view.findViewById(R.id.floatingActionButtonCriarPublicacao);

        floatingActionButtonCriarPublicacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), CriarEventoActivity.class);
                startActivity(i);
            }
        });

        return view;

    }
}