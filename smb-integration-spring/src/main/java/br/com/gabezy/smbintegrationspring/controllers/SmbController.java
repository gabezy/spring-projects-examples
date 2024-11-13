package br.com.gabezy.smbintegrationspring.controllers;

import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.smb.inbound.SmbInboundFileSynchronizer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/smb")
public class SmbController {

    private final IntegrationFlow integrationFlow;
    private final SmbInboundFileSynchronizer fileSynchronizer;

    public SmbController(IntegrationFlow integrationFlow, SmbInboundFileSynchronizer fileSynchronizer) {
        this.integrationFlow = integrationFlow;
        this.fileSynchronizer = fileSynchronizer;
    }


}
