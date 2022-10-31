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

package org.fundsxml.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fundsxml.convert.HTMLConverter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Controller("/convert")
public class ConverterController {
    private final static Logger logger = LogManager.getLogger(ConverterController.class);

    HTMLConverter htmlConverter = new HTMLConverter();

    @Post(uri = "/", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
    HttpResponse<String> upload(CompletedFileUpload file) {
        logger.debug("Converting file: {}", file.getFilename());

        try {
            File targetFile = new File(file.getFilename());
            Files.copy(
                    file.getInputStream(),
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            var output = htmlConverter.saxonTransform(targetFile);

            return HttpResponse.ok(output);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return HttpResponse.status(HttpStatus.BAD_REQUEST, "ERROR in converting file.");
    }
}
