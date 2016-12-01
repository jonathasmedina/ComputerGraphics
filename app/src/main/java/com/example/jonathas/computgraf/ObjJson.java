package com.example.jonathas.computgraf;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Asus on 01/02/2016.
 */


public class ObjJson {

    private String id;
    private String label;
    private String description;
    private int numberOfVertices;
    private int numberOfNormals;
    private int numberOfTriangles;
    private int numberOfColors;
    //informações sobre as posições X,Y e Z de cada vértice; ex {"x":"-2.039150","y":"3.455440","z":"-5.975610"},{"x":"-2.007780","y":"3.431870","z":"-5.985680"}
    //private String vertices;
    private ArrayList<Float> vertices = new ArrayList<>();

    //ids dos vértices dos triângulos; ex triangles":[{"v0":1142,"v1":1141,"v2":1132},{"v0":1142,"v1":1132,"v2":"1132"}
    private int triangles[];
    //?private ArrayList<Float> triangles = new ArrayList<Float>();

    //normais - ex "normals":[{"x":"0.000000","y":"0.000000","z":"-1.000000"},{"x":"-0.707102","y":"-0.707112","z":"0.000000"},
    private float normals[];

    private float colors[];


    public ArrayList<Float> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Float> vertices) {
        this.vertices = vertices;
    }

    public int[] getTriangles() {
        return triangles;
    }

    public void setTriangles(int[] triangles) {
        this.triangles = triangles;
    }

    public float[] getNormals() {
        return normals;
    }

    public void setNormals(float[] normals) {
        this.normals = normals;
    }

    public float[] getColors() {
        return colors;
    }

    public void setColors(float[] colors) {
        this.colors = colors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfVertices() {
        return numberOfVertices;
    }

    public void setNumberOfVertices(int numberOfVertices) {
        this.numberOfVertices = numberOfVertices;
    }

    public int getNumberOfNormals() {
        return numberOfNormals;
    }

    public void setNumberOfNormals(int numberOfNormals) {
        this.numberOfNormals = numberOfNormals;
    }

    public int getNumberOfTriangles() {
        return numberOfTriangles;
    }

    public void setNumberOfTriangles(int numberOfTriangles) {
        this.numberOfTriangles = numberOfTriangles;
    }

    public int getNumberOfColors() {
        return numberOfColors;
    }

    public void setNumberOfColors(int numberOfColors) {
        this.numberOfColors = numberOfColors;
    }


    @Override
    public String toString() {
        return this.id + " -- " + this.label + " -- " + this.description;
    }
}

