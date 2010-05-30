package se.pp.gustafson.marten.mime.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.junit.Test;

import se.pp.gustafson.marten.mime.HandlerMap;
import se.pp.gustafson.marten.mime.MimeTypeHandler;

public class HandlerMapTest
{
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";

    @Test
    public void handlerIsMappedToType() throws MimeTypeParseException
    {
        final MimeTypeHandler<String> handler = new MimeTypeHandler<String>()
        {
            final MimeType json = new MimeType(APPLICATION_JSON);

            @Override
            public MimeType[] appliesTo()
            {
                return new MimeType[] { this.json };
            }

            @Override
            public void process(final String data)
            {}
        };
        assertTrue(new HandlerMap(handler).handles(APPLICATION_JSON));
        assertSame(handler, new HandlerMap(handler).getFor(APPLICATION_JSON));
    }

    @Test
    public void handlerIsMappedToTypeWithoutParameters() throws MimeTypeParseException
    {
        final MimeTypeHandler<String> handler = new MimeTypeHandler<String>()
        {
            final MimeType json = new MimeType(APPLICATION_JSON_UTF8);

            @Override
            public MimeType[] appliesTo()
            {
                return new MimeType[] { this.json };
            }

            @Override
            public void process(final String data)
            {}
        };
        assertTrue(new HandlerMap(handler).handles(APPLICATION_JSON));
        assertSame(handler, new HandlerMap(handler).getFor(APPLICATION_JSON));
    }

    @Test
    public void unmappedMimeTypeReturnsNullHandler()
    {
        assertNotNull(new HandlerMap(new MimeTypeHandler[0]).getFor("nonexistant"));
    }
}