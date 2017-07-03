package cc.jcsw.main;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalTime;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cc.jcsw.client.LoadJavaData;

/**
 * Build properties and run {@link LoadJavaData} .
 * 
 * @author Chris Carcel
 *
 */
@SpringBootApplication(scanBasePackages = "cc.jcsw")
public class Main implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @Autowired
    private LoadJavaData loadJavaData;

    /**
     * @param args
     * @throws ArchiveException
     * @throws IOException
     * @throws URISyntaxException
     * @throws SolrServerException
     */
    public static void main(String[] args) throws IOException, URISyntaxException, SolrServerException {

        LocalTime start = LocalTime.now();

        SpringApplication.run(Main.class);

        Duration between = Duration.between(start, LocalTime.now());

        double minutes = Math.floor(between.getSeconds() / 60);
        double seconds = between.getSeconds() - (minutes * 60);

        LOG.info(String.format("%1$1.0f minutes, %2$1.0f seconds", minutes, seconds));

    }

    @Override
    public void run(ApplicationArguments arg0) throws Exception {

        loadJavaData.updateSolr();

    }

}
