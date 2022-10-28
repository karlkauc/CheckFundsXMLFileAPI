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

@Controller("/check")
public class CheckFundsXMLController {

    private final static Logger logger = LogManager.getLogger(CheckFundsXMLController.class);

    @Post(uri = "/", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
    HttpResponse<String> upload(CompletedFileUpload file){

        PerformChecks performChecks = new PerformChecks();
        try {
            File targetFile = new File(file.getName());
            Files.copy(
                    file.getInputStream(),
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            performChecks.setXmlFile(targetFile);
            String result = performChecks.performAllChecks();

            logger.debug("Errors: {}", result);


            if (result.contains("ERROR")) {
                return HttpResponse.status(HttpStatus.BAD_REQUEST);
            }

            return HttpResponse.ok();
        }
        catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return HttpResponse.ok();
    }

}



