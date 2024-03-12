/*
package pl.sda.carrental.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.sda.carrental.model.Employee;
import pl.sda.carrental.model.Rent;
import pl.sda.carrental.model.Reservation;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class RentRepositoryTest {
    @Autowired
    private RentRepository rentRepository;

    private Rent rent;

    @BeforeEach
    public void setUp() {
        rent = new Rent(1L, "", LocalDate.of(2024, 1, 27), new Employee(), new Reservation());
    }

    @Test
    void x() {
        //given
        //when
        Rent savedRent = rentRepository.save(rent);

        //then
        assertThat(savedRent).isNotNull();
        assertThat(savedRent.getRentId()).isNotNull();
        assertThat(savedRent).isEqualTo(rent);
    }

    @Test
    void y() {
        //given
        Rent savedRent = rentRepository.save(rent);
        savedRent.setComments("new comment");

        //when
        Rent updatedRent = rentRepository.save(rent);

        //then
        assertThat(updatedRent.getComments()).isEqualTo("new comment");
    }

    */
/*findById, update, findAll, findRentalsWithReservationId, findRentsByEmployeeId, delete *//*

}*/
