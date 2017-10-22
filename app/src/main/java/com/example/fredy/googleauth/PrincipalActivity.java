package com.example.fredy.googleauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

public class PrincipalActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        getViews();
        getConfigGoogle();
    }

    private void getViews(){
        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        idTextView = (TextView) findViewById(R.id.idTextView);
    }

    //Configuraciond de google
    private void getConfigGoogle(){
        //Configurar el inicio de session para solicitar la ID del usuario Y correo electronico
        GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

     @Override
    protected void onStart() {
        super.onStart();

         //Verifica que si hay session inciada
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    //Verificara el resultado del la sesion
    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            //Obtenemos los datos del usuario logeado
            GoogleSignInAccount account = result.getSignInAccount();

            //Agregando a las visas
            nameTextView.setText(account.getDisplayName());
            emailTextView.setText(account.getEmail());
            idTextView.setText(account.getId());

            //Usando glide para mostrar la url de la imagen
            Glide.with(this).load(account.getPhotoUrl()).into(photoImageView);
        }else{
            goLogInScreen();
        }
    }

    //Ir a login en de la app
    private void goLogInScreen() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    //cerrar session
    public void logOut(View view) {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Para remover cuenta del usuario
    public void revoke(View view) {

        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_revoke, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Verificando las fallas de conexion
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
