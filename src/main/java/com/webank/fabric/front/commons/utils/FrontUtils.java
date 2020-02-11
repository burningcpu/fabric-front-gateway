package com.webank.fabric.front.commons.utils;

import com.webank.fabric.front.commons.exception.FrontException;
import com.webank.fabric.front.commons.pojo.base.ConstantCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;

import static java.lang.String.format;


@Slf4j
@Component
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
            String errorMsg = format("file %s does not exist", fullPathname);
            log.error(errorMsg);
            throw new FrontException(ConstantCode.SYSTEM_EXCEPTION.getCode(), errorMsg);
        }
        try (FileInputStream stream = new FileInputStream(file)) {
            pemString = IOUtils.toString(stream, "UTF-8");
        } catch (IOException ioe) {
            String errorMsg = format("Failed to read file: %s", fullPathname);
            log.error(errorMsg);
            throw new FrontException(ConstantCode.SYSTEM_EXCEPTION.getCode(), errorMsg, ioe);
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


    public static <T> T getInstanceByReflection(Class<T> clazz, List<Object> params) {
        try {
            Class[] paramClazzArr = params.stream().map(p -> p.getClass()).toArray(Class[]::new);
            Constructor<T> constructor = clazz.getDeclaredConstructor(paramClazzArr);
            constructor.setAccessible(true);
            return constructor.newInstance(params.toArray());
        } catch (Exception ex) {
            log.error("create instance by reflect exception", ex);
        }
        return null;
    }


}
