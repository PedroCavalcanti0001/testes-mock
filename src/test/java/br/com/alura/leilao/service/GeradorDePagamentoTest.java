package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GeradorDePagamentoTest {

    @Mock
    private PagamentoDao pagamentoDao;

    private GeradorDePagamento gerador;

    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);

        gerador = new GeradorDePagamento(pagamentoDao);
    }

    @Test
    void deveriaCriarPagamentoParaVencedorDoLeilao(){
        Leilao leilao = leilao();
        Lance lance = leilao.getLanceVencedor();
        gerador.gerarPagamento(lance);

        Mockito.verify(pagamentoDao).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();

        Assertions.assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());

        Assertions.assertEquals(lance.getValor(), pagamento.getValor());
        Assertions.assertFalse(pagamento.getPago());
        Assertions.assertEquals(lance.getUsuario(), pagamento.getUsuario());
        System.out.println("pagamento leilao "+pagamento);
        Assertions.assertEquals(leilao, pagamento.getLeilao());
    }

    private Leilao leilao() {
        Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));
        List<Lance> lances = new ArrayList<>();;
        Lance lance = new Lance(new Usuario("Beltrano"), new BigDecimal("900"));
        lances.add(lance);
        leilao.setLances(lances);
        leilao.propoe(lance);
        leilao.setLanceVencedor(lance);
        return leilao;
    }

}
