package com.dias.mayara.webook.adapter;

import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dias.mayara.webook.R;

import org.w3c.dom.Text;

public class PublicacoesAdapter extends RecyclerView.Adapter<PublicacoesAdapter.MyViewHolder> {
    @NonNull
    @Override
    public PublicacoesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacoesAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewFotoPerfil;
        private TextView textViewNomeAutorPost;
        private TextView textViewNomeLivro;
        private TextView textViewTextoPublicacao;
        private TextView imageViewImagemPublicacao;
        private TextView textViewNumeroCurtidas;
        private TextView textViewNumeroComentarios;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewFotoPerfil = itemView.findViewById(R.id.imageViewFotoPerfil);
            textViewNomeAutorPost = itemView.findViewById(R.id.textViewNomeAutorPost);
            textViewNomeLivro = itemView.findViewById(R.id.textViewNomeLivro);
            textViewTextoPublicacao = itemView.findViewById(R.id.textViewTextoPublicacao);
            imageViewImagemPublicacao = itemView.findViewById(R.id.imageViewImagemPublicacao);
            textViewNumeroCurtidas = itemView.findViewById(R.id.textViewNumeroCurtidas);
            textViewNumeroComentarios = itemView.findViewById(R.id.textViewNumeroComentarios);


        }
    }
}
