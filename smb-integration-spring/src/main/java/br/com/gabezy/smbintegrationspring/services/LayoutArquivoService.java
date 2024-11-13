package br.com.gabezy.smbintegrationspring.services;

import br.com.gabezy.smbintegrationspring.domain.enumerated.Produto;
import br.com.gabezy.smbintegrationspring.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LayoutArquivoService {

    private static final int POS_INI_CODIGO_PRODUTO = 16;
    private static final int POS_INI_CONTA = 33 - 1;
    private static final int POS_INI_CENTRO_CUSTO = 48 - 1;
    private static final int POS_INI_NATUREZA = 82 - 1;
    private static final int POS_INI_DATA = 102 - 1;
    private static final int POS_INI_VALOR = 110 - 1;
    private static final int POS_INI_HISTORICO = 143 - 1;
    private static final int POS_FIN_CODIGO_PRODUTO = 19;
    private static final int POS_FIN_CONTA = 47;
    private static final int POS_FIN_CENTRO_CUSTO = 51;
    private static final int POS_FIN_NATUREZA = 82;
    private static final int POS_FIN_DATA = 109;
    private static final int POS_FIN_VALOR = 124;
    private static final int POS_FIN_HISTORICO = 350;

    public Produto getProduto(String linha) {
        String codigo = linha.substring(POS_INI_CODIGO_PRODUTO, POS_FIN_CODIGO_PRODUTO);
        return Produto.getByCodigo(codigo);
    }

    public LocalDate getData(String linha) {
        return DateUtils.converteStringParaLocalDate(linha.substring(POS_INI_DATA, POS_FIN_DATA));
    }

    public String getConta(String linha) {
        return linha.substring(POS_INI_CONTA, POS_FIN_CONTA);
    }

    public String getCentroCusto(String linha) {
        int centroCusto = Integer.parseInt(linha.substring(POS_INI_CENTRO_CUSTO, POS_FIN_CENTRO_CUSTO));
        return Integer.toString(centroCusto);
    }

    public String getNatureza(String linha) {
        return linha.substring(POS_INI_NATUREZA, POS_FIN_NATUREZA);
    }

    public Double getValor(String linha) {
        String valor = limparZerosEsquerda(linha.substring(POS_INI_VALOR, POS_FIN_VALOR));
        return Double.parseDouble(valor) * 0.01;
    }

    public String getHistorico(String linha) {
        return linha.substring(POS_INI_HISTORICO, POS_FIN_HISTORICO).trim();
    }

    public LocalDate getDataByNomeArquivo(String nomeArquivo) {
        return DateUtils.converteStringParaLocalDate(nomeArquivo.substring(4,12));
    }

    public Produto getProdutoByNomeArquivo(String nomeArquivo) {
        String produtoNome = nomeArquivo.substring(2, 4);
        return Produto.valueOf(produtoNome);
    }

    private String limparZerosEsquerda(String valor) {
        return valor.replaceFirst("^0+", "");
    }

}
