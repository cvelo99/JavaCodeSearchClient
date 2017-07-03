package cc.jcsw;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

import cc.jcsw.client.LoadConfigurationImpl;
import cc.jcsw.client.Props;

/**
 * Test that {@link Props} loads properly from boot.
 * 
 * @author Chris Carcel
 *
 */
@SpringBootTest(classes = TestConfig.class)
@RunWith(SpringRunner.class)
@IfProfileValue(name = "test-group", value = "integration")
public class LoadConfigurationTest {

    @Autowired
    private LoadConfigurationImpl loadConfigurationImpl;

    @Test
    public void branchesTest() throws URISyntaxException, IOException {

        Map<String, List<Pattern>> branches = loadConfigurationImpl.getBranchPatterns();
        Assert.assertNotNull(branches);
        Assert.assertTrue(!branches.isEmpty());
        // make sure all branches have a directory
        branches.entrySet().stream().forEach(e -> Assert.assertTrue(e.getKey(), !e.getValue().isEmpty()));
    }

    @Test
    public void testRest() throws URISyntaxException, IOException {

        Assert.assertNotNull(loadConfigurationImpl.getSolrServer());
        Assert.assertNotNull(loadConfigurationImpl.getRootPath());
        Assert.assertNotNull(loadConfigurationImpl.getAcceptFilter());
        Assert.assertNotNull(loadConfigurationImpl.getBranchPatterns());
    }
}
