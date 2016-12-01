package com.example.jonathas.computgraf;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Jonathas on 21/11/2016.
 */

public class InformacoesActivity extends Activity {
    private TextView id;
    private TextView label;
    private TextView description;
    private ObjJson objJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        objJson = (ObjJson) getIntent().getSerializableExtra("objJson");

        id = (TextView) findViewById(R.id.id_value);
        label = (TextView) findViewById(R.id.texto_value);

        id.setText(objJson.getId());
        label.setText(objJson.getLabel());

    }

}


