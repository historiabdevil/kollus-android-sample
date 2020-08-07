package d.factory.haeming.data;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

public enum ContentTypes {
    VOD("VOD", Color.BLUE),
    AOD("AOD", Color.GREEN),
    LIVE("LIVE", Color.RED);
    private String typeString;
    private Color backgroundColor;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ContentTypes(String typeString, int backgroundColor){
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
