package cc.jcsw;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

import cc.jcsw.client.LoadConfigurationImpl;

@SpringBootTest(classes = TestConfig.class)
@RunWith(SpringRunner.class)
@IfProfileValue(name = "test-group", value = "integration")
public class AcceptFilterIntegrationTest {

    @Autowired
    private LoadConfigurationImpl loadConfiguration;

    @Test
    public void integrationTest() throws URISyntaxException, IOException {

        // get the path to this directory
        Path p = Paths.get(this.getClass().getResource(".").toURI());

        // should contain the same number of non .class files we have
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p, loadConfiguration.getAcceptFilter())) {
            stream.forEach(files::add);
        }
        Assert.assertEquals("Found in " + p, 2, files.size());

    }

}
