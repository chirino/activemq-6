# Spring Integration

Apache ActiveMQ provides a simple bootstrap class,
`org.apache.activemq.integration.spring.SpringJmsBootstrap`, for
integration with Spring. To use it, you configure Apache ActiveMQ as you always
would, through its various configuration files like
`activemq-configuration.xml`, `activemq-jms.xml`, and
`activemq-users.xml`. The Spring helper class starts the Apache ActiveMQ server
and adds any factories or destinations configured within
`activemq-jms.xml` directly into the namespace of the Spring context.
Let's take this `activemq-jms.xml` file for instance:

    <configuration xmlns="urn:activemq"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="urn:activemq /schema/activemq-jms.xsd">

       <!--the queue used by the example-->
       <queue name="exampleQueue"/>
    </configuration>

Here we've specified a `javax.jms.ConnectionFactory` we want bound to a
`ConnectionFactory` entry as well as a queue destination bound to a
`/queue/exampleQueue` entry. Using the `SpringJmsBootStrap` bean will
automatically populate the Spring context with references to those beans
so that you can use them. Below is an example Spring JMS bean file
taking advantage of this feature:

    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

       <bean id="EmbeddedJms" class="org.apache.activemq.integration.spring.SpringJmsBootstrap" init-method="start"/>

       <bean id="listener" class="org.apache.activemq.tests.integration.spring.ExampleListener"/>

       <bean id="listenerContainer" class="org.springframework.jms.listener.DefaultMessageListenerContainer">
          <property name="connectionFactory" ref="ConnectionFactory"/>
          <property name="destination" ref="/queue/exampleQueue"/>
          <property name="messageListener" ref="listener"/>
       </bean>
    </beans>

As you can see, the `listenerContainer` bean references the components
defined in the `activemq-jms.xml` file. The `SpringJmsBootstrap` class
extends the EmbeddedJMS class talked about in [JMS API](embedding-activemq.md) and the same defaults
and configuration options apply. Also notice that an `init-method` must
be declared with a start value so that the bean's lifecycle is executed.
See the javadocs for more details on other properties of the bean class.
