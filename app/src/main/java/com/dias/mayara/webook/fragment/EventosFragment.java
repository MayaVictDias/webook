package com.dias.mayara.webook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
import java.util.Collections;
import java.util.List;

public class EventosFragment extends Fragment {

    private FloatingActionButton floatingActionButtonCriarPublicacao;
    private RecyclerView recyclerView;
    private EventosAdapter feedEventosAdapter;
    private List<Evento> listaFeedEventos = new ArrayList<>();
    private ValueEventListener valueEventListenerEventos;
    private DatabaseReference feedEventosRef;
    private UsuarioFirebase usuarioFirebase;
    private String idUsuarioLogado;
    private ImageButton imageButtonConfirmarPresenca;


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
        feedEventosRef = ConfiguracaoFirebase.getFirebase().child("feedEventos");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        feedEventosAdapter = new EventosAdapter(listaFeedEventos, getActivity());
        recyclerView.setAdapter(feedEventosAdapter);

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

        valueEventListenerEventos = feedEventosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren() ){
                    listaFeedEventos.add(ds.getValue(Evento.class));
                }
                Collections.reverse(listaFeedEventos); // Traz os eventos mais recentes como primeiros do feed
                feedEventosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        listaFeedEventos.clear();
        listarEventos();
    }

    @Override
    public void onStop() {
        super.onStop();

        feedEventosRef.removeEventListener(valueEventListenerEventos);
    }
}