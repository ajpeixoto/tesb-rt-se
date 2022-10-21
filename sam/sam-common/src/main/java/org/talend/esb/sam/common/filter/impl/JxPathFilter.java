/*
 * #%L
 * Service Activity Monitoring :: Common
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
package org.talend.esb.sam.common.filter.impl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The Class JxPathFilter.
 */
public class JxPathFilter implements EventFilter {
    String expression;

    /**
     * Instantiates a new jx path filter.
     */
    public JxPathFilter() {
    }

    /**
     * Instantiates a new jx path filter.
     *
     * @param expression the expression
     */
    public JxPathFilter(String expression) {
        super();
        this.expression = expression;
    }

    /**
     * Sets the expression.
     *
     * @param expression the new expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.common.spi.EventFilter#filter(org.talend.esb.sam.common.event.Event)
     */
    @Override
    public boolean filter(Event event) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(Event.class);
            Marshaller msh = ctx.createMarshaller();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // never forget this!
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            msh.marshal(event, doc);
            Node node = doc.getDocumentElement();
            XPathFactory xpathfactory = XPathFactory.newInstance();
            XPath xpath = xpathfactory.newXPath();
            Boolean result = (Boolean) xpath.evaluate(expression, node, XPathConstants.BOOLEAN);
            return result == null ? false : result.booleanValue();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Exception caught during XPath filter evaluation: ", e);
        }
    }

}
