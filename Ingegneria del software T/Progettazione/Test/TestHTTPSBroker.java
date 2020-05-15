package it.unibo.prevenditaelettronica.frontend.model.net.broker;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestHTTPSBroker {

   //Serie di oggetti finti per simulare un servizio middleware
   private MiddlewareFakeService serviceA;
   private MiddlewareFakeService serviceB;
   private MiddlewareFakeService serviceC;
   
   //Classe da testare
   private HTTPBrokerInterface broker;
  
    /**
     * Inizializzazione di una prova di HTTPSBroker.
     */
    @Before
    public void setup(){
       //Inizializzazione oggetti mock: ogniuno in listen su una porta locale diversa.
	   serviceA = new MiddlewareFakeService("https://localhost:8080");
	   serviceB = new MiddlewareFakeService("https://localhost:8081");
	   serviceC = new MiddlewareFakeService("https://localhost:8082");
	   
	   //Inizializzazione broker
	   broker = new HTTPSBroker();
    }


    /**
     * Test del broker a vuoto.
     */
    @Test
    public void voidBrokerTest(){
		
		//Non registro i servizi e vedo cosa restituisce
		assertEquals(null, broker.procuraCanale());
    }
	
	/**
     * Test del broker con la (de)registrazione dei servizi
     */
    @Test
    public void registerServiceBrokerTest(){
		
		broker.registraServizio("https://localhost:8080", ServicePriority.PRIMARY);
		broker.registraServizio("https://localhost:8080", ServicePriority.BACKUP);
		broker.registraServizio("https://localhost:8080", ServicePriority.LOCAL);
		
		//Dovrebbe restituire il primario
		assertEquals("https://localhost:8080", broker.procuraCanale().getUrl());
		
		//Stacco il servizio primario e rifaccio il test
		broker.disdiciServizio("https://localhost:8080");
		assertEquals("https://localhost:8081", broker.procuraCanale().getUrl());
		
		//Stacco il servizio backup e rifaccio il test
		broker.disdiciServizio("https://localhost:8081");
		assertEquals("https://localhost:8082", broker.procuraCanale().getUrl());
    }

	/**
     * Test del broker simulando un attacco DoS.
     */
    @Test
    public void dosServiceBrokerTest(){
		
		broker.registraServizio("https://localhost:8080", ServicePriority.PRIMARY);
		broker.registraServizio("https://localhost:8080", ServicePriority.BACKUP);
		broker.registraServizio("https://localhost:8080", ServicePriority.LOCAL);
		
		//Dovrebbe restituire il primario
		assertEquals("https://localhost:8080", broker.procuraCanale().getUrl());
		
		//Simulo un attacco sul servizio primario e rifaccio il test
		serviceA.dos();
		assertEquals("https://localhost:8081", broker.procuraCanale().getUrl());
		
		//Simulo un attacco sul servizio backup e rifaccio il test
		serviceB.dos();
		assertEquals("https://localhost:8081", broker.procuraCanale().getUrl());
		
		//Simulo un attacco sul servizio local e rifaccio il test
		serviceC.dos();
		assertEquals(null, broker.procuraCanale().getUrl());
    }

}
