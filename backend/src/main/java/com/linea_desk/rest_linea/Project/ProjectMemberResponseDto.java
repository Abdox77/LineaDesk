package com.linea_desk.rest_linea.Project;
public class ProjectMemberResponseDto {
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String role;
    public ProjectMemberResponseDto() {}
    public ProjectMemberResponseDto(ProjectMember member) {
        this.id = member.getId();
        this.userId = member.getUser().getUserId();
        this.username = member.getUser().getDisplayName();
        this.email = member.getUser().getEmail();
        this.role = member.getRole().name();
    }
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
