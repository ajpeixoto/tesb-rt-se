package org.hibersap.execution.jco;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JCoEnvironment {

    public static Map<String, Properties> ENDPOINT_MAP = new HashMap<>();

    public static void registerDestination(String endpointName, Properties properties) {
        ENDPOINT_MAP.put(endpointName, properties);
    }

    public static void unregisterDestination(String endpointName) {
        Properties result = ENDPOINT_MAP.remove(endpointName);
        if (result == null) {
            throw new RuntimeException("Endpoint " + endpointName + " has not been registered. ");
        }
    }
}
