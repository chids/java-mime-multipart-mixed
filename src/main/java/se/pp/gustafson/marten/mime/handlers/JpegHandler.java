package se.pp.gustafson.marten.mime.handlers;

import java.io.IOException;

import javax.activation.MimeType;

import se.pp.gustafson.marten.mime.MimeTypeHandler;

import com.sun.mail.util.BASE64DecoderStream;

public class JpegHandler implements MimeTypeHandler<BASE64DecoderStream>
{
    public static final MimeType IMAGE_JPEG = MimeTypeHandler.Util.mimeTypeForString("image/jpeg");
    private final Callback<byte[]> callback;

    public JpegHandler(final Callback<byte[]> callback)
    {
        this.callback = callback;
    }

    @Override
    public MimeType[] appliesTo()
    {
        return new MimeType[] { IMAGE_JPEG };
    }

    @Override
    public void process(final BASE64DecoderStream decoder)
    {
        try
        {
            final byte[] raw = new byte[decoder.available()];
            decoder.read(raw);
            this.callback.process(raw);
        }
        catch(final IOException e)
        {
            e.printStackTrace();
        }
    }
}
