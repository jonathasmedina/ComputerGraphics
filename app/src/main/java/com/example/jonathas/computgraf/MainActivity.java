package com.example.jonathas.computgraf;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jonathas.computgraf.R;

import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends ListActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<ObjJson> objJsons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //System.setProperty("http.proxyHost", "http://graphics.models.com/");

        super.onCreate(savedInstanceState);
        new DownloadJsonAsyncTask()
                    //.execute("http://10.0.2.2:8000/API/id/1");
                    //.execute("http://10.3.1.157/API/id/2");
                    .execute("http://192.168.15.4/API/id/2");
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ObjJson objJson= (ObjJson) l.getAdapter().getItem(position);

        Intent intent = new Intent(this, InformacoesActivity.class);
        intent.putExtra("objJson", (Serializable) objJson);
        startActivity(intent);
    }


    public class DownloadJsonAsyncTask extends AsyncTask<String, Void, List<ObjJson>> {
        ProgressDialog dialog;
        String e2="";
        //Exibe pop-up indicando que está sendo feito o download do JSON
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MainActivity.this, "Aguarde",
                    "Fazendo download do JSON");
        }

        //Acessa o serviço do JSON e retorna a lista de objetos
        @Override
        protected List<ObjJson> doInBackground(String... params) {
            String urlString = params[0];
            URL url=null;
            try {
                 url = new URL(urlString);
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                //httpURLConnection.setConnectTimeout(15000000);
                httpURLConnection.setReadTimeout(15000000);
                httpURLConnection.connect();


                InputStream response = httpURLConnection.getInputStream();
                String text = new Scanner(response).useDelimiter("\\A").next();
               // System.out.println(text);


                if (text != null) {
                    objJsons = getObjSons(text);
                    return objJsons;
                }
                else
                    return null;
            } catch (Exception e) {
                Log.e("Erro", "Falha ao acessar Web service", e);
                return null;
            }

            //return null;
        }

        //Depois de executada a chamada do serviço
        @Override
        protected void onPostExecute(List<ObjJson> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.size() > 0) {
                ArrayAdapter<ObjJson> adapter = new ArrayAdapter<ObjJson>(
                        MainActivity.this,
                        android.R.layout.simple_list_item_1, result);
                setListAdapter(adapter);
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
        private List<ObjJson> getObjSons(String text) { //jsonString - string com todo o resultado

            // esta string text é um OBJETO json (e não um ARRAY), pois começa com {}. deve ser jogada em um
            //objeto json, e não em um array json; isso em http://192.168.15.4/API/id/2 !!
            //http://192.168.15.4/API/id/2 retorna objeto {}
            //http://192.168.15.4/API/label/2 retorna um value []
            //no caso de value, usar jsonArray pois em json:
            //array é composto de values - ex Array [ value1, value2 ]
            //objeto é composto de string:value -ex Obj { string:value, string2:value2 }

            List<ObjJson> objJsonList = new ArrayList<>(); //objetos - array do tipo da classe pojo ObjSon
            try {
                JSONObject jsonObject2= new JSONObject(text); //objJsons - jsonString convertido em Json
                JSONObject jsonObject; //objJson - objeto JSON - a cada instancia da string cria um
                int j = jsonObject2.length();


                //for (int i = 0; i < jsonObject2.length(); i++) { //para mais de um objeto
                //o for atualmente está percorrendo cada um dos campos do mesmo objeto
                //deve percorrer cada objeto por completo, e não seus campos, quando considerar tratar mais de um obj
                   // jsonObject = new JSONObject(jsonArray.getString(i));

                ObjJson objJson = new ObjJson(); //instancia da minha classe, setters

                objJson.setLabel(jsonObject2.getString("label"));
                objJson.setId(jsonObject2.getString("id"));
                objJson.setDescription(jsonObject2.getString("description"));
                objJson.setNumberOfVertices(Integer.parseInt(jsonObject2.getString("numberOfVertices")));
                objJson.setNumberOfNormals(Integer.parseInt(jsonObject2.getString("numberOfNormals")));
                objJson.setNumberOfTriangles(Integer.parseInt(jsonObject2.getString("numberOfTriangles")));
                objJson.setNumberOfColors(Integer.parseInt(jsonObject2.getString("numberOfColors")));

                JSONArray jsonArray = new JSONArray();
                jsonArray = (jsonObject2.getJSONArray("vertices"));
                //Map map;



               /* String c = (jsonObject2.getString("colors"));
                String[] cArray = new String[] {c};
                Float x[] = new Float[10];
                float y;
                y = Float.parseFloat(c);*/


//                objJson.setColors(x);

                objJsonList.add(objJson);//adiciona o objeto populado pojo na lista

               // }

            } catch (JSONException e) {
                Log.e("Erro", "Erro no parsing do JSON", e);
            }
            return objJsonList;
        }

    }

}