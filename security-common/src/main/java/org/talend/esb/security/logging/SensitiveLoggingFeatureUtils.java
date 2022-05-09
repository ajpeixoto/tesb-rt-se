package org.talend.esb.security.logging;

import java.util.Collection;

import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensitiveLoggingFeatureUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SensitiveLoggingFeatureUtils.class);
    private static LoggingFeature CXF_LOGGING_FEATURE = null;

    /**
     * Configure bus to use LoggingFeature declared in {@link org.apache.cxf.ext.logging.osgi.Activator}
     *
     * @param logMessages true or false
     * @param bus         bus needing logging
     */
    public static void setMessageLogging(boolean logMessages, Bus bus) {
        setMessageLogging(logMessages, bus, bus);
    }

    /**
     * Configure bus to use LoggingFeature declared in {@link org.apache.cxf.ext.logging.osgi.Activator}
     *
     * @param logMessages true or false
     * @param provider    provider needing logging
     * @param bus         bus linked to provider
     */
    public static void setMessageLogging(boolean logMessages, InterceptorProvider provider, Bus bus) {
        if (provider != null && bus != null) {
            if (logMessages) {
                if (!hasLoggingFeature(bus))
                    addMessageLogging(provider, bus);
            } else {
                if (hasLoggingFeature(bus))
                    removeMessageLogging(provider, bus);
            }
        }
    }

    static void setLoggingFeature(LoggingFeature loggingFeature) {
        CXF_LOGGING_FEATURE = loggingFeature;
    }

    private static boolean hasLoggingFeature(Bus bus) {
        Collection<Feature> features = bus.getFeatures();
        if (features != null) {
            for (Feature feature : features) {
                if (feature instanceof LoggingFeature)
                    return true;
            }
        }
        return false;
    }

    private static void addMessageLogging(InterceptorProvider provider, Bus bus) {
        LoggingFeature logFeature = CXF_LOGGING_FEATURE;
        
        if (logFeature == null) {
            LOG.warn("Common CXF LoggingFeature not available, using new one");
            logFeature = new LoggingFeature();
        }
        
        logFeature.initialize(provider, bus);
        bus.getFeatures().add(logFeature);
    }

    private static void removeMessageLogging(InterceptorProvider provider, Bus bus) {
        Collection<Feature> features = bus.getFeatures();
        Feature logFeature = null;
        Interceptor<? extends Message> inLogInterceptor = null;
        Interceptor<? extends Message> outLogInterceptor = null;
        for (Feature feature : features) {
            if (feature instanceof LoggingFeature) {
                logFeature = feature;
                break;
            }
        }
        if (logFeature != null) {
            features.remove(logFeature);
        }
        for (Interceptor<? extends Message> interceptor : provider.getInInterceptors()) {
            if (interceptor instanceof LoggingInInterceptor) {
                inLogInterceptor = interceptor;
                break;
            }
        }
        for (Interceptor<? extends Message> interceptor : provider.getOutInterceptors()) {
            if (interceptor instanceof LoggingOutInterceptor) {
                outLogInterceptor = interceptor;
                break;
            }
        }
        if (inLogInterceptor != null) {
            provider.getInInterceptors().remove(inLogInterceptor);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Remove in Interceptor = {}", inLogInterceptor.getClass().getName());
            }
        }
        if (outLogInterceptor != null) {
            provider.getOutInterceptors().remove(outLogInterceptor);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Remove out Interceptor = {}", outLogInterceptor.getClass().getName());
            }
        }
    }
}
