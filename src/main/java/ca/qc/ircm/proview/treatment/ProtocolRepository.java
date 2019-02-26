package ca.qc.ircm.proview.treatment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Protocol repository.
 */
public interface ProtocolRepository extends JpaRepository<Protocol, Long> {
  List<Protocol> findByType(Protocol.Type type);
}
