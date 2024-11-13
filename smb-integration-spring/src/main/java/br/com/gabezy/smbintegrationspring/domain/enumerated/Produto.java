package br.com.gabezy.smbintegrationspring.domain.enumerated;

import java.util.Arrays;

public enum Produto {

    ES("550", "EMS"),
    EF("555", "EMF"),
    FD("565", "FEL"),
    EC("560", "ESC"),
    CR("570", "PJE"),
    CF("575", "PJF");

    private final String lote;
    private final String codigo;

    Produto(String lote, String codigo) {
        this.lote = lote;
        this.codigo = codigo;
    }

    public String getLote() {
        return lote;
    }

    public String getCodigo() {
        return codigo;
    }

    public static Produto getByCodigo(String codigo) {
        return Arrays.stream(Produto.values())
                .filter(empresa -> empresa.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Código do produto inválido: " + codigo));
    }
}
