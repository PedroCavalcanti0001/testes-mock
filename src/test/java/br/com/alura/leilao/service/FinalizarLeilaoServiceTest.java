package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FinalizarLeilaoServiceTest {

    private FinalizarLeilaoService service;

    @Mock
    private LeilaoDao leilaoDao;

    @Mock
    private EnviadorDeEmails enviadorDeEmails;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        service = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
    }

    private List<Leilao> leilaoList() {
        List<Leilao> lista = new ArrayList<>();

        Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));
        List<Lance> lances = new ArrayList<>();

        Lance primeiro = new Lance(new Usuario("Fulano"), new BigDecimal("300"));
        Lance segundo = new Lance(new Usuario("Ciclano"), new BigDecimal("600"));
        Lance terceiro = new Lance(new Usuario("Beltrano"), new BigDecimal("900"));
        lances.add(primeiro);
        lances.add(segundo);
        lances.add(terceiro);
        leilao.setLances(lances);
        lista.add(leilao);
        return lista;
    }

    @Test
    void deveriaEnviarEmailParaVencedorDoEmail() {
        List<Leilao> leilaoList = leilaoList();

        Mockito
                .when(leilaoDao.buscarLeiloesExpirados())
                .thenReturn(leilaoList);


        service.finalizarLeiloesExpirados();

        Leilao leilao = leilaoList.get(0);
        Lance vencedor = leilao.getLanceVencedor();
        Mockito.verify(enviadorDeEmails).enviarEmailVencedorLeilao(vencedor);
    }

    @Test
    void naoDeveriaEnviarEmailParaVencedorDoLeilaoEmCasoDeErroAoEncerrarOLeilao() {
        List<Leilao> leilaoList = leilaoList();
        Mockito
                .when(leilaoDao.buscarLeiloesExpirados())
                .thenReturn(leilaoList);
        Mockito.when(leilaoDao.salvar(Mockito.any())).thenThrow(RuntimeException.class);
        try{
            service.finalizarLeiloesExpirados();
            Mockito.verifyNoInteractions(enviadorDeEmails);
        }catch (Exception e){ }


    }


    @Test
    void deveriaFinalizarUmLeilao() {
        List<Leilao> leilaoList = leilaoList();

        Mockito
                .when(leilaoDao.buscarLeiloesExpirados())
                .thenReturn(leilaoList);


        service.finalizarLeiloesExpirados();

        Leilao leilao = leilaoList.get(0);
        Assertions.assertTrue(leilao.isFechado());
        Assertions.assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor());

        Mockito.verify(leilaoDao).salvar(leilao);
    }
}
