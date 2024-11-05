package org.pofo.domain.user;

public enum UserSocialType {
    GITHUB;

    public static UserSocialType getSocialType(String registrationId) {
        switch (registrationId) {
            case "github":
                return UserSocialType.GITHUB;
        }
        throw new IllegalArgumentException("Unknown registration id: " + registrationId);
    }
}
