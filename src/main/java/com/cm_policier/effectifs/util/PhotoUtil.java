package com.cm_policier.effectifs.util;

public class PhotoUtil {

    public static String buildPhotoUrl(String pkPhoto) {
        if (pkPhoto == null || pkPhoto.isBlank() || pkPhoto.equals("null")) {
            return null;
        }
        return "photos/" + pkPhoto + ".jpg";
    }
}