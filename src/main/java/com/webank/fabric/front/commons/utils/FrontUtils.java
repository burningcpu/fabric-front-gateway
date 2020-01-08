package com.webank.fabric.front.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static java.lang.String.format;

@Slf4j
public class FrontUtils {


    /**
     * extract string from file.
     */
    public static String extractFileString(String path) {

        String pemString = null;

        if (StringUtils.isBlank(path)) {
            log.warn("extractFileString fail. path is null");
            return null;
        }
        // Determine full pathname and ensure the file exists
        File file = new File(path);
        String fullPathname = file.getAbsolutePath();
        if (!file.exists()) {
            throw new RuntimeException(format("file %s does not exist", fullPathname));
        }
        try (FileInputStream stream = new FileInputStream(file)) {
            pemString = IOUtils.toString(stream, "UTF-8");
        } catch (IOException ioe) {
            throw new RuntimeException(format("Failed to read file: %s", fullPathname), ioe);
        }

        return pemString;
    }

    /**
     * check ip address.
     */
    public static boolean isIP(String address) {
        if (address.length() < 7 || address.length() > 15 || "".equals(address)) {
            return false;
        }
        String pattern = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        return address.matches(pattern);
    }

}
