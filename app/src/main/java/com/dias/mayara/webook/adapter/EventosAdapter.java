package com.dias.mayara.webook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.model.Evento;

import java.util.List;

public class EventosAdapter extends RecyclerView.Adapter<EventosAdapter.MyViewHolder> {

        private List<Evento> listaEventos;
        private Context context;

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

            holder.nomeEvento.setText(evento.getNomeEvento());
            holder.nomeLocalEvento.setText(evento.getNomeLocalEvento());
            holder.dataHoraEvento.setText(evento.getDataHoraEvento());
            holder.sobreEvento.setText(evento.getSobreEvento());

    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nomeEvento;
        private TextView nomeLocalEvento;
        private TextView dataHoraEvento;
        private TextView sobreEvento;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nomeEvento = itemView.findViewById(R.id.textViewNomeEvento);
            nomeLocalEvento = itemView.findViewById(R.id.textViewLocalEvento);
            dataHoraEvento = itemView.findViewById(R.id.textViewDataEvento);
            sobreEvento = itemView.findViewById(R.id.textViewSobreEvento);

        }
    }
}
