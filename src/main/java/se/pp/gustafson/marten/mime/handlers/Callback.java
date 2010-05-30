package se.pp.gustafson.marten.mime.handlers;

public interface Callback<T>
{
    public void process(T data);
}
