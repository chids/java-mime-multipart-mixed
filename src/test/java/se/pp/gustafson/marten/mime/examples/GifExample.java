package se.pp.gustafson.marten.mime.examples;

import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Test;

import se.pp.gustafson.marten.mime.BodyPartHandler;
import se.pp.gustafson.marten.mime.MultipartMixed;
import se.pp.gustafson.marten.mime.tests.TestUtil;

import com.sun.mail.util.BASE64DecoderStream;

public class GifExample extends JPanel implements BodyPartHandler
{
    private static final long serialVersionUID = 8478769362814237906L;
    private static final MimeType MIME_TYPE;
    private byte[] raw;

    static
    {
        try
        {
            MIME_TYPE = new MimeType("image/jpeg");
        }
        catch(final MimeTypeParseException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Test
    public void json() throws MimeTypeParseException
    {
        final MultipartMixed mm = new MultipartMixed(this);
        mm.process(TestUtil.readTestFile(TestUtil.Files.GIF_FILE));
    }

    @Override
    public void process(final MimeType mimeType, final Object content)
    {
        if(MIME_TYPE.match(mimeType) && content instanceof BASE64DecoderStream)
        {
            try
            {
                final BASE64DecoderStream decoder = (BASE64DecoderStream)content;
                this.raw = new byte[decoder.available()];
                decoder.read(this.raw);
                final JFrame f = new JFrame();
                f.getContentPane().add(this);
                f.setBounds(0, 0, 100, 100);
                f.setVisible(true);
                Thread.sleep(1000);
            }
            catch(final IOException e)
            {
                e.printStackTrace();
            }
            catch(final InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            throw new IllegalArgumentException("Can't handle " + mimeType.getBaseType() + ", content: " + content);
        }
    }

    @Override
    public void paint(Graphics g)
    {
        try
        {
            g.drawImage(ImageIO.read(new ByteArrayInputStream(this.raw)), 0, 0, this);
        }
        catch(final IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public MimeType[] appliesTo()
    {
        return new MimeType[] { MIME_TYPE };
    }
}