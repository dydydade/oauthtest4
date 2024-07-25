package login.tikichat.global.component;

import java.net.MalformedURLException;
import java.net.URL;

public interface FileUrlGenerator {
    URL generatePublicUrl(String path) throws MalformedURLException;
}