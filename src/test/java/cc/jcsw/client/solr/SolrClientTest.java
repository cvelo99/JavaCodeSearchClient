package cc.jcsw.client.solr;

import java.util.Set;

import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

import cc.jcsw.TestConfig;

@SpringBootTest(classes = TestConfig.class)
@RunWith(SpringRunner.class)
@IfProfileValue(name = "test-group", value = "integration")
public class SolrClientTest {

    @Autowired
    private SourceSolrRepository solrClient;

    @Test
    public void testFindSingle() {

        SourceCode sourceCode = solrClient.findById(SolrClientModificationAddTest.ID);
        Assert.assertThat(sourceCode, IsNot.not(IsNull.notNullValue()));
        Assert.assertEquals(SolrClientModificationAddTest.HASH_VALUE, sourceCode.getHashValue());

    }

    @Test
    public void testCount() {
        long count = solrClient.findCount();
        Assert.assertTrue(count == 1);
    }

    @Test
    public void termsTest() {
        Set<String> terms = solrClient.terms();
        Assert.assertNotNull(terms);
        Assert.assertTrue("terms size is " + terms.size(), terms.size() == 1);
    }

}
