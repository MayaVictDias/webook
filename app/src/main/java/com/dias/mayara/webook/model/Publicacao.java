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
    private String nomeLivro;
    private String textoPublicacao;
    private String caminhoFotoPublicacao;
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
            dadosSeguidor.put("caminhoFotoPublicacao", getCaminhoFotoPublicacao());
            dadosSeguidor.put("textoPublicacao", getTextoPublicacao());
            dadosSeguidor.put("id", getId());

            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNomeUsuario());
            dadosSeguidor.put("caminhoFotoUsuario", usuarioLogado.getCaminhoFotoUsuario());

            String idsAtualizacao = "/" + idSeguidor + "/" + getId();
            objeto.put("/feed" + idsAtualizacao, dadosSeguidor);
        }

        firebaseRef.updateChildren(objeto);

        // postagensRef.setValue(this);

        return true;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getNomeLivro() {
        return nomeLivro;
    }

    public void setNomeLivro(String nomeLivro) {
        this.nomeLivro = nomeLivro;
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

    public String getTextoPublicacao() {
        return textoPublicacao;
    }

    public void setTextoPublicacao(String textoPublicacao) {
        this.textoPublicacao = textoPublicacao;
    }

    public String getCaminhoFotoPublicacao() {
        return caminhoFotoPublicacao;
    }

    public void setCaminhoFotoPublicacao(String caminhoFotoPublicacao) {
        this.caminhoFotoPublicacao = caminhoFotoPublicacao;
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
        publicacaoMap.put("nomeLivro", getNomeLivro());
        publicacaoMap.put("textoPublicacao", getTextoPublicacao());
        publicacaoMap.put("caminhoFotoPublicacao", getCaminhoFotoPublicacao());

        return publicacaoMap;
    }
}
