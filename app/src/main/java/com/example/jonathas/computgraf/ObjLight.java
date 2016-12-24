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

    public ArrayList<Float> getPosition() {
        return position;
    }

    public ArrayList<Float> getColor() {
        return color;
    }

    public void setColor(ArrayList<Float> color) {
        this.color = color;
    }



    public void setPosition(ArrayList<Float> position) {
        this.position = position;
    }

    private ArrayList<Float> color = new ArrayList<>();

}
