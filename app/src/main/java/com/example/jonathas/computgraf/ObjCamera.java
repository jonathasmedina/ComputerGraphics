package com.example.jonathas.computgraf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jonathas on 19/12/2016.
 */

public class ObjCamera implements Serializable{
    private ArrayList<Float> position = new ArrayList<>();
    private ArrayList<Float> dop = new ArrayList<>();
    private ArrayList<Float> vup = new ArrayList<>();
    private float angle_view;

    public float getAngle_view() {
        return angle_view;
    }

    public void setAngle_view(float angle_view) {
        this.angle_view = angle_view;
    }

    public ArrayList<Float> getPosition(JSONObject arr) throws JSONException {

        JSONObject jsonObject;
        jsonObject = arr.getJSONObject("position");

            Float x = Float.parseFloat(jsonObject.getString("x"));
            Float y = Float.parseFloat(jsonObject.getString("y"));
            Float z = Float.parseFloat(jsonObject.getString("z"));

            position.add(x);
            position.add(y);
            position.add(z);
        return position;
    }
    public void setPosition(ArrayList<Float> position) {
        this.position = position;
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

    public void setDop(ArrayList<Float> dop) {
        this.dop = dop;
    }


    public void setVup(ArrayList<Float> vup) {
        this.vup = vup;
    }


    public ArrayList<Float> getVup(JSONObject arr) throws JSONException {

//        JSONArray jsonArray;
//        jsonArray = (arr.getJSONArray("vup"));

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


}
