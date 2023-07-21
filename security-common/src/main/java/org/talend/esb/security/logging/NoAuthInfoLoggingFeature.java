package org.talend.esb.security.logging;

import java.util.Arrays;
import java.util.HashSet;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.helpers.HttpHeaderHelper;

public class NoAuthInfoLoggingFeature extends  LoggingFeature {

    public NoAuthInfoLoggingFeature() {
        super();
        addSensitiveProtocolHeaderNames(new HashSet<>(Arrays.asList(HttpHeaderHelper.AUTHORIZATION)));
        addSensitiveElementNames(new HashSet<>(Arrays.asList("password")));
    }
}
