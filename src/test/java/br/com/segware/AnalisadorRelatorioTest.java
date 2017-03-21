package br.com.segware;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class AnalisadorRelatorioTest {

    IAnalisadorRelatorio analisador;

    @Before
    public void before() throws IOException, URISyntaxException {
        // analisador = sua implementação
        analisador = new AnalisadorRelatorioImpl();
        File arquivo = new File("src/test/java/br/com/segware/relatorio.csv");
        analisador.processaArquivo(arquivo);        
    }

    @Test
    public void totalDeEventosDoCliente0001() {
        assertEquals(7, analisador.getTotalEventosCliente().get("0001"), 0);
    }

    @Test
    public void totalDeEventosDoCliente0003() {
        assertEquals(3, analisador.getTotalEventosCliente().get("0003"), 0);
    }

    @Test
    public void tempoMedioDeAtendimentoEmSegundosDoAtendenteAT01() {
        assertEquals(159, analisador.getTempoMedioAtendimentoAtendente().get("AT01"), 0);
    }

    @Test
    public void tempoMedioDeAtendimentoEmSegundosDoAtendenteAT02() {
        assertEquals(156, analisador.getTempoMedioAtendimentoAtendente().get("AT02"), 0);
    }

    @Test
    public void tipoComMaisEventos() {
        assertArrayEquals(new Tipo[] { Tipo.ALARME, Tipo.DESARME, Tipo.TESTE, Tipo.ARME },
                analisador.getTiposOrdenadosNumerosEventosDecrescente().toArray(new Tipo[Tipo.values().length]));
    }

    @Test
    public void identificarEvento() {
        assertArrayEquals(new Integer[] { 7 }, analisador.getCodigoSequencialEventosDesarmeAposAlarme().toArray(new Integer[1]));
    }
}