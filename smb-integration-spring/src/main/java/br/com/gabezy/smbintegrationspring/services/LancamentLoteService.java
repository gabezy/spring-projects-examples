package br.com.gabezy.smbintegrationspring.services;

import br.com.gabezy.smbintegrationspring.domain.dtos.LoteContabilDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LancamentLoteService {

    private final ObjectMapper objectMapper;

    private static final String DIRECTORY_BASE_PATH ="home/m/Documents/Poupex/arquivos_lote/";
    private static final Logger log = LoggerFactory.getLogger(LancamentLoteService.class);

    public LancamentLoteService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void lancarLoteContabil(LoteContabilDTO dto) {
        criarArquivoInformacoesLote(dto);
    }

    private void criarArquivoInformacoesLote(LoteContabilDTO dto) {
        String nomeArquivo = String.format("%s%s.json",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-ss")), dto.getLote());

        Path filePath = Paths.get(DIRECTORY_BASE_PATH, nomeArquivo);

        try {
            String conteudoFormatado = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(dto);

//            Files.writeString(filePath, conteudoFormatado, StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.error("Unable to write at {}", filePath);
            throw new RuntimeException("Unable to write file");
        }
    }

}
