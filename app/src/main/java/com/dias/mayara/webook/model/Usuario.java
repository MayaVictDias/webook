package com.dias.mayara.webook.model;

import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String id;
    private String nomeUsuario;
    private String emailUsuario;
    private String senhaUsuario;
    private String caminhoFoto;
    private int numeroPostagens = 0;
    private int numeroFavoritos = 0;
    private int numeroSeguidores = 0;
    private int numeroSeguindo = 0;

    public int getNumeroPostagens() {
        return numeroPostagens;
    }

    public void setNumeroPostagens(int numeroPostagens) {
        this.numeroPostagens = numeroPostagens;
    }

    public int getNumeroFavoritos() {
        return numeroFavoritos;
    }

    public void setNumeroFavoritos(int numeroFavoritos) {
        this.numeroFavoritos = numeroFavoritos;
    }

    public int getNumeroSeguidores() {
        return numeroSeguidores;
    }

    public void setNumeroSeguidores(int numeroSeguidores) {
        this.numeroSeguidores = numeroSeguidores;
    }

    public int getNumeroSeguindo() {
        return numeroSeguindo;
    }

    public void setNumeroSeguindo(int numeroSeguindo) {
        this.numeroSeguindo = numeroSeguindo;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public Usuario() {
    }

    // Referencia ao banco de dados Usuario dentro do firebase
    public void salvar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(getId());
        usuariosRef.setValue( this );
    }

    // Metodo que atualiza os valores no firebase
    public void atualizar() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(getId());

        Map<String, Object> valoresUsuario = converterParaMap();
        usuariosRef.updateChildren(valoresUsuario);

    }

    /*
     * Recupera os valores do firebase que estão nos formatos que são do tipo chave String e valor
     * Object
     */
    public Map<String, Object> converterParaMap() {

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmailUsuario());
        usuarioMap.put("nome", getNomeUsuario());
        usuarioMap.put("id", getId());
        usuarioMap.put("caminhoFoto", getCaminhoFoto());

        usuarioMap.put("numeroPostagens", getNumeroPostagens());
        usuarioMap.put("numeroFavoritos", getNumeroFavoritos());
        usuarioMap.put("numeroSeguidores", getNumeroSeguidores());
        usuarioMap.put("numeroSeguindo", getNumeroSeguindo());

        return usuarioMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    // Notação Exclude desconsidera o atributo marcado na hora de salvar no banco de dados
    @Exclude
    public String getSenhaUsuario() {
        return senhaUsuario;
    }

    public void setSenhaUsuario(String senhaUsuario) {
        this.senhaUsuario = senhaUsuario;
    }
}
