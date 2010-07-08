package com.db4odoc.clientserver.messaging;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ClientConfiguration;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.messaging.MessageContext;
import com.db4o.messaging.MessageRecipient;
import com.db4o.messaging.MessageSender;


public class MessagingExample {
    private static final String DATABASE_FILE = "database.db4o";
    private static final int PORT_NUMBER = 1337;
    private static final String USER_AND_PASSWORD = "sa";

    public static void main(String[] args) {
        messagingExample();
    }

    private static void messagingExample() {
        ObjectServer server = startUpServer();

        ClientContainerAndMessageSender client = startClient();
        MessageSender sender = client.getSender();

        sender.send(new HelloMessage("Hi Server!"));

        waitForAWhile();
        ObjectContainer container = client.getContainer();
        container.commit();
        container.close();


        server.close();
    }

    private static void waitForAWhile() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static ClientContainerAndMessageSender startClient() {
        ClientConfiguration configuration = Db4oClientServer.newClientConfiguration();
        configuration.networking().messageRecipient(new MessageRecipient() {
            public void processMessage(MessageContext messageContext, Object o) {
                System.out.println("The client received a '"+o+"' message");
            }
        });
        MessageSender messageSender = configuration.messageSender();
        ObjectContainer container = Db4oClientServer.openClient(configuration, "localhost",PORT_NUMBER, USER_AND_PASSWORD,USER_AND_PASSWORD);
        return new ClientContainerAndMessageSender(messageSender, container);
    }

    private static ObjectServer startUpServer() {
        ServerConfiguration configuration = Db4oClientServer.newServerConfiguration();
        configuration.networking().messageRecipient(new MessageRecipient() {
            public void processMessage(MessageContext messageContext, Object o) {
                System.out.println("The server received a '"+o+"' message");
                messageContext.sender().send(new HelloMessage("Hi Client!"));
            }
        });
        ObjectServer server = Db4oClientServer.openServer(configuration, DATABASE_FILE, PORT_NUMBER);
        server.grantAccess(USER_AND_PASSWORD, USER_AND_PASSWORD);
        return server;
    }


    private static class ClientContainerAndMessageSender{
        private final MessageSender sender;
        private final ObjectContainer container;

        private ClientContainerAndMessageSender(MessageSender sender, ObjectContainer container) {
            this.sender = sender;
            this.container = container;
        }

        public MessageSender getSender() {
            return sender;
        }

        public ObjectContainer getContainer() {
            return container;
        }
    }
}
