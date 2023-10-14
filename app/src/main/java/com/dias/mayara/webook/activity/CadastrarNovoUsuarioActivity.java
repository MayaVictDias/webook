package com.dias.mayara.webook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastrarNovoUsuarioActivity extends AppCompatActivity {

    private TextInputEditText editTextCadastroNome, editTextCadastroEmail,
            editTextCadastroSenha, editTextCadastroConfirmarSenha;
    private Button buttonCadastroCadastrar;
    private ProgressBar progressBarCadastrarNovoUsuario;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_novo_usuario);

        inicializarComponentes();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Cadastro");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Configurar botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_branco);

        // Ao clique do botão, o usuário é cadastrado
        progressBarCadastrarNovoUsuario.setVisibility(View.GONE);
        buttonCadastroCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Verificar se todos os campos estão preenchidos
                String textoNome = editTextCadastroNome.getText().toString();
                String textoEmail = editTextCadastroEmail.getText().toString();
                String textoSenha = editTextCadastroSenha.getText().toString();
                String textoConfirmarSenha = editTextCadastroConfirmarSenha.getText().toString();

                // Se algum campo não estiver preenchido, exibe uma mensagem de erro pro usuário
                if(!textoNome.isEmpty()) {
                    if(!textoEmail.isEmpty()) {
                        if(!textoSenha.isEmpty()) {
                            if(!textoConfirmarSenha.isEmpty()) {
                                if(!textoSenha.equals(textoConfirmarSenha)) {
                                    Toast.makeText(CadastrarNovoUsuarioActivity.this, "Senhas digitadas não coincidem!", Toast.LENGTH_SHORT).show();
                                } else {

                                    // Exibir progress bar
                                    progressBarCadastrarNovoUsuario.setVisibility(View.VISIBLE);

                                    // Caso todas as verificações sejam bem-sucedidas, cria uma classe usuário para salvar no banco de dados

                                    usuario = new Usuario();

                                    usuario.setNomeUsuario(textoNome);
                                    usuario.setEmailUsuario(textoEmail);
                                    usuario.setSenhaUsuario(textoSenha);

                                    // Insere usuário no banco de dados
                                    cadastrarUsuario(usuario);
                                }
                            } else {
                                Toast.makeText(CadastrarNovoUsuarioActivity.this, "Preencha o campo Confirmar Senha!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(CadastrarNovoUsuarioActivity.this, "Preencha o campo Senha!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(CadastrarNovoUsuarioActivity.this, "Preencha o campo Email!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastrarNovoUsuarioActivity.this, "Preencha o campo Nome!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Metodo que inicializa os componentes do arquivo xml
    public void inicializarComponentes() {

        editTextCadastroNome = findViewById(R.id.editTextCadastroNome);
        editTextCadastroEmail = findViewById(R.id.editTextCadastroEmail);
        editTextCadastroSenha = findViewById(R.id.editTextCadastroSenha);
        editTextCadastroConfirmarSenha = findViewById(R.id.editTextCadastroConfirmarSenha);

        buttonCadastroCadastrar = findViewById(R.id.buttonCadastroCadastrar);

        progressBarCadastrarNovoUsuario = findViewById(R.id.progressBarCadastrarNovoUsuario);
    }

    // Metodo que insere um usuario no banco de dados
    public void cadastrarUsuario(Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmailUsuario(),
                usuario.getSenhaUsuario()
        ).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String erroExcecao = null;
                        if (task.isSuccessful()) { // Login bem-sucedido

                            try {

                                progressBarCadastrarNovoUsuario.setVisibility(View.GONE);

                                // Salvar dados do usuário no firebase
                                String idUsuario = task.getResult().getUser().getUid(); // Recupera ID do usuario criado pelo firebase
                                usuario.setId(idUsuario);
                                usuario.salvar();

                                // Salvar nome do usuário
                                UsuarioFirebase.atualizarNomeUsuario(usuario.getNomeUsuario());

                                Toast.makeText(CadastrarNovoUsuarioActivity.this,
                                        "Cadastro realizado com sucesso!",
                                        Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish(); // Método para encerrar a activity atual de cadastrar usuário

                            } catch (Exception e) {

                                e.printStackTrace();
                            }

                        } else { // Login com erro

                            progressBarCadastrarNovoUsuario.setVisibility(View.GONE);

                            // Tratamento do erro
                            erroExcecao = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                erroExcecao = "Digite uma senha mais forte!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erroExcecao = "Por favor, digite um email válido";
                            } catch (FirebaseAuthUserCollisionException e) {
                                erroExcecao = "Esta conta já foi cadastrada";
                            } catch (Exception e) {
                                erroExcecao = "ao cadastrar usuario " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(CadastrarNovoUsuarioActivity.this,
                                    "Erro: " + erroExcecao,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}