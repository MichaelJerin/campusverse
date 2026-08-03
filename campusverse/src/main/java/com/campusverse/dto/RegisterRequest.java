package com.campusverse.dto;

import com.campusverse.model.DegreeLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required, Are you blind? did you expect me instruct you everytime")
    @Size(min = 8, message = "Password must be at least 8 character")
    private String password;

    private String branch;
    private DegreeLevel degreeLevel;
    private Integer batchStartYear;
    private Integer batchEndYear;
}
