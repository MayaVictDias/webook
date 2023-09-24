package com.dias.mayara.webook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.activity.PerfilAmigoActivity;
import com.dias.mayara.webook.adapter.PesquisaAdapter;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.RecyclerItemClickListener;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PesquisarFragment extends Fragment {

    private SearchView searchViewPesquisa;
    private RecyclerView recyclerViewPesquisa;
    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;
    // TODO criar livrosRef
    // TODO criar eventosRef
    // Todo criar autoresRef
    private PesquisaAdapter pesquisaAdapter;
    private String idUsuarioLogado;

    public PesquisarFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisar, container, false);

        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerViewPesquisa = view.findViewById(R.id.recyclerViewPesquisa);

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        // Configuração do RecyclerView
        recyclerViewPesquisa.setHasFixedSize(true);
        recyclerViewPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));

        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");

        // Configuração do adapter de pesquisa
        pesquisaAdapter = new PesquisaAdapter(listaUsuarios, getContext());
        recyclerViewPesquisa.setAdapter(pesquisaAdapter);

        // COnfiguração do evento de clique
        recyclerViewPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Usuario usuarioSelecionado = listaUsuarios.get(position);
                        Intent i = new Intent(getActivity(), PerfilAmigoActivity.class);

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

        // Configuração da searchView
        searchViewPesquisa.setQueryHint("Buscar");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String textoDigitado) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String textoDigitado) {

                pesquisarUsuarios(textoDigitado);

                return true;
            }
        });

        return view;
    }

    private void pesquisarUsuarios(String textoDigitado) {

        // Limpa a lista
        listaUsuarios.clear();

        // Confere se tem algum texto pra ser pesquisado
        if(textoDigitado.length() > 0) {
            Query query = usuariosRef.orderByChild("nomeUsuario")
                    .startAt(textoDigitado).endAt(textoDigitado + "\uf8ff");
            // OrderByChild: Ordenar por nome que começa com textoDigitado ou termina com ele

            // @TODO adicionar nó no firebase para salvar os nomes com uppercase, pra fazer o filtro com ele

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // Limpa a lista
                    listaUsuarios.clear();

                    for (DataSnapshot ds : snapshot.getChildren()){

                        // Verifica se é o usuário logado e remove da lista
                        Usuario usuario = ds.getValue(Usuario.class);
                        if(idUsuarioLogado.equals(usuario.getId())) {
                            continue;
                        }

                        listaUsuarios.add(usuario);

                    }

                    int totalUsuariosRetornados = listaUsuarios.size();
                    Log.i("totalUsuariosRetornadosFirebase", "totalUsuariosRetornados: "+ totalUsuariosRetornados);

                    // Avisar o adapter que houve uma atualização nos itens retornados
                    pesquisaAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}