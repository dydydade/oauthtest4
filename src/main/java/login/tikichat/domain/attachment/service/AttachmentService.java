package login.tikichat.domain.attachment.service;

import login.tikichat.domain.attachment.model.Attachment;
import login.tikichat.domain.attachment.repository.AttachmentRepository;
import login.tikichat.domain.user.repository.UserRepository;
import login.tikichat.global.component.FileStorage;
import login.tikichat.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final FileStorage fileStorage;

    @Transactional
    public Long uploadFile(
            Long uploaderUserId,
            MultipartFile multipartFile
    ) throws IOException {
        final var ext = FileUtils.getExtByContentType(multipartFile.getContentType());
        final var user = this.userRepository.findById(uploaderUserId)
                .orElseThrow();

        final var path = FileUtils.getTimePath();
        final var filename = FileUtils.getRandomFilename(ext);

        this.fileStorage.upload(
                path + "/" + filename,
                multipartFile.getInputStream()
        );

        final var attachment = new Attachment(
                user,
                path,
                filename,
                multipartFile.getOriginalFilename(),
                ext
        );

        this.attachmentRepository.save(attachment);

        return attachment.getId();
    }
}
