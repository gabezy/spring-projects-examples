package br.com.gabezy.smbintegrationspring.services;

import br.com.gabezy.smbintegrationspring.domain.dtos.LinhaDTO;
import br.com.gabezy.smbintegrationspring.domain.dtos.LoteContabilDTO;
import br.com.gabezy.smbintegrationspring.domain.entity.ArquivoInterface;
import br.com.gabezy.smbintegrationspring.domain.enumerated.Produto;
import br.com.gabezy.smbintegrationspring.repositories.ArquivoInterfaceRepository;
import br.com.gabezy.smbintegrationspring.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ArquivoInterfaceProcessingService {

    private final LayoutArquivoService layoutArquivoService;
    private final LancamentLoteService lancamentLoteService;
    private final ArquivoInterfaceRepository arquivoInterfaceRepository;

    public ArquivoInterfaceProcessingService(LayoutArquivoService layoutArquivoService, LancamentLoteService lancamentLoteService,
                                             ArquivoInterfaceRepository arquivoInterfaceRepository) {
        this.layoutArquivoService = layoutArquivoService;
        this.lancamentLoteService = lancamentLoteService;
        this.arquivoInterfaceRepository = arquivoInterfaceRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(ArquivoInterfaceProcessingService.class);

    public void processArquivInterface(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            log.info("Staring process file: {} ...", file.getName());

            List<LinhaDTO> linhasProcessadas = new ArrayList<>();

            LocalDate dataLote = null;
            Produto produto = null;

            boolean primeiraLinha = true;

            String linha;


            while ((linha = reader.readLine()) != null) {

                if (primeiraLinha) {
                    dataLote = layoutArquivoService.getData(linha);
                    produto = layoutArquivoService.getProduto(linha);
                    primeiraLinha = false;
                }

                var linhaDTO = new LinhaDTO();

                String conta = layoutArquivoService.getConta(linha);
                String centroCusto = layoutArquivoService.getCentroCusto(linha);

                if (layoutArquivoService.getNatureza(linha).equals("C")) {
                    linhaDTO.setContaCredito(conta);
                    linhaDTO.setCentroCustoCredito(centroCusto);
                } else {
                    linhaDTO.setContaDebito(conta);
                    linhaDTO.setCentroCustoDebito(centroCusto);
                }

                linhaDTO.setValor(layoutArquivoService.getValor(linha));
                linhaDTO.setHistorico(layoutArquivoService.getHistorico(linha));

                linhasProcessadas.add(linhaDTO);
            }

            var arquivoInterface = new ArquivoInterface();
            arquivoInterface.setNome(file.getAbsolutePath());

            if (Objects.nonNull(dataLote) && Objects.nonNull(produto)) {
                arquivoInterface.setData(dataLote);
                arquivoInterface.setProduto(produto);

                lancamentLoteService.lancarLoteContabil(mapToDTO(arquivoInterface, linhasProcessadas));
            } else {
                arquivoInterface.setData(layoutArquivoService.getDataByNomeArquivo(file.getName()));
                arquivoInterface.setProduto(layoutArquivoService.getProdutoByNomeArquivo(file.getName()));
            }

            arquivoInterfaceRepository.save(arquivoInterface);
        } catch (IOException ex) {
            log.error("Error while processing file: {}", file, ex);
        }
    }

    private LoteContabilDTO mapToDTO(ArquivoInterface arquivoInterface, List<LinhaDTO> linhas) {
        var loteContabilDTO = new LoteContabilDTO();
        loteContabilDTO.setData(DateUtils.converteLocalDateParaProtheus(arquivoInterface.getData()));
        loteContabilDTO.setLote(arquivoInterface.getProduto().getLote());
        loteContabilDTO.setLinhas(linhas);
        return  loteContabilDTO;
    }

}
