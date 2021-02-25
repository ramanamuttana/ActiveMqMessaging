
package org.demo.Pu;

import org.fusesource.stomp.jms.*;

import java.util.Scanner;

import javax.jms.*;

class Publisher1 {

	public static void main(String []args) throws JMSException {

        String user = env("ACTIVEMQ_USER", "admin");
        String password = env("ACTIVEMQ_PASSWORD", "admin");
        String host = env("ACTIVEMQ_HOST", "localhost");
        int port = Integer.parseInt(env("ACTIVEMQ_PORT", "61613"));
        String destination = arg(args, 0, "/topic/videoAlert");

        StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
        factory.setBrokerURI("tcp://" + host + ":" + port);

        Connection connection = factory.createConnection(user, password);
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = new StompJmsDestination(destination);
        MessageProducer producer = session.createProducer(dest);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

       
        Scanner scanner = new Scanner(System.in);
        
        int i=0;  
        while(true) {
        	String s = scanner.nextLine();
        	if(s!=null) {
            if(s instanceof String) {
               // System.out.printf("created message : ", s);
                TextMessage msg = session.createTextMessage(s);
                msg.setIntProperty("id", i);
                   if(i==100) {
                      producer.send(session.createTextMessage("SHUTDOWN"));
                      connection.close();
                      scanner.close();
                   } 
                producer.send(msg);
              //  System.out.println(s);
                i++;
            }
            
           /* if( (i % 2000) == 0) {
                System.out.println(String.format("Sent %d messages", i));
            }*/
        }else {
        	
        }
        	
        }	
        }

	private static String env(String key, String defaultValue) {
		String rc = System.getenv(key);
		if (rc == null)
			return defaultValue;
		return rc;
	}

	private static String arg(String[] args, int index, String defaultValue) {
		if (index < args.length)
			return args[index];
		else
			return defaultValue;
	}

}