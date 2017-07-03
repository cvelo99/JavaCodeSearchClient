package cc.jcsw;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

/**
 * Validate that Google's sha-256 has matches the output from Linux sha256sum
 * 
 * @author Chris Carcel
 *
 */
public class HashTest {

    public static final String TEST_FILE_LINUX_HASH = "5ec4610ed32771f302384d4d433ece9809919c37dd8b4ef29d291f662e4d39a6";

    @Test
    public void validateMatchingShaHash() throws IOException, URISyntaxException {

        String fileContents = testFileContents();

        HashFunction hf = Hashing.sha256();
        Hasher hasher = hf.newHasher();
        hasher.putString(fileContents, Charsets.UTF_8);
        HashCode hashResult = hasher.hash();
        String hash = hashResult.toString();

        Assert.assertTrue(TEST_FILE_LINUX_HASH.equals(hash));
    }

    public String testFileContents() throws URISyntaxException, IOException {
        Path path = Paths.get(this.getClass().getResource(".").toURI()).resolve(this.getClass().getSimpleName() + "-1.txt");
        String fileContents = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        return fileContents;
    }

}
