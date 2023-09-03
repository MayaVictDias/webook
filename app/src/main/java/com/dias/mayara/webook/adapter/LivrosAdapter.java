package com.dias.mayara.webook.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dias.mayara.webook.R;

import com.dias.mayara.webook.R;

public class LivrosAdapter extends RecyclerView.Adapter<LivrosAdapter.MyViewHolder> {
    @NonNull
    @Override
    public LivrosAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LivrosAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewCapaLivro;
        private TextView textViewTituloLivro;
        private TextView textViewAutorLivro;
        private TextView textViewGeneroLivro;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewCapaLivro = itemView.findViewById(R.id.imageViewCapaLivro);
            textViewTituloLivro = itemView.findViewById(R.id.textViewTituloLivro);
            textViewAutorLivro = itemView.findViewById(R.id.textViewAutorLivro);
            textViewGeneroLivro = itemView.findViewById(R.id.textViewGeneroLivro);
        }



    }
}
