package com.example.jonathas.computgraf;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Jonathas on 27/12/2016.
 */

public class MainActivityLista extends ListActivity {

    private static final String[] CORES = new String[] {"Verde","Azul","Amarelo","Branco"};
    private ArrayAdapter<String> meuAdaptador;
    private ArrayList<ListaCenas> listaCenas;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DownloadJsonAsyncTask().execute("http://192.168.15.2/API/");

//        setContentView(R.layout.content_main);

//        meuAdaptador  = new ArrayAdapter(this,android.R.layout.simple_list_item_1, CORES);
//         setListAdapter(meuAdaptador);

        ListView lv = getListView();
        registerForContextMenu(lv);

    }

    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        Toast.makeText(this, "Usuário selecionou a cor " + CORES[position], Toast.LENGTH_SHORT).show();

        url = listaCenas.get(position).getUrl();

        Intent intent = new Intent(MainActivityLista.this, MainActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }



    //cria o menu de contexto - clique longo
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("-Visualizar infos-");
        menu.add(0, v.getId(), 0, "Dados da Câmera");
        menu.add(0, v.getId(), 0, "Dados da iluminação");
        menu.add(0, v.getId(), 0, "Dados do Ator");
    }

    //trata as opções do menu de contexto
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.opcao1:

                return true;
            case R.id.opcao2:
                Toast.makeText(this, "Opção 2 - "
                        , Toast.LENGTH_SHORT).show();
                return true;
            case R.id.opcao3:
                Toast.makeText(this, "Opção 3 - "
                        , Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public class DownloadJsonAsyncTask extends AsyncTask<String, Void, ArrayList<ListaCenas>> {
        ProgressDialog dialog;

        //Exibe pop-up indicando que está sendo feito o download do JSON
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MainActivityLista.this, "Aguarde",
                    "Fazendo download das Cenas");
        }

        //Acessa o serviço do JSON e retorna a lista de cenas
        @Override
        protected ArrayList<ListaCenas> doInBackground(String... params) {
            String urlString = params[0];
            URL url = null;
            try {
                url = new URL(urlString);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                //httpURLConnection.setConnectTimeout(15000000);
                httpURLConnection.setReadTimeout(15000000);
                httpURLConnection.connect();

                InputStream response = httpURLConnection.getInputStream();
                String text = new Scanner(response).useDelimiter("\\A").next();

                if (text != null) {
                    listaCenas = getCenas(text); //método principal, converte a string em obj
                    return listaCenas;
                } else
                    return null;
            } catch (Exception e) {
                Log.e("Erro", "Falha ao acessar Web service", e);
                return null;
            }

            //return null;
        }

        //Depois de executada a chamada do serviço
        @Override
        protected void onPostExecute(ArrayList<ListaCenas> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.size() > 0) {
                ArrayAdapter<ListaCenas> adapter = new ArrayAdapter<>(
                      MainActivityLista.this,
                    R.layout.activity_menu, result);
                 setListAdapter(adapter);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        MainActivityLista.this)
                        .setTitle("Erro")
                        .setMessage("Não foi possível acessar as informações!!")
                        .setPositiveButton("OK", null);
                builder.create().show();
            }
        }

        //Retorna uma lista de objetos com as informações retornadas do JSON
        private ArrayList<ListaCenas> getCenas(String text) { //jsonString - string com todo o resultado

            ArrayList<ListaCenas> listaCenas = new ArrayList<>(); //objetos - array do tipo da classe pojo Cena


            try {
                JSONArray jsonArray = new JSONArray(text);

                for (int i = 0; jsonArray.length() > i; i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    ListaCenas cenas = new ListaCenas();

                    cenas.setLabel(item.getString("label"));
                    cenas.setId(item.getString("id"));
                    cenas.setDescription(item.getString("description"));
                    cenas.setUrl("http://192.168.15.2/API/id/"+item.getString("id"));
                    //alterar - ip onde o serviço está hospedado

                    listaCenas.add(cenas);
                }

//                final Bundle bundle = new Bundle();
//                bundle.putBinder("cena", new ObjectWrapperForBinder(cena));
//                startActivity(new Intent(getApplicationContext(), ActOpenGLES.class).putExtras(bundle));

            } catch (JSONException e) {
                Log.e("Erro", "Erro no parsing do JSON", e);
            }

            return listaCenas;
        }

    }

    public void goIntent(){

    }


}
