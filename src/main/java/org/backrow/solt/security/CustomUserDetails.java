package org.backrow.solt.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    private final long memberId;

    public CustomUserDetails(long memberId, String username, String password, boolean enabled,
                             boolean accountNonExpired, boolean credentialsNonExpired,
                             boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.memberId = memberId;
    }

    public long getMemberId() {
        return memberId;
    }

    public static class CustomUserDetailsBuilder {
        private long memberId;
        private String username;
        private String password;
        private boolean enabled = true;
        private boolean accountNonExpired = true;
        private boolean credentialsNonExpired = true;
        private boolean accountNonLocked = true;
        private Collection<? extends GrantedAuthority> authorities;

        public CustomUserDetailsBuilder memberId(long memberId) {
            this.memberId = memberId;
            return this;
        }

        public CustomUserDetailsBuilder username(String username) {
            this.username = username;
            return this;
        }

        public CustomUserDetailsBuilder password(String password) {
            this.password = password;
            return this;
        }

        public CustomUserDetailsBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public CustomUserDetailsBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public CustomUserDetailsBuilder accountNonExpired(boolean accountNonExpired) {
            this.accountNonExpired = accountNonExpired;
            return this;
        }

        public CustomUserDetailsBuilder credentialsNonExpired(boolean credentialsNonExpired) {
            this.credentialsNonExpired = credentialsNonExpired;
            return this;
        }

        public CustomUserDetailsBuilder accountNonLocked(boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        public CustomUserDetails build() {
            return new CustomUserDetails(memberId, username, password, enabled, accountNonExpired,
                    credentialsNonExpired, accountNonLocked, authorities);
        }
    }
}