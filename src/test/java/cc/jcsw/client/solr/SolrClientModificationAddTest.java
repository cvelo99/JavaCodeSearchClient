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
public class SolrClientModificationAddTest {

    static final String HASH_VALUE = "AABBCC";
    static final String ID = "test-test-test-test-test";

    @Autowired
    private SourceSolrRepository solrClient;

    @Test
    public void testAdd() {

        SourceCode sc = new SourceCode();
        sc.setBranch("branch");
        sc.setFileExtension(".java");
        sc.setFileName("unit-test-unit-test");
        sc.setFileName2("unit-test-unit-test");
        sc.setFolder("folder");
        sc.setHashValue(HASH_VALUE);
        sc.setId(ID);
        sc.setJavaSource("java");
        sc.setJbossVersion(7);
        sc.setNameAndFile("name_and_file");
        sc.setSize(10L);

        solrClient.add(sc);

    }

}
