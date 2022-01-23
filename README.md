# JBoss JMS Clustering 
Example project that demonstrates JBoss EAP JMS message clustering and shared subscriptions

Used JBoss EAP 7.3 quickstarts project as base example application and modified and added multiple MDB that uses Shared subscription for Topic to demonstrate WebLogic topicMessagesDistributionMode = One-Copy-Per-Application behaviour using JMS 2.0

There are 2 projects
1, helloworld-mdb - This is actual MDB code. This code is modified to add new classes.
2, messaging-clustering - contains scripts that creates Domain servers or standalone servers with clustering. This has no changes from original JBoss code.

domain.xml and host.xml - these are modifed files as generated by scripts in messaging-clustering install-domain.cli. Additionally TOpics/Queues used in testing are added in domain.xml.


