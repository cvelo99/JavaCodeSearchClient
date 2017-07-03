package cc.jcsw;

import java.nio.file.Paths;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import cc.jcsw.client.LoadJavaData;

public class LoadJavaDataTest {

    @Test
    public void testFileExtension2() {
        LoadJavaData t = new LoadJavaData();

        Optional<String> o1 = ReflectionTestUtils.invokeMethod(t, "fileExtension", Paths.get("bob.txt"));
        Assert.assertTrue(o1.isPresent());
        Assert.assertEquals("txt", o1.get());

        Optional<String> o2 = ReflectionTestUtils.invokeMethod(t, "fileExtension", Paths.get("bob.TXT"));
        Assert.assertTrue(o2.isPresent());
        Assert.assertEquals("TXT", o2.get());

        Optional<String> o3 = ReflectionTestUtils.invokeMethod(t, "fileExtension", Paths.get("bob"));
        Assert.assertFalse(o3.isPresent());
    }

}
