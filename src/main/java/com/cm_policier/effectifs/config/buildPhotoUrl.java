package com.cm_policier.effectifs.config;

public class buildPhotoUrl {
    public static String buildPhotoUrls(String pkPhoto) {
    if (pkPhoto == null || pkPhoto.isBlank() || pkPhoto.equals("null")) {
        return null;
    }

    return pkPhoto + ".jpg";
}

}
