/*
package pl.sda.carrental.service;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sda.carrental.exceptionHandling.ObjectAlreadyAssignedToBranchException;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.model.*;
import pl.sda.carrental.model.DTO.CarDTO;
import pl.sda.carrental.model.enums.Position;
import pl.sda.carrental.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
class BranchServiceTest {
    //@Mock
    private final BranchRepository branchRepositoryMock = mock(BranchRepository.class);
    //@Mock
    private final CarRepository carRepositoryMock = mock(CarRepository.class);
    //@Mock
    private final EmployeeRepository employeeRepositoryMock = mock(EmployeeRepository.class);
    //@Mock
    private final ReservationRepository reservationRepositoryMock = mock(ReservationRepository.class);
    //@Mock
    private final CarRentalRepository carRentalRepositoryMock = mock(CarRentalRepository.class);

    //@InjectMocks
    private final BranchService branchService = new BranchService(branchRepositoryMock, carRepositoryMock, employeeRepositoryMock, reservationRepositoryMock, carRentalRepositoryMock);

    private CarRental carRental;
    private Branch branch;
    private Branch branchWithData;
    private Employee manager;
    private Employee employee;
    private List<Reservation> reservations;
    private Car car;
    private Branch branch1;
    private Branch branch2;


    @BeforeEach
    public void setUp() {
        carRental = new CarRental();
        branch = new Branch();
        branchWithData = new Branch(111L, "name", "address", 1L, new HashSet<>(), new HashSet<>(), new HashSet<>(), carRental, new Revenue());
        manager = new Employee(1L, "", "", null, null);
        employee = manager;
        reservations = new ArrayList<>() {
            {
                add(new Reservation().withEndBranch(branchWithData));
                add(new Reservation().withStartBranch(branchWithData));
                add(new Reservation());
            }
        };
        for (Reservation reservation : reservations) {
            reservation.setStartBranch(branchWithData);
            reservation.setEndBranch(branchWithData);
        }
        car = new Car();

        branch1 = new Branch();
        branch2 = new Branch();
        branch1.setBranchId(1L);
        branch2.setBranchId(2L);

        car.setBranch(branch1);
        Set<Reservation> reservationsForCar = new HashSet<>();
        reservationsForCar.add(new Reservation(1L,
                new Client(),
                car,
                LocalDate.of(2024, 1, 27),
                LocalDate.of(2024, 1, 29),
                new BigDecimal(200),
                branch1, branch2,
                null, null)
        );
        reservationsForCar.add(new Reservation(2L,
                new Client(),
                car,
                LocalDate.of(2024, 2, 3),
                LocalDate.of(2024, 2, 8),
                new BigDecimal(200),
                branch2, branch2,
                null, null)
        );
        reservationsForCar.add(new Reservation(3L,
                new Client(),
                car,
                LocalDate.of(2024, 2, 11),
                LocalDate.of(2024, 2, 17),
                new BigDecimal(200),
                branch2, branch1,
                null, null)
        );

        car.setReservations(reservationsForCar);
        Set<Car> reservedCarsForBranch1 = new HashSet<>();
        reservedCarsForBranch1.add(car);
        branch1.setCars(reservedCarsForBranch1);

        Car anotherCar = new Car();
        anotherCar.setBranch(branch2);
        Set<Reservation> reservationsForAnotherCar = new HashSet<>();
        reservationsForAnotherCar.add(new Reservation(1L,
                new Client(),
                anotherCar,
                LocalDate.of(2024, 1, 27),
                LocalDate.of(2024, 1, 29),
                new BigDecimal(200),
                branch1, branch2,
                null, null)
        );
        reservationsForAnotherCar.add(new Reservation(2L,
                new Client(),
                anotherCar,
                LocalDate.of(2024, 2, 3),
                LocalDate.of(2024, 2, 8),
                new BigDecimal(200),
                branch2, branch2,
                null, null)
        );
        reservationsForAnotherCar.add(new Reservation(3L,
                new Client(),
                anotherCar,
                LocalDate.of(2024, 2, 11),
                LocalDate.of(2024, 2, 17),
                new BigDecimal(200),
                branch2, branch1,
                null, null)
        );
        anotherCar.setReservations(reservationsForAnotherCar);
        Set<Car> reservedCarsForBranch2 = new HashSet<>();
        reservedCarsForBranch2.add(anotherCar);
        branch2.setCars(reservedCarsForBranch2);

    }

    @Test
    void shouldSaveBranchWithoutManager() {
        //given
        given(branchRepositoryMock.save(branch)).willReturn(branch);
        given(carRentalRepositoryMock.findAll()).willReturn(List.of(carRental));

        //when
        Branch savedBranch = branchService.addBranch(branch);

        //then
        assertThat(savedBranch).isNotNull();
        verify(branchRepositoryMock, never()).findById(anyLong());
        verify(carRentalRepositoryMock, times(2)).findAll();
    }

    @Test
    void shouldSaveBranchWithManager() {
        //given
        branch.setManagerId(1L);
        given(branchRepositoryMock.save(branch)).willReturn(branch);
        given(employeeRepositoryMock.findById(anyLong())).willReturn(Optional.of(manager));
        given(carRentalRepositoryMock.findAll()).willReturn(List.of(carRental));

        //when
        Branch savedBranch = branchService.addBranch(branch);

        //then
        assertThat(savedBranch).isNotNull();
        assertThat(savedBranch.getManagerId()).isNotNull();
        verify(employeeRepositoryMock, times(1)).findById(anyLong());
        verify(employeeRepositoryMock, times(1)).save(manager);
        verify(carRentalRepositoryMock, times(2)).findAll();
    }

    @Test
    void shouldNotFindCarRentalForBranch() {
        //given
        given(carRentalRepositoryMock.findAll()).willReturn(new ArrayList<>());

        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.addBranch(branch);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("No Car Rental for branch to be assigned to");
    }

    @Test
    void shouldNotFindManagerForBranch() {
        //given
        given(carRentalRepositoryMock.findAll()).willReturn(List.of(carRental));
        branch.setManagerId(1L);

        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.addBranch(branch);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("Cannot find employee to assign as Manager!");
        verify(carRentalRepositoryMock, times(2)).findAll();
        verify(employeeRepositoryMock, never()).save(manager);
        verify(branchRepositoryMock, never()).save(branch);
    }

    @Test
    void shouldGetAllBranches() {
        //given
        given(branchRepositoryMock.findAll()).willReturn(List.of(branch, branch, branch));

        //when
        List<Branch> allBranches = branchService.getAllBranches();

        //then
        assertThat(allBranches).isNotNull();
        assertThat(allBranches).isNotEmpty();
        assertThat(allBranches.size()).isEqualTo(3);
    }

    @Test
    void shouldGetAllBranchesWithEmptyList() {
        //given
        given(branchRepositoryMock.findAll()).willReturn(new ArrayList<>());

        //when
        List<Branch> allBranches = branchService.getAllBranches();

        //then
        assertThat(allBranches).isNotNull();
        assertThat(allBranches.size()).isEqualTo(0);
        assertThat(allBranches).isEmpty();
    }

    @Test
    void shouldEditBranchById() {
        //given
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));
        given(branchRepositoryMock.save(branch)).willReturn(branch);

        //when
        Branch modified = branchService.editBranch(1L, branchWithData);

        //then
        assertThat(modified).isNotNull();
        assertThat(modified.getName()).isEqualTo("name");
        assertThat(modified.getAddress()).isEqualTo("address");
        verify(branchRepositoryMock, times(1)).save(branch);
    }

    @Test
    void shouldGetBranchById() {
        //given
        branch.setBranchId(1L);
        given(branchRepositoryMock.findById(1L)).willReturn(Optional.of(branch));

        //when
        Branch foundBranch = branchService.getById(1L);

        //then
        assertThat(foundBranch).isNotNull();
        assertThat(foundBranch.getBranchId()).isEqualTo(1L);
        verify(branchRepositoryMock, times(1)).findById(1L);
    }

    @Test
    void shouldNotGetBranchById() {
        //given
        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.getById(1L);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("No branch under ID #1");
    }

    @Test
    void shouldRemoveBranchWithAllAssociatedReservations() {
        //given
        willDoNothing().given(reservationRepositoryMock).deleteAll(reservations);
        willDoNothing().given(branchRepositoryMock).deleteById(anyLong());
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branchWithData));
        given(reservationRepositoryMock.findAll()).willReturn(reservations);

        //when
        branchService.removeBranch(111L);

        //then
        verify(reservationRepositoryMock, times(1)).deleteAll(reservations);
        verify(branchRepositoryMock, times(1)).deleteById(111L);
    }

    @Test
    void shouldAddCarToBranchByAccordingId() {
        //given
        given(branchRepositoryMock.findAll()).willReturn(List.of(branch));
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));
        given(carRepositoryMock.save(car)).willReturn(car);

        //when
        Car savedCar = branchService.addCarToBranchByAccordingId(1L, car);

        //then
        assertThat(savedCar).isNotNull();
        assertThat(savedCar.getBranch()).isEqualTo(branch);
        assertThat(branch.getCars()).contains(car);
        verify(branchRepositoryMock, times(1)).findById(1L);
        verify(carRepositoryMock, times(1)).save(car);
    }

    @Test
    void shouldNotAddCarToBranchByAccordingId() {
        //given
        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.addCarToBranchByAccordingId(1L, car);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("There are no created branches currently");
    }

    @Test
    void shouldRemoveCarFromBranch() {
        //given
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));
        car.setCarId(1L);
        branch.setCars(new HashSet<>(Set.of(car)));

        //when
        branchService.removeCarFromBranch(1L, 1L);

        //then
        assertThat(branch.getCars()).isEmpty();
        assertThat(car.getBranch()).isNull();
        verify(branchRepositoryMock, times(1)).save(branch);
        verify(carRepositoryMock, times(1)).save(car);
    }

    @Test
    void shouldNotRemoveCarFromBranch() {
        //given
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));
        branch.setCars(new HashSet<>());

        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.removeCarFromBranch(1L, 1L);

        //then
        verify(branchRepositoryMock, never()).save(branch);
        verify(carRepositoryMock, never()).save(car);
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("No car under ID #1 is assigned to branch under ID #1");
    }

    @Test
    void shouldAssignCarToBranch() {
        //given
        given(carRepositoryMock.findById(anyLong())).willReturn(Optional.of(car));
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));
        car.setBranch(null);

        //when
        branchService.assignCarToBranch(1L, 1L);

        //then
        verify(branchRepositoryMock, times(1)).save(branch);
        verify(carRepositoryMock, times(1)).save(car);
        assertThat(branch.getCars()).isNotEmpty();
        assertThat(branch.getCars()).contains(car);
        assertThat(car.getBranch()).isNotNull();
        assertThat(car.getBranch()).isEqualTo(branch);
    }

    @Test
    void shouldNotAssignCarToBranchWhenThereIsNoCar() {
        //given
        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.assignCarToBranch(1L, 1L);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("No car under ID #1");
    }

    @Test
    void shouldNotAssignCarToBranchWhenCarIsAlreadyAssigned() {
        //given
        given(carRepositoryMock.findById(anyLong())).willReturn(Optional.of(car));
        car.setBranch(branch);

        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.assignCarToBranch(1L, 1L);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectAlreadyAssignedToBranchException.class)
                .hasMessage("Car already assigned to existing branch!");
    }

    @Test
    void shouldAssignEmployeeToBranch() {
        //given
        given(employeeRepositoryMock.findById(anyLong())).willReturn(Optional.of(employee));
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));

        //when
        branchService.assignEmployeeToBranch(1L, 1L);

        //then
        verify(branchRepositoryMock, times(1)).save(branch);
        verify(employeeRepositoryMock, times(1)).save(employee);
        assertThat(branch.getEmployees()).isNotEmpty();
        assertThat(branch.getEmployees()).contains(employee);
        assertThat(employee.getBranch()).isNotNull();
        assertThat(employee.getBranch()).isEqualTo(branch);
    }

    @Test
    void shouldNotAssignEmployeeToBranchWhenThereIsNoEmployee() {
        //given
        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.assignEmployeeToBranch(1L, 1L);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("No employee under ID #1");
    }

    @Test
    void shouldNotAssignEmployeeToBranchWhenEmployeeIsAlreadyAssigned() {
        //given
        given(employeeRepositoryMock.findById(anyLong())).willReturn(Optional.of(employee));
        employee.setBranch(branch);

        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.assignEmployeeToBranch(1L, 1L);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectAlreadyAssignedToBranchException.class)
                .hasMessage("Employee already assigned to existing branch!");
    }

    @Test
    void shouldRemoveEmployeeFromBranch() {
        //given
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));
        branch.getEmployees().add(employee);
        employee.setEmployeeId(1L);
        employee.setBranch(branch);

        //when
        branchService.removeEmployeeFromBranch(1L, 1L);

        //then
        assertThat(branch.getEmployees()).doesNotContain(employee);
        assertThat(employee.getBranch()).isNull();
        verify(branchRepositoryMock, times(1)).save(branch);
        verify(employeeRepositoryMock, times(1)).save(employee);
    }

    @Test
    void shouldNotRemoveEmployeeFromBranchWhenNoEmployeeWasFound() {
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));
        branch.getEmployees().add(employee);
        employee.setEmployeeId(1L);
        employee.setBranch(branch);

        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.removeEmployeeFromBranch(2L, 1L);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("No employee under ID #2 is assigned to branch under ID #1");
    }

    @Test
    void shouldAddManagerToBranch() {
        //given
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));
        given(employeeRepositoryMock.findById(anyLong())).willReturn(Optional.of(manager));

        //when
        branchService.addManagerForBranch(1L, 1L);

        //then
        assertThat(branch.getManagerId()).isNotNull();
        assertThat(manager.getPosition()).isNotNull();

        assertThat(branch.getManagerId()).isEqualTo(1L);
        assertThat(manager.getPosition()).isEqualTo(Position.MANAGER);
        assertThat(manager.getBranch()).isEqualTo(branch);

        verify(branchRepositoryMock, times(1)).save(branch);
        verify(employeeRepositoryMock, times(1)).save(manager);
    }

    @Test
    void shouldNotAddManagerForBranchWhenManagerIsAlreadyAssigned() {
        //given
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));
        branch.setManagerId(1L);

        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.addManagerForBranch(1L, 1L);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectAlreadyAssignedToBranchException.class)
                .hasMessage("Branch already has Manager!");

        verify(branchRepositoryMock, never()).save(branch);
        verify(employeeRepositoryMock, never()).save(manager);
    }

    @Test
    void shouldNotAddManagerForBranchWhenThereIsNoEmployeeUnderThatId() {
        //given
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));

        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.addManagerForBranch(1L, 1L);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("Cannot find employee to assign as Manager!");

        verify(branchRepositoryMock, never()).save(branch);
        verify(employeeRepositoryMock, never()).save(manager);
    }

    @Test
    void shouldRemoveManagerFromBranch() {
        //given
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));
        branch.setManagerId(1L);

        //when
        branchService.removeManagerFromBranch(1L);

        //then
        assertThat(branch.getBranchId()).isNull();
        verify(branchRepositoryMock, times(1)).save(branch);
    }

    @Test
    void shouldNotRemoveManagerFromBranch() {
        //given
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch));

        //when
        ThrowableAssert.ThrowingCallable callable = () -> branchService.removeManagerFromBranch(1L);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("Branch does not have any assigned Manager!");
        verify(branchRepositoryMock, never()).save(branch);
    }

    @Test
    void shouldGetCarsAvailableAtBranchOnDate() {
        //given
        given(branchRepositoryMock.findById(anyLong())).willReturn(Optional.of(branch2));
        given(branchRepositoryMock.findAll()).willReturn(List.of(branch1, branch2));

        //when
        List<CarDTO> carsAvailableAtBranchOnDate = branchService.getCarsAvailableAtBranchOnDate(2L, "2024-01-30");

        //then
        assertThat(carsAvailableAtBranchOnDate).isNotNull();
        assertThat(carsAvailableAtBranchOnDate).isNotEmpty();
        assertThat(carsAvailableAtBranchOnDate.size()).isEqualTo(2);
       }
}*/
