package br.com.gabezy.smbintegrationspring.domain.dtos;

import java.util.List;

public class LoteContabilDTO {

    private String data;

    private String lote;

    private String sublote = "001";

    private List<LinhaDTO> linhas;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getSublote() {
        return sublote;
    }

    public List<LinhaDTO> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<LinhaDTO> linhas) {
        this.linhas = linhas;
    }
}
