package net.p0f.quarkus.acs.webhooks;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

@ApplicationScoped
public class Endpoints extends RouteBuilder {
    @Inject
    CamelContext ctx;
    
    public void configure() {
        ctx.getShutdownStrategy().setTimeout(-1);

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
            .routeId("unknownPayload")
            .log(LoggingLevel.INFO, "unknownPayload", "Generic message received.")
            .convertBodyTo(String.class)
            .to("log:unknownPayload?level=INFO&showBody=true");

        from("direct:alertLog")
            .routeId("alertLog")
            .log(LoggingLevel.INFO, "alertLog", "Alert message received")
            .setHeader("Alert-Id", jsonpath(".alert.id"))
            .setHeader("Alert-Name", jsonpath(".alert.policy.name"))
            .setHeader("Alert-Severity", jsonpath(".alert.policy.severity"))
            .setHeader("Alert-Lifecycle", jsonpath(".alert.policy.lifecycleStage"))
            .setHeader("Alert-Deployment", jsonpath(".alert.policy.deployment.name"))
            .setHeader("Alert-Namespace", jsonpath(".alert.policy.deployment.namespace"))
            .setHeader("Alert-Source", jsonpath(".alert.policy.eventSource"))
            .convertBodyTo(String.class)
            .to("log:alertLog?level=INFO&showBody=true&showHeaders=true");

        from("direct:auditLog")
            .routeId("auditLog")
            .log(LoggingLevel.INFO, "auditLog", "Audit message received")
            .setHeader("Audit-User", jsonpath(".audit.user.friendlyName"))
            .setHeader("Audit-Action", jsonpath(".audit.interaction"))
            .setHeader("Audit-Method", jsonpath(".audit.request.method"))
            .setHeader("Audit-Endpoint", jsonpath(".audit.request.endpoint"))
            .setHeader("Audit-Status", jsonpath(".audit.status"))
            .convertBodyTo(String.class)
            .to("log:auditLog?level=INFO&showBody=true&showHeaders=true");
    }
}
