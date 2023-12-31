package kr.co.sugarmanager.userservice.user.vo;

public enum RoleType {
    ADMIN("ROLE_ADMIN"), MEMBER("ROLE_MEMBER"), GUEST("ROLE_GUEST");

    private String value;

    RoleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
