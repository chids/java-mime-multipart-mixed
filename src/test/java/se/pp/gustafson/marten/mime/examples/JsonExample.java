package se.pp.gustafson.marten.mime.examples;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.junit.Test;

import se.pp.gustafson.marten.mime.BodyPartHandler;
import se.pp.gustafson.marten.mime.MultipartMixed;
import se.pp.gustafson.marten.mime.tests.Util;

public class JsonExample implements BodyPartHandler
{
    private static final MimeType MIME_TYPE;
    private String json;

    static
    {
        try
        {
            MIME_TYPE = new MimeType("application/json");
        }
        catch(final MimeTypeParseException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Test
    public void json() throws MimeTypeParseException
    {
        final MultipartMixed mm = new MultipartMixed(this);
        mm.process(Util.readTestFile(Util.Files.RIAK_LINK_WALKING_FILE));
        System.err.println(getJson());
    }

    @Override
    public void process(final MimeType mimeType, final Object content)
    {
        if(MIME_TYPE.match(mimeType) && content instanceof ByteArrayInputStream)
        {
            try
            {
                final ByteArrayInputStream bos = (ByteArrayInputStream)content;
                final byte[] raw = new byte[bos.available()];
                bos.read(raw);
                this.json = new String(raw);
            }
            catch(final IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            throw new IllegalArgumentException("Can't handle " + mimeType.getBaseType() + ", content: " + content);
        }
    }

    @Override
    public MimeType[] appliesTo()
    {
        return new MimeType[] { MIME_TYPE };
    }

    public String getJson()
    {
        return this.json;
    }
}