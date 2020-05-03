package test.dominio;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestEvento {

    private Evento evento;
	
	//La classe rappresenta un periodo temporale.
	//Nella implementazione potrebbe variare
	private PeriodoTemporale periodoTemporale;	
	
	//Classi aggiunte che sono associate a Evento, anche indirettamente
	private Staff staff;
	private Staff staff2;
	
	private PeriodoTemporale periodoTemporale2;

    /**
     * Inizializzazione di una prova di Evento.
     */
    @Before
    public void utenteSetup(){
		staff = new Staff("Staff1", "12345");
		periodoTemporale = new PeriodoTemporale("03/05/2020 16:00", "03/05/2020 18:00");
        evento = new Evento("Evento1", "Desc", periodoTemporale, "Bologna", staff);
		
		staff2 = new Staff("Staff2", "123456");
		periodoTemporale2 = new PeriodoTemporale("03/05/2020 15:00", "03/05/2020 15:30");
    }


    /**
     * Test dei getter di Evento.
     */
    @Test
    public void gettersTest(){
		assertEquals("Evento1", evento.getNome());
		assertEquals("Desc", evento.getDescrizione());
		assertEquals(periodoTemporale, evento.getPeriodoTemporale());
		assertEquals("Bologna", evento.getLuogo());
		
		//Questo Equals va testato con classe di test a parte.
		
		assertEquals(staff, evento.getStaffAssociato());
		
    }

    /**
     * Test dei setter di Evento.
     * Se nella progettazione viene descritta come classe immutabile, ignorare questo test.
     */
    @Test
    public void settersTest(){
		evento.setNome("Evento2");
		evento.setDescrizione("Desc2");
		evento.setPeriodoTemporale(periodoTemporale2);
		evento.setLuogo("Milano");
		evento.setStaff(staff2);
		
		assertEquals("Evento2", evento.getNome());
		assertEquals("Desc2", evento.getDescrizione());
		assertEquals(periodoTemporale2, evento.getPeriodoTemporale());
		assertEquals("Milano", evento.getLuogo());
		
		//Questo Equals va testato con classe di test a parte.
		
		assertEquals(staff2, evento.getStaffAssociato());
		
    }


}
