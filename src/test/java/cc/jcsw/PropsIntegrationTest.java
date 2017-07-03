package cc.jcsw;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

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
public class PropsIntegrationTest {

    @Autowired
    private Props props;

    @Test
    public void branchesTest() throws URISyntaxException, IOException {

        Map<String, List<String>> branches = props.getBranches();
        Assert.assertNotNull(branches);
        Assert.assertTrue(!branches.isEmpty());
        // make sure all branches have a directory
        branches.entrySet().stream().forEach(e -> Assert.assertTrue(e.getKey(), !e.getValue().isEmpty()));
    }

    @Test
    public void dirsTest() throws URISyntaxException, IOException {

        Map<Integer, List<String>> dirs = props.getDirs();
        Assert.assertNotNull(dirs);
        Assert.assertTrue(!dirs.isEmpty());
        // make sure all branches have a directory
        dirs.entrySet().stream().forEach(e -> Assert.assertTrue(e.getKey().toString(), !e.getValue().isEmpty()));
    }

    @Test
    public void testRest() throws URISyntaxException, IOException {

        Assert.assertNotNull(props.getExtensions());
        Assert.assertFalse(props.getExtensions().isEmpty());
        Assert.assertNotNull(props.getSolrUrl());
        Assert.assertNotNull(props.getSourceDir());
        Assert.assertNotNull(props.getExtensionsBinary());
        Assert.assertFalse(props.getExtensionsBinary().isEmpty());

    }
}
