package com.dias.mayara.webook.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dias.mayara.webook.R;
import com.dias.mayara.webook.helper.ConfiguracaoFirebase;
import com.dias.mayara.webook.helper.UsuarioFirebase;
import com.dias.mayara.webook.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EdicaoPerfilActivity extends AppCompatActivity {

    private CircleImageView ImageViewFotoPerfilUsuario;
    private TextView textViewAlterarFoto;
    private TextInputEditText editTextEditarNomeUsuario;
    private Button buttonSalvarAlteracoes;
    private Usuario usuarioLogado;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageRef;
    private String identificadorUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicao_perfil);

        inicializarComponentes();

        // Configuração iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar perfil");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        // Configurar botão de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_branco);

        // Recuperar dados do usuário
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editTextEditarNomeUsuario.setText(usuarioPerfil.getDisplayName());

        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null) {

            Glide.with(EdicaoPerfilActivity.this).load(url).into(ImageViewFotoPerfilUsuario);
        } else {
            ImageViewFotoPerfilUsuario.setImageResource(R.drawable.icone_account_circle);
        }

    }

    public void inicializarComponentes() {

        ImageViewFotoPerfilUsuario = findViewById(R.id.ImageViewFotoPerfilUsuario);
        textViewAlterarFoto = findViewById(R.id.textViewAlterarFoto);
        editTextEditarNomeUsuario = findViewById(R.id.editTextEditarNomeUsuario);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);

        // Salvar alterações no nome
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nomeAtualizado = editTextEditarNomeUsuario.getText().toString();

                // Atualizar nome no firebase
                UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                // Atualizar nome dentro do banco de dados
                usuarioLogado.setNomeUsuario(nomeAtualizado);
                usuarioLogado.atualizar();

                Toast.makeText(EdicaoPerfilActivity.this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();

            }
        });

        // Alterar foto do usuário
        textViewAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // Abre a galeria do celular

                if(i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

            Bitmap imagem = null;

            try {
                // Selecao apenas da galeria de fotos
                if(requestCode == SELECAO_GALERIA) {
                    Uri localImagemSelecionada = data.getData();
                    imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                }

                // Caso tenha sido escolhida uma imagem
                if(imagem != null) {

                    // Configura imagem na tela
                    ImageViewFotoPerfilUsuario.setImageBitmap(imagem);

                    // Recuperar dados da imagem para salvar no firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // Salvar imagem no firebase
                    final StorageReference imagemRef = storageRef.child("imagens").
                            child("perfil").child(identificadorUsuario + ".jpeg");
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EdicaoPerfilActivity.this, "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizarFotoUsuario(url);
                                }
                            });
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void atualizarFotoUsuario(Uri url) {

        // Atualizar foto no perfil
        UsuarioFirebase.atualizarFotoUsuario(url);

        // Atualizar foto no firebase
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizar();

        Toast.makeText(this, "Sua foto foi atualizada!", Toast.LENGTH_SHORT).show();

    }

    /*
     * Método que ajusta o botão de voltar para que, ao invés de levar para a MainActivity com
     * o PublicacoesFragment configurado, configure o PerfilFragment
     */
    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;
    }
}