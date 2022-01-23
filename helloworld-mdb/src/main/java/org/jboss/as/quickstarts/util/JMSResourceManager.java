package org.jboss.as.quickstarts.util;

import javax.jms.Destination;
import javax.jms.XAConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.jms.TransactionInProgressException;
import javax.jms.XAConnection;
import javax.jms.XASession;

import java.util.logging.Logger;

public class JMSResourceManager {
	
	private static final Logger LOGGER = Logger.getLogger(JMSResourceManager.class.toString());

	private Context context;
	private static final JMSResourceManager INSTANCE = new JMSResourceManager();
	
	private static final String DESTINATION_CONN_FACOTRY_KEY = "destinationConnFacotry";
	private static final String DESTINATION_KEY = "destination";
	private static final String DESTINATION_CONNECTION_KEY = "destinationConnection";
	private static final String DESTINATION_SESSION_KEY = "destinationSession";
	private static final String DESTINATION_SENDER_KEY = "destinationSender";
	
	private JMSResourceManager() {
		try {
			context = new InitialContext();
		} catch (NamingException e) {
			LOGGER.warning("Not able to obtain the initial context");
		}
	}

	public static JMSResourceManager getInstance() {
		return INSTANCE;
	}

	void setContext(Context context) {
		this.context = context;
	}
	
	Destination getDestination(String jndiName) {
		Destination destination = null;

		try {
			destination = (Destination) context.lookup(jndiName);
		} catch (Exception e) {
			LOGGER.warning("Could not obtain destination from the context : "
					+ jndiName);
		}
		return destination;
	}

	
	XAConnectionFactory getXAConnFactory(String jndiName) {
		XAConnectionFactory xaConnFactory = null;

		try {
			xaConnFactory = (XAConnectionFactory) context.lookup(jndiName);
		} catch (Exception excep) {
			LOGGER.warning("Could not obtain XA connection facotry from the context : "
					+ jndiName);
		}
		return xaConnFactory;
	}
	
	public boolean postMessage(String message,
			String destination, String xaConnFactory) throws Exception {
	      	
		Map<String, Object> resourceMap = null;
		try {

			resourceMap = aquireResources(destination, xaConnFactory, message);
			postMessage(message, destination, xaConnFactory, resourceMap);

		} finally {
			try {
				cleanUpResources(resourceMap);
			} catch (JMSException exception) {
				LOGGER.warning("Could not clear JMS resources. " + exception.getMessage());
				throw new Exception(exception);
			}
		}
		resourceMap = null;
		return true;
	}
	
	private boolean postMessage(String message,
			String topic, String topicConnFactory,
			Map<String, Object> resourceMap) throws Exception {
			TextMessage textMessage = null;
		try {
			XASession topicSession = (XASession) resourceMap
					.get(DESTINATION_SESSION_KEY);

			textMessage = topicSession.createTextMessage(message);
						
		} catch (Exception exception) {
			LOGGER.warning("could not create object message : " + topic
					+ "Error Message : " + exception.getMessage());
			throw new Exception(exception);
		}
		try {
			MessageProducer topicPublisher = (MessageProducer) resourceMap
					.get(DESTINATION_SENDER_KEY);
			topicPublisher.send(textMessage);
		} catch (JMSException exception) {
			LOGGER.warning("could not send the message to the given destination : "
					+ topic + "Error Message : " + exception.getMessage());
			throw new Exception(exception);
		}
		return true;
	}

	Map<String, Object> aquireResources(
			String destinationName, String xaConnFacotryName, String message)
			throws Exception {

		Map<String, Object> resourceMap = new WeakHashMap<String, Object>();
		XAConnectionFactory topicConnFactory = INSTANCE
				.getXAConnFactory(xaConnFacotryName);
		
		Destination destination = INSTANCE
				.getDestination(destinationName);

		resourceMap.put(DESTINATION_CONN_FACOTRY_KEY, topicConnFactory);
		resourceMap.put(DESTINATION_KEY, destination);

		try {
			XAConnection xaConnection = topicConnFactory.createXAConnection();
			
			XASession xaSession = xaConnection.createXASession();

			int priority = 4;// Default Priority for JMS
			
			MessageProducer producer = xaSession.createProducer(destination);
			producer.setPriority(priority);
			Long timeToLive = 0l;
			
			producer.setTimeToLive(timeToLive);
			resourceMap.put(DESTINATION_CONNECTION_KEY, xaConnection);
			resourceMap.put(DESTINATION_SESSION_KEY, xaSession);
			resourceMap.put(DESTINATION_SENDER_KEY, producer);

		} catch (JMSException excep) {
			LOGGER.warning("could not create JMS resources to send the message");
			throw new Exception(excep);
		}
		return resourceMap;
	}

	void cleanUpResources(Map<String, Object> resourceMap) throws JMSException {
		try {
			
			MessageProducer producer = (MessageProducer) resourceMap
					.get(DESTINATION_SENDER_KEY);
			if (!isEmpty(producer))
				producer.close();

			XAConnection xaConnection = (XAConnection) resourceMap
					.get(DESTINATION_CONNECTION_KEY);
			if (!isEmpty(xaConnection))
				xaConnection.close();
		} catch (JMSException jmsException) {
			LOGGER.warning("Unknow exception " + jmsException.getMessage());
		}
	}
	
	private boolean isEmpty(Object obj) {
		return (null == obj || "".equals(obj.toString()));
	}
}
