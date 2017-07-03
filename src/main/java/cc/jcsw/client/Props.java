package cc.jcsw.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties. Loaded by Spring Boot via the yml files in /config . Typically accessed via {@link LoadConfiguration} .
 * 
 * @see LoadConfiguration
 * @author Chris Carcel
 *
 */
@ConfigurationProperties
@Component
public class Props {

    /**
     * Keys are jboss versions, values are list of directories to search for code running in those versions.
     */
    private Map<Integer, List<String>> dirs = new HashMap<>();

    /**
     * Keys are branch names, value are a list of directories in that branch (typically only one)
     */
    private Map<String, List<String>> branches = new HashMap<>();

    private List<String> extensions = new ArrayList<>();

    private List<String> extensionsBinary = new ArrayList<>();

    @Value("${source_dir}")
    private String sourceDir;

    @Value("${solr_url}")
    private String solrUrl;

    @Value("${core}")
    private String core;

    public Map<Integer, List<String>> getDirs() {
        return dirs;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public String getSolrUrl() {
        return solrUrl;
    }

    public Map<String, List<String>> getBranches() {
        return branches;
    }

    public String getCore() {
        return core;
    }

    public List<String> getExtensionsBinary() {
        return extensionsBinary;
    }

    public void setExtensionsBinary(List<String> extensionsBinary) {
        this.extensionsBinary = extensionsBinary;
    }

}
