package com.dias.mayara.webook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dias.mayara.webook.R;
import com.dias.mayara.webook.activity.CriarEventoActivity;
import com.dias.mayara.webook.activity.ListaUsuariosParticipantesActivity;
import com.dias.mayara.webook.activity.PerfilAmigoActivity;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Evento;
import com.dias.mayara.webook.model.PresencasConfirmadas;
import com.dias.mayara.webook.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventosAdapter extends RecyclerView.Adapter<EventosAdapter.MyViewHolder> {

    private List<Evento> listaEventos;
    private Context context;
    private DatabaseReference eventoRef;
    private DatabaseReference feedEventoRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference presencasConfirmadas;
    private String idUsuarioLogado;
    private List<Usuario> listaUsuarios = new ArrayList<>();
    private Drawable drawable;

    private FirebaseUser usuarioPerfil;
    private AlertDialog dialog;

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
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebase().child("usuarios")
                .child(evento.getIdUsuario());
        DatabaseReference usuarioNomeRef = usuarioRef.child("nomeUsuario");
        DatabaseReference fotoUsuarioRef = usuarioRef.child("caminhoFoto");

        eventoRef = ConfiguracaoFirebase.getFirebase().child("eventos")
                .child(evento.getIdUsuario())
                .child(evento.getId());

        feedEventoRef = ConfiguracaoFirebase.getFirebase().child("feedEventos")
                .child(evento.getId());

        presencasConfirmadas = ConfiguracaoFirebase.getFirebase().child("presencasConfirmadas")
                .child(evento.getId());

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        usuarioLogadoRef = ConfiguracaoFirebase.getFirebase().child(idUsuarioLogado);

        Uri uriFotoUsuario = Uri.parse(String.valueOf(fotoUsuarioRef));

        fotoUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String urlFoto = dataSnapshot.getValue(String.class);
                if (urlFoto != null && !urlFoto.isEmpty()) {
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

        // Recuperar os dados da postagem curtida
        DatabaseReference presencaRef = ConfiguracaoFirebase.getFirebase().
                child("presencasConfirmadas")
                .child(evento.getId());

        presencaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                PresencasConfirmadas presencasConfirmadas = snapshot.getValue(PresencasConfirmadas.class);

                int quantidadePresencasConfirmadas = 0;

                if(snapshot.hasChild("quantidadePresencasConfirmadas")) { // Se o usuario já tiver confirmado presença...

                    PresencasConfirmadas presencasConfirmada = snapshot.getValue(PresencasConfirmadas.class);
                    quantidadePresencasConfirmadas = presencasConfirmada.getQuantidadePresencasConfirmadas();

                }

                // Montar objeto que retorna sobre as presenças confirmadas
                PresencasConfirmadas presencas = new PresencasConfirmadas();
                presencas.setEvento(evento);
                presencas.setUsuario(usuarioLogado);
                presencas.setQuantidadePresencasConfirmadas(quantidadePresencasConfirmadas);

                // Verifica se evento que está sendo exibido é do usuário logado
                if (!evento.getIdUsuario().equals(idUsuarioLogado)) { // Se o evento não for do usuario logado...

                    holder.buttonMenu.setVisibility(View.GONE);
                    holder.imageButtonConfirmarPresenca.setVisibility(View.VISIBLE);

                    if(snapshot.hasChild(usuarioLogado.getId())) {

                        holder.presencaConfirmada = true;
                        drawable = ContextCompat.getDrawable(context, R.drawable.check_button);
                        holder.imageButtonConfirmarPresenca.setBackground(drawable);

                    } else {

                        holder.presencaConfirmada = false;
                        drawable = ContextCompat.getDrawable(context, R.drawable.add_button);
                        holder.imageButtonConfirmarPresenca.setBackground(drawable);
                    }

                    // Se o usuario clicar no botão de confirmar presença...
                    holder.imageButtonConfirmarPresenca.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!holder.presencaConfirmada) {
                                // Presença não confirmada, salvar
                                presencas.salvar();
                                holder.presencaConfirmada = true;
                                drawable = ContextCompat.getDrawable(context, R.drawable.check_button);
                                holder.imageButtonConfirmarPresenca.setBackground(drawable);
                                holder.textViewQuantidadeParticipantes.setText(presencas.getQuantidadePresencasConfirmadas() + " participantes");
                                Toast.makeText(context, "Presença confirmada!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Presença confirmada, remover
                                presencas.remover();
                                holder.presencaConfirmada = false;
                                drawable = ContextCompat.getDrawable(context, R.drawable.add_button);
                                holder.imageButtonConfirmarPresenca.setBackground(drawable);
                                holder.textViewQuantidadeParticipantes.setText(presencas.getQuantidadePresencasConfirmadas() + " participantes");
                                Toast.makeText(context, "Presença removida!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    holder.textViewQuantidadeParticipantes.setText(presencas.getQuantidadePresencasConfirmadas() + " participantes");

                } else { // Se o evento for do usuário logado...

                    holder.imageButtonConfirmarPresenca.setVisibility(View.GONE);
                    holder.buttonMenu.setVisibility(View.VISIBLE);

                    holder.textViewQuantidadeParticipantes.setText(presencas.getQuantidadePresencasConfirmadas() + " participantes");

                    holder.buttonMenu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showPopupMenu(view, position);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.textViewQuantidadeParticipantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ListaUsuariosParticipantesActivity.class);
                i.putExtra("evento", evento);
                view.getContext().startActivity(i);
            }
        });

        // Se o usuario clicar no nome do usuario, vai para a tela de perfil dele
        holder.nomeUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario usuarioSelecionado = dataSnapshot.getValue(Usuario.class);

                        if (usuarioSelecionado != null) {
                            Intent i = new Intent(context, PerfilAmigoActivity.class);
                            i.putExtra("usuarioSelecionado", usuarioSelecionado);
                            context.startActivity(i);
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    private void showPopupMenu(View view, int position) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_eventos, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {

                    abrirDialogCarregamento("Deletando evento");

                    eventoRef = ConfiguracaoFirebase.getFirebase().child("eventos")
                            .child(listaEventos.get(position).getIdUsuario())
                                    .child(listaEventos.get(position).getId());

                    feedEventoRef = ConfiguracaoFirebase.getFirebase().child("feedEventos")
                            .child(listaEventos.get(position).getId());

                    presencasConfirmadas = ConfiguracaoFirebase.getFirebase().child("presencasConfirmadas")
                            .child(listaEventos.get(position).getId());

                    // Código para excluir o item
                    eventoRef.removeValue();
                    feedEventoRef.removeValue();
                    presencasConfirmadas.removeValue();

                    // Remove o item da lista de eventos
                    listaEventos.remove(position);

                    // Atualize o RecyclerView
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, listaEventos.size());

                    dialog.cancel();

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

    private void abrirDialogCarregamento(String titulo) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(titulo);
        alert.setCancelable(false); // Impede que o usuário cancele a tela de carregamento
        alert.setView(R.layout.dialog_carregamento);

        dialog = alert.create();
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nomeEvento, nomeLocalEvento, dataHoraEvento, nomeLivro,
                textViewQuantidadeParticipantes, sobreEvento, nomeUsuario;
        private CircleImageView imageViewFotoUsuario;
        private ImageButton buttonMenu;
        private ImageButton imageButtonConfirmarPresenca;

        private boolean presencaConfirmada = false;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewFotoUsuario = itemView.findViewById(R.id.imageViewFotoUsuario);
            nomeUsuario = itemView.findViewById(R.id.textViewNomeUsuarioAdapterEventos);
            nomeEvento = itemView.findViewById(R.id.textViewNomeEvento);
            nomeLocalEvento = itemView.findViewById(R.id.textViewLocalEvento);
            textViewQuantidadeParticipantes = itemView.findViewById(R.id.textViewQuantidadeParticipantes);
            dataHoraEvento = itemView.findViewById(R.id.textViewDataEvento);
            nomeLivro = itemView.findViewById(R.id.textViewNomeLivroAdapterEventos);
            sobreEvento = itemView.findViewById(R.id.textViewSobreEvento);
            buttonMenu = itemView.findViewById(R.id.buttonMenu);
            imageButtonConfirmarPresenca = itemView.findViewById(R.id.imageButtonConfirmarPresenca);

        }
    }
}
