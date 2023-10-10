package com.dias.mayara.webook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Evento;
import com.dias.mayara.webook.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CriarEventoActivity extends AppCompatActivity {

    private Button buttonSelecionarDataHora;
    private Calendar calendar;
    private String dataHoraFormatada;
    private Button buttonCriarEvento;
    private AlertDialog dialog;
    private String idUsuarioLogado;
    private EditText editTextNomeEvento, editTextLocalEvento, editTextNomeLivroASerDiscutido,
            dataHoraEvento, editTextSobreOEvento;
    private Usuario usuarioLogado;
    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private DataSnapshot seguidoresSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_evento);

        // Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Criar evento");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Configurar botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_branco);

        // Configurações iniciais
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        recuperarDadosEvento();

        // Inicialização dos componentes
        buttonSelecionarDataHora = findViewById(R.id.btnSelecionarDataHora);
        calendar = Calendar.getInstance();
        dataHoraEvento = findViewById(R.id.dataHoraEvento);
        buttonCriarEvento = findViewById(R.id.buttonCriarEvento);
        editTextNomeEvento = findViewById(R.id.editTextNomeEvento);
        editTextLocalEvento = findViewById(R.id.editTextLocalEvento);
        editTextNomeLivroASerDiscutido = findViewById(R.id.editTextNomeLivroASerDiscutido);
        editTextSobreOEvento = findViewById(R.id.editTextSobreOEvento);

        buttonSelecionarDataHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDatePicker();
            }
        });

        buttonCriarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                criarEvento();
            }
        });
    }

    private void criarEvento() {

        abrirDialogCarregamento("Criando evento");

        Evento evento = new Evento();

        evento.setIdUsuario(idUsuarioLogado);
        evento.setNomeEvento(editTextNomeEvento.getText().toString());
        evento.setNomeLocalEvento(editTextLocalEvento.getText().toString());
        evento.setDataHoraEvento(dataHoraEvento.getText().toString());
        evento.setNomeLivro(editTextNomeLivroASerDiscutido.getText().toString());
        evento.setSobreEvento(editTextSobreOEvento.getText().toString());

        // Atualizar quantidade de eventos
        int quantidadeEventos = usuarioLogado.getNumeroEventos() + 1;
        usuarioLogado.setNumeroEventos( quantidadeEventos );
        usuarioLogado.atualizarQuantidadeEventos();

        if(evento.salvar(seguidoresSnapshot)) {

            Toast.makeText(CriarEventoActivity.this,
                    "Sucesso ao salvar evento!",
                    Toast.LENGTH_SHORT).show();

            dialog.cancel();
            finish();
        } else {
            Toast.makeText(CriarEventoActivity.this,
                    "Erro!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Abre calendario para o usuário selecionar uma data
    private void abrirDatePicker() {
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        abrirTimePicker();
                    }
                }, ano, mes, dia);

        datePickerDialog.show();
    }

    // Abre relogio para usuário selecionar uma hora
    private void abrirTimePicker() {
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        dataHoraFormatada = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(calendar.getTime());
                        dataHoraEvento.setText(dataHoraFormatada);
                        dataHoraEvento.setVisibility(View.VISIBLE);
                        buttonSelecionarDataHora.setText("Alterar data e hora");
                    }
                }, hora, minuto, true);

        timePickerDialog.show();
    }

    private void abrirDialogCarregamento(String titulo) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false); // Impede que o usuário cancele a tela de carregamento
        alert.setView(R.layout.dialog_carregamento);

        dialog = alert.create();
        dialog.show();

    }

    private void recuperarDadosEvento(){

        abrirDialogCarregamento("Carregando dados. Aguarde!");

        usuarioLogadoRef = usuariosRef.child( idUsuarioLogado );
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Recupera dados de usuário logado
                usuarioLogado = snapshot.getValue( Usuario.class );

                // Recuperar os seguidores do usuário
                DatabaseReference seguidoresRef = firebaseRef.child("seguindo")
                        .child(idUsuarioLogado);

                seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        seguidoresSnapshot = snapshot;

                        dialog.cancel();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    // Método que ajusta o botão de voltar para ele fechar a activity atual
    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}