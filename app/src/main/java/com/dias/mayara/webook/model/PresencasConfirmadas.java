package com.dias.mayara.webook.model;

import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;



public class PresencasConfirmadas {

    private int quantidadePresencasConfirmadas = 0;
    private Evento evento;
    private Usuario usuario;


    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
    DatabaseReference presencasConfirmadasRef;
    DatabaseReference quantidadePresencasConfimadasRef;

    public PresencasConfirmadas() {

    }

    public void atualizarQuantidadePresencasConfirmadas(int valor) {

        setQuantidadePresencasConfirmadas(getQuantidadePresencasConfirmadas() + valor);

        quantidadePresencasConfimadasRef.setValue(getQuantidadePresencasConfirmadas());

    }

    public void remover() {

        quantidadePresencasConfimadasRef = firebaseRef.child("presencasConfirmadas")
                .child(evento.getId())
                .child("quantidadePresencasConfirmadas");

        presencasConfirmadasRef = firebaseRef.child("presencasConfirmadas")
                .child(evento.getId())
                .child(usuario.getId());

        quantidadePresencasConfimadasRef.removeValue();
        presencasConfirmadasRef.removeValue();

        atualizarQuantidadePresencasConfirmadas(-1);

    }

    public void salvar() {

        quantidadePresencasConfimadasRef = firebaseRef.child("presencasConfirmadas")
                .child(evento.getId())
                .child("quantidadePresencasConfirmadas");

        presencasConfirmadasRef = firebaseRef.child("presencasConfirmadas")
                .child(evento.getId())
                .child(usuario.getId());

        // Cria objeto Usuario
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("idUsuario", usuario.getId());

        presencasConfirmadasRef.setValue(dadosUsuario);

        atualizarQuantidadePresencasConfirmadas(1);
    }

    public int getQuantidadePresencasConfirmadas() {
        return quantidadePresencasConfirmadas;
    }

    public void setQuantidadePresencasConfirmadas(int quantidadePresencasConfirmadas) {
        this.quantidadePresencasConfirmadas = quantidadePresencasConfirmadas;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
