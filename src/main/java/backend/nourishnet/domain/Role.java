package backend.nourishnet.domain;

public enum Role {
    DONATOR, ONG, ADMIN;

    public String asAuthority() {
        return "ROLE_" + this.name();
    }
}
