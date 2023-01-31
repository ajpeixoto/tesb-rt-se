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
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

    private String expression;
    private JAXBContext context = initJAXBContext();
    private DocumentBuilder builder = initDocumentBuilder();
    private XPathFactory xpathfactory = initXPathFactory();

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
            Marshaller msh = initMarshaller();
            Document doc = initDocument();
            msh.marshal(event, doc);
            Node node = doc.getDocumentElement();
            XPath xpath = initXPath();
            Boolean result = (Boolean) xpath.evaluate(expression, node, XPathConstants.BOOLEAN);
            return result == null ? false : result.booleanValue();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Exception caught during XPath filter evaluation: ", e);
        }
    }

    private Marshaller initMarshaller() throws JAXBException {
        synchronized (context) {
            return context.createMarshaller();
        }
    }

    private Document initDocument() {
        synchronized (builder) {
            return builder.newDocument();
        }
    }

    private XPath initXPath() {
        synchronized (xpathfactory) {
            return xpathfactory.newXPath();
        }
    }

    private static JAXBContext initJAXBContext() {
        try {
            return JAXBContext.newInstance(Event.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Exception caught initializing JAXB context for Event class. ", e);
        }
    }

    private static DocumentBuilder initDocumentBuilder() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // never forget this!
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Exception caught setting up document builder. ", e);
        }
    }

    private static XPathFactory initXPathFactory() {
        return XPathFactory.newInstance();
    }
}
