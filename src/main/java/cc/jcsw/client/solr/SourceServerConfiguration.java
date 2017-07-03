package cc.jcsw.client.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;
import org.springframework.data.solr.server.support.HttpSolrClientFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import cc.jcsw.client.LoadConfigurationImpl;

/**
 * Bumping into: https://jira.spring.io/browse/DATASOLR-364
 * 
 * https://jira.spring.io/browse/DATASOLR-363
 * 
 * @author Chris Carcel
 *
 */
@Configuration
@EnableSolrRepositories(basePackages = "cc.jcsw.client.solr", repositoryBaseClass = SourceSolrRepositoryImpl.class)
@EnableTransactionManagement
public class SourceServerConfiguration {

    @Autowired
    private LoadConfigurationImpl loadConfiguration;

    @Bean
    public SourceSolrRepository repo() throws Exception {

        return new SolrRepositoryFactory(solrClient(solrServerFactoryBean())).getRepository(SourceSolrRepository.class,
                new SourceSolrRepositoryImpl(solrTemplate(), loadConfiguration.getCore()));

    }

    @Bean
    public HttpSolrClientFactoryBean solrServerFactoryBean() {
        HttpSolrClientFactoryBean factory = new HttpSolrClientFactoryBean();
        factory.setUrl(loadConfiguration.getSolrServer());
        return factory;
    }

    @Bean
    public SolrClient solrClient(HttpSolrClientFactoryBean factory) {
        return solrServerFactoryBean().getSolrClient();
    }

    @Bean
    public SolrTemplate solrTemplate() {
        return new SolrTemplate(solrServerFactoryBean());
    }

}
