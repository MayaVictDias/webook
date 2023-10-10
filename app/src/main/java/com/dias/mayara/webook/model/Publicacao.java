package com.dias.mayara.webook.model;

import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Publicacao {

    private String id;
    private String idUsuario;
    private String nomePublicacao;
    private String descricao;
    private String caminhoFoto;
    private int numeroCurtidas;
    private int numeroComentarios;

    public Publicacao() {

        // Configuracao de um id unico para a postagem
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference postagemRef = firebaseRef.child("postagens");
        String idPostagem = postagemRef.push().getKey();
        setId(idPostagem);

    }

    // Metodo criado utilizando estratégia de espalhamento do firebase
    public boolean salvar(DataSnapshot seguidoresSnapshot) {

        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        //String idUsuarioLogado = usuarioLogado.getId();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        String combinacaoId = "/" + getIdUsuario() + "/" + getId();
        objeto.put("/postagens" + combinacaoId, this);

        for(DataSnapshot seguidores: seguidoresSnapshot.getChildren()) {

            String idSeguidor = seguidores.getKey();

            HashMap<String, Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("caminhoFoto", getCaminhoFoto());
            dadosSeguidor.put("descricao", getDescricao());
            dadosSeguidor.put("id", getId());

            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNomeUsuario());
            dadosSeguidor.put("caminhoFotoUsuario", usuarioLogado.getCaminhoFoto());

            String idsAtualizacao = "/" + idSeguidor + "/" + getId();
            dadosSeguidor.put("/feedEventos" + idsAtualizacao, dadosSeguidor);
        }

        firebaseRef.updateChildren(objeto);

        // postagensRef.setValue(this);

        return true;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomePublicacao() {
        return nomePublicacao;
    }

    public void setNomePublicacao(String nomePublicacao) {
        this.nomePublicacao = nomePublicacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public int getNumeroCurtidas() {
        return numeroCurtidas;
    }

    public void setNumeroCurtidas(int numeroCurtidas) {
        this.numeroCurtidas = numeroCurtidas;
    }

    public int getNumeroComentarios() {
        return numeroComentarios;
    }

    public void setNumeroComentarios(int numeroComentarios) {
        this.numeroComentarios = numeroComentarios;
    }

    /*
     * Recupera os valores do firebase que estão nos formatos que são do tipo chave String e valor
     * Object
     */
    public Map<String, Object> converterParaMap() {

        HashMap<String, Object> publicacaoMap = new HashMap<>();
        publicacaoMap.put("nomePublicacao", getNomePublicacao());
        publicacaoMap.put("descricao", getDescricao());
        publicacaoMap.put("caminhoFoto", getCaminhoFoto());
        publicacaoMap.put("numeroCurtidas", getNumeroCurtidas());
        publicacaoMap.put("numeroComentarios", getNumeroComentarios());

        return publicacaoMap;
    }
}
