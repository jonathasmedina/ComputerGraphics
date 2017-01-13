package com.example.jonathas.computgraf;


import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Scanner;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    List<Cena> dadosCena;
    JSONObject jsonObject2;
    Cena cena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //recebe o intent e seu conteúdo
        Intent intent = getIntent();
        String url = (String) intent.getSerializableExtra("url");

        super.onCreate(savedInstanceState);

        //instancia a classe e executa o método de conexão na cena escolhida na lista (url da cena recebida via intent)
        new DownloadJsonAsyncTask()
                    .execute(url);
    }



    public class DownloadJsonAsyncTask extends AsyncTask<String, Void, List<Cena>> {
        ProgressDialog dialog;
        //Exibe pop-up indicando que está sendo feito o download do JSON
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MainActivity.this, "Aguarde",
                    "Fazendo download do JSON");
        }

        //Acessa o serviço do JSON e retorna a lista de objetos
        @Override
        protected List<Cena> doInBackground(String... params) {
            String urlString = params[0];
            URL url;
            try {
                 url = new URL(urlString);
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(15000000);
                httpURLConnection.connect();

                InputStream response = httpURLConnection.getInputStream();
                String text = new Scanner(response).useDelimiter("\\A").next();

                if (text != null) {
                    dadosCena = getCenaData(text); //método principal, converte a string em obj
                    return dadosCena;
                }
                else
                    return null;
            } catch (Exception e) {
                Log.e("Erro", "Falha ao acessar Web service", e);
                return null;
            }
        }

        //Depois de executada a chamada do serviço
        @Override
        protected void onPostExecute(List<Cena> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.size() > 0) { //não faz nada
                //inicia a próxima activity
                //bundle - objeto encapsulado
                final Bundle bundle = new Bundle();
                bundle.putBinder("cena", new ObjectWrapperForBinder(cena));
                startActivity(new Intent(getApplicationContext(), ActOpenGLES.class).putExtras(bundle));
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        MainActivity.this)
                        .setTitle("Erro")
                        .setMessage("Não foi possível acessar as informações!!")
                        .setPositiveButton("OK", null);
                builder.create().show();
            }
        }

        //Retorna uma lista de objetos com as informações retornadas do JSON
        private List<Cena> getCenaData(String text) { //string com  o resultado da conexão

            // esta string text é um OBJETO json (e não um ARRAY), pois começa com {}. deve ser jogada em um
            //objeto json, e não em um array json; isso em http://192.168.15.4/API/id/2 !!
            //http://192.168.15.4/API/id/2 retorna objeto {}
            //http://192.168.15.4/API/label/2 retorna um value []
            //no caso de value, usar jsonArray pois em json:
            //array é composto de values - ex Array [ value1, value2 ]
            //objeto é composto de string:value -ex Obj { string:value, string2:value2 }

            List<Cena> objJsonList = new ArrayList<>(); //objetos - array do tipo da classe pojo Cena

            try {
                jsonObject2 = new JSONObject(text); //string em JSONObject
                RecuperaDados recuperaDados = new RecuperaDados(); //classe que auxilia na recuperação dos dados
                cena = new Cena();

                //informações gerais da cena
                cena.setLabel(jsonObject2.getString("label"));
                cena.setId(jsonObject2.getString("id"));
                cena.setDescription(jsonObject2.getString("description"));

                //recuperando dados da camera
                JSONObject jsonObjectCam = jsonObject2.getJSONObject("camera");
                ObjCamera objCamera = new ObjCamera();
                objCamera.setAngle_view(jsonObjectCam.getInt("angle_view"));
                objCamera.setDop(recuperaDados.getDop(jsonObjectCam));
                objCamera.setPosition(recuperaDados.getPositionCam(jsonObjectCam));
                objCamera.setVup(recuperaDados.getVup(jsonObjectCam));
                cena.setObjCamera(objCamera);//setando o atributo objCamera da cena;

                //recuperando dados da iluminação
                JSONObject jsonObjectLight = jsonObject2.getJSONObject("light");
                ObjLight objLight = new ObjLight();
                objLight.setPosition(recuperaDados.getPositionLight(jsonObjectLight));
                objLight.setColor(recuperaDados.getColorLight(jsonObjectLight));
                cena.setObjLight(objLight);//setando o atributo objLight da cena;

                //recuperando dados do ator
                JSONObject jsonObjectActor = jsonObject2.getJSONObject("actor");
                ObjActor objActor = new ObjActor();
                objActor.setNumberOfVertices(Integer.parseInt(jsonObjectActor.getString("numberOfVertices")));
                objActor.setNumberOfNormals(Integer.parseInt(jsonObjectActor.getString("numberOfNormals")));
                objActor.setNumberOfTriangles(Integer.parseInt(jsonObjectActor.getString("numberOfTriangles")));
                objActor.setNumberOfColors(Integer.parseInt(jsonObjectActor.getString("numberOfColors")));
                objActor.setNumberOfTextures(Integer.parseInt(jsonObjectActor.getString("numberOfTextures")));

                //recupera vértices (está em formato array de objetos: [...{...},{...},{...}]
                objActor.setVertices(recuperaDados.getVertices(jsonObjectActor));
                //triangulo com todos os dados;
//                objActor.setTriangles(recuperaDados.getTriangles(jsonObjectActor));
                //triangulo somente com vo,v1,v2(v);
                objActor.setTrianglesV(recuperaDados.getTrianglesV(jsonObjectActor));
                //triangulo somente com vn's;
                objActor.setTrianglesVN(recuperaDados.getTrianglesVN(jsonObjectActor));
                //triangulo somente com vt's
//                objActor.setTrianglesVT(recuperaDados.getTrianglesVT(jsonObjectActor));

                objActor.setNormals(recuperaDados.getNormals(jsonObjectActor));
                objActor.setColors(recuperaDados.getColors(jsonObjectActor));
                objActor.setMaterial(recuperaDados.getMaterial(jsonObjectActor));
                objActor.setTextures(recuperaDados.getTextures(jsonObjectActor));

                cena.setObjActor(objActor);

                objJsonList.add(cena);

                //download do arquivo de textura
//                downloadTextura.downloadTextura(objActor.getArquivoTextura() ou url);
                //validar se tem a informação antes de chamar o método...
                /*if (url != null) {
                * }*/
                downloadTextura();
                SystemClock.sleep(5000);

            } catch (JSONException e) {
                Log.e("Erro", "Erro no parsing do JSON", e);
            }

            return objJsonList;
        }

    }

    public void downloadTextura(){
            isStoragePermissionGranted();
            String url = "https://thumbs.dreamstime.com/z/textura-da-pele-do-gato-21777149.jpg";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Some descrition");
            request.setTitle("textura12.jpg");

            // in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "textura12.jpg");

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

}