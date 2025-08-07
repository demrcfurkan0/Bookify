package com.bookify.user_service;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Schema(description = "User model")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "User ID", example = "1") /*  */
    private Long id;

    @Column(name = "first_name")
    @Schema(description = "First Name", example = "Furkan")
    private String firstName;

    @Column(name = "last_name")
    @Schema(description = "Last Name", example = "Demirci")
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    @Schema(description = "E-mail ", example = "furkan.demirci@example.com", required = true)
    private String email;
}