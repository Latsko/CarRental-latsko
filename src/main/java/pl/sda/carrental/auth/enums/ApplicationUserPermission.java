package pl.sda.carrental.auth.enums;

import lombok.Getter;

@Getter
public enum ApplicationUserPermission {
    STUDENT_READ("student:read"),
    STUDENT_WRITE("student:write"),
    COURSE_READ("course:read"),
    COURSE_WRITE("course:write"),
    USERS_WRITE("users:write");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

}
