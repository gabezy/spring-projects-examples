-- CREATE TABLE --
CREATE TABLE SMB_INTEGRATION.ARQUIVO_INTERFACE (
    IDT_ARQUIVO_INTERFACE BIGINT PRIMARY KEY AUTO_INCREMENT,
    NOM_ARQUIVO_INTERFACE VARCHAR(1000) NOT NULL UNIQUE,
    DTH_LOTE VARCHAR(100) NOT NULL,
    NOM_PRODUTO VARCHAR(150) NOT NULL,
    DTH_PROCESSAMENTO TIMESTAMP NOT NULL
);

