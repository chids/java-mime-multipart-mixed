package se.pp.gustafson.marten.mime;

import javax.activation.MimeType;

@Deprecated
public interface TypedBodyPartHandler<T>
{
    public MimeType appliesTo();
    public void process(T content);
}
