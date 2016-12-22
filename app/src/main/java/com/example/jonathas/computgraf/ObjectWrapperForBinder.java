package com.example.jonathas.computgraf;

import android.os.Binder;

/**
 * Created by Jonathas on 20/12/2016.
 */
public class ObjectWrapperForBinder extends Binder {

    private final Cena mCena;

    public ObjectWrapperForBinder(Cena data) {
        mCena = data;
    }

    public Cena getData() {
        return mCena;
    }
}