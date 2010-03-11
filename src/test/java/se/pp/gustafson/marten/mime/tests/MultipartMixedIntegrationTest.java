package se.pp.gustafson.marten.mime.tests;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.junit.Test;

import se.pp.gustafson.marten.mime.MultipartMixed;
import se.pp.gustafson.marten.mime.BodyPartHandler;

import com.sun.mail.util.BASE64DecoderStream;

public class MultipartMixedIntegrationTest
{

    @Test
    public void octetStream_textPlainPassedAsString_octetStreamPassedAsBASE64DecoderStream() throws MimeTypeParseException
    {
        final MimeType octetStream = new MimeType("application/octet-stream");
        final MimeType textPlain = new MimeType("text/plain");

        final BodyPartHandler handler = mock(BodyPartHandler.class);
        when(handler.appliesTo()).thenReturn(new MimeType[] { octetStream, textPlain });

        new MultipartMixed(handler).process(TestUtil.readTestFile(TestUtil.Files.OCTET_STREAM_FILE));

        verify(handler).process(TestUtil.eq(textPlain), eq("This is the body of the message."));
        verify(handler).process(TestUtil.eq(octetStream), isA(BASE64DecoderStream.class));
    }

    @Test
    public void riakLink_jsonPassedAsByteArrayInputStream() throws MimeTypeParseException
    {
        final MimeType json = new MimeType("application/json");

        final BodyPartHandler handler = mock(BodyPartHandler.class);
        when(handler.appliesTo()).thenReturn(new MimeType[] { json });

        new MultipartMixed(handler).process(TestUtil.readTestFile(TestUtil.Files.RIAK_LINK_WALKING_FILE));

        verify(handler).process(TestUtil.eq(json), isA(ByteArrayInputStream.class));
    }
}