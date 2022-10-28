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

    public String performAllChecks() {
        assert this.xmlFile != null;

        var errorList = checkSchemaValidity();
        StringBuilder schemaValidationErrors = new StringBuilder();
        for (SAXParseException saxParseException : errorList) {
            schemaValidationErrors.append(saxParseException.getLocalizedMessage()).append(System.lineSeparator());
        }

        String fundName = hasFundName();


        return schemaValidationErrors + fundName + System.lineSeparator();
    }

    public List<SAXParseException> checkSchemaValidity() {
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

            return exceptions;

        } catch (SAXException | IOException e) {
            logger.error(e.getMessage());
            return exceptions;
        }
    }

    public String hasFundName() {
        var fundName = getXmlFromXpath("/FundsXML4/Funds/Fund/Names/OfficialName/text()");
        logger.debug("Fund Name: {}", fundName);

        if (!fundName.trim().isEmpty()) {
            return "OK - has Fund Name: " + fundName;
        }
        return "ERROR - no Fund Name";
    }

    public String getXmlFromXpath(String xPath) {
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

}
