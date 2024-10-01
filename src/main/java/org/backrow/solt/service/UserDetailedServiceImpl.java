package org.backrow.solt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.Member;
import org.backrow.solt.dto.login.LoginDTO;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.backrow.solt.repository.LoginRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

        UserBuilder userBuilder = null;

        if (!loginDTO.isPresent()) {
            throw new UsernameNotFoundException(email);
        } else {
            LoginDTO currentUser = loginDTO.get();
            userBuilder = org.springframework.security.core.userdetails.User.withUsername(currentUser.getEmail());
            userBuilder.password(currentUser.getPassword());
            userBuilder.roles("USER");
        }
        return userBuilder.build();
    }
}
