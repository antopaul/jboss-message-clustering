# JBoss JMS Clustering 
Example project that demonstrates JBoss EAP JMS message clustering and shared subscriptions

Used JBoss EAP 7.3 quickstarts project as base example application and modified and added multiple MDB that uses Shared subscription for Topic to demonstrate WebLogic topicMessagesDistributionMode = One-Copy-Per-Application behaviour using JMS 2.0

There are 2 projects
- helloworld-mdb - This is actual MDB code. This code is modified to add new classes.
- messaging-clustering - contains scripts that creates Domain servers or standalone servers with clustering. This has no changes from original JBoss code.

domain.xml and host.xml are taken from EAP 7.4 - these are modifed files as generated by scripts in messaging-clustering install-domain.cli.  

Running install-domain.cli adds below entries. NOTE: Running remove-domain.cli does not remove below entires. Please remove those manually.  

```<jms-queue name="HelloWorld_DQ" entries="java:/hello/HelloWorldDQ"/>
<jms-topic name="HelloWorld_DT" entries="java:/hello/HelloWorldDT"/>
<jms-topic name="HelloWorldOneCopyPerServer_DT" entries="java:/hello/HelloWorldOneCopyPerServerDT"/>
<jms-topic name="HelloWorldOneCopyPerApplication_DT" entries="java:/hello/HelloWorldOneCopyPerApplicationDT"/>
<pooled-connection-factory name="HelloWorldOneCopyPerServer_CF" entries="java:/hello/HelloWorldOneCopyPerServerCF" connectors="in-vm"/>
<pooled-connection-factory name="HelloWorldOneCopyPerApplication_CF" entries="java:/hello/HelloWorldOneCopyPerApplicationCF" connectors="in-vm"/>
```


To test application access URL - http://localhost:9080/helloworld-mdb/HelloWorldMDBServletClientMine?topic  

Then check server.log files of servers located in jboss-eap-7.4\domain\servers directory.

Server logs  
**quickstart-messagingcluster-node1**
```
2022-01-23 22:28:42,178 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerServerTopicMDBOne] (Thread-1 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 1
2022-01-23 22:28:42,178 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerServerTopicMDBTwo] (Thread-3 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 1
2022-01-23 22:28:42,206 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerServerTopicMDBTwo] (Thread-10 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 2
2022-01-23 22:28:42,207 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerServerTopicMDBOne] (Thread-9 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 2
2022-01-23 22:28:42,216 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerServerTopicMDBTwo] (Thread-5 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 3
2022-01-23 22:28:42,216 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerServerTopicMDBOne] (Thread-0 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 3
2022-01-23 22:28:42,228 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerAppTopicMDBOne] (Thread-11 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 1
2022-01-23 22:28:42,229 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerAppTopicMDBTwo] (Thread-4 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 1
2022-01-23 22:28:42,261 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerAppTopicMDBOne] (Thread-2 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 3
2022-01-23 22:28:42,262 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerAppTopicMDBTwo] (Thread-6 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 3
```
**quickstart-messagingcluster-node2**
```
2022-01-23 22:28:42,315 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerAppTopicMDBTwo] (Thread-7 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 2
2022-01-23 22:28:42,316 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerAppTopicMDBOne] (Thread-6 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 2
2022-01-23 22:28:42,314 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerServerTopicMDBOne] (Thread-3 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 2
2022-01-23 22:28:42,315 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerServerTopicMDBOne] (Thread-1 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 3
2022-01-23 22:28:42,314 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerServerTopicMDBTwo] (Thread-4 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 1
2022-01-23 22:28:42,314 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerServerTopicMDBOne] (Thread-2 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 1
2022-01-23 22:28:42,315 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerServerTopicMDBTwo] (Thread-0 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 2
2022-01-23 22:28:42,314 INFO  [class org.jboss.as.quickstarts.mdb.HelloOneCopyPerServerTopicMDBTwo] (Thread-5 (ActiveMQ-client-global-threads)) Received Message from topic: This is messageeeee 3
```
