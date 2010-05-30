package se.pp.gustafson.marten.mime.handlers;

import javax.activation.MimeType;

import se.pp.gustafson.marten.mime.MimeTypeHandler;

public class PlainTextHandler implements MimeTypeHandler<String>
{
    public static final MimeType TEXT_PLAIN = MimeTypeHandler.Util.mimeTypeForString("text/plain");
    private final Callback<String> callback;

    public PlainTextHandler(final Callback<String> callback)
    {
        this.callback = callback;
    }

    @Override
    public MimeType[] appliesTo()
    {
        return new MimeType[] { TEXT_PLAIN };
    }

    @Override
    public void process(final String data)
    {
        this.callback.process(data);
    }
}
