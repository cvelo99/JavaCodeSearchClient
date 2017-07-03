package cc.jcsw.client.solr;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

import cc.jcsw.TestConfig;

@SpringBootTest(classes = TestConfig.class)
@RunWith(SpringRunner.class)
@IfProfileValue(name = "test-group", value = "integration-modification")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SolrClientModificationRemoveTest {

    @Autowired
    private SourceSolrRepository solrClient;

    @Test
    public void testRemove() {

        solrClient.delete(SolrClientModificationAddTest.ID);

    }
}
