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
    private int angle_view;

    //getters e setters
    public int getAngle_view() {
        return angle_view;
    }

    public void setAngle_view(int angle_view) {
        this.angle_view = angle_view;
    }

    public void setPosition(ArrayList<Float> position) {
        this.position = position;
    }

    public void setDop(ArrayList<Float> dop) {
        this.dop = dop;
    }

    public void setVup(ArrayList<Float> vup) {
        this.vup = vup;
    }

    public ArrayList<Float> getPosition() {
        return position;
    }

    public ArrayList<Float> getDop() {
        return dop;
    }

    public ArrayList<Float> getVup() {
        return vup;
    }
}
