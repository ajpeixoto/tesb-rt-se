package org.talend.esb.security.logging;

import java.util.Optional;

import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.feature.Feature;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Bundle to track CXF LoggingFeature
 */
public class SensitiveLoggingFeatureActivator implements BundleActivator {
    private ServiceTracker<AbstractFeature, AbstractFeature> cxfLoggingFeatureServiceTracker = null;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        cxfLoggingFeatureServiceTracker = new ServiceTracker<AbstractFeature, AbstractFeature>(bundleContext,
                AbstractFeature.class, null) {
            @Override
            public AbstractFeature addingService(ServiceReference<AbstractFeature> reference) {
                Feature feature = bundleContext.getService(reference);
                if (feature instanceof LoggingFeature) {
                    SensitiveLoggingFeatureUtils.setLoggingFeature((LoggingFeature) feature);
                }
                return super.addingService(reference);
            }
        };

        cxfLoggingFeatureServiceTracker.open();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        cxfLoggingFeatureServiceTracker.close();
    }
}
