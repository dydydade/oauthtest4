package login.tikichat.domain.attachment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import login.tikichat.domain.user.model.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4000, nullable = false)
    private String path;

    @Column(length = 1000, nullable = false)
    private String filename;

    @Column(length = 1000, nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String ext;

    @ManyToOne()
    @JoinColumn(name = "uploader_user_id", nullable = false)
    private User uploaderUser;

    public Attachment(
            User uploaderUser,
            String path,
            String filename,
            String originalFilename,
            String ext
    ) {
        this.filename = filename;
        this.uploaderUser = uploaderUser;
        this.path = path;
        this.originalFilename = originalFilename;
        this.ext = ext;
    }
}
