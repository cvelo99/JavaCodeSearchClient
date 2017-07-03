package cc.jcsw;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import cc.jcsw.client.AcceptFilter;

/**
 * Accept filter tests.
 * 
 * @author Chris Carcel
 *
 */
public class AcceptFilterTest {

    @Test
    public void testIgnoreVcs() {

        AcceptFilter acceptFilter = new AcceptFilter(Collections.emptyList());
        Path p1 = Paths.get("/bob/.svn");
        Assert.assertFalse(acceptFilter.accept(p1));
        Path p2 = Paths.get("/bob/.git");
        Assert.assertFalse(acceptFilter.accept(p2));

    }

    @Test
    public void testAccept() {

        List<Pattern> list = Stream.of(Pattern.compile("bob.txt")).collect(Collectors.toList());
        AcceptFilter acceptFilter = new AcceptFilter(list);

        Path p1 = Paths.get("/bob/.svn");
        Assert.assertFalse(acceptFilter.accept(p1));
        Path p2 = Paths.get("/bob/.git");
        Assert.assertFalse(acceptFilter.accept(p2));

        Assert.assertTrue(acceptFilter.accept(Paths.get("/bob/bob.txt")));
        Assert.assertFalse(acceptFilter.accept(Paths.get("/bob/larry.txt")));
        Assert.assertFalse(acceptFilter.accept(Paths.get("/bob.txt/larry.txt")));

    }

}
