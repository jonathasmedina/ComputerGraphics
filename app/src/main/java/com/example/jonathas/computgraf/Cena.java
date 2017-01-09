package com.example.jonathas.computgraf;

import android.os.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jonathas on 30/11/2016.
 */

public class Cena implements Serializable{

    private String id;
    private String label;
    private String description;
    private ObjCamera objCamera;
    private ObjLight objLight;
    private ObjActor objActor;

    //construtor padrão
    public Cena(){

    }

    //getters e setters
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

    public ObjCamera getObjCamera() {
        return objCamera;
    }

    public void setObjCamera(ObjCamera objCamera) {
        this.objCamera = objCamera;
    }

    public ObjLight getObjLight() {
        return objLight;
    }

    public void setObjLight(ObjLight objLight) {
        this.objLight = objLight;
    }

    public ObjActor getObjActor() {
        return objActor;
    }

    public void setObjActor(ObjActor objActor) {
        this.objActor = objActor;
    }

    //objeto serializável - necessário equals() e hashcode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cena cena = (Cena) o;

        if (id != null ? !id.equals(cena.id) : cena.id != null) return false;
        if (label != null ? !label.equals(cena.label) : cena.label != null) return false;
        if (description != null ? !description.equals(cena.description) : cena.description != null)
            return false;
        if (objCamera != null ? !objCamera.equals(cena.objCamera) : cena.objCamera != null)
            return false;
        if (objLight != null ? !objLight.equals(cena.objLight) : cena.objLight != null)
            return false;
        return objActor != null ? objActor.equals(cena.objActor) : cena.objActor == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (objCamera != null ? objCamera.hashCode() : 0);
        result = 31 * result + (objLight != null ? objLight.hashCode() : 0);
        result = 31 * result + (objActor != null ? objActor.hashCode() : 0);
        return result;
    }
}


