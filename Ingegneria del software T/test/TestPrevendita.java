package test.dominio;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestPrevendita {

    private Prevendita prevendita;

    //Classi aggiunte che sono associate a Prevendita, anche indirettamente
    private Utente utente;

    private Staff staff;
    private PR pr;

    private Evento evento;
    private TipologiaPrevendita tipologiaPrevendita;

    //Sono usati nei setters.
    private Utente utente2;
    private PR pr2;

    private Evento evento2;
    private TipologiaPrevendita tipologiaPrevendita2;


    /**
     * Inizializzazione di una prova di Prevendita.
     * Sono necessari molte classi accessorie per creare una istanza di Prevendita.
     */
    @Before
    public void utenteSetup(){
        utente = new Utente("Marcello", "Rossi", "+39333222154", "pippo", "123456");
        staff = new Staff("Staff1", "12345");
        pr = new PR(utente, staff, new Ruolo[]{Ruolo.PR});

        utente2 = new Utente("Mario", "Rossi", "+39333212154", "pippo2", "1234567");
        pr2 = new PR(utente2, staff, new Ruolo[]{Ruolo.PR});

        evento = new Evento("Evento1", "Desc",
					new PeriodoTemporale("03/05/2020 16:00", "03/05/2020 18:00"), "Bologna", staff);

        tipologiaPrevendita = new TipologiaPrevendita("Tip1", "Desc2", 10.0,
					new PeriodoTemporale("03/05/2020 13:00", "03/05/2020 15:00"), evento);

        evento2 = new Evento("Evento2", "Desc",
					new PeriodoTemporale("03/05/2020 16:00", "03/05/2020 18:00"), "Bologna", staff);
        tipologiaPrevendita2 = new TipologiaPrevendita("Tip2", "Desc3", 10.0,
					new PeriodoTemporale("03/05/2020 13:00", "03/05/2020 15:00"), evento2);

        prevendita = new Prevendita("Luciano", "Rossi", pr, tipologiaPrevendita);
    }


    /**
     * Test dei getter di Prevendita.
     */
    @Test
    public void gettersTest(){
		//Equals Nome Cliente
        assertEquals("Luciano", prevendita.getNomeCliente());
		//Equals Cognome Cliente
        assertEquals("Rossi", prevendita.getCognomeCliente());

        //Questi equals sono da testare nelle classi di test apposite.

		//Equals PR
        assertEquals(pr, prevendita.getPRAssociato());
		//Equals Tipologia Prevendita
        assertEquals(tipologiaPrevendita, prevendita.getTipologiaPrevenditaAssociata());
    }

    /**
     * Test dei setter di Prevendita.
     * Se nella progettazione viene descritta come classe immutabile, ignorare questo test.
     */
    @Test
    public void settersTest(){

        prevendita.setNome("Franco");
        prevendita.setCognome("Franco");
        prevendita.setPR(pr2);
        prevendita.setTipologiaPrevendita(tipologiaPrevendita2);

		//Equals Nome Cliente
        assertEquals("Franco", prevendita.getNomeCliente());
		//Equals Cognome Cliente
        assertEquals("Franco", prevendita.getCognomeCliente());

        //Questi equals sono da testare nelle classi di test apposite.

		//Equals PR
        assertEquals(pr2, prevendita.getPRAssociato());
		//Equals Tipologia Prevendita
        assertEquals(tipologiaPrevendita2, prevendita.getTipologiaPrevenditaAssociata());
    }


}
