package com.dias.mayara.webook.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dias.mayara.webook.R;
import com.dias.mayara.webook.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PesquisaAdapter extends RecyclerView.Adapter<PesquisaAdapter.MyViewHolder> {

    private List<Usuario> listaUsuario;
    private Context context;

    public PesquisaAdapter(List<Usuario> listaUsuario, Context context) {
        this.listaUsuario = listaUsuario;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesquisa_usuario, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Usuario usuario = listaUsuario.get(position);

        holder.nomeUsuario.setText(usuario.getNomeUsuario());

        // Testar se o usuário tem uma imagem pra ser recuperada
        if( usuario.getCaminhoFoto() != null && !usuario.getCaminhoFoto().isEmpty() ){
            Uri uri = Uri.parse( usuario.getCaminhoFoto() );
            Glide.with(context).load(uri).into(holder.fotoUsuario);
        }else {
            holder.fotoUsuario.setImageResource(R.drawable.icone_account_circle);
        }

    }

    @Override
    public int getItemCount() {
        return listaUsuario.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView fotoUsuario;
        TextView nomeUsuario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fotoUsuario = itemView.findViewById(R.id.imageViewPesquisaFotoUsuario);
            nomeUsuario = itemView.findViewById(R.id.textViewPesquisaNomeUsuario);

        }
    }


}
