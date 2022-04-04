package net.p0f.quarkus.acs.webhooks;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class Endpoints extends RouteBuilder {
    public void configure() {
        // TODO: configure route timeout

        restConfiguration()
            .component("platform-http")
            .contextPath("/api")
            .bindingMode(RestBindingMode.json);

        rest("/v1")
            .consumes("application/json")
            .produces("application/json")
            .post("/generic")
                .to("direct:unknownPayload")
            .post("/alert")
                .to("direct:alertLog")
            .post("/audit")
                .to("direct:auditLog");

        from("direct:unknownPayload")
            .log(LoggingLevel.INFO, "unknownPayload", "Generic message received.")
            .to("log:unknownPayload?level=INFO&showBody=true")
            .convertBodyTo(String.class)
            .to("log:unknownPayload?level=INFO&showBody=true");

        from("direct:alertLog")
            .log(LoggingLevel.INFO, "alertLog", "Alert message received")
            .to("log:alertLog?level=INFO&showBody=true")
            .convertBodyTo(String.class)
            .to("log:alertLog?level=INFO&showBody=true");

        from("direct:auditLog")
            .log(LoggingLevel.INFO, "auditLog", "Audit message received")
            .to("log:auditLog?level=INFO&showBody=true")
            .convertBodyTo(String.class)
            .to("log:auditLog?level=INFO&showBody=true");
    }
}
