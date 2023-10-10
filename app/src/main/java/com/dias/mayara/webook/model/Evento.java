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
    private String nomeLivro;
    private String sobreEvento;

    public Evento() {

        // Configuracao de um id unico para o evento
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference postagemRef = firebaseRef.child("eventos");
        String idEvento = postagemRef.push().getKey();
        setId(idEvento);
    }

    public boolean salvar(DataSnapshot seguidoresSnapshot) {

        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        String combinacaoId = "/" + getIdUsuario() + "/" + getId();
        objeto.put("/eventos" + combinacaoId, this);

            HashMap<String, Object> dadosEvento = new HashMap<>();
            dadosEvento.put("nomeEvento", getNomeEvento());
            dadosEvento.put("nomeLocalEvento", getNomeLocalEvento());
            dadosEvento.put("dataHoraEvento", getDataHoraEvento());
            dadosEvento.put("sobreEvento", getSobreEvento());
            dadosEvento.put("nomeLivroASerDiscutido", getNomeLivro());

            dadosEvento.put("idPublicacao", getId());
            dadosEvento.put("idUsuario", usuarioLogado.getId());

            String idsAtualizacao = "/" + getId();
            objeto.put("/feedEventos" + idsAtualizacao, dadosEvento);


        firebaseRef.updateChildren(objeto);

        return true;
    }

    public Map<String, Object> converterParaMap() {

        HashMap<String, Object> eventoMap = new HashMap<>();
        eventoMap.put("id", getId());
        eventoMap.put("nomeEvento", getNomeEvento());
        eventoMap.put("nomeLocalEvento", getNomeLocalEvento());
        eventoMap.put("dataHoraEvento", getDataHoraEvento());
        eventoMap.put("nomeLivro", getNomeLivro());
        eventoMap.put("sobreEvento", getSobreEvento());

        return eventoMap;
    }

    public String getId() {
        return id;
    }

    public String getNomeLivro() {
        return nomeLivro;
    }

    public void setNomeLivro(String nomeLivro) {
        this.nomeLivro = nomeLivro;
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
