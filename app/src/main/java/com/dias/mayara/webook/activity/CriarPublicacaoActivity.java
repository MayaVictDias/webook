package com.dias.mayara.webook.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dias.mayara.webook.R;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Publicacao;
import com.dias.mayara.webook.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class CriarPublicacaoActivity extends AppCompatActivity {

    private EditText editTextTituloPublicacao, editTextNomeLivro, editTextTextoPublicacao;

    private Button buttonAdicionarImagem, buttonCriarPublicacao;
    private ImageView imageViewImagemSelecionada;
    private static final int SELECAO_GALERIA = 200;
    private String idUsuarioLogado;
    private Bitmap imagemPublicacao = null;
    private DatabaseReference usuariosRef;
    private DatabaseReference firebaseRef;
    private DatabaseReference usuarioLogadoRef;
    private Usuario usuarioLogado;
    private AlertDialog dialog;
    private DataSnapshot seguidoresSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_publicacao);

        // Configurações iniciais
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        // Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Criar publicação");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Configurar botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_branco);

        inicializarComponentes();

        recuperarDadosPostagem();

        buttonAdicionarImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // Abre a galeria do celular

                if(i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        buttonCriarPublicacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                criarPublicacao();
            }
        });
    }

    private void criarPublicacao() {

        abrirDialogCarregamento("Salvando publicação");

        Publicacao publicacao = new Publicacao();

        publicacao.setIdUsuario(idUsuarioLogado);
        publicacao.setTextoPublicacao(editTextTextoPublicacao.getText().toString());

        // Recuperar dados da imagem para o firebase caso tenha sido escolhida uma imagem
        if(imagemPublicacao != null) {

            // Recuperar dados da imagem para salvar no firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagemPublicacao.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] dadosImagem = baos.toByteArray();

            // Salvar imagem no firebase
            StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
            StorageReference imagemRef = storageRef.child("imagens").
                    child("publicacoes").child(publicacao.getId() + ".jpeg");
            UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CriarPublicacaoActivity.this, "Erro ao salvar a postagem!",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri url = task.getResult();
                            publicacao.setCaminhoFotoPublicacao(url.toString());
                            Log.i("Caminho foto publicacao: ", publicacao.getCaminhoFotoPublicacao());

                        }
                    });
                }
            });
        }

        // Atualizar quantidade de postagens
        int quantidadePostagens = usuarioLogado.getNumeroPostagens() + 1;
        usuarioLogado.setNumeroPostagens( quantidadePostagens );
        usuarioLogado.atualizarQuantidadePostagens();

        if(publicacao.salvar(seguidoresSnapshot)) {

            Toast.makeText(CriarPublicacaoActivity.this,
                    "Sucesso ao salvar a postagem!",
                    Toast.LENGTH_SHORT).show();

            dialog.cancel();
            finish();
        } else {
            Toast.makeText(CriarPublicacaoActivity.this,
                    "Erro!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Abrir tela de carregamento quando o usuario clica no botao de "criar publicação"
    private void abrirDialogCarregamento(String titulo) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false); // Impede que o usuário cancele a tela de carregamento
        alert.setView(R.layout.dialog_carregamento);

        dialog = alert.create();
        dialog.show();

    }

    private void recuperarDadosPostagem(){

        abrirDialogCarregamento("Carregando dados. Aguarde!");
        usuarioLogadoRef = usuariosRef.child( idUsuarioLogado );
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Recupera dados de usuário logado
                usuarioLogado = snapshot.getValue( Usuario.class );

                // Recuperar os seguidores do usuário
                DatabaseReference seguidoresRef = firebaseRef.child("seguidores")
                        .child(idUsuarioLogado);

                seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        seguidoresSnapshot = snapshot;

                        dialog.cancel();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

            try {
                // Selecao apenas da galeria de fotos
                if(requestCode == SELECAO_GALERIA) {
                    Uri localImagemSelecionada = data.getData();
                    imagemPublicacao = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                }

                // Caso tenha sido escolhida uma imagem
                if(imagemPublicacao != null) {

                    imageViewImagemSelecionada.setVisibility(View.VISIBLE);
                    buttonAdicionarImagem.setText("Alterar imagem");

                    // Configura imagem na tela
                    imageViewImagemSelecionada.setImageBitmap(imagemPublicacao);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void inicializarComponentes() {

        editTextTituloPublicacao = findViewById(R.id.editTextTituloPublicacao);
        editTextNomeLivro = findViewById(R.id.editTextNomeLivro);
        editTextTextoPublicacao = findViewById(R.id.editTextDescricao);

        buttonAdicionarImagem = findViewById(R.id.buttonAdicionarImagem);
        buttonCriarPublicacao = findViewById(R.id.buttonCriarPublicacao);
        imageViewImagemSelecionada = findViewById(R.id.imageViewImagemSelecionada);

    }

    // Método que ajusta o botão de voltar para ele fechar a activity atual
    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;
    }
}