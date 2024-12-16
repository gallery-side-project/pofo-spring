package org.pofo.domain.rds.domain.user;

public enum UserSocialType {
    GITHUB;

    public static UserSocialType getSocialType(String registrationId) {
        if (registrationId.equals("github")) {
            return UserSocialType.GITHUB;
        }
        throw new IllegalArgumentException("Unknown registration id: " + registrationId);
    }
}
