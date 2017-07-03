package cc.jcsw.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * Analyze java source code.
 * 
 * @author Chris Carcel
 *
 */
@Component
public class SourceAnalyzer {

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^package\\s+([^;/]+)");

    /**
     * Return the source code of the file.
     * 
     * @param path
     * @return
     * @throws IOException
     */
    public String sourceExtract(Path path) throws IOException {
        String source = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        return source;
    }

    /**
     * Return the (possibly null) package name for the class.
     * 
     * @param source
     * @param path
     * @return
     */
    public String packageExtract(String source, Path path) {

        String result = null;
        if (isJavaFile(path)) {
            String[] lines = source.split("\n");
            for (String line : lines) {
                Matcher m = PACKAGE_PATTERN.matcher(line);
                if (m.find()) {
                    result = m.group(1);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Return true if this is a java file.
     * 
     * @param p
     * @return
     */
    public boolean isJavaFile(Path p) {
        return p.getFileName().toString().endsWith(".java");
    }

    /**
     * Return the java class name for given path, or null if it is not a java file.
     * 
     * @param p
     * @return
     */
    public String javaClassName(Path p) {
        String fileName = p.getFileName().toString();
        if (isJavaFile(p)) {
            return fileName.substring(0, fileName.lastIndexOf('.'));
        } else {
            return null;
        }
    }

    /**
     * Create the fully qualified name.
     * 
     * @param source
     * @param path
     * @return
     * @throws IOException
     */
    public String createFqn(String source, Path path) {
        String result;
        if (isJavaFile(path)) {
            String className = javaClassName(path);
            String packageName = packageExtract(source, path);
            if (null != packageName) {
                result = packageName + "." + className;
            } else {
                result = className;
            }
        } else {
            result = null;
        }
        return result;
    }

}
