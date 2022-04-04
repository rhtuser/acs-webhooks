package net.p0f.quarkus.acs.webhooks;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class Endpoints extends RouteBuilder {
    public void configure() {
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
            .convertBodyTo(String.class)
            .log(LoggingLevel.INFO, "unknownPayload", "Generic message received: ${body}");

        from("direct:alertLog")
            .convertBodyTo(String.class)
            .log(LoggingLevel.INFO, "alertLog", "Alert message received: ${body}");

        from("direct:auditLog")
            .convertBodyTo(String.class)
            .log(LoggingLevel.INFO, "auditLog", "Audit message received: ${body}");

    }
}
