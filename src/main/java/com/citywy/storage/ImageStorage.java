package com.citywy.storage;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;

/** 文件只以随机标识保存在独立目录；两种编辑器共用同一内容规则。 */
public final class ImageStorage {
    public static final String CONTEXT_KEY = ImageStorage.class.getName();
    public static final int MAX_BYTES = 1024 * 1024;
    public static final int MAX_PIXELS = 4 * 1024 * 1024;
    private static final byte[] PNG_HEADER = {(byte)137,80,78,71,13,10,26,10};
    private static final byte[] PNG_END = {0,0,0,0,73,69,78,68,(byte)174,66,96,(byte)130};
    private final Path root;

    public ImageStorage(Path directory) throws IOException {
        root = directory.toAbsolutePath().normalize();
        if (Files.isSymbolicLink(root)) throw new IOException("上传目录不能是符号链接");
        Files.createDirectories(root);
        if (!Files.isDirectory(root, LinkOption.NOFOLLOW_LINKS)) throw new IOException("上传目录无效");
    }

    public String store(String filename, String contentType, InputStream input) throws IOException {
        if (filename == null || !filename.matches("[A-Za-z0-9][A-Za-z0-9._-]{0,115}\\.[pP][nN][gG]") || filename.contains(".."))
            throw new UploadException(400, "文件名必须是没有路径的 PNG 文件名");
        if (!"image/png".equals(contentType)) throw new UploadException(415, "仅支持 PNG 图片");
        byte[] bytes = readBounded(input);
        validatePng(bytes);
        String id = UUID.randomUUID().toString() + ".png";
        Path temporary = Files.createTempFile(root, "upload-", ".part");
        try {
            Files.write(temporary, bytes);
            // 同卷硬链接原子发布且拒绝已有目标，不以覆盖式 rename 掩盖碰撞。
            Files.createLink(root.resolve(id), temporary);
        } finally {
            Files.delete(temporary);
        }
        return id;
    }

    public byte[] read(String id) throws IOException {
        if (id == null || !id.matches("[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}\\.png"))
            throw new UploadException(400, "无效的图片标识");
        Path file = root.resolve(id);
        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) throw new UploadException(404, "图片不存在");
        try (InputStream input = Files.newInputStream(file, LinkOption.NOFOLLOW_LINKS)) { return readBounded(input); }
    }

    private static byte[] readBounded(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        for (int count; (count = input.read(buffer)) != -1;) {
            if (output.size() + count > MAX_BYTES) throw new UploadException(413, "图片不能超过 1 MiB");
            output.write(buffer, 0, count);
        }
        return output.toByteArray();
    }

    private static void validatePng(byte[] bytes) throws IOException {
        if (bytes.length < PNG_HEADER.length + PNG_END.length
                || !Arrays.equals(PNG_HEADER, Arrays.copyOf(bytes, PNG_HEADER.length))
                || !Arrays.equals(PNG_END, Arrays.copyOfRange(bytes, bytes.length - PNG_END.length, bytes.length)))
            throw new UploadException(415, "PNG 格式无效或存在尾随内容");
        try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("png");
            if (!readers.hasNext()) throw new IllegalStateException("PNG 解码器不可用");
            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                int width = reader.getWidth(0), height = reader.getHeight(0);
                if (width <= 0 || height <= 0 || (long) width * height > MAX_PIXELS)
                    throw new UploadException(413, "图片像素不能超过 4194304");
                BufferedImage image = reader.read(0);
                if (image == null) throw new UploadException(415, "PNG 内容不能解码");
            } catch (IIOException error) {
                throw new UploadException(415, "PNG 内容损坏");
            } finally { reader.dispose(); }
        }
    }
}
