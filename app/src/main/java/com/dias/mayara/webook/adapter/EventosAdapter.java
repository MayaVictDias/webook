package com.dias.mayara.webook.adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dias.mayara.webook.R;
import com.dias.mayara.webook.activity.CriarEventoActivity;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Evento;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventosAdapter extends RecyclerView.Adapter<EventosAdapter.MyViewHolder> {

        private List<Evento> listaEventos;
        private Context context;
        private DatabaseReference eventoRef;
        private DatabaseReference feedEventoRef;
        private DatabaseReference usuarioLogadoRef;
        private String idUsuarioLogado;
    private FirebaseUser usuarioPerfil;

        public EventosAdapter(List<Evento> listaEventos, Context context) {
            this.listaEventos = listaEventos;
            this.context = context;
        }

    @NonNull
    @Override
    public EventosAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_eventos, parent, false);

        return new EventosAdapter.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull EventosAdapter.MyViewHolder holder, int position) {

        Evento evento = listaEventos.get(position);
        DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebase().child("usuarios")
                .child(evento.getIdUsuario());
        DatabaseReference usuarioNomeRef = usuarioRef.child("nomeUsuario");
        DatabaseReference fotoUsuarioRef = usuarioRef.child("caminhoFoto");

        eventoRef = ConfiguracaoFirebase.getFirebase().child("eventos")
                .child(evento.getIdUsuario())
                .child(evento.getId());

        feedEventoRef = ConfiguracaoFirebase.getFirebase().child("feedEventos")
                .child(evento.getId());

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        usuarioLogadoRef = ConfiguracaoFirebase.getFirebase().child(idUsuarioLogado);

        Uri uriFotoUsuario = Uri.parse(String.valueOf(fotoUsuarioRef));

        fotoUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String urlFoto = dataSnapshot.getValue(String.class);
                if (urlFoto != null) {
                    Uri uriFotoUsuario = Uri.parse(urlFoto);
                    Glide.with(context).load(uriFotoUsuario).into(holder.imageViewFotoUsuario);
                } else {
                    holder.imageViewFotoUsuario.setImageResource(R.drawable.icone_account_circle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Trate os erros aqui, se necessário
            }
        });

        // Adicionando um ValueEventListener para obter o valor de usuarioNomeRef
        usuarioNomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nomeUsuario = dataSnapshot.getValue(String.class);
                if (nomeUsuario != null) {
                    holder.nomeUsuario.setText(nomeUsuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Trate os erros aqui, se necessário
            }
        });

        holder.nomeEvento.setText(evento.getNomeEvento());
        holder.nomeLocalEvento.setText(evento.getNomeLocalEvento());
        holder.dataHoraEvento.setText(evento.getDataHoraEvento());
        holder.nomeLivro.setText(evento.getNomeLivro());
        holder.sobreEvento.setText(evento.getSobreEvento());

        holder.buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
    }

    private void showPopupMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_eventos, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {
                    // Código para excluir o item
                    eventoRef.removeValue();
                    feedEventoRef.removeValue();

                    Toast.makeText(view.getContext(),
                            "Evento deletado com sucesso! Atualize a tela para visualizar as alterações",
                            Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }


    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nomeEvento;
        private TextView nomeLocalEvento;
        private TextView dataHoraEvento;
        private TextView nomeLivro;
        private TextView sobreEvento;
        private CircleImageView imageViewFotoUsuario;
        private TextView nomeUsuario;
        private ImageButton buttonMenu;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewFotoUsuario = itemView.findViewById(R.id.imageViewFotoUsuario);
            nomeUsuario = itemView.findViewById(R.id.textViewNomeUsuarioAdapterEventos);
            nomeEvento = itemView.findViewById(R.id.textViewNomeEvento);
            nomeLocalEvento = itemView.findViewById(R.id.textViewLocalEvento);
            dataHoraEvento = itemView.findViewById(R.id.textViewDataEvento);
            nomeLivro = itemView.findViewById(R.id.textViewNomeLivroAdapterEventos);
            sobreEvento = itemView.findViewById(R.id.textViewSobreEvento);
            buttonMenu = itemView.findViewById(R.id.buttonMenu);

        }
    }
}
