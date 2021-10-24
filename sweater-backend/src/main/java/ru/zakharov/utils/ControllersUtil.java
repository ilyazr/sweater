package ru.zakharov.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;

@Slf4j
public class ControllersUtil {

    /**
     *
     * @param args: args[0] = String.class; args[1] = Object.class;
     * @return LinkedHashMap that contains record like JSON
     */
    public static LinkedHashMap<String, Object> composeResponse(Object... args) {
        if (args.length % 2 != 0) {
            log.error("Amount of arguments must be even!");
            return new LinkedHashMap<>();
        } else {
            LinkedHashMap<String, Object> data = new LinkedHashMap<>();
            for (int i = 0; i < args.length - 1; i+=2) {
                String fieldName = (String) args[i];
                Object value = args[i + 1];
                data.put(fieldName, value);
            }
            return data;
        }
    }

}
