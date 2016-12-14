package com.example.jonathas.computgraf;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jonathas on 02/12/2016.
 */

public class RecuperaDados implements Serializable{

    ArrayList<Float> vertices = new ArrayList<>();
    ArrayList<Integer> triangles = new ArrayList<>();
    ArrayList<Float> normals = new ArrayList<>();
    ArrayList<Float> colors = new ArrayList<>();

    public RecuperaDados(){

    }

    protected RecuperaDados(Parcel in) {
    }



    public ArrayList<Float> getVertices(JSONObject jsonObject2) throws JSONException {

        JSONArray jsonArray;
        jsonArray = (jsonObject2.getJSONArray("vertices"));

        for (int i = 0; jsonArray.length() > i; i++) {
            JSONObject item = jsonArray.getJSONObject(i);

            Float x = Float.parseFloat(item.getString("x"));
            Float y = Float.parseFloat(item.getString("y"));
            Float z = Float.parseFloat(item.getString("z"));

            this.vertices.add(x);
            this.vertices.add(y);
            this.vertices.add(z);
        }
        return this.vertices;
    }

    public ArrayList<Integer> getTriangles(JSONObject jsonObject2) throws JSONException {

        JSONArray jsonArray;
        jsonArray = (jsonObject2.getJSONArray("triangles"));

        for (int i = 0; jsonArray.length() > i; i++) {
            JSONObject item = jsonArray.getJSONObject(i);

            Integer v0 = Integer.parseInt(item.getString("v0"));
            Integer v1 = Integer.parseInt(item.getString("v1"));
            Integer v2 = Integer.parseInt(item.getString("v2"));

            triangles.add(v0);
            triangles.add(v1);
            triangles.add(v2);
        }
        return triangles;
    }

    public ArrayList<Float> getNormals(JSONObject jsonObject2) throws JSONException {

        JSONArray jsonArray;
        jsonArray = (jsonObject2.getJSONArray("normals"));

        for (int i = 0; jsonArray.length() > i; i++) {
            JSONObject item = jsonArray.getJSONObject(i);

            Float x = Float.parseFloat(item.getString("x"));
            Float y = Float.parseFloat(item.getString("y"));
            Float z = Float.parseFloat(item.getString("z"));

            normals.add(x);
            normals.add(y);
            normals.add(z);
        }
        return normals;
    }


    public ArrayList<Float> getColors(JSONObject jsonObject2) throws JSONException {

        JSONArray jsonArray;
        jsonArray = (jsonObject2.getJSONArray("colors"));

        for (int i = 0; jsonArray.length() > i; i++) {
            JSONObject item = jsonArray.getJSONObject(i);

           //NÃO SEI COMO VEM UMA COR! ver e mudar..
          //  Float v0 = Float.parseFloat(item.getString("v0"));
          //  Float v1 = Float.parseFloat(item.getString("v1"));
          //  Float v2 = Float.parseFloat(item.getString("v2"));

          //  triangles.add(v0);
          //  triangles.add(v1);
          //  vertices.add(v2);
        }
        return colors;
    }


}
