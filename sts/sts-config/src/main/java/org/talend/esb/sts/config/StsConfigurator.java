/*
 * #%L
 * STS :: Config
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
package org.talend.esb.sts.config;

import java.util.Collection;
import org.apache.cxf.Bus;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.talend.esb.security.logging.SensitiveLoggingFeatureUtils;

public class StsConfigurator {

	private String useMessageLogging = null;

	private Bus bus;

	public StsConfigurator(Bus bus) {
		this.bus = bus;
	}

	public void init() {
		setMessageLogging(useMessageLogging != null && useMessageLogging.equalsIgnoreCase("true"));
	}

	public void setUseMessageLogging(String useMessageLogging){
		this.useMessageLogging = useMessageLogging;
	}

	private void setMessageLogging(boolean logMessages) {
		SensitiveLoggingFeatureUtils.setMessageLogging(logMessages, bus);
	}

}
