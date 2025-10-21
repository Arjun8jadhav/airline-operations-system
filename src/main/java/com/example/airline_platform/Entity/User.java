package com.example.airline_platform.Entity;
import com.example.airline_platform.Entity.Role;
import jakarta.persistence.*;



@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "name", nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('USER', 'ADMIN') DEFAULT 'USER'")
    private Role role= Role.USER;
    // Getters and Setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public User() {}

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        if(!role.equals(Role.USER) && !role.equals(Role.ADMIN)) {
            this.role = Role.USER;
        }else {
            this.role = role;
        }
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = Role.USER;
    }


}
