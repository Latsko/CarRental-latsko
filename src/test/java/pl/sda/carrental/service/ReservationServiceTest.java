/*
package pl.sda.carrental.service;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.exceptionHandling.ReservationTimeCollisionException;
import pl.sda.carrental.model.*;
import pl.sda.carrental.model.DTO.ReservationDTO;
import pl.sda.carrental.model.enums.Status;
import pl.sda.carrental.repository.BranchRepository;
import pl.sda.carrental.repository.CarRepository;
import pl.sda.carrental.repository.ClientRepository;
import pl.sda.carrental.repository.ReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReservationServiceTest {

    @Mock
    private CarRepository carRepositoryMock;
    @Mock
    private ClientRepository clientRepositoryMock;
    @Mock
    private BranchRepository branchRepositoryMock;
    @Mock
    private ReservationRepository reservationRepositoryMock;
    @Mock
    private RevenueService revenueServiceMock;
    @InjectMocks
    private ReservationService reservationService;


    @Test
    void shouldGetAllReservations() {
        // given
        Reservation reservation1 = createReservation(1L, LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 5), new Car());
        Reservation reservation2 = createReservation(2L, LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 2, 5), new Car());

        when(reservationRepositoryMock.findAll()).thenReturn(Arrays.asList(reservation1, reservation2));

        // when
        List<ReservationDTO> result = reservationService.getAllReservations();

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void shouldGetReservationsWhenEmpty() {

        when(reservationRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        // when
        List<ReservationDTO> result = reservationService.getAllReservations();

        // then
        assertThat(result.size()).isEqualTo(0);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldSaveFirstReservation() {
        //given
        Branch branch1 = createBranch(1L);
        Branch branch2 = createBranch(2L);
        Car car = createCar(1L, "Sedan", new BigDecimal(100), "RED", "Volvo");
        Client client = createClient("Address 1", "Name 1", "Email1", "Surname 1");
        ReservationDTO reservationDto = createReservationDTO(null, 1L,
                LocalDate.of(2023, 11, 20),
                LocalDate.of(2023, 11, 22),
                2L);

        when(branchRepositoryMock.findById(1L)).thenReturn(Optional.of(branch1));
        when(branchRepositoryMock.findById(2L)).thenReturn(Optional.of(branch2));
        when(carRepositoryMock.findById(1L)).thenReturn(Optional.of(car));
        when(clientRepositoryMock.findById(1L)).thenReturn(Optional.of(client));

        //when
        reservationService.saveReservation(reservationDto);

        //then
        verify(carRepositoryMock).findById(1L);
        verify(branchRepositoryMock).findById(1L);
        verify(branchRepositoryMock).findById(2L);
        verify(clientRepositoryMock).findById(1L);
        verify(revenueServiceMock).updateRevenue(1L, new BigDecimal("300.00"));

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepositoryMock).save(captor.capture());
        Reservation result = captor.getValue();

        assertThat(result.getEndDate()).isEqualTo("2023-11-22");
        assertThat(result.getStartDate()).isEqualTo("2023-11-20");
        assertThat(result.getPrice().intValue()).isEqualTo(300);
    }

    @Test
    void shouldSaveReservationWithDateValidations() {
        //given
        Branch branch = createBranch(1L);
        Car car = createCar(1L, "Sedan", new BigDecimal(100), "RED", "Volvo");
        Client client = createClient("Address 1", "Name 1", "Email1", "Surname 1");
        ReservationDTO reservationDtoToSave = createReservationDTO(null, 1L,
                LocalDate.of(2023, 11, 20),
                LocalDate.of(2023, 11, 22),
                1L);
        Reservation reservation1 = createReservation(1L, LocalDate.of(2023, 11, 10),
                LocalDate.of(2023, 11, 15), car);
        Reservation reservation2 = createReservation(1L, LocalDate.of(2023, 11, 25),
                LocalDate.of(2023, 11, 29), car);

        car.setReservations(Set.of(reservation1, reservation2));

        when(branchRepositoryMock.findById(1L)).thenReturn(Optional.of(branch));
        when(carRepositoryMock.findById(1L)).thenReturn(Optional.of(car));
        when(clientRepositoryMock.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepositoryMock.findAll()).thenReturn(List.of(reservation1, reservation2));

        //when
        reservationService.saveReservation(reservationDtoToSave);

        //then
        verify(carRepositoryMock).findById(1L);
        verify(branchRepositoryMock, times(2)).findById(1L);
        verify(clientRepositoryMock).findById(1L);
        verify(revenueServiceMock).updateRevenue(1L, new BigDecimal(200));

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepositoryMock).save(captor.capture());
        Reservation result = captor.getValue();

        assertThat(result.getEndDate()).isEqualTo("2023-11-22");
        assertThat(result.getStartDate()).isEqualTo("2023-11-20");
        assertThat(result.getPrice().intValue()).isEqualTo(200);
    }

    @Test
    void shouldNotFindCarWhenTriesToSaveReservation() {
        //given
        ReservationDTO reservationDtoToSave = createReservationDTO(null, 1L,
                LocalDate.of(2023, 11, 20),
                LocalDate.of(2023, 11, 22),
                1L);
        when(carRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when
        ThrowableAssert.ThrowingCallable callable = () ->
                reservationService.saveReservation(reservationDtoToSave);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("No car under that ID");
    }

    @Test
    void shouldNotFindClientWhenTriesToSaveReservation() {
        //given
        Car car = createCar(1L, "Cabriolet", new BigDecimal(150), "WHITE", "Audi");
        ReservationDTO reservationDtoToSave = createReservationDTO(null, 1L,
                LocalDate.of(2023, 11, 20),
                LocalDate.of(2023, 11, 22),
                1L);
        when(carRepositoryMock.findById(1L)).thenReturn(Optional.of(car));
        when(clientRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when
        ThrowableAssert.ThrowingCallable callable = () ->
                reservationService.saveReservation(reservationDtoToSave);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("No customer under that ID");
    }

    @Test
    void shouldThrowReservationTimeCollisionExceptionWhenTriesToSaveReservation() {
        //given
        Car car = createCar(1L, "Cabriolet", new BigDecimal(150), "WHITE", "Audi");
        Client client = createClient("Test Address", "Test Name", "TestEmail", "Test Surname");
        ReservationDTO reservationDtoToSave = createReservationDTO(null, 1L,
                LocalDate.of(2023, 11, 20),
                LocalDate.of(2023, 11, 22),
                1L);
        Reservation reservation1 = createReservation(1L, LocalDate.of(2023, 11, 20),
                LocalDate.of(2023, 11, 24), car);
        Reservation reservation2 = createReservation(1L, LocalDate.of(2023, 11, 25),
                LocalDate.of(2023, 11, 29), car);

        car.setReservations(Set.of(reservation1, reservation2));

        when(carRepositoryMock.findById(1L)).thenReturn(Optional.of(car));
        when(clientRepositoryMock.findById(1L)).thenReturn(Optional.of(client));


        //when
        ThrowableAssert.ThrowingCallable callable = () ->
                reservationService.saveReservation(reservationDtoToSave);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ReservationTimeCollisionException.class)
                .hasMessage("Car cannot be reserved for given time period!");
    }

    @Test
    void shouldThrowReservationTimeCollisionExceptionWhenRentBranchIsNotAvailable() {
        //given
        Branch branch = createBranch(1L);
        Car car = createCar(1L, "Cabriolet", new BigDecimal(150), "WHITE", "Audi");
        Client client = createClient("Test Address", "Test Name", "TestEmail", "Test Surname");
        ReservationDTO reservationDtoToSave = createReservationDTO(null, 1L,
                LocalDate.of(2023, 11, 15),
                LocalDate.of(2023, 11, 22),
                1L);
        Reservation reservation1 = createReservation(1L, LocalDate.of(2023, 11, 11),
                LocalDate.of(2023, 11, 14), car);
        Reservation reservation2 = createReservation(1L, LocalDate.of(2023, 11, 25),
                LocalDate.of(2023, 11, 29), car);

        car.setReservations(Set.of(reservation1, reservation2));

        when(branchRepositoryMock.findById(1L)).thenReturn(Optional.of(branch));
        when(carRepositoryMock.findById(1L)).thenReturn(Optional.of(car));
        when(clientRepositoryMock.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepositoryMock.findAll()).thenReturn(List.of(reservation1, reservation2));

        //when
        ThrowableAssert.ThrowingCallable callable = () ->
                reservationService.saveReservation(reservationDtoToSave);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ReservationTimeCollisionException.class)
                .hasMessage("Car can be rented only from Branch #null for the selected date!");
    }

    @Test
    void shouldThrowReservationTimeCollisionExceptionWhenReturnBranchIsNotAvailable() {
        //given
        Branch branch = createBranch(1L);
        Car car = createCar(1L, "Cabriolet", new BigDecimal(150), "WHITE", "Audi");
        Client client = createClient("Test Address", "Test Name", "TestEmail", "Test Surname");
        ReservationDTO reservationDtoToSave = createReservationDTO(null, 1L,
                LocalDate.of(2023, 11, 15),
                LocalDate.of(2023, 11, 22),
                1L);
        Reservation reservation1 = createReservation(1L, LocalDate.of(2023, 11, 11),
                LocalDate.of(2023, 11, 13), car);
        Reservation reservation2 = createReservation(1L, LocalDate.of(2023, 11, 23),
                LocalDate.of(2023, 11, 29), car);

        car.setReservations(Set.of(reservation1, reservation2));

        when(branchRepositoryMock.findById(1L)).thenReturn(Optional.of(branch));
        when(carRepositoryMock.findById(1L)).thenReturn(Optional.of(car));
        when(clientRepositoryMock.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepositoryMock.findAll()).thenReturn(List.of(reservation1, reservation2));

        //when
        ThrowableAssert.ThrowingCallable callable = () ->
                reservationService.saveReservation(reservationDtoToSave);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ReservationTimeCollisionException.class)
                .hasMessage("Car can be returned only to Branch #null for the selected date!");
    }

    @Test
    void shouldEditReservation() {
        //given
        ReservationDTO reservationDto = createReservationDTO(null, 2L,
                LocalDate.of(2023, 11, 20),
                LocalDate.of(2023, 11, 22),
                1L);
        Branch branch = createBranch(1L);
        Car car = createCar(2L, "Crossover", new BigDecimal(100), "BLUE", "Mazda");
        Client client = createClient("Client Address", "Client Name", "ClientEmail", "Client Surname");
        Reservation reservation = createReservation(1L, LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 19), car);

        when(branchRepositoryMock.findById(1L)).thenReturn(Optional.of(branch));
        when(carRepositoryMock.findById(2L)).thenReturn(Optional.of(car));
        when(clientRepositoryMock.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(reservation));

        //when
        reservationService.editReservation(1L, reservationDto);

        //then
        verify(reservationRepositoryMock).findById(1L);
        verify(carRepositoryMock).findById(2L);
        verify(branchRepositoryMock, times(2)).findById(1L);
        verify(clientRepositoryMock).findById(1L);
        verify(revenueServiceMock).updateRevenue(1L, new BigDecimal(200));

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepositoryMock).save(captor.capture());
        Reservation result = captor.getValue();

        assertThat(result.getCar()).isEqualTo(car);
        assertThat(result.getEndDate()).isEqualTo("2023-11-22");
        assertThat(result.getStartDate()).isEqualTo("2023-11-20");
        assertThat(result.getPrice().intValue()).isEqualTo(200);
    }

    @Test
    void shouldNotFindReservationByIdToEdit() {
        //given
        when(reservationRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());
        ReservationDTO reservationDTO =
                createReservationDTO(1L, 1L,
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 1, 2),
                        1L);

        //when
        ThrowableAssert.ThrowingCallable callable = () -> reservationService.editReservation(1L, reservationDTO);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("No reservation under ID #1");
    }

    @Test
    void shouldDeleteReservationById() {
        //given
        Reservation reservation = createReservation(1L, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5), new Car());
        when(reservationRepositoryMock.findById(anyLong())).thenReturn(Optional.of(reservation));

        //when
        reservationService.deleteReservationById(1L);

        //then
        verify(reservationRepositoryMock).delete(reservation);
    }

    @Test
    void shouldNotDeleteReservationById() {
        //given
        when(reservationRepositoryMock.findById(anyLong())).thenReturn(Optional.empty());

        //when
        ThrowableAssert.ThrowingCallable callable = () -> reservationService.deleteReservationById(1L);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("No reservation under ID #1");

    }

    @Test
    void shouldCancelReservationLessOrExactlyThanTwoDayPrior() {
        //given
        LocalDate today = LocalDate.now();
        Branch branch = createBranch(1L);
        Car car = createCar(1L, "bodyStyle", new BigDecimal(200), "COLOR", "Brand");
        car.setBranch(branch);
        Reservation reservation = createReservation(1L, today.plusDays(3), today.plusDays(5), car);
        Rent rent = createRent(reservation.getStartDate(), reservation);
        reservation.setRent(rent);
        reservation.setPrice(new BigDecimal(200));
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(reservation));

        //when
        reservationService.cancelReservationById(1L);

        //then
        verify(revenueServiceMock).updateRevenue(1L, new BigDecimal(-200));
    }

    @Test
    void shouldCancelReservationMoreThanTwoDayPrior() {
        //given
        LocalDate today = LocalDate.now();
        Branch branch = createBranch(1L);
        Car car = createCar(1L, "bodyStyle", new BigDecimal(200), "COLOR", "Brand");
        car.setBranch(branch);
        Reservation reservation = createReservation(1L, today.plusDays(2), today.plusDays(5), car);
        Rent rent = createRent(reservation.getStartDate(), reservation);
        reservation.setRent(rent);
        reservation.setPrice(new BigDecimal(200));
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(reservation));

        //when
        reservationService.cancelReservationById(1L);

        //then
        verify(revenueServiceMock).updateRevenue(1L, new BigDecimal("-160.0"));
    }

    @Test
    void shouldCancelReservationMoreThanTwoDayPriorWithoutRent() {
        //given
        LocalDate today = LocalDate.now();
        Branch branch = createBranch(1L);
        Car car = createCar(1L, "bodyStyle", new BigDecimal(200), "COLOR", "Brand");
        car.setBranch(branch);
        Reservation reservation = createReservation(1L, today.plusDays(2), today.plusDays(5), car);
        reservation.setPrice(new BigDecimal(200));
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.of(reservation));

        //when
        reservationService.cancelReservationById(1L);

        //then
        verify(revenueServiceMock).updateRevenue(1L, new BigDecimal(-200));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenThereIsNoReservationUnderGivenId() {
        // given
        when(reservationRepositoryMock.findById(1L)).thenReturn(Optional.empty());

        //when
        ThrowableAssert.ThrowingCallable callable = () ->
                reservationService.cancelReservationById(1L);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("No reservation under ID #1");
    }

    private Reservation createReservation(Long id, LocalDate start, LocalDate end, Car car) {
        return new Reservation()
                .withReservationId(id)
                .withCar(car)
                .withClient(new Client())
                .withPrice(new BigDecimal(0))
                .withStartBranch(new Branch())
                .withEndBranch(new Branch())
                .withStartDate(start)
                .withEndDate(end);
    }

    private Branch createBranch(Long id) {
        return new Branch()
                .withBranchId(id)
                .withAddress("Address 1")
                .withCars(new HashSet<>())
                .withClients(new HashSet<>())
                .withEmployees(new HashSet<>())
                .withName("Name 1")
                .withRevenue(new Revenue()
                        .withRevenueId(1L)
                        .withTotalAmount(new BigDecimal(10000)))
                .withManagerId(1L);
    }

    private Car createCar(Long id, String bodyStyle, BigDecimal price, String color, String make) {
        return new Car()
                .withCarId(id)
                .withBodyStyle(bodyStyle)
                .withPrice(price)
                .withColour(color)
                .withMake(make)
                .withBranch(createBranch(1L))
                .withMileage(0)
                .withYear(2005)
                .withStatus(Status.AVAILABLE);
    }

    private Client createClient(String address, String name, String email, String surname) {
        return new Client()
                .withClientId(1L)
                .withAddress(address)
                .withName(name)
                .withEmail(email)
                .withBranch(new Branch())
                .withSurname(surname);
    }


    private ReservationDTO createReservationDTO(Long id, Long carId, LocalDate start, LocalDate end, Long endBranchId) {
        return new ReservationDTO(
                id,
                1L,
                carId,
                start,
                end,
                1L,
                endBranchId,
                null,
                null
        );
    }

    private Rent createRent(LocalDate date, Reservation reservation) {
        return new Rent()
                .withRentId(1L)
                .withRentDate(date)
                .withReservation(reservation);
    }
}*/
