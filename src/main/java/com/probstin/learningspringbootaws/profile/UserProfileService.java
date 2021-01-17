package com.probstin.learningspringbootaws.profile;

import com.probstin.learningspringbootaws.filestore.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final FileStore fileStore;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, FileStore fileStore) {
        this.userProfileRepository = userProfileRepository;
        this.fileStore = fileStore;
    }

    @Value("${aws.uploadBucket}")
    private String AWS_UPLOAD_BUCKET;


    List<UserProfile> getUserProfiles() {
        return userProfileRepository.getUserProfiles();
    }

    public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {

        // check if image is not empty
        isFileEmpty(file);

        // check if file is an image
        isFileAnImage(file);

        // check if the user exists (filtering entire list because we aren't using a JPA repo)
        UserProfile user = getUserProfileOrThrow(userProfileId);

        // grab metadata from file
        Map<String, String> metadata = extractMetadata(file);

        // store in s3
        String bucketPath = String.format("%s/%s", AWS_UPLOAD_BUCKET, user.getUserProfileId());
        String fileName = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());

        try {
            fileStore.save(bucketPath, fileName, Optional.of(metadata), file.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to upload file to S3: ", e);
        }

        // update the database (userProfileImageLink) with S3 fileName
        user.setUserProfileImageLink(fileName);
    }

    byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile user = getUserProfileOrThrow(userProfileId);

        String path = String.format("%s/%s", AWS_UPLOAD_BUCKET, user.getUserProfileId());

        return user.getUserProfileImageLink()
                .map(key -> fileStore.download(path, key))
                .orElse(new byte[0]);
    }

    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    private UserProfile getUserProfileOrThrow(UUID userProfileId) {
        return userProfileRepository
                .getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find user with ID: " + userProfileId));
    }

    private void isFileAnImage(MultipartFile file) {
        if (!Arrays
                .asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType())
                .contains(file.getContentType())
        ) {
            throw new IllegalStateException("File must be an image [JPEG, PNG, GIF]: " + file.getContentType());
        }
    }

    private void isFileEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("File cannot be empty");
        }
    }

}
