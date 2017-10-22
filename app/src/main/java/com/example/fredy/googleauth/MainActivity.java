package com.example.fredy.googleauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //objeto de el asiste de googleClient
    private GoogleApiClient googleApiClient;

    //Objeto del View
    private SignInButton signInButton;

    private static final int RC_sIGN_IN= 777;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getView();
        getgoogleClient();
        getOnClick();
    }

    public void getView(){
        //Obtenemos todos los componentes del layout
        signInButton = (SignInButton) findViewById(R.id.my_googleSignin);
    }

    public void getgoogleClient(){
        //Configurar el inicio de session para solicitar la ID del usuario Y correo electronico
        GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


    }

    private void getOnClick(){
        //Agregando el evento OnClick
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLogin();
            }
        });
    }

    private void goLogin() {
        //Metodo que realizara el loginpor medio de google
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_sIGN_IN);
    }

    //Recuperar el resultado de inicio de sesion
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_sIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    //Verificando que el inicio de sesion sea exitoso
    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            sucessLogin();
        }else{
            Toast.makeText(this, R.string.no_inicio, Toast.LENGTH_SHORT).show();
        }
    }

    private void sucessLogin() {
        //Intanciando la nueva actividad
        Intent i = new Intent(this, PrincipalActivity.class);
        Toast.makeText(this, "Bienvenido usuarios ", Toast.LENGTH_SHORT).show();
        startActivity(i);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
