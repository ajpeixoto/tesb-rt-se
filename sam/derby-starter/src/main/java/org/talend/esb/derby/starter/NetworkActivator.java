/*
 * #%L
 * Service Activity Monitoring :: Derby Starter
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.derby.starter;

import java.net.InetAddress;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.derby.drda.NetworkServerControl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkActivator implements BundleActivator {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkActivator.class);
    private static final String DERBY_HOST_PROPERTY = "org.talend.esb.derby.host";
    private static final String DERBY_PORT_PROPERTY = "org.talend.esb.derby.port";
    private static final byte[] LOCAL_HOST = { 127, 0, 0, 1 };
    private static final byte[] ALL_HOSTS = { 0, 0, 0, 0 };
    private static final int DEFAULT_PORT = 1527;

    private static final String HOST_FALLBACK_MESSAGE = "System property " + DERBY_HOST_PROPERTY
            + " not set, using default host \"127.0.0.1\". ";
    private static final String HOST_ERROR_MESSAGE = "System property " + DERBY_HOST_PROPERTY
            + " has an unsupported value of {}, using default host \"127.0.0.1\". ";
    private static final String HOST_CONFIGURED_MESSAGE = "System property " + DERBY_HOST_PROPERTY
            + " is applied, setting Derby host to {}. ";
    private static final String PORT_FALLBACK_MESSAGE = "System property " + DERBY_PORT_PROPERTY
            + " not set, using default port " + DEFAULT_PORT + ". ";
    private static final String PORT_ERROR_MESSAGE = "System property " + DERBY_PORT_PROPERTY
            + " has an illegal port value of {}, using default port " + DEFAULT_PORT + ". ";
    private static final String PORT_CONFIGURED_MESSAGE = "System property " + DERBY_PORT_PROPERTY
            + " is applied, setting Derby port to {}. ";

    private NetworkServerControl server;

    public void start(BundleContext context) throws Exception {
        LOG.info("Starting internal Derby DB...");

        server = new NetworkServerControl(InetAddress.getByAddress(resolveDerbyHost()), resolveDerbyPort());
        server.start(null);

        // if it already exists nothing should happen
        DriverManager.getConnection(getDerbyJDBC_Create("db"));
    }

    public void stop(BundleContext context) throws Exception {
        LOG.info("Stopping internal Derby DB...");

        try {
            DriverManager.getConnection(getDerbyJDBC_Shutdown("db"));
        } catch (SQLException e) {
            if (!"08006".equals(e.getSQLState())) {
                LOG.error("Exception during db shutdown. ", e);
            }
        }

        server.shutdown();
    }

    private static String getDerbyJDBC_Create(String databaseName) {
        return "jdbc:derby:" + databaseName + ";create=true";
    }

    private static String getDerbyJDBC_Shutdown(String databaseName) {
        return "jdbc:derby:" + databaseName + ";shutdown=true";
    }

    private static byte[] resolveDerbyHost() {
        String value = System.getProperty(DERBY_HOST_PROPERTY);
        if (value == null) {
            LOG.info(HOST_FALLBACK_MESSAGE);
            return LOCAL_HOST;
        }
        byte[] result = null;
        if (value.equals("127.0.0.1")) {
            result = LOCAL_HOST;
        } else if (value.equals("0.0.0.0")) {
            result = ALL_HOSTS;
        } else {
            LOG.warn(HOST_ERROR_MESSAGE, value);
            return LOCAL_HOST;
        }
        LOG.info(HOST_CONFIGURED_MESSAGE, value);
        return result;
    }

    private static int resolveDerbyPort() {
        Integer value = Integer.getInteger(DERBY_PORT_PROPERTY);
        if (value == null) {
            LOG.info(PORT_FALLBACK_MESSAGE);
            return DEFAULT_PORT;
        }
        int result = value.intValue();
        if (result < 1 || result > 65535) {
            LOG.warn(PORT_ERROR_MESSAGE, result);
            return DEFAULT_PORT;
        }
        LOG.info(PORT_CONFIGURED_MESSAGE, result);
        return result;
    }
}
