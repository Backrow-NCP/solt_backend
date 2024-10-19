package org.backrow.solt.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.member.Member;
import org.backrow.solt.dto.login.LoginDTO;
import org.backrow.solt.repository.member.LoginRepository;
import org.backrow.solt.security.CustomUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserDetailedServiceImpl implements UserDetailsService {

    private final LoginRepository loginRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = loginRepository.findByEmail(email);
        Optional<LoginDTO> loginDTO = Optional.ofNullable(modelMapper.map(member, LoginDTO.class));

        if (loginDTO.isEmpty()) {
            throw new UsernameNotFoundException(email);
        } else {
            LoginDTO login = loginDTO.get();
            return new CustomUserDetails.CustomUserDetailsBuilder()
                    .memberId(member.getMemberId())
                    .username(login.getEmail())
                    .password(login.getPassword())
                    .name(member.getName())
                    .authorities(Collections.singleton(new SimpleGrantedAuthority("USER")))
                    .build();
        }
    }
}
