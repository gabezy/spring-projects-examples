package br.com.gabezy.smbintegrationspring;

import br.com.gabezy.smbintegrationspring.config.properties.SmbProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(SmbProperties.class)
@SpringBootApplication
public class SmbIntegrationSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmbIntegrationSpringApplication.class, args);
    }

}
