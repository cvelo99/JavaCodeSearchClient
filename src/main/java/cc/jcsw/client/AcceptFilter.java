package cc.jcsw.client;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter files and directories accepting non svn and non git dirs and all files matching the patterns passed in the constructor.
 * 
 * @author Chris Carcel
 *
 */
public class AcceptFilter implements DirectoryStream.Filter<Path> {

    private static Logger LOG = LoggerFactory.getLogger(AcceptFilter.class);

    private final Collection<Pattern> acceptList;

    /**
     * Setup
     * 
     * @param patterns
     *            the patterns we accept for files.
     */
    public AcceptFilter(Collection<Pattern> patterns) {
        this.acceptList = patterns;
    }

    @Override
    public boolean accept(Path entry) {
        if (Files.isDirectory(entry)) {
            Path fileName = entry.getFileName();
            return !(fileName.startsWith(".svn") || fileName.startsWith(".git"));
        } else {
            String fn = entry.toFile().getName();
            for (Pattern pattern : this.acceptList) {
                boolean match = pattern.matcher(fn).matches();
                if (match) {
                    return true;
                }
            }

            LOG.debug("Ignore file " + entry.toFile().getAbsolutePath());

            return false;
        }
    }

}
