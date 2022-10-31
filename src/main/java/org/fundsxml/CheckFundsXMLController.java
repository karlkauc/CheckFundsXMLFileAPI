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

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Controller("/check")
public class CheckFundsXMLController {

    private final static Logger logger = LogManager.getLogger(CheckFundsXMLController.class);

    @Post(uri = "/", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
    HttpResponse<String> upload(CompletedFileUpload file){

        PerformChecks performChecks = new PerformChecks();
        List<CheckResults> result = new ArrayList<>();
        try {
            File targetFile = new File(file.getName());
            Files.copy(
                    file.getInputStream(),
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            performChecks.setXmlFile(targetFile);
            result = performChecks.performAllChecks();

            logger.debug("Errors: {}", result);

            for (CheckResults checkResults : result) {
                if (checkResults.getResultStatus() == CheckResults.RESULTS.ERROR) {
                    return HttpResponse.status(HttpStatus.BAD_REQUEST, performChecks.formatErrors(result));
                }
            }

            return HttpResponse.ok(performChecks.formatErrors(result));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return HttpResponse.ok(performChecks.formatErrors(result));
    }


}



