package se.pp.gustafson.marten.mime;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

public interface MimeTypeHandler<T>
{
    public MimeType[] appliesTo();

    public <A> void process(T data);

    public static final class Util
    {
        /**
         * Utility method to avoid having static initializer blocks with exception handling in all implementing classes.
         * @param mime
         * @return
         */
        public static MimeType mimeTypeForString(final String mime)
        {
            try
            {
                return new MimeType(mime);
            }
            catch(final MimeTypeParseException e)
            {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
