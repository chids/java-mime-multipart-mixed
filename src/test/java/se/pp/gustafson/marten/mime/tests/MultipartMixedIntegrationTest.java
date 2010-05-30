package se.pp.gustafson.marten.mime.tests;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.junit.Test;

import se.pp.gustafson.marten.mime.HandlerMap;
import se.pp.gustafson.marten.mime.MimeTypeHandler;
import se.pp.gustafson.marten.mime.MultipartMixed;

import com.sun.mail.util.BASE64DecoderStream;

public class MultipartMixedIntegrationTest
{

    @Test
    public void octetStream_textPlainPassedAsString_octetStreamPassedAsBASE64DecoderStream() throws MimeTypeParseException
    {
        final MimeType octetStream = new MimeType("application/octet-stream");
        final MimeType textPlain = new MimeType("text/plain");

        final MimeTypeHandler<String> plainTextHandler = mock(MimeTypeHandler.class);
        when(plainTextHandler.appliesTo()).thenReturn(new MimeType[] { textPlain });

        final MimeTypeHandler<BASE64DecoderStream> octetStreamHandler = mock(MimeTypeHandler.class);
        when(octetStreamHandler.appliesTo()).thenReturn(new MimeType[] { octetStream });

        new MultipartMixed(new HandlerMap(plainTextHandler, octetStreamHandler)).process(TestUtil.readTestFile(TestUtil.Files.OCTET_STREAM_FILE));

        verify(plainTextHandler).process(eq("This is the body of the message."));
        verify(octetStreamHandler).process(isA(BASE64DecoderStream.class));
    }

    @Test
    public void riakLink_jsonPassedAsByteArrayInputStream() throws MimeTypeParseException
    {
        final MimeType json = new MimeType("application/json");

        final MimeTypeHandler<ByteArrayInputStream> handler = mock(MimeTypeHandler.class);
        when(handler.appliesTo()).thenReturn(new MimeType[] { json });

        new MultipartMixed(new HandlerMap(handler)).process(TestUtil.readTestFile(TestUtil.Files.RIAK_LINK_WALKING_FILE));

        verify(handler, times(2)).process(isA(ByteArrayInputStream.class));
    }
}