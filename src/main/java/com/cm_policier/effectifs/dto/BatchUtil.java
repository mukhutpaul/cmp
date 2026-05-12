package com.cm_policier.effectifs.dto;

import java.util.ArrayList;
import java.util.List;

public class BatchUtil {

    public static <T> List<List<T>> batch(List<T> list, int size) {

        List<List<T>> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }

        return result;
    }
}