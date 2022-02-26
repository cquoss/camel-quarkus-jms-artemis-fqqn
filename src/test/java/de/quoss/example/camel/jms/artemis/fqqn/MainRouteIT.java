package de.quoss.example.camel.jms.artemis.fqqn;


import io.quarkus.artemis.test.ArtemisTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

@QuarkusIntegrationTest
@QuarkusTestResource(ArtemisTestResource.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
class MainRouteIT {

    private static final Logger LOGGER = Logger.getLogger(MainRouteIT.class);

    private static final ActiveMQConnectionFactory CONNECTION_FACTORY = new ActiveMQConnectionFactory();

    private static final String JDBC_URL = ConfigProvider.getConfig().getConfigValue("quarkus.datasource.jdbc.url").getValue();

    private static final String BROKER_URL = ConfigProvider.getConfig().getConfigValue("quarkus.artemis.url").getValue();

    @Test
    void testMainRouteDefault() throws Exception {

        queryAddress("foo");

        TimeUnit.SECONDS.sleep(10L);

    }

    private void queryAddress(final String name) throws Exception {
        ServerLocator locator = ActiveMQClient.createServerLocator(BROKER_URL);
        ClientSessionFactory factory = locator.createSessionFactory();
        ClientSession session = factory.createSession();
        ClientSession.AddressQuery query = session.addressQuery(new SimpleString(name));
        LOGGER.infof("Address: %s", name);
        List<SimpleString> queueNames = query.getQueueNames();
        for (SimpleString queueName : queueNames) {
            LOGGER.infof("Address: %s --> Queue: %s", name, queueName);
            ClientSession.QueueQuery queueQuery = session.queueQuery(queueName);
            LOGGER.infof("Address: %s --> Queue: %s --> Auto created: %s", name, queueName, queueQuery.isAutoCreated());
            LOGGER.infof("Address: %s --> Queue: %s --> Durable     : %s", name, queueName, queueQuery.isDurable());
            LOGGER.infof("Address: %s --> Queue: %s --> Exclusive   : %s", name, queueName, queueQuery.isExclusive());
            LOGGER.infof("Address: %s --> Queue: %s --> Routing type: %s", name, queueName, queueQuery.getRoutingType());
        }
    }

}
