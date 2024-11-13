package br.com.gabezy.smbintegrationspring.domain.entity;

import br.com.gabezy.smbintegrationspring.domain.enumerated.Produto;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ARQUIVO_INTERFACE", schema = "SMB_INTEGRATION")
public class ArquivoInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDT_ARQUIVO_INTERFACE", nullable = false)
    private Long id;

    @Column(name = "NOM_ARQUIVO_INTERFACE")
    private String nome;

    @Column(name = "DTH_LOTE")
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(name = "NOM_PRODUTO")
    private Produto produto;

    @Column(name = "DTH_PROCESSAMENTO")
    private LocalDateTime dataProcessamento;

    @PrePersist
    public void setDataProcessamento() {
        this.dataProcessamento = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }
}
