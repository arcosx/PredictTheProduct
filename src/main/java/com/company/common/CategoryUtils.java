package com.company.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
@Slf4j
public class CategoryUtils {
    private CategoryUtils() {
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 判断本地文件（目录）是否存在
     *
     * @param path 文件路径
     * @return boolean 存在为true， 反之false
     */
    public static boolean isFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static <T> T fromJson(String jsonStr, TypeReference<T> reference) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, reference);
        } catch (IOException e) {
            log.error("Fail convert json string to object {} , {}", jsonStr, reference, e);
        }

        return null;
    }
}
