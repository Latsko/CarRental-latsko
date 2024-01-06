package pl.sda.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.sda.carrental.model.Rent;

import java.util.List;

public interface RentRepository extends JpaRepository<Rent, Long> {
    @Query("SELECT res.reservationId FROM Rent r JOIN r.reservation res WHERE res.reservationId = :reservationId")
    List<Long> findRentalsWithReservationId(Long reservationId);

    @Query("SELECT r FROM Rent r JOIN r.employee e WHERE e.employee_id = :employeeId")
    List<Rent> findRentsByEmployeeId(Long employeeId);
}
