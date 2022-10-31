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

package org.fundsxml.convert;

import net.sf.saxon.TransformerFactoryImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class HTMLConverter {

    private final static Logger logger = LogManager.getLogger(HTMLConverter.class);

    public String saxonTransform(File xmlFile) {
        try {
            InputStream xsltFile = getClass().getClassLoader().getResourceAsStream("Check_FundsXML_File.xslt");

            TransformerFactoryImpl f = new TransformerFactoryImpl();
            f.setAttribute("http://saxon.sf.net/feature/version-warning", Boolean.FALSE);

            StreamSource streamSource = new StreamSource(xsltFile);
            Transformer t = f.newTransformer(streamSource);
            StreamSource src = new StreamSource(new FileInputStream(xmlFile));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            t.transform(src, res);
            return res.getOutputStream().toString();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }

}
