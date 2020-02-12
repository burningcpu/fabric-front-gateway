package com.webank.fabric.front.commons.utils;

import com.webank.fabric.front.commons.exception.FrontException;
import com.webank.fabric.front.commons.pojo.base.ConstantCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static java.lang.String.format;

/**
 * file utils.
 */
@Slf4j
@Component
public class FileUtils {

    private static final String JAVA_FILE_SUFFIX = ".java";
    private static final String GO_FILE_SUFFIX = ".go";
    private static final String NODE_FILE_SUFFIX = ".js";


    /**
     * write constant to file.
     */
    public static void writeConstantToFile(File file, String constant) {
        try {
            FileUtils.createFileIfNotExist(file, true, true);
        } catch (IOException ioe) {
            String errorMsg = format("Failed to create file: %s", file.getAbsolutePath());
            log.error(errorMsg);
            throw new FrontException(ConstantCode.SYSTEM_EXCEPTION.getCode(), errorMsg, ioe);
        }

        try (FileOutputStream stream = new FileOutputStream(file)) {
            IOUtils.write(constant.getBytes("UTF-8"), stream);
            stream.flush();
        } catch (IOException ioe) {
            String errorMsg = format("Failed to write file: %s", file.getAbsolutePath());
            log.error(errorMsg);
            throw new FrontException(ConstantCode.SYSTEM_EXCEPTION.getCode(), errorMsg, ioe);
        }
    }


    /**
     * create file if not exist.
     */
    public static void createFileIfNotExist(File targetFile, boolean deleteOld, boolean isFile) throws IOException {
        Objects.requireNonNull(targetFile, "fail to create file:targetFile is null");
        File parentFile = targetFile.getParentFile();
        if (!parentFile.exists())
            parentFile.mkdirs();

        if (deleteOld)
            targetFile.deleteOnExit();

        if (targetFile.exists())
            return;

        if (isFile) {
            targetFile.createNewFile();
        } else {
            targetFile.mkdir();
        }
    }

    /**
     * build name of chainCodeFile.
     */
    public static String buildChainCodeFileName(String channelName, String chainCodeName, String version) {
        if (StringUtils.isAnyBlank(channelName, chainCodeName, version)) {
            String errorMsg = "buildChainCodeFileName fail:channelName、chainCodeName、version or constant is null";
            log.error(errorMsg);
            throw new FrontException(ConstantCode.OPERATION_EXCEPTION.getCode(), errorMsg);
        }

        String fileName = channelName + "_" + chainCodeName + "_" + version;
        return fileName;
    }

    /**
     * choose file suffix by language.
     */
    public static String chooseChainCodeFileSuffix(String chainCodeLang) {
        String suffix;
        switch (chainCodeLang) {
            case "JAVA":
                suffix = JAVA_FILE_SUFFIX;
                break;
            case "NODE":
                suffix = NODE_FILE_SUFFIX;
                break;
            default:
                suffix = GO_FILE_SUFFIX;
                break;
        }
        return suffix;
    }
}
