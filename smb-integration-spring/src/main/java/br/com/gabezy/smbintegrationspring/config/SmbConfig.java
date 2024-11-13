package br.com.gabezy.smbintegrationspring.config;

import br.com.gabezy.smbintegrationspring.config.properties.SmbProperties;
import br.com.gabezy.smbintegrationspring.services.ArquivoInterfaceProcessingService;
import jcifs.DialectVersion;
import jcifs.smb.SmbFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.smb.filters.SmbRegexPatternFileListFilter;
import org.springframework.integration.smb.inbound.SmbInboundFileSynchronizer;
import org.springframework.integration.smb.inbound.SmbInboundFileSynchronizingMessageSource;
import org.springframework.integration.smb.session.SmbSessionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Configuration
public class SmbConfig {

    @Autowired
    private SmbProperties smbProperties;

    @Autowired
    private ArquivoInterfaceProcessingService arquivoInterfaceProcessingService;

    @Bean
    public SmbSessionFactory smbSessionFactory() {
        SmbSessionFactory smbSession = new SmbSessionFactory();
        smbSession.setPort(445);
        smbSession.setHost(smbProperties.getHost());
        smbSession.setDomain(smbProperties.getDomain());
        smbSession.setUsername(smbProperties.getUsername());
        smbSession.setPassword(smbProperties.getPassword());
        smbSession.setShareAndDir(smbProperties.getSharePath());
        smbSession.setSmbMinVersion(DialectVersion.SMB1);
        smbSession.setSmbMaxVersion(DialectVersion.SMB210);
        return smbSession;
    }


    @Bean
    public CompositeFileListFilter<SmbFile> compositeFileListFilter() {
        CompositeFileListFilter<SmbFile> filters = new CompositeFileListFilter<>();
        filters.addFilter(new SmbRegexPatternFileListFilter("^(?i).+((\\.001))$"));
//        filters.addFilter(new SmbRegexPatternFileListFilter("^(?i).+((\\.002))$"));
        return filters;
    }

    @Bean
    public MessageChannel smbFileInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public SmbInboundFileSynchronizer smbInboundFileSynchronizer() {
        SmbInboundFileSynchronizer fileSynchronizer = new SmbInboundFileSynchronizer(smbSessionFactory());
        fileSynchronizer.setFilter(compositeFileListFilter());
        fileSynchronizer.setRemoteDirectory("/Arquivos_interface_2");
        fileSynchronizer.setDeleteRemoteFiles(false);
        return fileSynchronizer;
    }

    @Bean
    @InboundChannelAdapter(value = "smbFileInputChannel", poller = @Poller(fixedDelay = "2000"))
    public MessageSource<File> smbMessageSource() {
        SmbInboundFileSynchronizingMessageSource messageSource =
                new SmbInboundFileSynchronizingMessageSource(smbInboundFileSynchronizer());
        messageSource.setLocalDirectory(new File("/home/m/Desktop/temp"));
        messageSource.setAutoCreateLocalDirectory(true);
        return messageSource;
    }


    @Bean
    @ServiceActivator(inputChannel = "smbFileInputChannel")
    public MessageHandler handleSmbFile() {
        return message -> {
            File file = (File) message.getPayload();
            arquivoInterfaceProcessingService.processArquivInterface(file);
        };
    }

}
