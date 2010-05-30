package se.pp.gustafson.marten.mime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MultipartMixed implements MimeTypeHandler<MimeMultipart>
{
    static final Logger log = LoggerFactory.getLogger(MultipartMixed.class);
    static final MimeType MULTIPART_MIXED;
    private final HandlerMap handlers;

    static
    {
        try
        {
            MULTIPART_MIXED = new MimeType("multipart/mixed");
        }
        catch(final MimeTypeParseException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public MultipartMixed(final HandlerMap handlers)
    {
        this.handlers = handlers;
        if(!this.handlers.handles(MULTIPART_MIXED.getBaseType()))
        {
            this.handlers.addHandler(MULTIPART_MIXED.getBaseType(), this);
        }
    }

    public void process(final byte[] data)
    {
        try
        {
            process(new MimeMultipart(new DataSource()
            {
                @Override
                public OutputStream getOutputStream() throws IOException
                {
                    throw new UnsupportedOperationException();
                }

                @Override
                public String getName()
                {
                    throw new UnsupportedOperationException();
                }

                @Override
                public InputStream getInputStream() throws IOException
                {
                    return new ByteArrayInputStream(data);
                }

                @Override
                public String getContentType()
                {
                    return MULTIPART_MIXED.getBaseType();
                }
            }));
        }
        catch(final MessagingException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void process(final MimeMultipart multipart)
    {
        try
        {
            final int count = multipart.getCount();
            if(count == 0)
            {
                log.info("No content");
            }
            else
            {
                for(int i = 0; i < count; i++)
                {
                    final BodyPart bodypart = multipart.getBodyPart(i);
                    final MimeType mimeType = new MimeType(bodypart.getContentType());
                    if(this.handlers.handles(mimeType.getBaseType()))
                    {
                        final MimeTypeHandler<T> handler = this.handlers.getFor(mimeType.getBaseType());
                        handler.process((T)bodypart.getContent());
                    }
                    else
                    {
                        log.warn("Can't handle " + mimeType.getBaseType());
                    }
                }
            }
        }
        catch(final MessagingException e)
        {
            e.printStackTrace();
        }
        catch(final IOException e)
        {
            e.printStackTrace();
        }
        catch(MimeTypeParseException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public MimeType[] appliesTo()
    {
        return new MimeType[] { MULTIPART_MIXED };
    }
}