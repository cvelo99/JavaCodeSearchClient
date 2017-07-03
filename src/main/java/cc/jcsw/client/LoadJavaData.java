package cc.jcsw.client;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import cc.jcsw.client.solr.SourceCode;
import cc.jcsw.client.solr.SourceSolrRepository;

/**
 * Primary class to read a source code directory and upload it to solr.
 * 
 * @author Chris Carcel
 *
 */
@Component
@Scope("prototype")
public class LoadJavaData {

    private static final Logger LOG = LoggerFactory.getLogger(LoadJavaData.class);

    @Autowired
    private LoadConfigurationImpl loadConfigurationImpl;

    @Autowired
    private SourceSolrRepository solrClient;

    @Autowired
    private SourceAnalyzer sourceAnalyzer;

    private final HashFunction hf = Hashing.sha256();

    private boolean isWindows;

    public LoadJavaData() {

        this.isWindows = "\\".equals(System.getProperty("file.separator"));

    }

    /**
     * Public method to update solr, adding all new files and removing all files in the index which no longer exist.
     * 
     * @throws IOException
     * @throws URISyntaxException
     * @throws SolrServerException
     */
    public void updateSolr() throws IOException, URISyntaxException, SolrServerException {

        MutableInt i = new MutableInt(0);

        // upload new files
        List<String> filesUploaded = processDirectory(loadConfigurationImpl.getRootPath(), i);

        solrClient.commit();

        LOG.info("Updated {} files", i);

        // remove files which no longer exist
        i.setValue(0);
        removeNonexistantFiles(filesUploaded, i);

        solrClient.commit();

        LOG.info("Remove {} files", i);
    }

    /**
     * Removes all files in solr which are not still on the file system.
     * 
     * @param filesUploaded
     * @param i
     * @throws IOException
     * @throws SolrServerException
     */
    private void removeNonexistantFiles(List<String> filesUploaded, MutableInt i) throws SolrServerException, IOException {

        Set<String> uploaded = new TreeSet<>(filesUploaded);

        Set<String> allIds = solrClient.terms();
        for (String id : allIds) {
            if (!uploaded.contains(id)) {
                LOG.trace("Removed {}", id);
                // delete
                solrClient.delete(id);
                i.add(1);
            }
        }

    }

    /**
     * Iterator over a directory, calling this method for any subdirectories and and {@link #uploadFile(Path, MutableInt)} for files.
     * 
     * @param directory
     * @param i
     * @return the list of files (not including directories) we uploaded. This is the solr "id" of the document.
     * @throws IOException
     * @throws SolrServerException
     */
    private List<String> processDirectory(Path directory, MutableInt i) throws IOException, SolrServerException {

        List<String> result = new ArrayList<>();

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory, loadConfigurationImpl.getAcceptFilter())) {

            Iterator<Path> iterator = ds.iterator();
            while (iterator.hasNext()) {

                Path path = iterator.next();

                if (Files.isDirectory(path)) {
                    List<String> subDirList = processDirectory(path, i);
                    result.addAll(subDirList);
                } else {
                    String id = uploadFile(path, i);
                    if (null != id) {
                        result.add(id);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Upload a file.
     * 
     * @param path
     *            the file we insert/update
     * @param i
     *            the counter
     * @return the ID of the solr document if it was inserted/updated, or else null
     * @throws IOException
     * @throws SolrServerException
     */
    private String uploadFile(Path path, MutableInt i) throws IOException, SolrServerException {

        final String result;

        String relativePath = loadConfigurationImpl.getRootPath().relativize(path).toString();
        String usePath = standardizeFileSystemSeparator(relativePath);

        result = usePath;

        Predicate<Pattern> patternPredicate = p -> p.matcher(usePath).matches();
        Predicate<Entry<String, List<Pattern>>> branchPredicate = e -> e.getValue().stream().filter(patternPredicate).findFirst()
                .isPresent();
        Predicate<Entry<Integer, List<Pattern>>> versionPredicate = e -> e.getValue().stream().filter(patternPredicate).findFirst()
                .isPresent();

        Optional<Entry<String, List<Pattern>>> branch = loadConfigurationImpl.getBranchPatterns().entrySet().stream()
                .filter(branchPredicate).findFirst();
        Optional<Entry<Integer, List<Pattern>>> version = loadConfigurationImpl.getVersionPatterns().entrySet().stream()
                .filter(versionPredicate).findFirst();

        if (!branch.isPresent() || !version.isPresent()) {
            LOG.debug("Unknown branch or version for file {}, stopping.", usePath);
            // unknown branch, stop
            return null;
        }

        String source;
        if (isBinaryFile(path)) {
            source = sourceFromBinary(path);
        } else {
            source = sourceAnalyzer.sourceExtract(path);
        }

        if (StringUtils.isBlank(source)) {
            LOG.warn("Source for " + path + " is blank");
            source = " ";
        }

        Path fileName = path.getFileName();

        String fqn = sourceAnalyzer.createFqn(source, fileName);

        SourceCode sc = new SourceCode();
        sc.setId(usePath);
        sc.setFileName(fileName.getFileName().toString());
        sc.setFileName2(fileName.getFileName().toString());
        sc.setFqn(fqn);
        sc.setBranch(branch.get().getKey());
        sc.setFileExtension(fileExtension(fileName).map(s -> s.toLowerCase()).orElse(null));
        sc.setJavaSource(source);
        sc.setNameAndFile(usePath + source);
        sc.setSize(Files.size(path));
        sc.setHashValue(hashContents(source));
        sc.setJbossVersion(version.get().getKey());

        String folder = relativePath.substring(0, relativePath.indexOf(File.separatorChar));
        sc.setFolder(folder);

        SourceCode existing = solrClient.findById(sc.getId());

        LOG.trace("Process file " + relativePath);

        if (null == existing) {
            solrClient.add(sc);
            i.add(1);
        } else {
            if (!existing.getHashValue().equals(sc.getHashValue())) {
                LOG.debug("Changed file: {}", relativePath);
                solrClient.delete(sc.getId());
                solrClient.add(sc);
                i.add(1);
            }
        }

        if (i.intValue() % 1000 == 0) {
            solrClient.commit();
        }

        return result;

    }

    /**
     * Return the source code from a binary.
     * 
     * @param path
     * @return
     */
    private String sourceFromBinary(Path path) {
        String text = solrClient.extractText(path);
        return text;
    }

    /**
     * Return true if this is a binary file.
     * 
     * @param path
     * @return
     */
    private boolean isBinaryFile(Path path) {

        for (Pattern pattern : this.loadConfigurationImpl.getPatternsBinary()) {
            boolean match = pattern.matcher(path.getFileName().toString()).matches();
            if (match) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the (possibly null) file name.
     * 
     * @param fileName
     * @return
     */
    private Optional<String> fileExtension(Path fileName) {
        int k = fileName.toString().lastIndexOf('.');
        String ext;
        if (k > -1) {
            ext = fileName.toString().substring(k + 1);
        } else {
            ext = null;
        }
        return Optional.ofNullable(ext);
    }

    private String hashContents(String source) {
        Hasher hasher = this.hf.newHasher();
        hasher.putString(source, Charsets.UTF_8);
        HashCode hashResult = hasher.hash();
        String hash = hashResult.toString();
        return hash;
    }

    /**
     * Convert windows file system separators to unix if necessary.
     * 
     * @param relativePath
     * @return
     */
    private String standardizeFileSystemSeparator(String relativePath) {
        if (isWindows) {
            return relativePath.replace('\\', '/');
        } else {
            return relativePath;
        }
    }

}
