package org.backrow.solt.security;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@ToString
public class CustomUserDetails extends User {
    private final Long memberId;
    private final String name;

    public CustomUserDetails(Long memberId, String name,
                             String username, String password, boolean enabled,
                             boolean accountNonExpired, boolean credentialsNonExpired,
                             boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.memberId = memberId;
        this.name = name;
    }

    public static class CustomUserDetailsBuilder {
        private Long memberId;
        private String name;
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

        public CustomUserDetailsBuilder name(String name) {
            this.name = name;
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
            return new CustomUserDetails(memberId, name, username, password, enabled, accountNonExpired,
                    credentialsNonExpired, accountNonLocked, authorities);
        }
    }
}