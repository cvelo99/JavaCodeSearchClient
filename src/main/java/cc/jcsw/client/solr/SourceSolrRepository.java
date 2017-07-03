package cc.jcsw.client.solr;

import java.nio.file.Path;
import java.util.Set;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.solr.repository.SolrRepository;

@NoRepositoryBean
public interface SourceSolrRepository extends SolrRepository<SourceCode, String> {

    SourceCode findById(String id);

    Set<String> terms();

    Long findCount();

    void add(SourceCode sc);

    void delete(String id);

    void commit();

    String extractText(Path p);

}
