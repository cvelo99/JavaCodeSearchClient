package cc.jcsw.client.solr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleTermsQuery;
import org.springframework.data.solr.core.query.TermsQuery;
import org.springframework.data.solr.core.query.result.TermsPage;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;

public class SourceSolrRepositoryImpl extends SimpleSolrRepository<SourceCode, String> implements SourceSolrRepository {

    private static final Logger LOG = LoggerFactory.getLogger(SourceSolrRepositoryImpl.class);

    private String core;
    private String extractUrl;

    public SourceSolrRepositoryImpl(SolrOperations solrOperations, String core) {
        super(solrOperations);
        this.core = core;

        StringBuilder b = new StringBuilder();
        b.append("/");
        b.append(core);
        b.append("/update/extract");
        this.extractUrl = b.toString();
    }

    @Override
    public SourceCode findById(String id) {
        SimpleQuery simpleQuery = new SimpleQuery();
        simpleQuery.addCriteria(Criteria.where("id").is(id));
        return getSolrOperations().queryForObject(core, simpleQuery, SourceCode.class);
    }

    @Override
    public Set<String> terms() {
        TermsQuery query = SimpleTermsQuery.queryBuilder().fields("id").limit(Integer.MAX_VALUE).build();

        TermsPage page = getSolrOperations().queryForTermsPage(core, query);

        HashSet<String> terms = StreamSupport
                .stream( // stream
                        Spliterators.spliteratorUnknownSize(page.iterator(), Spliterator.ORDERED), false) // spliterator
                .map(p -> p.getValue()) // map
                .collect(Collectors.toCollection(HashSet::new));
        return terms;

    }

    @Override
    public Long findCount() {
        return getSolrOperations().count(core, new SimpleQuery(new Criteria(Criteria.WILDCARD).expression(Criteria.WILDCARD)));
    }

    @Override
    public void add(SourceCode sc) {
        getSolrOperations().saveBean(core, sc);
    }

    @Override
    public void delete(String id) {
        getSolrOperations().deleteById(core, id);
    }

    @Override
    public void commit() {
        getSolrOperations().commit(core);
    }

    @Override
    public String extractText(Path path) {

        try {
            ContentStreamUpdateRequest req = new ContentStreamUpdateRequest(extractUrl);
            req.addFile(path.toFile(), Files.probeContentType(path));
            req.setParam("extractFormat", "text");
            req.setParam("extractOnly", "true");
            NamedList<Object> result = getSolrOperations().getSolrClient().request(req);
            return (String) result.get(null);
        } catch (Exception e) {
            LOG.warn("Unable to extract text from " + path, e);
            return "";
        }

    }

}
