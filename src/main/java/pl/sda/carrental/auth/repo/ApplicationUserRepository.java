package pl.sda.carrental.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sda.carrental.auth.entity.ApplicationUserEntity;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUserEntity, Long> {
}
