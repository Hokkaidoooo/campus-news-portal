package com.citywy.storage;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import org.junit.*;
import static org.junit.Assert.*;

public class ImageStorageTest {
    private Path root;
    private ImageStorage storage;
    private byte[] png;
    @Before public void create() throws Exception {
        root = Files.createTempDirectory("news-upload-test-");
        storage = new ImageStorage(root);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        assertTrue(ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", bytes));
        png = bytes.toByteArray();
    }
    @After public void cleanup() throws Exception {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(root)) {
            for (Path file : files) { assertEquals(root, file.getParent()); Files.delete(file); }
        }
        Files.delete(root);
    }
    @Test public void sameNamesHaveDistinctIdsAndExactReadableBytes() throws Exception {
        String first = storage.store("photo.png", "image/png", new ByteArrayInputStream(png));
        String second = storage.store("photo.png", "image/png", new ByteArrayInputStream(png));
        assertNotEquals(first, second);
        assertArrayEquals(png, storage.read(first));
        assertArrayEquals(png, storage.read(second));
    }
    @Test public void traversalAndFakePngCannotCreateAnyFile() throws Exception {
        for (String name : Arrays.asList("../photo.png", "a/b.png", "C:\\a.png", "evil.jsp")) {
            assertThrows(UploadException.class, () -> storage.store(name, "image/png", new ByteArrayInputStream(png)));
        }
        assertThrows(UploadException.class, () -> storage.store("bad.png", "image/png", new ByteArrayInputStream("fake".getBytes("UTF-8"))));
        byte[] trailing = Arrays.copyOf(png, png.length + 1);
        assertThrows(UploadException.class, () -> storage.store("tail.png", "image/png", new ByteArrayInputStream(trailing)));
        assertThrows(UploadException.class, () -> storage.read("../photo.png"));
        assertEquals(0, Objects.requireNonNull(root.toFile().list()).length);
    }
    @Test public void oversizedAndInterruptedInputLeaveNoPartialFile() throws Exception {
        UploadException error = assertThrows(UploadException.class, () -> storage.store("big.png", "image/png", new ByteArrayInputStream(new byte[ImageStorage.MAX_BYTES + 1])));
        assertEquals(413, error.getStatus());
        InputStream interrupted = new InputStream() {
            @Override public int read() throws IOException { throw new IOException("注入读取失败"); }
        };
        assertThrows(IOException.class, () -> storage.store("broken.png", "image/png", interrupted));
        assertEquals(0, Objects.requireNonNull(root.toFile().list()).length);
    }
}
