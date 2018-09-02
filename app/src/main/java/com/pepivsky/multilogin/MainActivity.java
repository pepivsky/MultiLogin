package com.pepivsky.multilogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    @BindView(R.id.imgFotoPerfil)
    ImageView imgFotoPerfil;
    @BindView(R.id.tvNombreUsusario)
    TextView tvNombreUsusario;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.tvProvider)
    TextView tvProvider;


    private FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener mauthStateListener;
    private static final String PROVEEDOR_DESCONOCIDO = "Proveedor desconocido :(";
    private static final String PASSWORD_FIREBASE = "pasword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mfirebaseAuth = FirebaseAuth.getInstance();

        mauthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    CargardatosUsuario(user.getDisplayName(), user.getEmail(), user.getProviders() != null ?
                            user.getProviders().get(0) : PROVEEDOR_DESCONOCIDO);
                } else {
                    onSignedOutLimpiar();
                    //Inicializar los proveedores
                    //Email y contraseña
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setTosUrl("https://www.youtube.com/watch?v=uLe-QQEKOwA")
                            .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build()))
                            .build(), RC_SIGN_IN);

                }
            }
        };
    }

    private void onSignedOutLimpiar() {
        CargardatosUsuario("", "", "");
    }

    private void CargardatosUsuario(String username, String email, String provider) {
        tvNombreUsusario.setText(username);
        tvEmail.setText(email);

        int drawableRes;

        switch (provider){
            case PASSWORD_FIREBASE:
                drawableRes = R.drawable.ic_firebase;
                break;

                default:
                    drawableRes = R.drawable.ic_block_helper;
                    provider = PROVEEDOR_DESCONOCIDO;
                    break;


        }

        tvProvider.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableRes, 0, 0 ,0);
        tvProvider.setText(provider);
    }

    //Metodo para saber el resultado


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                //Aqui van las acciones depues de logearse
                Toast.makeText(this, "Bienvenido! :)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Algo salió mal :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mfirebaseAuth.addAuthStateListener(mauthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mauthStateListener != null) {
            mfirebaseAuth.addAuthStateListener(mauthStateListener);
        }
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_salir, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Accion del menu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cerrar_sesion:
                AuthUI.getInstance().signOut(this); //Cerrar sesion de firebase
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }

    }
}
