package com.example.davidpazo.okescriba;
import android.speech.RecognizerIntent;
import android.speech.RecognitionListener;
/**
 * Created by david.pazo on 27/4/16.
 */
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Locale;

public class Speech extends Activity implements TextToSpeech.OnInitListener {


    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 2;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 3;

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;

    private TextToSpeech textToSpeech;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        }
        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(new Locale("spa", "ESP"));

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Metodo usado en el boton del microfono
     * Este metodo usa la api de google para el reconocimiento de voz. Una vez con el intent
     * empieza la startActivityForRest donde se trata el text y se determina que accion quiere
     * realizar el usuario
     *
     * @param v view de la vista
     */
    public void micro(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Indicamos el modelo de lenguaje para el intent
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Definimos el mensaje que aparecerá
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Qué es lo que quieres hacer?");
        // Lanzamos la actividad esperando resultados
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**
     * Metodo action_llamar() que realiza todas las operaciones necesarias para realizar
     * una llamada cuando esta es requerida por el usuario
     *
     * @param palabras arraylist con todas las palabras reconocidas por la api de google
     */
    private void action_llamar(String[] palabras) {

        if (palabras.length > 3) {
            for (int i = 3; i < palabras.length; i++) {
                palabras[2] += " " + palabras[i];
            }
        }

        String number = Methods.findNumber(this.getApplicationContext(), palabras[2]);

        if (!number.equals("error")) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));

            // se deberia introducir un mecanismo de control
            //  dejo la estructura hecha
            if ("" == "") {
                startActivity(callIntent);
                //} else {

                //}

            } else {
                textToSpeech.speak("No he podido llamar a: " + palabras[2] +
                                " porque no me has dado los permisos para poder usar el telefono",
                        1, null, null);
            }
        } else {
            textToSpeech.speak("No encontré a: " + palabras[2] + " entre tus contactos",
                    1, null, null);

        }
    }

    /**
     * Metodo onActivityResult() que trata el texto reconocido del usuario y define la accion
     * que quiere realizar el usuario y llama al metodo correspondiente
     *
     * @param requestCode codigo de la peticion
     * @param resultCode  codigo de resultado
     * @param data         Intent con los datos del micro
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {

            ArrayList <String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String[] palabras = matches.get(0).split(" ");

            if (palabras[0].equals("llamar")) {

                action_llamar(palabras);

            } else if ( (palabras[0].equals("contar") && palabras[1].equals("chiste")) ||
                    ( palabras[0].equals("cuéntame") && palabras[2].equals("chiste")) ) {

                textToSpeech.speak(Methods.tell_a_joke(), 1, null, null);

            } else if ( palabras[1].equalsIgnoreCase("hora") ){

                textToSpeech.speak(Methods.time(), 1, null, null);

            } else {

                String frase = "";
                for (int i=0; i<palabras.length; i++)
                    frase += palabras[i] + " ";

                Toast.makeText(this, frase, Toast.LENGTH_LONG).show();

                textToSpeech.speak("Lo siento, no tengo esa orden registrada", 1, null, null);

            }

        }
    }

    /**Este metodo lo necesitamos para la version
     * 6.0 de android ya que en ella es necesario
     * pedirle al usuario que permita a la aplicacion
     * acceder a las distintas parte del telefono.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                //If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                }
                return;
            }
        }

    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.LANG_MISSING_DATA | status == TextToSpeech.LANG_NOT_SUPPORTED) {

            Toast.makeText(this, "ERROR LANG_MISSING_DATA | LANG_NOT_SUPPORTED", Toast.LENGTH_SHORT)
                    .show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.david/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.david/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
