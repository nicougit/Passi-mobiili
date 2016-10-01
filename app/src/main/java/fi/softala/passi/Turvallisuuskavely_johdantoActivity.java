package fi.softala.passi;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Turvallisuuskavely_johdantoActivity extends AppCompatActivity {

    TabHost tabHost;
    File file;
    Uri fileUri;
    File kuva1, kuva2, kuva3, kuva4, kuva5;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;
    int kameraButtonPressed = 0;
    final int RC_TAKE_PHOTO = 1;
    int selectedId;
    RadioButton radioButton;
    String valinta1, valinta2, valinta3, valinta4, valinta5;
    String suunnitelmaString;
    String selostus1, selostus2, selostus3, selostus4, selostus5;
    Integer selectedOptionID1, selectedOptionID2, selectedOptionID3, selectedOptionID4, selectedOptionID5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turvallisuuskavely_johdanto);


        final TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                for (int i = 0; i < host.getTabWidget().getChildCount(); i++) {
                    host.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.valilehti_nappula);
                }

                host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(Color.TRANSPARENT);
            }

        });


        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.Johdanto);
        spec.setIndicator("Johdanto");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.Suunnitelma);
        spec.setIndicator("Suunnitelma");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.Toteutus);
        spec.setIndicator("Toteutus");
        host.addTab(spec);

        for (int i = 0; i < host.getTabWidget().getChildCount(); i++) {
            host.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.valilehti_nappula);
        }

        host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(Color.TRANSPARENT);

        ImageButton lahetaNappula = (ImageButton) findViewById(R.id.lahetaNappula);
        final ProgressDialog progressDialog = new ProgressDialog(Turvallisuuskavely_johdantoActivity.this,
                R.style.AppTheme_Dark_Dialog);
        lahetaNappula.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    keraaTiedot();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());


    }

    // kun painetaan kameranappia
    public void onButtonClick(View view) {
        switch(view.getId()) {
            case R.id.kameraButton1:
                kameraButtonPressed = 1;
                break;
            case R.id.kameraButton2:
                kameraButtonPressed = 2;
                break;
            case R.id.kameraButton3:
                kameraButtonPressed = 3;
                break;
            case R.id.kameraButton4:
                kameraButtonPressed = 4;
                break;
            case R.id.kameraButton5:
                kameraButtonPressed = 5;
                break;
        }

        Intent kameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Turvallisuuskavely_johdantoActivity.this.getExternalCacheDir(),
                String.valueOf(System.currentTimeMillis() + ".jpg"));
        fileUri = Uri.fromFile(file);
        kameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        Turvallisuuskavely_johdantoActivity.this.startActivityForResult(kameraIntent, RC_TAKE_PHOTO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Turvallisuuskavely_johdantoActivity.super.onActivityResult(requestCode, resultCode, data);
        ImageButton kameraButton1 = (ImageButton) findViewById(R.id.kameraButton1);
        ImageButton kameraButton2 = (ImageButton) findViewById(R.id.kameraButton2);
        ImageButton kameraButton3 = (ImageButton) findViewById(R.id.kameraButton3);
        ImageButton kameraButton4 = (ImageButton) findViewById(R.id.kameraButton4);
        ImageButton kameraButton5 = (ImageButton) findViewById(R.id.kameraButton5);

        if (requestCode == RC_TAKE_PHOTO && resultCode == RESULT_OK) {

            File stringUri = new File(file.toString());
            Context context = getApplicationContext();
            if (kameraButtonPressed == 1) {
                // vaihetaan nappulan taustakuva
                kuva1 = stringUri;
                kameraButton1.setBackground(ContextCompat.getDrawable(context, R.drawable.red_face_pressed)
                );
                kameraButton1.setEnabled(false);
            } else if (kameraButtonPressed == 2) {
                kuva2 = stringUri;
                kameraButton2.setBackground(ContextCompat.getDrawable(context, R.drawable.red_face_pressed)
                );
                kameraButton2.setEnabled(false);
            } else if (kameraButtonPressed == 3) {
                kuva3 = stringUri;
                kameraButton3.setBackground(ContextCompat.getDrawable(context, R.drawable.red_face_pressed)
                );
                kameraButton3.setEnabled(false);
            } else if (kameraButtonPressed == 4) {
                kuva4 = stringUri;
                kameraButton4.setBackground(ContextCompat.getDrawable(context, R.drawable.red_face_pressed)
                );
                kameraButton4.setEnabled(false);
            } else if (kameraButtonPressed == 5) {
                kuva5 = stringUri;
                kameraButton5.setBackground(ContextCompat.getDrawable(context, R.drawable.red_face_pressed)
                );
                kameraButton5.setEnabled(false);
            }
            kameraButtonPressed = 0;

        }

    }

    private void errorLuokka() {
        Toast.makeText(getApplicationContext(), "Vastaa kaikkiin kohtiin", Toast.LENGTH_LONG).show();
    }

    private int haeRadioVastaus(int kysymysnumero, String valinta){
        final String ok = "Kaikki ok";
        final String puutteita ="Vaarallinen tai selkeitä puuteitaValinta";
        final String korjattavaa ="Korjattavaa löytyy";
        int laskuNumero = 0;

        Integer laskettuVastaus =0;
        // Laskee radiobutton id. Vaihtoehtoinen tapa on "valintanumero + (kysymysnumero -1) * 3"
        // Molemmat vaativat kysymysnumeron lähtevän 1.
        if(valinta.equals(ok)){
            laskuNumero = -2;
            laskettuVastaus = kysymysnumero*3-laskuNumero;

        }
        if(valinta.equals(puutteita)){
            laskuNumero = -1;
            laskettuVastaus = kysymysnumero*3-laskuNumero;

        }
        if(valinta.equals(korjattavaa)){
            laskuNumero = 0;
            laskettuVastaus = kysymysnumero*3-laskuNumero;

        }

        return laskettuVastaus;
    };

    private void keraaTiedot() throws JsonProcessingException {
        EditText selostus;
        RadioGroup radioGroup1 = (RadioGroup)findViewById(R.id.radio1);
        RadioGroup radioGroup2 = (RadioGroup)findViewById(R.id.radio2);
        RadioGroup radioGroup3 = (RadioGroup)findViewById(R.id.radio3);
        RadioGroup radioGroup4 = (RadioGroup)findViewById(R.id.radio4);
        RadioGroup radioGroup5 = (RadioGroup)findViewById(R.id.radio5);

        if (radioGroup1.getCheckedRadioButtonId() == -1 ) {
            errorLuokka();
        } else {
            selectedId = radioGroup1.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);
            valinta1 = radioButton.getText().toString();

            selectedOptionID1 = haeRadioVastaus(1, valinta1);

        }

        if (radioGroup2.getCheckedRadioButtonId() == -1 ) {
            errorLuokka();
        } else {
            selectedId = radioGroup2.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);
            valinta2 = radioButton.getText().toString();

            selectedOptionID2 = haeRadioVastaus(2, valinta2);
        }

        if (radioGroup3.getCheckedRadioButtonId() == -1 ) {
            errorLuokka();
        } else {
        selectedId = radioGroup3.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
            valinta3 = radioButton.getText().toString();
            selectedOptionID3 = haeRadioVastaus(3, valinta3);
        }

        if (radioGroup4.getCheckedRadioButtonId() == -1 ) {
            errorLuokka();
        } else {
        selectedId = radioGroup4.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
            valinta4 = radioButton.getText().toString();
            selectedOptionID4 = haeRadioVastaus(4, valinta4);
        }

        if (radioGroup5.getCheckedRadioButtonId() == -1 ) {
            errorLuokka();
        } else {
        selectedId = radioGroup5.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
            valinta5 = radioButton.getText().toString();
            selectedOptionID5 = haeRadioVastaus(5, valinta5);
        }

        EditText suunnitelma = (EditText) findViewById(R.id.suunnitelmaKentta);
        suunnitelmaString = suunnitelma.getText().toString();

        selostus = (EditText) findViewById(R.id.selostusKentta1);
        selostus1 = selostus.getText().toString();

        selostus = (EditText) findViewById(R.id.selostusKentta2);
        selostus2 = selostus.getText().toString();

        selostus = (EditText) findViewById(R.id.selostusKentta3);
        selostus3 = selostus.getText().toString();

        selostus = (EditText) findViewById(R.id.selostusKentta4);
        selostus4 = selostus.getText().toString();

        selostus = (EditText) findViewById(R.id.selostusKentta5);
        selostus5 = selostus.getText().toString();

        Log.e("Passi", "Valinta1 = " + valinta1
        + "Valinta2 = " + valinta2
        + "Valinta3 = " + valinta3
        + "Valinta4 = " + valinta4
        + "Valinta5 = " + valinta5
        + "Selostus1 = " + selostus1
        + "Selostus2 = " + selostus2
        + "Selostus3 = " + selostus3
        + "Selostus4 = "  + selostus4
        + "Selostus5 = " + selostus5
        + "Suunnitelma =  " + suunnitelmaString
        + "kuva1" + kuva1
        + "kuva2 " + kuva2
                + "kuva1" + kuva3
                + "kuva1" + kuva4
                + "kuva1" + kuva5);

        startUpload();

    }
    private void startUpload() {

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(Turvallisuuskavely_johdantoActivity.this);
        Intent untent = new Intent(Turvallisuuskavely_johdantoActivity.this, ValikkoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                untent, 0);
        mBuilder.setContentTitle("Vastaus")
                .setContentText("Tallennetaan...")
                .setSmallIcon(R.drawable.ic_cloud_upload_white_24dp)
                .setContentIntent(pendingIntent);

        new Turvallisuuskavely_johdantoActivity.PoisaVastaus().execute("1");
          }
    private class PoisaVastaus extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... path) {
            Integer paluukoodi = 1;
            try {
                SharedPreferences mySharedPreferences = getSharedPreferences("konfiguraatio", Context.MODE_PRIVATE);
                String username = mySharedPreferences.getString("tunnus", "");
                String base = mySharedPreferences.getString("token", "");
                String basicAuth = "Basic " + base;

                // http://proto384.haaga-helia.fi/passi-rest/answer/ { worksheetID } / { username }
                String urlPoistoTunnuksella = "http://proto384.haaga-helia.fi/passi-rest/answer/1/"+username;
                URL urli = new URL(urlPoistoTunnuksella);

                HttpURLConnection clientti = (HttpURLConnection) urli.openConnection();
                clientti.setRequestMethod("DELETE");
                clientti.setRequestProperty("Authorization", basicAuth);
                clientti.setRequestProperty("Content-Type", "application/json");

                paluukoodi = clientti.getResponseCode();
                String paluukoodiString = Integer.toString(clientti.getResponseCode());

                Log.e("Paluukoodi poisto: ", paluukoodiString);
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return paluukoodi;
        }

        @Override
        protected void onPostExecute (Integer result){
            Log.d("Passi", "Poisto " + result);

            super.onPostExecute(result);


            if (result == 204) {
                new Turvallisuuskavely_johdantoActivity.UploadImage().execute("1");

            }else if(result == 404 ){
                new Turvallisuuskavely_johdantoActivity.UploadImage().execute("1");
            }
            else if (result == 417){
                new Turvallisuuskavely_johdantoActivity.UploadImage().execute("1");
            }
            else {
                new Turvallisuuskavely_johdantoActivity.UploadImage().execute("1");
            }
        }
    }

    private class UploadImage extends AsyncTask<String, Integer, Integer> {
        final ProgressDialog progressDialog = new ProgressDialog(Turvallisuuskavely_johdantoActivity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Displays the progress bar for the first time.
            mBuilder.setProgress(100, 0, false);
            mNotifyManager.notify(id, mBuilder.build());
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Vastauksen lähetys");
            progressDialog.setMessage("Lähetetään...");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Update progress
            mBuilder.setProgress(100, values[0], false);
            mNotifyManager.notify(id, mBuilder.build());
            super.onProgressUpdate(values);
        }

        @Override
        protected Integer doInBackground(String... path) {

            String resultVanha = path[0];

            Integer paluukoodi = 1;

            String url = " http://proto384.haaga-helia.fi/passi-rest/answer/";

            SharedPreferences mySharedPreferences = getSharedPreferences("konfiguraatio", Context.MODE_PRIVATE);

            String username = mySharedPreferences.getString("tunnus", "");



            OkHttpClient client = new OkHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            List<Etappi> etappiLista = new ArrayList<>();
            Etappi etappi = new Etappi();

            Vastaus vastaus = new Vastaus();

            etappi.setAnsweWaypointID(0);
            etappi.setAnswerID(0);
            etappi.setWaypointID(1);
            etappi.setSelectedOptionID(selectedOptionID1);
            etappi.setImageURL("ukko");
            etappi.setAnswerText(selostus1);
            etappi.setInstructorComment("moi");

            etappiLista.add(etappi);

            etappi = new Etappi();

            etappi.setAnsweWaypointID(0);
            etappi.setAnswerID(0);
            etappi.setWaypointID(2);
            etappi.setSelectedOptionID(selectedOptionID2);
            etappi.setImageURL("ukko");
            etappi.setAnswerText(selostus2);
            etappi.setInstructorComment("moi");

            etappiLista.add(etappi);

            etappi = new Etappi();

            etappi.setAnsweWaypointID(0);
            etappi.setAnswerID(0);
            etappi.setWaypointID(3);
            etappi.setSelectedOptionID(selectedOptionID3);
            etappi.setImageURL("ukko");
            etappi.setAnswerText(selostus3);
            etappi.setInstructorComment("moi");

            etappiLista.add(etappi);

            etappi = new Etappi();

            etappi.setAnsweWaypointID(0);
            etappi.setAnswerID(0);
            etappi.setWaypointID(4);
            etappi.setSelectedOptionID(selectedOptionID4);
            etappi.setImageURL("ukko");
            etappi.setAnswerText(selostus4);
            etappi.setInstructorComment("moi");

            etappiLista.add(etappi);

            etappi = new Etappi();

            etappi.setAnsweWaypointID(0);
            etappi.setAnswerID(0);
            etappi.setWaypointID(5);
            etappi.setSelectedOptionID(selectedOptionID5);
            etappi.setImageURL("ukko");
            etappi.setAnswerText(selostus5);
            etappi.setInstructorComment("moi");

            etappiLista.add(etappi);

            vastaus.setAnswerID(0);
            vastaus.setWorksheetID(1);
            vastaus.setUsername(username);
            vastaus.setPlanningText(suunnitelmaString);
            vastaus.setWaypoints(etappiLista);
            vastaus.setInstructorComment("moi");

            String JSONjeesus = "";

            try {
                JSONjeesus = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vastaus);
                Log.d("Passi", JSONjeesus);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            String pohja;
            String urlIlmantunnusta = "http://proto384.haaga-helia.fi/passi-rest/answer/";
            String base = mySharedPreferences.getString("token", "");
            String basicAuth = "Basic " + base;
            Integer rv = Integer.parseInt(resultVanha);
            Log.e("Tässä on: ", resultVanha);


            try {
                URL urli = new URL(urlIlmantunnusta);

                HttpURLConnection clientti = (HttpURLConnection) urli.openConnection();
                clientti.setRequestMethod("POST");
                clientti.setRequestProperty("Authorization", basicAuth);
                clientti.setRequestProperty("Content-Type", "application/json");
                clientti.setDoOutput(true);
                clientti.setDoInput(true);


                OutputStreamWriter wr = new OutputStreamWriter(clientti.getOutputStream());
                if (JSONjeesus.length() > 1){
                    wr.write(JSONjeesus);
                }
                wr.flush();
                wr.close();

                paluukoodi = clientti.getResponseCode();
                String paluukoodiString = Integer.toString(clientti.getResponseCode());

                Log.e("Paluukoodi lähety: ", paluukoodiString);

                if (client != null) {
                    clientti.disconnect();
                }
/*
            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM).addFormDataPart("file", "image.png",
                            RequestBody.create(MediaType.parse("image/png"), kuva1))
                    .build();

            Request request = new Request.Builder().url(url).post(formBody).build();


            Response response = null;

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {

                e.printStackTrace();
            }

            int kahvi = 0;

            Log.d("Passi", "Jee " + response);

            if (response != null) {
                if (response.isSuccessful()) {
                    kahvi = 1;
                }
            }

            response.close();
            for (i = 0; i <= 100; i += 5) {
                // Sets the progress indicator completion percentage
                publishProgress(Math.min(i, 100));

            }*/

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return paluukoodi;
        }


            @Override
            protected void onPostExecute (Integer result){
                Log.d("Passi", "Jee jöö " + result);

                super.onPostExecute(result);

                progressDialog.dismiss();
                if (result == 201) {
                    mBuilder.setContentText("Vastaus tallennettu");
                    Toast.makeText(getApplicationContext(), "Vastaus tallennettu!", Toast.LENGTH_LONG);

                }else if(result == 409){
                    //new Turvallisuuskavely_johdantoActivity.UploadImage().execute(new String[]{Integer.toString(result)});
                }
                else {
                    mBuilder.setContentText("Tallennus epäonnistui");
                    Toast.makeText(getApplicationContext(), "Tallennus epäonnistui!", Toast.LENGTH_LONG);
                }
                Intent intent = new Intent(Turvallisuuskavely_johdantoActivity.this, ValikkoActivity.class);
                startActivity(intent);
                // Removes the progress bar
                mBuilder.setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
            }
        }


    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }


    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(Turvallisuuskavely_johdantoActivity.this, TehtavakortinValintaActivity.class);
        startActivity(intent);

    }


}
