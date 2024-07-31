package com.example.login_test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class FilePathUtil {

    public static String getCSVFilePath() {
        try {
            URL resource = FilePathUtil.class.getResource("/items.csv");
            if (resource != null) {
                return new File(resource.toURI()).getPath();
            } else {
                throw new RuntimeException("Resource not found");
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error getting file path", e);
        }
    }
}
