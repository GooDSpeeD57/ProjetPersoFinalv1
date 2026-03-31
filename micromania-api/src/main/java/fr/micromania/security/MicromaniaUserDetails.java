package fr.micromania.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MicromaniaUserDetails {
    private final Long   userId;
    private final String email;
    private final String userType;   // CLIENT | EMPLOYE

    public boolean isClient()  { return "CLIENT".equals(userType); }
    public boolean isEmploye() { return "EMPLOYE".equals(userType); }
}
