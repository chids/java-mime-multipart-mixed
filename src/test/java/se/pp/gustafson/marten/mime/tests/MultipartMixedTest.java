package se.pp.gustafson.marten.mime.tests;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.junit.Test;

import se.pp.gustafson.marten.mime.MultipartMixed;
import se.pp.gustafson.marten.mime.BodyPartHandler;

public class MultipartMixedTest
{
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";

    @Test
    public void handlerIsMappedToType() throws MimeTypeParseException
    {
        final BodyPartHandler handler = new BodyPartHandler()
        {
            final MimeType json = new MimeType(APPLICATION_JSON);

            @Override
            public MimeType[] appliesTo()
            {
                return new MimeType[] { this.json };
            }

            @Override
            public void process(MimeType mimeType, Object content)
            {}
        };
        assertTrue(new MultipartMixed(handler).handles(APPLICATION_JSON));
        assertSame(handler, new MultipartMixed(handler).getFor(APPLICATION_JSON));
    }

    @Test
    public void handlerIsMappedToTypeWithoutParameters() throws MimeTypeParseException
    {
        final BodyPartHandler handler = new BodyPartHandler()
        {
            final MimeType json = new MimeType(APPLICATION_JSON_UTF8);

            @Override
            public MimeType[] appliesTo()
            {
                return new MimeType[] { this.json };
            }

            @Override
            public void process(MimeType mimeType, Object content)
            {}
        };
        assertTrue(new MultipartMixed(handler).handles(APPLICATION_JSON));
        assertSame(handler, new MultipartMixed(handler).getFor(APPLICATION_JSON));
    }

    @Test
    public void unmappedMimeTypeReturnsNullHandler() throws MimeTypeParseException
    {
        assertNotNull(new MultipartMixed(new BodyPartHandler[0]).getFor("nonexistant"));
    }
}