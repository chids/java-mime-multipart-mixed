package se.pp.gustafson.marten.mime;

import java.util.HashMap;
import java.util.Map;

import javax.activation.MimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HandlerMap
{
    static final Logger log = LoggerFactory.getLogger(MultipartMixed.class);
    private final Map<String, MimeTypeHandler<?>> handlerMappings;

    public HandlerMap(final MimeTypeHandler<?>... handlers)
    {
        this.handlerMappings = new HashMap<String, MimeTypeHandler<?>>();
        for(final MimeTypeHandler<?> handler : handlers)
        {
            for(final MimeType mimeType : handler.appliesTo())
            {
                addHandler(mimeType.getBaseType(), handler);
            }
        }
    }

    public <T> void addHandler(final String key, final MimeTypeHandler<T> handler)
    {
        final MimeTypeHandler<?> oldHandler = this.handlerMappings.put(key, handler);
        if(oldHandler == null)
        {
            log.debug("Added handler for " + key + ": " + handler.getClass().getName());
        }
        else
        {
            log.debug("Handler: " + oldHandler.getClass().getName() + " for " + key + " was replaced by " + handler.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    public MimeTypeHandler getFor(final String mimeType)
    {
        if(this.handlerMappings.containsKey(mimeType))
        {
            return this.handlerMappings.get(mimeType);
        }
        else
        {
            return new MimeTypeHandler<Object>()
            {
                @Override
                public MimeType[] appliesTo()
                {
                    return new MimeType[0];
                }

                @Override
                public <A> void process(final Object data)
                {
                    log.info("Null handler called for " + mimeType + ", content: " + data);
                }
            };
        }
    }

    public boolean handles(final String mimeType)
    {
        return this.handlerMappings.containsKey(mimeType);
    }
}
