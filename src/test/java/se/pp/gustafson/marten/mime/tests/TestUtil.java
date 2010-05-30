package se.pp.gustafson.marten.mime.tests;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimeType;

import org.junit.Test;
import org.mockito.ArgumentMatcher;

public final class TestUtil
{
    
    @Test
    public void noOpTestToMakeMavenTestPhaseHappy()
    {}
    
    public static final class MimeTypeMatcher extends ArgumentMatcher<MimeType>
    {
        private final MimeType match;

        public MimeTypeMatcher(final MimeType match)
        {
            this.match = match;
        }

        @Override
        public boolean matches(Object o)
        {
            return ((MimeType)o).match(this.match);
        }
    }
    
    public static MimeType eq(final MimeType mimeType)
    {
        return argThat(new MimeTypeMatcher(mimeType));
    }

    public static enum Files
    {
        OCTET_STREAM_FILE("octet-stream.txt"), RIAK_LINK_WALKING_FILE("riak-link-walking-json.txt"), JPEG_AND_PLAIN_TEXT_FILE("plain-text-and-jpeg-sample.txt");

        public final String filename;

        Files(final String name)
        {
            this.filename = name;
        }
    };

    public static byte[] readTestFile(final Files file)
    {
        try
        {
            final InputStream is = TestUtil.class.getClassLoader().getResourceAsStream(file.filename);
            final byte[] data = new byte[is.available()];
            is.read(data);
            is.close();
            return data;
        }
        catch(final IOException e)
        {
            fail(e.getMessage());
            return null;
        }
    }
}
