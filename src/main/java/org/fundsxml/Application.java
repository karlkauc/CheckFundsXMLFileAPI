package org.fundsxml;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.info.*;

@OpenAPIDefinition(
    info = @Info(
            title = "Check FundsXML File Service",
            version = "0.1",
            description = "API for checking FundsXML Files",
            license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"),
            contact = @Contact(url = "http://www.fundsxml.org", name = "Karl Kauc", email = "karl.kauc@erste-am.com")
    )
)
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
