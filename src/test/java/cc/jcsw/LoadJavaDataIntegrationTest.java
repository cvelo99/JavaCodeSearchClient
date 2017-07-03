package cc.jcsw;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import cc.jcsw.client.LoadJavaData;

@SpringBootTest(classes = TestConfig.class)
@RunWith(SpringRunner.class)
@IfProfileValue(name = "test-group", value = "integration")
public class LoadJavaDataIntegrationTest {

    @Autowired
    private LoadJavaData loadJavaData;

    /**
     * Validate that the hash used in {@link LoadJavaData} remains constant.
     * 
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void hashTest() throws URISyntaxException, IOException {

        String hash = ReflectionTestUtils.invokeMethod(loadJavaData, "hashContents", new HashTest().testFileContents());
        Assert.assertEquals(HashTest.TEST_FILE_LINUX_HASH, hash);

    }

    /**
     * Validate that the hash used in {@link LoadJavaData} remains constant.
     * 
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void binaryFileTest() throws URISyntaxException, IOException {

        boolean isBinary = ReflectionTestUtils.invokeMethod(loadJavaData, "isBinaryFile", Paths.get("bob.xlsx"));
        Assert.assertTrue(isBinary);

    }

}
