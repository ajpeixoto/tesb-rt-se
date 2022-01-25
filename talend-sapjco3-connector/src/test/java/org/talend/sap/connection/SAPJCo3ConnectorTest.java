package org.talend.sap.connection;

import java.util.Properties;

import org.hibersap.execution.jco.JCoEnvironment;
import org.junit.Assert;
import org.junit.Test;

public class SAPJCo3ConnectorTest {

    @Test
    public void testSAPJCo3Connector() throws Exception {
        SAPJCo3Connector connector = new SAPJCo3Connector();
        Properties props = new Properties();
        props.setProperty("jco.client.ashost", "host");
        props.setProperty("jco.client.sysnr", "00");
        props.setProperty("jco.client.client", "000");
        props.setProperty("jco.client.user", "user");
        props.setProperty("jco.client.passwd", "password");
        props.setProperty("jco.client.lang", "EN");
        props.setProperty("jco.destination.peak_limit", "10");
        props.setProperty("jco.destination.pool_capacity", "3");
        props.setProperty("endpoint.SAP_PEER_CONNECTION_POOL.jco.client.ashost", "peerhost");
        props.setProperty("endpoint.SAP_PEER_CONNECTION_POOL.jco.client.sysnr", "00");
        props.setProperty("endpoint.SAP_PEER_CONNECTION_POOL.jco.client.client", "000");
        props.setProperty("endpoint.SAP_PEER_CONNECTION_POOL.jco.client.user", "peeruser");
        props.setProperty("endpoint.SAP_PEER_CONNECTION_POOL.jco.client.passwd", "peerpassword");
        props.setProperty("endpoint.SAP_PEER_CONNECTION_POOL.jco.client.lang", "EN");
        props.setProperty("endpoint.SAP_PEER_CONNECTION_POOL.jco.destination.peak_limit", "10");
        props.setProperty("endpoint.SAP_PEER_CONNECTION_POOL.jco.destination.pool_capacity", "3");
        connector.setConnectionPoolName("SAP_DEFAULT_CONNECTION_POOL");
        connector.setProperties(props);
        connector.init();
        Assert.assertEquals(2, JCoEnvironment.ENDPOINT_MAP.size());
        Properties defProps = JCoEnvironment.ENDPOINT_MAP.get("SAP_DEFAULT_CONNECTION_POOL");
        Properties peerProps = JCoEnvironment.ENDPOINT_MAP.get("SAP_PEER_CONNECTION_POOL");
        Assert.assertNotNull(defProps);
        Assert.assertNotNull(peerProps);
        Assert.assertEquals(8, defProps.size());
        Assert.assertEquals(8, peerProps.size());
        Assert.assertEquals("host", defProps.getProperty("jco.client.ashost"));
        Assert.assertEquals("00", defProps.getProperty("jco.client.sysnr"));
        Assert.assertEquals("000", defProps.getProperty("jco.client.client"));
        Assert.assertEquals("user", defProps.getProperty("jco.client.user"));
        Assert.assertEquals("password", defProps.getProperty("jco.client.passwd"));
        Assert.assertEquals("EN", defProps.getProperty("jco.client.lang"));
        Assert.assertEquals("10", defProps.getProperty("jco.destination.peak_limit"));
        Assert.assertEquals("3", defProps.getProperty("jco.destination.pool_capacity"));
        Assert.assertEquals("peerhost", peerProps.getProperty("jco.client.ashost"));
        Assert.assertEquals("00", peerProps.getProperty("jco.client.sysnr"));
        Assert.assertEquals("000", peerProps.getProperty("jco.client.client"));
        Assert.assertEquals("peeruser", peerProps.getProperty("jco.client.user"));
        Assert.assertEquals("peerpassword", peerProps.getProperty("jco.client.passwd"));
        Assert.assertEquals("EN", peerProps.getProperty("jco.client.lang"));
        Assert.assertEquals("10", peerProps.getProperty("jco.destination.peak_limit"));
        Assert.assertEquals("3", peerProps.getProperty("jco.destination.pool_capacity"));
        connector.destroy();
        Assert.assertTrue(JCoEnvironment.ENDPOINT_MAP.isEmpty());
    }

}
