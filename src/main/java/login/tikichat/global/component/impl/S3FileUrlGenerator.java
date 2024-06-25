package login.tikichat.global.component.impl;

import com.amazonaws.services.s3.AmazonS3;
import login.tikichat.global.component.FileUrlGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class S3FileUrlGenerator implements FileUrlGenerator {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Override
    public URL generatePublicUrl(String path) throws MalformedURLException {
        String urlString = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, path);
        return new URL(urlString);
    }
}
