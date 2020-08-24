package com.se.kollus.data;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

public enum EncryptTypes {
    NONE("NONE", Color.BLUE),
    KOLLUS("KOLLUS", Color.MAGENTA),
    WIDEVINE("WIDEVINE", Color.RED);
    private String typeString;
    private Color backgroundColor;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private EncryptTypes(String typeString, int backgroundColor){
        this.typeString = typeString;
        this.backgroundColor = Color.valueOf(backgroundColor);
    }
    public String getTypeString(){
        return this.typeString;
    }
    public Color getBackgroundColor(){
        return this.backgroundColor;
    }
}
