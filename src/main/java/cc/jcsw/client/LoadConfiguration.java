package cc.jcsw.client;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Information needed to upload the file tree into solr.
 * 
 * @author Chris Carcel
 *
 */
public interface LoadConfiguration {

    /**
     * When processing a directory this removes directories we ignore, like .svn or .git.
     * 
     * @return
     */
    public AcceptFilter getAcceptFilter();

    /**
     * Keys are branches, values are a list of Patterns, any one of which means that file is in that branch.
     * 
     * @return
     */
    public Map<String, List<Pattern>> getBranchPatterns();

    /**
     * Keys are versions, values are a list of Patterns, a match on any one means the file is in that version.
     * 
     * @return
     */
    public Map<Integer, List<Pattern>> getVersionPatterns();

    /**
     * The root of the svn directory.
     * 
     * @return
     */
    public Path getRootPath();

    /**
     * The path to the solr server.
     * 
     * @return
     */
    public String getSolrServer();

    /**
     * The Solr core name.
     * 
     * @return
     */
    public String getCore();

}
