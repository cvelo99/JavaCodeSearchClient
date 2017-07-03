package cc.jcsw.client.solr;

import static org.hamcrest.core.IsNot.not;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.util.NamedList;
import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.object.IsCompatibleType;
import org.hamcrest.text.IsEmptyString;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

import cc.jcsw.TestConfig;
import cc.jcsw.client.LoadConfigurationImpl;

@SpringBootTest(classes = TestConfig.class)
@RunWith(SpringRunner.class)
@IfProfileValue(name = "test-group", value = "integration")
public class ExtractIntegrationTest {

    @Autowired
    private LoadConfigurationImpl loadConfigurationImpl;

    @Test
    public void extractNativeSolrClient() throws IOException, URISyntaxException, SolrServerException {

        try (SolrClient sc = new HttpSolrClient(loadConfigurationImpl.getSolrServer().concat("/javasource"))) {
            ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update/extract");

            Path path = Paths.get(this.getClass().getResource("../../../../DNBCountryCodes.xlsx").toURI());
            req.addFile(path.toFile(), Files.probeContentType(path));
            req.setParam("extractFormat", "text");
            req.setParam("extractOnly", "true");
            NamedList<Object> result = sc.request(req);

            Assert.assertThat(result, not(IsEmptyIterable.emptyIterable()));
            Object object = result.get(null);
            Assert.assertNotNull(object);
            Assert.assertThat(object.getClass(), IsCompatibleType.typeCompatibleWith(String.class));
            Assert.assertThat(object.toString(), not(IsEmptyString.isEmptyOrNullString()));

        }
    }
}
