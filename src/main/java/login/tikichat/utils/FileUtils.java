package login.tikichat.utils;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileUtils {
    private static final DateTimeFormatter dtfhFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/hh");

    public static String getExtByContentType(String contentType) {
        MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
        try {
            MimeType mimeType = allTypes.forName(contentType);

            return mimeType.getExtension().replaceAll("\\.", "");
        } catch (MimeTypeException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTimePath() {
        return LocalDateTime.now().format(dtfhFormatter);
    }

    public static String getRandomFilename() {
        return UUID.randomUUID().toString();
    }

    public static String getRandomFilename(String ext) {
        return FileUtils.getRandomFilename() + "." + ext;
    }
}