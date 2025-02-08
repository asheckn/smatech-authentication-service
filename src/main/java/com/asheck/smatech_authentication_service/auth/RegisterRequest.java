package com.asheck.smatech_authentication_service.auth;


public record RegisterRequest(String firstName, String lastName, String email, String password, String phoneNumber) {

}
