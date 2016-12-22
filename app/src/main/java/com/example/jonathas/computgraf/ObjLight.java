package com.example.jonathas.computgraf;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jonathas on 19/12/2016.
 */

public class ObjLight implements Serializable{
    private ArrayList<Float> position = new ArrayList<>();

    public ArrayList<Float> getColor(JSONObject arr) throws JSONException {

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

    public void setColor(ArrayList<Float> color) {
        this.color = color;
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

    private ArrayList<Float> color = new ArrayList<>();

}
