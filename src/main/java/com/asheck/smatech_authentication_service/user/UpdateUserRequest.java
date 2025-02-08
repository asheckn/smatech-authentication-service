package com.asheck.smatech_authentication_service.user;

public record UpdateUserRequest(String firstName, String lastName, String email, String phoneNumber, String address) {
}
