/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.as.quickstarts.util.JMSResourceManager;

/**
 * <p>
 * A simple servlet 3 as client that sends several messages to a queue or a topic.
 * </p>
 *
 * <p>
 * The servlet is registered and mapped to /HelloWorldMDBServletClient using the {@linkplain WebServlet
 * @HttpServlet}.
 * </p>
 *
 * @author Serge Pagop (spagop@redhat.com)
 *
 */
@WebServlet("/HelloWorldMDBServletClientMine")
public class HelloWorldMDBServletClientMine extends HttpServlet {

    private static final long serialVersionUID = -8314035702649252239L;

    private static final int MSG_COUNT = 3;
	
	private static final JMSResourceManager rm = JMSResourceManager.getInstance();

    private String queue = "java:/hello/HelloWorldDQ";
	
	private String topicPerServer = "hello/HelloWorldOneCopyPerServerDT";

    private String topicPerApp = "hello/HelloWorldOneCopyPerApplicationDT";
	
	private String xaConnFactory = "hello/HelloWorldOneCopyPerApplicationCF";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.write("<h1>Quickstart: Example demonstrates the use of <strong>JMS 2.0</strong> and <strong>EJB 3.2 Message-Driven Bean</strong> in JBoss EAP.</h1>");
        try {
            boolean useTopic = req.getParameterMap().keySet().contains("topic");
            final String destination = useTopic ? topicPerServer : queue;

            sendMessage(destination, out);
			
			if(useTopic) {
				sendMessage(topicPerApp, out);
			}
			
            out.write("<p><i>Go to your JBoss EAP server console or server log to see the result of messages processing.</i></p>");
        } catch(Exception e) {
			out.write("Caught exception");
			e.printStackTrace(out);
		} finally {
            if (out != null) {
                out.close();
            }
        }
    }
	
	protected void sendMessage(String destination, PrintWriter out) throws Exception {
		out.write("<p>Sending messages to <em>" + destination + "</em></p>");
		out.write("<h2>The following messages will be sent to the destination:</h2>");
		for (int i = 0; i < MSG_COUNT; i++) {
			String text = "This is messageeeee " + (i + 1);
			
			rm.postMessage(text, destination, xaConnFactory);
			
			out.write("Message (" + i + "): " + text + "</br>");
		}
	}

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
