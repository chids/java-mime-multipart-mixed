package se.pp.gustafson.marten.mime.examples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Test;

import se.pp.gustafson.marten.mime.HandlerMap;
import se.pp.gustafson.marten.mime.MultipartMixed;
import se.pp.gustafson.marten.mime.handlers.Callback;
import se.pp.gustafson.marten.mime.handlers.JpegHandler;
import se.pp.gustafson.marten.mime.handlers.PlainTextHandler;
import se.pp.gustafson.marten.mime.tests.TestUtil;

public class PlainTextAndGifExample extends JPanel
{
    private static final long serialVersionUID = 8478769362814237906L;
    private byte[] raw;

    @Test
    public void displayJpegAndVerifyPlainText()
    {
        final MultipartMixed mm = new MultipartMixed(new HandlerMap(new JpegHandler(new Callback<byte[]>()
        {
            @Override
            public void process(final byte[] data)
            {
                processImage(data);
            }
        }), new PlainTextHandler(new Callback<String>()
        {
            @Override
            public void process(final String data)
            {
                assertEquals("This is the plain-text body!\nThank you.", data);
            }
        })));
        mm.process(TestUtil.readTestFile(TestUtil.Files.JPEG_AND_PLAIN_TEXT_FILE));
    }

    public void processImage(byte[] data)
    {
        this.raw = data;
        final JFrame f = new JFrame();
        f.getContentPane().add(this);
        f.setBounds(0, 0, 100, 100);
        f.setVisible(true);
        try
        {
            Thread.sleep(1000);
        }
        catch(final InterruptedException e)
        {
            fail(e.getMessage());
        }
    }

    @Override
    public void paint(final Graphics g)
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
}