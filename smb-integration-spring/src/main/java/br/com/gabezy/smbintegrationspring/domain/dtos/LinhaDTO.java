package br.com.gabezy.smbintegrationspring.domain.dtos;

public class LinhaDTO {

    private String contaDebito;
    private String contaCredito;
    private String centroCustoDebito;
    private String centroCustoCredito;
    private String historico;
    private Double valor;

    public String getContaDebito() {
        return contaDebito;
    }

    public void setContaDebito(String contaDebito) {
        this.contaDebito = contaDebito;
    }

    public String getContaCredito() {
        return contaCredito;
    }

    public void setContaCredito(String contaCredito) {
        this.contaCredito = contaCredito;
    }

    public String getCentroCustoDebito() {
        return centroCustoDebito;
    }

    public void setCentroCustoDebito(String centroCustoDebito) {
        this.centroCustoDebito = centroCustoDebito;
    }

    public String getCentroCustoCredito() {
        return centroCustoCredito;
    }

    public void setCentroCustoCredito(String centroCustoCredito) {
        this.centroCustoCredito = centroCustoCredito;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
