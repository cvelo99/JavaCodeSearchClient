package cc.jcsw.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for loading java source from the file system.
 * 
 * @author Chris Carcel
 *
 */
@Component
public class LoadConfigurationImpl implements LoadConfiguration {

    @Autowired
    private Props props;

    private AcceptFilter acceptFilter;

    private Map<Integer, List<Pattern>> versionPatterns;

    private Map<String, List<Pattern>> branchPatterns;

    private Path rootPath;

    private String solrServer;

    private List<Pattern> patternsBinary;

    /*
     * (non-Javadoc)
     * 
     * @see cc.jcsw.client.LoadConfiguration#getAcceptFilter()
     */
    @Override
    public AcceptFilter getAcceptFilter() {
        return acceptFilter;
    }

    protected void setAcceptFilter(AcceptFilter acceptFilter) {
        this.acceptFilter = acceptFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see cc.jcsw.client.LoadConfiguration#getBranchPatterns()
     */
    @Override
    public Map<String, List<Pattern>> getBranchPatterns() {
        return branchPatterns;
    }

    protected void setBranchPatterns(Map<String, List<Pattern>> branchPatterns) {
        this.branchPatterns = branchPatterns;
    }

    /*
     * (non-Javadoc)
     * 
     * @see cc.jcsw.client.LoadConfiguration#getVersionPatterns()
     */
    @Override
    public Map<Integer, List<Pattern>> getVersionPatterns() {
        return versionPatterns;
    }

    protected void setVersionPatterns(Map<Integer, List<Pattern>> versionPatterns) {
        this.versionPatterns = versionPatterns;
    }

    /*
     * (non-Javadoc)
     * 
     * @see cc.jcsw.client.LoadConfiguration#getRootPath()
     */
    @Override
    public Path getRootPath() {
        return rootPath;
    }

    protected void setRootPath(Path rootPath) {
        this.rootPath = rootPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see cc.jcsw.client.LoadConfiguration#getSolrServer()
     */
    @Override
    public String getSolrServer() {
        return solrServer;
    }

    protected void setSolrServer(String solrServer) {
        this.solrServer = solrServer;
    }

    @PostConstruct
    private void setup() {
        setSolrServer(props.getSolrUrl());

        // root of java dir
        setRootPath(Paths.get(props.getSourceDir()));

        // accept filter
        List<Pattern> patterns = props.getExtensions().stream().map(s -> Pattern.compile(s, Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toList());
        List<Pattern> patternsBinary = props.getExtensionsBinary().stream().map(s -> Pattern.compile(s, Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toList());
        this.patternsBinary = patternsBinary;

        // combine both binary and text patterns into a single list
        setAcceptFilter(new AcceptFilter(Stream.concat(patterns.stream(), patternsBinary.stream()).collect(Collectors.toList())));

        // create patterns
        Map<Integer, List<Pattern>> versions = new HashMap<>();
        props.getDirs().forEach((v, l) -> versions.put(v, l.stream().map(s -> Pattern.compile(s)).collect(Collectors.toList())));
        setVersionPatterns(versions);

        // create branches
        Map<String, List<Pattern>> branches = new HashMap<>();
        props.getBranches().forEach((v, l) -> branches.put(v, l.stream().map(s -> Pattern.compile(s)).collect(Collectors.toList())));
        setBranchPatterns(branches);

    }

    @Override
    public String getCore() {
        return props.getCore();
    }

    public List<Pattern> getPatternsBinary() {
        return patternsBinary;
    }
}
