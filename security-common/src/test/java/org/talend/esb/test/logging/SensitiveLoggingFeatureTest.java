package org.talend.esb.test.logging;

import java.util.HashSet;

import org.apache.cxf.ext.logging.LoggingFeature;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensitiveLoggingFeatureTest {

    Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void ensure_forked_cxf() {
        try {
            LoggingFeature loggingFeature = new LoggingFeature();
            loggingFeature.setSensitiveElementNames(new HashSet<>());
            loggingFeature.setSensitiveProtocolHeaderNames(new HashSet<>());
        } catch (Exception e) {
            Assert.fail("Ensure JAR cxf-rt-features-logging version contains additional methods " +
                    "setSensitiveElementNames and setSensitiveProtocolHeaderNames. " +
                    "This may require fork of CXF and backport of TPRUN-3354 or CXF-8699");
        }
    }
}
