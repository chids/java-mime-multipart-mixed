package se.pp.gustafson.marten.mime.examples;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimeType;

import org.junit.Test;

import se.pp.gustafson.marten.mime.HandlerMap;
import se.pp.gustafson.marten.mime.MimeTypeHandler;
import se.pp.gustafson.marten.mime.MultipartMixed;
import se.pp.gustafson.marten.mime.tests.TestUtil;

/**
 * Example implementation for parsing mutliple chunks of JSON data.
 * 
 * Run as part of the other Junit tests, hence the test method at the bottom.
 */
public class JsonExample implements MimeTypeHandler<ByteArrayInputStream>
{
    private static final MimeType APPLICATION_JSON = MimeTypeHandler.Util.mimeTypeForString("application/json");
    private List<String> json;

    public MimeType[] appliesTo()
    {
        return new MimeType[] { APPLICATION_JSON };
    }

    @Test
    public void verifyMultipleJsonChunks()
    {
        this.json = new ArrayList<String>();
        final MultipartMixed mm = new MultipartMixed(new HandlerMap(this));
        mm.process(TestUtil.readTestFile(TestUtil.Files.RIAK_LINK_WALKING_FILE));
        assertEquals(2, this.json.size());
        assertEquals("{\"riak\":\"CAP\"}", this.json.get(0));
        assertEquals("{\"foo\":\"bar\"}", this.json.get(1));
    }

    @Override
    public void process(final ByteArrayInputStream bos)
    {
        try
        {
            final byte[] raw = new byte[bos.available()];
            bos.read(raw);
            this.json.add(new String(raw));
        }
        catch(final IOException e)
        {
            e.printStackTrace();
        }
    }
}