package com.dias.mayara.webook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.activity.CriarEventoActivity;
import com.dias.mayara.webook.adapter.EventosAdapter;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Evento;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventosFragment extends Fragment {

    private FloatingActionButton floatingActionButtonCriarPublicacao;
    private RecyclerView recyclerView;
    private EventosAdapter eventosAdapter;
    private List<Evento> listaEventos = new ArrayList<>();
    private ValueEventListener valueEventListenerEventos;
    private DatabaseReference eventosRef;
    private UsuarioFirebase usuarioFirebase;
    private String idUsuarioLogado;


    public EventosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eventos, container, false);

        // Configurações iniciais
        idUsuarioLogado = usuarioFirebase.getIdentificadorUsuario();

        floatingActionButtonCriarPublicacao = view.findViewById(R.id.floatingActionButtonCriarPublicacao);
        recyclerView = view.findViewById(R.id.recyclerView);
        eventosRef = ConfiguracaoFirebase.getFirebase().child("eventos").child(idUsuarioLogado);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventosAdapter = new EventosAdapter(listaEventos, getActivity());
        recyclerView.setAdapter(eventosAdapter);

        floatingActionButtonCriarPublicacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), CriarEventoActivity.class);
                startActivity(i);
            }
        });

        return view;

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
        listarEventos();
    }

    @Override
    public void onStop() {
        super.onStop();
        eventosRef.removeEventListener(valueEventListenerEventos);
    }
}