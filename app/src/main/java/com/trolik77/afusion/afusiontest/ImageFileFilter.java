package com.trolik77.afusion.afusiontest;

import java.io.File;
import java.io.FileFilter;

public class ImageFileFilter implements FileFilter {

    private String[] extensions = new String[] {".jpg", ".gif", ".png", ".jpeg"};

    @Override public boolean accept(File pathname) {
        for (String ext : extensions) {
            if (pathname.getName().endsWith(ext) || pathname.isDirectory()) {
                return true;
            }
        }

        return false;
    }
}
