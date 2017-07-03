package cc.jcsw;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cc.jcsw.client.SourceAnalyzer;

public class SourceAnalyzerTest {

    private static Path createTempDirectory;
    private static List<Path> filesList;

    /**
     * Extract zip files to a temporary directory.
     * 
     * @throws IOException
     */
    @BeforeClass
    public static void beforeAll() throws IOException {

        createTempDirectory = Files.createTempDirectory("sat");

        try (InputStream in = SourceAnalyzerTest.class.getResourceAsStream("../../zip.zip"); ZipInputStream zin = new ZipInputStream(in);) {
            ZipEntry ze = zin.getNextEntry();
            while ((ze = zin.getNextEntry()) != null) {
                Path path = createTempDirectory.resolve(ze.getName());
                Files.write(path, IOUtils.toByteArray(zin));
            }
        }

        List<Path> files = new ArrayList<>();
        DirectoryStream<Path> ds = Files.newDirectoryStream(createTempDirectory);
        ds.forEach(files::add);
        ds.close();
        filesList = Collections.unmodifiableList(files);

        System.out.println("Created and populated " + createTempDirectory);
    }

    private SourceAnalyzer sourceAnalyzer;

    @Before
    public void before() {
        this.sourceAnalyzer = new SourceAnalyzer();
    }

    /**
     * Clean up and remove our temporary directory.
     * 
     * @throws IOException
     */
    @AfterClass
    public static void after() throws IOException {

        DirectoryStream<Path> ds = Files.newDirectoryStream(createTempDirectory);
        ds.forEach(p -> {
            try {
                Files.delete(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        ds.close();
        Files.delete(createTempDirectory);

    }

    @Test
    public void testIsJavaFile() {

        Map<String, Boolean> expections = new HashMap<>();
        expections.put("Bob.java", true);
        expections.put("bootstrap.min.css", false);
        expections.put("Component.java", true);
        expections.put("IOException.java", true);
        expections.put("StandardCharsets.java", true);
        expections.put("angular-resource.min", false);

        for (Path path : filesList) {
            String string = path.getFileName().toString();
            Assert.assertTrue(expections.get(string).booleanValue() == sourceAnalyzer.isJavaFile(path));
        }
    }

    @Test
    public void testPackageExtract() throws IOException {

        Map<String, String> expections = new HashMap<>();
        expections.put("Bob.java", null);
        expections.put("bootstrap.min.css", null);
        expections.put("Component.java", "org.springframework.stereotype");
        expections.put("IOException.java", "java.io");
        expections.put("StandardCharsets.java", "java.nio.charset");
        expections.put("angular-resource.min", null);

        for (Path path : filesList) {
            String string = path.getFileName().toString();
            Assert.assertEquals("Fail for " + string, // message
                    expections.get(string), // expected
                    sourceAnalyzer.packageExtract(sourceAnalyzer.sourceExtract(path), path));
        }
    }

    @Test
    public void testJavaClassname() throws IOException {

        Map<String, String> expections = new HashMap<>();
        expections.put("Bob.java", "Bob");
        expections.put("bootstrap.min.css", null);
        expections.put("Component.java", "Component");
        expections.put("IOException.java", "IOException");
        expections.put("StandardCharsets.java", "StandardCharsets");
        expections.put("angular-resource.min", null);

        for (Path path : filesList) {
            String string = path.getFileName().toString();
            Assert.assertEquals("Fail for " + string, // message
                    expections.get(string), // expected
                    sourceAnalyzer.javaClassName(path));
        }
    }

    @Test
    public void testJavaFqn() throws IOException {

        Map<String, String> expections = new HashMap<>();
        expections.put("Bob.java", "Bob");
        expections.put("bootstrap.min.css", null);
        expections.put("Component.java", "org.springframework.stereotype.Component");
        expections.put("IOException.java", "java.io.IOException");
        expections.put("StandardCharsets.java", "java.nio.charset.StandardCharsets");
        expections.put("angular-resource.min", null);

        for (Path path : filesList) {
            String string = path.getFileName().toString();
            Assert.assertEquals("Fail for " + string, // message
                    expections.get(string), // expected
                    sourceAnalyzer.createFqn(sourceAnalyzer.sourceExtract(path), path));
        }
    }

}
