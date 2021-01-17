package com.probstin.learningspringbootaws.datastore;

import com.probstin.learningspringbootaws.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("1a536d5d-8327-4a61-8c3d-706bb0428a4f"), "mr. test", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("2112bb30-e13d-4670-ab8e-b3d294a5530e"), "mrs. test", null));
    }

    public List<UserProfile> getUserProfiles() {
        return USER_PROFILES;
    }
}

