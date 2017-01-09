package com.example.jonathas.computgraf;

import android.os.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jonathas on 19/12/2016.
 */

public class ObjActor implements Serializable {
    private int numberOfVertices;
    private int numberOfNormals;
    private int numberOfTriangles;
    private int numberOfColors;
    private int numberOfTextures;
    private ArrayList<Float> material = new ArrayList<>();

    //informações sobre as posições X,Y e Z de cada vértice; ex {"x":"-2.039150","y":"3.455440","z":"-5.975610"},{"x":"-2.007780","y":"3.431870","z":"-5.985680"}
    //private String vertices;
    private ArrayList<Float> vertices = new ArrayList<>();

    //ids dos vértices dos triângulos; ex triangles":[{"v0":1142,"v1":1141,"v2":1132},{"v0":1142,"v1":1132,"v2":"1132"}
    private ArrayList<Integer> triangles = new ArrayList<>();

    //separando...somente vértices, somente texturas e somente normais dos triângulos
    private ArrayList<Integer> trianglesV = new ArrayList<>();
    private ArrayList<Integer> trianglesVT = new ArrayList<>();
    private ArrayList<Integer> trianglesVN = new ArrayList<>();

    //normais - ex "normals":[{"x":"0.000000","y":"0.000000","z":"-1.000000"},{"x":"-0.707102","y":"-0.707112","z":"0.000000"},
    private ArrayList<Float> normals = new ArrayList<>();

    private ArrayList<Float> colors = new ArrayList<>();

    private ArrayList<Float> textures = new ArrayList<>();


    //construtor padrão
    public ObjActor(){

    }

    //getters e setters
    public ArrayList<Integer> getTrianglesV() {
        return trianglesV;
    }

    public void setTrianglesV(ArrayList<Integer> trianglesV) {
        this.trianglesV = trianglesV;
    }

    public ArrayList<Integer> getTrianglesVN() {
        return trianglesVN;
    }

    public void setTrianglesVN(ArrayList<Integer> trianglesVN) {
        this.trianglesVN = trianglesVN;
    }

    public ArrayList<Integer> getTrianglesVT() {
        return trianglesVT;
    }

    public void setTrianglesVT(ArrayList<Integer> trianglesVT) {
        this.trianglesVT = trianglesVT;
    }

    public ArrayList<Float> getTextures() {
        return textures;
    }

    public void setTextures(ArrayList<Float> textures) {
        this.textures = textures;
    }

    public ArrayList<Float> getMaterial() {
        return material;
    }

    public void setMaterial(ArrayList<Float> material) {
        this.material = material;
    }

    public int getNumberOfTextures() {
        return numberOfTextures;
    }

    public void setNumberOfTextures(int numberOfTextures) {
        this.numberOfTextures = numberOfTextures;
    }

    public ArrayList<Float> getColors() {
        return colors;
    }

    public void setColors(ArrayList<Float> colors) {
        this.colors = colors;
    }

    public ArrayList<Float> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Float> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<Integer> getTriangles() {
        return triangles;
    }

    public void setTriangles(ArrayList<Integer> triangles) {
        this.triangles = triangles;
    }

    public ArrayList<Float> getNormals() {
        return normals;
    }

    public void setNormals(ArrayList<Float> normals) {
        this.normals = normals;
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

}
