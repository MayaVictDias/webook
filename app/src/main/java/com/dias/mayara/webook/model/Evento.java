package com.dias.mayara.webook.model;

import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Evento {

    private String id;
    private String idUsuario;
    private String nomeEvento;
    private String nomeLocalEvento;
    private String dataHoraEvento;
    private String sobreEvento;

    public Evento() {

        // Configuracao de um id unico para o evento
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference postagemRef = firebaseRef.child("eventos");
        String idEvento = postagemRef.push().getKey();
        setId(idEvento);
    }

    public boolean salvar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference eventosRef = firebaseRef
                .child("eventos")
                .child( getIdUsuario() );

        eventosRef.setValue(this);

        return true;
    }

    public Map<String, Object> converterParaMap() {

        HashMap<String, Object> eventoMap = new HashMap<>();
        eventoMap.put("nomeEvento", getNomeEvento());
        eventoMap.put("nomeLocalEvento", getNomeLocalEvento());
        eventoMap.put("dataHoraEvento", getDataHoraEvento());
        eventoMap.put("sobreEvento", getSobreEvento());

        return eventoMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    public String getNomeLocalEvento() {
        return nomeLocalEvento;
    }

    public void setNomeLocalEvento(String nomeLocalEvento) {
        this.nomeLocalEvento = nomeLocalEvento;
    }

    public String getDataHoraEvento() {
        return dataHoraEvento;
    }

    public void setDataHoraEvento(String dataHoraEvento) {
        this.dataHoraEvento = dataHoraEvento;
    }

    public String getSobreEvento() {
        return sobreEvento;
    }

    public void setSobreEvento(String sobreEvento) {
        this.sobreEvento = sobreEvento;
    }
}
