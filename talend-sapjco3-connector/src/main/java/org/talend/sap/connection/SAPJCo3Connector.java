/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.talend.sap.connection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SAPJCo3Connector {

    private static final String ENDPOINT_PREFIX = "endpoint.";
    private static final int ENDPOINT_PREFIX_LENGTH = ENDPOINT_PREFIX.length();
    private String connectionPoolName;
    private Properties properties;
    private List<String> endpointNames;

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setConnectionPoolName(String connectionPoolName) {
        this.connectionPoolName = connectionPoolName;
    }

    public void init() throws Exception {
        endpointNames = new ArrayList<>();
        Map<String, Properties> endpoints = evaluateProperties();
        if (endpoints == null) {
            return;
        }
        Class<?> cls = Class.forName("org.hibersap.execution.jco.JCoEnvironment");
        Method regMethod = cls.getMethod("registerDestination", String.class, Properties.class);
        for (Map.Entry<String, Properties> e : endpoints.entrySet()) {
            String ep = e.getKey();
            regMethod.invoke(cls, ep, e.getValue());
            endpointNames.add(ep);
        }
    }

    public void destroy() throws Exception {
        if (endpointNames.isEmpty()) {
            return;
        }
        Class<?> cls = Class.forName("org.hibersap.execution.jco.JCoEnvironment");
        Method regMethod = cls.getMethod("unregisterDestination", String.class);
        for (String ep : endpointNames) { 
            regMethod.invoke(cls, ep);
        }
    }

    private Map<String, Properties> evaluateProperties() {
        if (properties == null) {
            return null;
        }
        Map<String, Properties> result = new LinkedHashMap<>();
        for (Map.Entry<Object, Object> e : properties.entrySet()) {
            String propName = (String) e.getKey();
            String propValue = (String) e.getValue();
            String endpointName = connectionPoolName;
            if (propName.startsWith(ENDPOINT_PREFIX)) {
                int ndx = propName.indexOf('.', ENDPOINT_PREFIX_LENGTH);
                if (ndx > 0) {
                    endpointName = propName.substring(ENDPOINT_PREFIX_LENGTH, ndx);
                    propName = propName.substring(ndx + 1);
                }
            }
            Properties endpointProps = result.get(endpointName);
            if (endpointProps == null) {
                endpointProps = new Properties();
                result.put(endpointName, endpointProps);
            }
            endpointProps.setProperty(propName, propValue);
        }
        return result;
    }
}