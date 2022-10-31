/*
 * Copyright (c) 2022 Karl Kauc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package org.fundsxml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PerformChecks {

    private final static Logger logger = LogManager.getLogger(PerformChecks.class);

    File xmlFile;

    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    Document xmlDocument;

    public File getXmlFile() {
        return xmlFile;
    }

    public void setXmlFile(File xmlFile) {
        this.xmlFile = xmlFile;

        try {
            FileInputStream fileIS = new FileInputStream(xmlFile);
            builder = builderFactory.newDocumentBuilder();
            xmlDocument = builder.parse(fileIS);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            logger.error(e.getMessage());
        }
    }

    public List<CheckResults> performAllChecks() {
        assert this.xmlFile != null;

        List<CheckResults> checkResults = new ArrayList<>();
        checkResults.add(checkSchemaValidity());
        checkResults.add(hasFundName());
        checkResults.add(checkPortfolioSums());

        return checkResults;
    }

    private CheckResults checkSchemaValidity() {
        CheckResults schemaError = new CheckResults();
        schemaError.setCheckNumber(1);
        schemaError.setCheckName("Schema Valid");

        final List<SAXParseException> exceptions = new LinkedList<>();
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(new File("FundsXML4.xsd")));
            Validator validator = schema.newValidator();

            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    exceptions.add(exception);
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    exceptions.add(exception);
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    exceptions.add(exception);
                }
            });

            StreamSource xmlFile = new StreamSource(this.xmlFile);
            validator.validate(xmlFile);

            if (exceptions.size() > 0) {
                StringBuilder schemaValidationErrors = new StringBuilder();
                for (SAXParseException saxParseException : exceptions) {
                    schemaValidationErrors.append(saxParseException.getLocalizedMessage()).append(System.lineSeparator());
                }
                schemaError.setErrorMessage(schemaValidationErrors.toString());

                schemaError.setResultStatus(CheckResults.RESULTS.ERROR);
            } else {
                schemaError.setResultStatus(CheckResults.RESULTS.OK);
            }

            return schemaError;
        } catch (SAXException | IOException e) {
            logger.error(e.getMessage());
            return schemaError;
        }
    }

    private CheckResults hasFundName() {
        var fundName = getXmlFromXpath("/FundsXML4/Funds/Fund/Names/OfficialName/text()");
        logger.debug("Fund Name: {}", fundName);

        CheckResults checkResults = new CheckResults();
        checkResults.checkNumber = 2;
        checkResults.setCheckName("Fund has FundName");

        if (!fundName.trim().isEmpty()) {
            checkResults.setErrorDetails("OK");
            checkResults.setResultStatus(CheckResults.RESULTS.OK);
        } else {
            checkResults.setErrorDetails("NO FUND NAME FOUND!");
            checkResults.setResultStatus(CheckResults.RESULTS.ERROR);
        }

        return checkResults;
    }

    private CheckResults checkPortfolioSums() {
        CheckResults error = new CheckResults();
        error.setCheckNumber(3);
        error.setCheckName("Sum of Portfolio match Fund Volume");
        // TODO: xPath insert here!


        return error;
    }


    private String getXmlFromXpath(String xPath) {
        try {
            XPath xPathPath = XPathFactory.newInstance().newXPath();
            var nodeList = (NodeList) xPathPath.compile(xPath).evaluate(xmlDocument, XPathConstants.NODESET);

            Node elem = nodeList.item(0);//Your Node
            StringWriter buf = new StringWriter();
            Transformer xform = TransformerFactory.newInstance().newTransformer();
            xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            xform.setOutputProperty(OutputKeys.INDENT, "yes");
            xform.transform(new DOMSource(elem), new StreamResult(buf));

            return buf.toString();
        } catch (XPathExpressionException | TransformerException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public String formatErrors(List<CheckResults> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (CheckResults checkResults : list) {
            stringBuilder.append(checkResults.toString()).append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }


}
