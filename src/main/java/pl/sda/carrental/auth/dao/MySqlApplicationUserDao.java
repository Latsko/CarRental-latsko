package pl.sda.carrental.auth.dao;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import pl.sda.carrental.auth.entity.ApplicationUserEntity;
import pl.sda.carrental.auth.repo.ApplicationUserRepository;
import pl.sda.carrental.auth.enums.ApplicationUserRole;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository("mysql")
public class MySqlApplicationUserDao implements ApplicationUserDao {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserRepository applicationUserRepository;

    public MySqlApplicationUserDao(PasswordEncoder passwordEncoder, ApplicationUserRepository applicationUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return getApplicationUsers()
                .stream()
                .filter(applicationUser -> username.equals(applicationUser.getUsername()))
                .findFirst();
    }

    private List<ApplicationUser> getApplicationUsers() {
        return applicationUserRepository
                .findAll()
                .stream()
                .map(this::mapEntityToModel).collect(Collectors.toList());
    }

    private ApplicationUser mapEntityToModel(ApplicationUserEntity entity) {
        return new ApplicationUser(
                entity.getUsername(),
                passwordEncoder.encode(entity.getPassword()),
                entity.getAuthorities().stream()
                        .flatMap(authority -> ApplicationUserRole.valueOf(authority.getAuthority()).getGrantedAuthorities().stream())
                        .collect(Collectors.toSet()),
                entity.isAccountNonExpired(),
                entity.isAccountNonLocked(),
                entity.isCredentialsNonExpired(),
                entity.isEnabled()
        );
    }
}
