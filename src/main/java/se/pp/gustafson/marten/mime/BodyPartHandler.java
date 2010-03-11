package se.pp.gustafson.marten.mime;

import javax.activation.MimeType;

public interface BodyPartHandler
{
    public MimeType[] appliesTo();
    public void process(MimeType mimeType, Object content);
}
