package br.com.gabezy.smbintegrationspring.repositories;

import br.com.gabezy.smbintegrationspring.domain.entity.ArquivoInterface;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArquivoInterfaceRepository extends JpaRepository<ArquivoInterface, Long> {
}
