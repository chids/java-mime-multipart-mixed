package se.pp.gustafson.marten.mime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataSource;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MultipartMixed implements BodyPartHandler
{
    static final Logger log = LoggerFactory.getLogger(MultipartMixed.class);
    static final MimeType MULTIPART_MIXED;
    private final Map<String, BodyPartHandler> handlerMappings;

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

    public MultipartMixed(final BodyPartHandler... handlers)
    {
        this.handlerMappings = new HashMap<String, BodyPartHandler>();
        addHandler(MULTIPART_MIXED.getBaseType(), this);
        for(final BodyPartHandler handler : handlers)
        {
            for(final MimeType mimeType : handler.appliesTo())
            {
                addHandler(mimeType.getBaseType(), handler);
            }
        }
    }

    private void addHandler(final String key, final BodyPartHandler handler)
    {
        final BodyPartHandler oldHandler = this.handlerMappings.put(key, handler);
        if(oldHandler == null)
        {
            log.debug("Added handler for " + key + ": " + handler.getClass().getName());
        }
        else
        {
            log.debug("Handler: " + oldHandler.getClass().getName() + " for " + key + " was replaced by " + handler.getClass().getName());
        }
    }

    public BodyPartHandler getFor(final String mimeType)
    {
        if(this.handlerMappings.containsKey(mimeType))
        {
            return this.handlerMappings.get(mimeType);
        }
        else
        {
            return new BodyPartHandler()
            {
                @Override
                public MimeType[] appliesTo()
                {
                    return new MimeType[0];
                }

                @Override
                public void process(final MimeType type, final Object content)
                {
                    log.info("Null handler called for " + type + ", content: " + content);
                }
            };
        }
    }

    public boolean handles(final String mimeType)
    {
        return this.handlerMappings.containsKey(mimeType);
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

    public void process(final MimeMultipart multipart)
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
                    if(handles(mimeType.getBaseType()))
                    {
                        final BodyPartHandler handler = getFor(mimeType.getBaseType());
                        handler.process(mimeType, bodypart.getContent());
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

    @Override
    public void process(final MimeType mimeType, final Object content)
    {
        if(MULTIPART_MIXED.match(mimeType) && content instanceof MimeMultipart)
        {
            process((MimeMultipart)content);
        }
        else
        {
            throw new IllegalArgumentException("Can't handle " + mimeType.getBaseType() + " for content: " + content);
        }
    }
}