package com.example.fredy.googleauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    //objeto de el asiste de googleClient
    private GoogleApiClient googleApiClient;

    //Objeto del View
    private SignInButton signInButton;

    private static final int RC_sIGN_IN= 777;

    //variable global de firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getView();
        getIntializeFirebase();
        getgoogleClient();
        getOnClick();
    }

    public void getView(){
        //Obtenemos todos los componentes del layout
        signInButton = (SignInButton) findViewById(R.id.my_googleSignin);
    }

    private void getIntializeFirebase(){
        //Metodo que Inicializa Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        //Verifica si estamos autentificados
        authStateListener = new FirebaseAuth.AuthStateListener() {
            //Metodo que checara si estamos autentificados o no
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //Se ejecuta cuando cambie el estado de autentificacion
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    sucessLogin(); //Abrira la nueva activity
                }
            }
        };
    }
    //Escuchara los cambios de estado de autentificacion
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public void getgoogleClient(){
        //Configurar el inicio de session para solicitar la ID del usuario Y correo electronico
        GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) //Pedimos el token que nos devuelve google
                .requestEmail()
                .build();

        //Construlle el apiCliente
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

    //Intent de la api de google
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
            //sucessLogin(); //sin firebase
            firebaseWithGoogle(result.getSignInAccount());
        }else{
            Toast.makeText(this, R.string.no_inicio, Toast.LENGTH_SHORT).show();
        }
    }
    //Metodo de autentificacion con firebases
    private void firebaseWithGoogle(GoogleSignInAccount signInAccount) {
        //Proporcionamos el token
        AuthCredential credencial = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);


        firebaseAuth.signInWithCredential(credencial).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Metodo que realiza cuando termina la peticion de autentificacion
                if(task.isSuccessful()){
                   //manda mensaje si no se pudo autentificar
                    Toast.makeText(getApplicationContext(),R.string.no_firebase_auth, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Metodo con autentificacion sin firebase
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

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
