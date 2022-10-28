package org.fundsxml;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/delete")
public class DummyController {
    @Get(uri = "/", produces = "text/plain")
    public String index() {
        return "Example Response";
    }

}
