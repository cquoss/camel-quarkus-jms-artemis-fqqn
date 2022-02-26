package de.quoss.example.camel.jms.artemis.fqqn;

import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.util.ObjectHelper;

@ApplicationScoped
public class MainRoute extends RouteBuilder {

    private ConnectionFactory connectionFactory;

    public MainRoute(final ConnectionFactory connectionFactory) {
        ObjectHelper.notNull(connectionFactory, "Connection factory");
    }

    @Override
    public void configure() throws Exception {
        from("jms:topic:foo::bar")
                .to("jdbc:default");

    }

}
