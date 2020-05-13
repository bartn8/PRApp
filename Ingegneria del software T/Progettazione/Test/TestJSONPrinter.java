package it.unibo.prevenditaelettronica.middleware.view;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestJSONPrinter {

	//Classe da testare
    private JSONPrinter printer;
	
	//Serve per simulare una connessione HTTPS, per leggere il risultato JSON prodotto.
	private HTTPSInterfaceMock httpsMock;

    /**
     * Inizializzazione di una prova di JSONPrinter.
     */
    @Before
    public void setup(){
		httpsMock = new HTTPSInterfaceMock();
		printer = new JSONPrinter(httpsMock);
    }


    /**
     * Test di una stampa JSON di un risultato.
     */
    @Test
    public void printResultTest(){
		
		//Imposto la printer
		printer.reset();
		printer.setComando(Comando.ECHO);
		printer.setStatus(Stato.OK);
		printer.setResult(null);
		printer.printResponse();
		
		String sniffResponseJSON = httpsMock.sniffing();
		
		assertEquals("{comando:\"ECHO\", stato:\"OK\", risultato:null}", sniffResponseJSON);
		
		//Test Comando non nullo
		Timestamp now =  Timestamp.now();
		
		//Imposto la printer
		printer.reset();
		printer.setComando(Comando.TIMESTAMP);
		printer.setStatus(Stato.OK);
		printer.setResult(now);
		printer.printResponse();
		
		String sniffResponseJSON = httpsMock.sniffing();
		
		assertEquals("{comando:\"TIMESTAMP\", stato:\"OK\", risultato:\""+ now.toString() +"\"}", sniffResponseJSON);
    }

     /**
     * Test di una stampa JSON di una eccezione
     */
    @Test
    public void printExceptionTest(){
		
		//Imposto la printer
		printer.reset();
		printer.setComando(Comando.ECHO);
		printer.setStatus(Stato.EXCEPTION);
		printer.setException(new Exception("Prova"));
		printer.printResponse();
		
		String sniffResponseJSON = httpsMock.sniffing();
		
		assertEquals("{comando:\"ECHO\", stato:\"EXCEPTION\", exception:{cause:\"Prova\"}}", sniffResponseJSON);
    }


}
