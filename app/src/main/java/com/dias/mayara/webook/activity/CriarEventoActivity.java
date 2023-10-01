package com.dias.mayara.webook.activity;

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

import com.dias.mayara.webook.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CriarEventoActivity extends AppCompatActivity {

    private Button buttonSelecionarDataHora;
    private Calendar calendar;
    private String dataHoraFormatada;
    private EditText dataHoraEvento;
    private Button buttonCriarEvento;

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

        // Inicialização dos componentes
        buttonSelecionarDataHora = findViewById(R.id.btnSelecionarDataHora);
        calendar = Calendar.getInstance();
        dataHoraEvento = findViewById(R.id.dataHoraEvento);

        buttonSelecionarDataHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDatePicker();
            }
        });
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


    // Método que ajusta o botão de voltar para ele fechar a activity atual
    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;
    }
}