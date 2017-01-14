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

    private String urlText;
    private String nomeTextura;

    ArrayList<Float> vertices = new ArrayList<>();
    ArrayList<Integer> triangles = new ArrayList<>();
    ArrayList<Integer> trianglesV = new ArrayList<>();
    ArrayList<Integer> trianglesVT = new ArrayList<>();
    ArrayList<Integer> trianglesVN = new ArrayList<>();
    ArrayList<Float> normals = new ArrayList<>();
    ArrayList<Float> colors = new ArrayList<>();
    ArrayList<Float> positionCam = new ArrayList<>();
    ArrayList<Float> positionLight = new ArrayList<>();
    ArrayList<Float> color = new ArrayList<>();
    ArrayList<Float> dop = new ArrayList<>();
    ArrayList<Float> vup = new ArrayList<>();
    private ArrayList<Float> material = new ArrayList<>();

    private ArrayList<Float> textures = new ArrayList<>();

    public RecuperaDados(){

    }

    public ArrayList<Float> getVertices(JSONObject jsonObject) throws JSONException {

        JSONArray jsonArray;
        jsonArray = (jsonObject.getJSONArray("vertices"));

        for (int i = 0; jsonArray.length() > i; i++) {
            JSONObject item = jsonArray.getJSONObject(i);

            Float x = Float.parseFloat(item.getString("x"));
            Float y = Float.parseFloat(item.getString("y"));
            Float z = Float.parseFloat(item.getString("z"));

            vertices.add(x);
            vertices.add(y);
            vertices.add(z);
        }
        return vertices;
    }


    public ArrayList<Integer> getTriangles(JSONObject jsonObject) throws JSONException {

        JSONArray jsonArray;
        jsonArray = (jsonObject.getJSONArray("triangles"));


        for (int i = 0; jsonArray.length() > i; i++) {
            JSONObject item = jsonArray.getJSONObject(i);

            Integer v0 = Integer.parseInt(item.getString("v0"));
            Integer v1 = Integer.parseInt(item.getString("v1"));
            Integer v2 = Integer.parseInt(item.getString("v2"));
            Integer vt0 = Integer.parseInt(item.getString("vt0"));
            Integer vt1 = Integer.parseInt(item.getString("vt1"));
            Integer vt2 = Integer.parseInt(item.getString("vt2"));
            Integer vn0 = Integer.parseInt(item.getString("vn0"));
            Integer vn1 = Integer.parseInt(item.getString("vn1"));
            Integer vn2 = Integer.parseInt(item.getString("vn2"));

            triangles.add(v0);
            triangles.add(v1);
            triangles.add(v2);
            triangles.add(vt0);
            triangles.add(vt1);
            triangles.add(vt2);
            triangles.add(vn0);
            triangles.add(vn1);
            triangles.add(vn2);
        }
        return triangles;
    }

    public ArrayList<Integer> getTrianglesV(JSONObject jsonObject) throws JSONException {

        JSONArray jsonArray;
        jsonArray = (jsonObject.getJSONArray("triangles"));


        for (int i = 0; jsonArray.length() > i; i++) {
            JSONObject item = jsonArray.getJSONObject(i);

            Integer v0 = Integer.parseInt(item.getString("v0"));
            Integer v1 = Integer.parseInt(item.getString("v1"));
            Integer v2 = Integer.parseInt(item.getString("v2"));

            trianglesV.add(v0);
            trianglesV.add(v1);
            trianglesV.add(v2);
        }
        return trianglesV;
    }

    public ArrayList<Integer> getTrianglesVN(JSONObject jsonObject) throws JSONException {

        JSONArray jsonArray;
        jsonArray = (jsonObject.getJSONArray("triangles"));


        for (int i = 0; jsonArray.length() > i; i++) {
            JSONObject item = jsonArray.getJSONObject(i);

            Integer vn0 = Integer.parseInt(item.getString("vn0"));
            Integer vn1 = Integer.parseInt(item.getString("vn1"));
            Integer vn2 = Integer.parseInt(item.getString("vn2"));

            trianglesVN.add(vn0);
            trianglesVN.add(vn1);
            trianglesVN.add(vn2);
        }
        return trianglesVN;
    }

    public ArrayList<Integer> getTrianglesVT(JSONObject jsonObject) throws JSONException {

        JSONArray jsonArray;
        jsonArray = (jsonObject.getJSONArray("triangles"));


        for (int i = 0; jsonArray.length() > i; i++) {
            JSONObject item = jsonArray.getJSONObject(i);

            Integer vt0 = Integer.parseInt(item.getString("vt0"));
            Integer vt1 = Integer.parseInt(item.getString("vt1"));
            Integer vt2 = Integer.parseInt(item.getString("vt2"));

            trianglesVT.add(vt0);
            trianglesVT.add(vt1);
            trianglesVT.add(vt2);
        }
        return trianglesVT;
    }

    public ArrayList<Float> getNormals(JSONObject jsonObject) throws JSONException {

        JSONArray jsonArray;
        jsonArray = (jsonObject.getJSONArray("normals"));

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

    public ArrayList<Float> getMaterial(JSONObject jsonObject2) throws JSONException {

        JSONObject jsonObject;
        jsonObject = jsonObject2.getJSONObject("material");
        JSONObject jsonObject_;

        String ka = jsonObject.getString("ka");
        if(ka != "null") {
            jsonObject_ = jsonObject.getJSONObject("ka");
            Float r_ka = Float.parseFloat(jsonObject_.getString("r"));
            Float g_ka = Float.parseFloat(jsonObject_.getString("g"));
            Float b_ka = Float.parseFloat(jsonObject_.getString("b"));
            Float a_ka = Float.parseFloat(jsonObject_.getString("a"));

            material.add(r_ka);
            material.add(g_ka);
            material.add(b_ka);
            material.add(a_ka);
        }else {
            material.add(0.0f);
            material.add(0.0f);
            material.add(0.0f);
            material.add(0.0f);
        }

        String kd = jsonObject.getString("kd");
        if (kd != "null") {
            jsonObject_ = jsonObject.getJSONObject("kd");
            Float r_kd = Float.parseFloat(jsonObject_.getString("r"));
            Float g_kd = Float.parseFloat(jsonObject_.getString("g"));
            Float b_kd = Float.parseFloat(jsonObject_.getString("b"));
            Float a_kd = Float.parseFloat(jsonObject_.getString("a"));

            material.add(r_kd);
            material.add(g_kd);
            material.add(b_kd);
            material.add(a_kd);
        } else {
            material.add(0.0f);
            material.add(0.0f);
            material.add(0.0f);
            material.add(0.0f);
        }

        String ks = jsonObject.getString("ks");
        if(ks != "null") {
            jsonObject_ = jsonObject.getJSONObject("ks");
            Float r_ks = Float.parseFloat(jsonObject_.getString("r"));
            Float g_ks = Float.parseFloat(jsonObject_.getString("g"));
            Float b_ks = Float.parseFloat(jsonObject_.getString("b"));
            Float a_ks = Float.parseFloat(jsonObject_.getString("a"));

            material.add(r_ks);
            material.add(g_ks);
            material.add(b_ks);
            material.add(a_ks);
        }else {
            material.add(0.0f);
            material.add(0.0f);
            material.add(0.0f);
            material.add(0.0f);
        }

        Float ns = Float.parseFloat(jsonObject.getString("ns"));
        Float tr = Float.parseFloat(jsonObject.getString("tr"));

        if (ns!=null)
            material.add(ns);
        else {
            material.add(0.0f);
        }
        if (tr!=null)
            material.add(tr);
        else {
            material.add(0.0f);
        }

        return material;
    }

    public ArrayList<Float> getTextures(JSONObject jsonObject) throws JSONException {

        JSONArray jsonArray;
        jsonArray = (jsonObject.getJSONArray("textures"));

        for (int i = 0; jsonArray.length() > i; i++) {
            JSONObject item = jsonArray.getJSONObject(i);

            Float x = Float.parseFloat(item.getString("x"));
            Float y = Float.parseFloat(item.getString("y"));
            Float z = Float.parseFloat(item.getString("z"));

            textures.add(x);
            textures.add(y);
            textures.add(z);
        }
        return textures;
    }


    public ArrayList<Float> getColors(JSONObject jsonObject) throws JSONException {

        JSONArray jsonArray;
        jsonArray = (jsonObject.getJSONArray("colors"));

        for (int i = 0; jsonArray.length() > i; i++) {
            JSONObject item = jsonArray.getJSONObject(i);

            Float r = Float.parseFloat(item.getString("r"));
            Float g = Float.parseFloat(item.getString("g"));
            Float b = Float.parseFloat(item.getString("b"));
            Float a = Float.parseFloat(item.getString("a"));

            colors.add(r);
            colors.add(g);
            colors.add(b);
            colors.add(a);
        }

        return colors;
    }

    public ArrayList<Float> getPositionCam(JSONObject arr) throws JSONException {

        JSONObject jsonObject;
        jsonObject = arr.getJSONObject("position");

        Float x = Float.parseFloat(jsonObject.getString("x"));
        Float y = Float.parseFloat(jsonObject.getString("y"));
        Float z = Float.parseFloat(jsonObject.getString("z"));

        positionCam.add(x);
        positionCam.add(y);
        positionCam.add(z);
        return positionCam;
    }

    public ArrayList<Float> getDop(JSONObject arr) throws JSONException {

        JSONObject jsonObject;
        jsonObject = arr.getJSONObject("dop");

        Float x = Float.parseFloat(jsonObject.getString("x"));
        Float y = Float.parseFloat(jsonObject.getString("y"));
        Float z = Float.parseFloat(jsonObject.getString("z"));

        dop.add(x);
        dop.add(y);
        dop.add(z);
        return dop;
    }

    public ArrayList<Float> getVup(JSONObject arr) throws JSONException {

        JSONObject jsonObject;
        jsonObject = arr.getJSONObject("vup");

        Float x = Float.parseFloat(jsonObject.getString("x"));
        Float y = Float.parseFloat(jsonObject.getString("y"));
        Float z = Float.parseFloat(jsonObject.getString("z"));

        vup.add(x);
        vup.add(y);
        vup.add(z);
        return vup;
    }

    public ArrayList<Float> getPositionLight(JSONObject arr) throws JSONException {

        JSONObject jsonObject;
        jsonObject = arr.getJSONObject("position");

        Float x = Float.parseFloat(jsonObject.getString("x"));
        Float y = Float.parseFloat(jsonObject.getString("y"));
        Float z = Float.parseFloat(jsonObject.getString("z"));

        positionLight.add(x);
        positionLight.add(y);
        positionLight.add(z);
        return positionLight;
    }

    public ArrayList<Float> getColorLight(JSONObject arr) throws JSONException {

        JSONObject jsonObject;
        jsonObject = arr.getJSONObject("color");

        Float r = Float.parseFloat(jsonObject.getString("r"));
        Float g = Float.parseFloat(jsonObject.getString("g"));
        Float b = Float.parseFloat(jsonObject.getString("b"));
        Float a = Float.parseFloat(jsonObject.getString("a"));

        color.add(r);
        color.add(g);
        color.add(b);
        color.add(a);
        return color;
    }

    public String getNomeTextura(JSONObject jsonObject2) throws JSONException {
        JSONObject jsonObject;
        jsonObject = jsonObject2.getJSONObject("material");

        String nomeTextura = jsonObject.getString("map_kd");

        String[] valorComSplit = nomeTextura.split("/");

        for(String s : valorComSplit){
            nomeTextura = s;
        }
        return nomeTextura;
    }

    public String getUrlText(JSONObject jsonObject2) throws JSONException {
        JSONObject jsonObject;
        jsonObject = jsonObject2.getJSONObject("material");

        String url = jsonObject.getString("map_kd");
        return url;
    }
}
