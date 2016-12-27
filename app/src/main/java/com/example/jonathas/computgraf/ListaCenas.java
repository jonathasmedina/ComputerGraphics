package com.example.jonathas.computgraf;

/**
 * Created by Jonathas on 27/12/2016.
 */

public class ListaCenas {
    private String id;
    private String label;
    private String description;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    @Override
    public String toString() {
        return "" +
                "Id: " + id + '\n' +
                "Label: " + label + '\n' +
                "URL: " + url + '\n' +
                "Description: " + description;
    }
}
