/*
package pl.sda.carrental.service;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pl.sda.carrental.exceptionHandling.BranchAlreadyOpenInCityException;
import pl.sda.carrental.exceptionHandling.ObjectAlreadyExistsException;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.CarRental;
import pl.sda.carrental.repository.BranchRepository;
import pl.sda.carrental.repository.CarRentalRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CarRentalServiceTest {
    @Mock
    private CarRentalRepository carRentalRepositoryMock;
    @Mock
    private BranchRepository branchRepositoryMock;

    @InjectMocks
    private CarRentalService carRentalService;

    @Test
    void shouldGetCarRental() {
        //given
        CarRental carRental = createCarRental();

        when(carRentalRepositoryMock.findAll()).thenReturn(Collections.singletonList(carRental));

        //when
        CarRental result = carRentalService.getCarRental();

        //then
        assertThat(result).isEqualTo(carRental);
    }

    @Test
    void shouldNotFindCarRental() {
        //given
        when(carRentalRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        //when
        ThrowableAssert.ThrowingCallable callable = () -> carRentalService.getCarRental();

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("There is no car rental company");
    }

    @Test
    void shouldSaveCarRental() {
        //given
        CarRental carRental = createCarRental();

        //when
        carRentalService.saveCarRental(carRental);

        //then
        ArgumentCaptor<CarRental> captor = ArgumentCaptor.forClass(CarRental.class);
        verify(carRentalRepositoryMock).save(captor.capture());
        CarRental result = captor.getValue();

        assertThat(result).isEqualTo(carRental);
    }

    @Test
    void shouldNotSaveCarRental() {
        //given
        CarRental carRental = createCarRental();
        when(carRentalRepositoryMock.findAll()).thenReturn(List.of(carRental));

        //when
        ThrowableAssert.ThrowingCallable callable = () -> carRentalService.saveCarRental(carRental);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectAlreadyExistsException.class)
                .hasMessage("Car Rental already exists!");
    }

    @Test
    void shouldEditCarRental() {
        //given
        CarRental carRental = createCarRental();
        CarRental modified = createCarRental();
        modified.setAddress("new address");
        modified.setName("new name");
        modified.setLogo("new logo");
        modified.setDomain("new domain");
        modified.setOwner("new owner");
        modified.setBranches(Set.of(new Branch(), new Branch(), new Branch()));
        when(carRentalRepositoryMock.findAll()).thenReturn(List.of(carRental));

        //when
        carRentalService.editCarRental(modified);

        //then
        ArgumentCaptor<CarRental> captor = ArgumentCaptor.forClass(CarRental.class);
        verify(carRentalRepositoryMock).save(captor.capture());
        CarRental result = captor.getValue();

        assertThat(result.getAddress()).isEqualTo("new address");
        assertThat(result.getName()).isEqualTo("new name");
        assertThat(result.getLogo()).isEqualTo("new logo");
        assertThat(result.getDomain()).isEqualTo("new domain");
        assertThat(result.getOwner()).isEqualTo("new owner");
        assertThat(result.getBranches().size()).isEqualTo(3);
    }

    @Test
    void shouldNotEditCarRental() {
        //given
        when(carRentalRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        //when
        ThrowableAssert.ThrowingCallable callable = () -> carRentalService.editCarRental(createCarRental());

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("There is no car rental company to edit");
    }

    @Test
    void shouldDeleteCarRental() {
        //given
        CarRental carRental = createCarRental();
        when(carRentalRepositoryMock.findAll()).thenReturn(List.of(carRental));

        //when
        carRentalService.deleteCarRental();

        //then
        verify(carRentalRepositoryMock).delete(carRental);
    }

    @Test
    void shouldNotDeleteCarRental() {
        //given
        when(carRentalRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        //when
        ThrowableAssert.ThrowingCallable callable = () -> carRentalService.deleteCarRental();

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("There is no car rental company");
    }

    @Test
    void shouldOpenNewBranch() {
        //given
        Branch newBranch = createBranch(1L, "Address1");
        List<Branch> openBranches = List.of(createBranch(2L, "Address2"), createBranch(3L, "Address3"));
        CarRental carRental = createCarRental();
        carRental.setBranches(new HashSet<>(openBranches));
        when(carRentalRepositoryMock.findAll()).thenReturn(List.of(carRental));

        //when
        carRentalService.openNewBranch(newBranch);

        //then
        ArgumentCaptor<Branch> branchCaptor = ArgumentCaptor.forClass(Branch.class);
        verify(branchRepositoryMock).save(branchCaptor.capture());
        Branch savedBranch = branchCaptor.getValue();

        ArgumentCaptor<CarRental> carRentalCaptor = ArgumentCaptor.forClass(CarRental.class);
        verify(carRentalRepositoryMock).save(carRentalCaptor.capture());
        CarRental savedCarRental = carRentalCaptor.getValue();

        assertThat(savedBranch).isEqualTo(newBranch);
        assertThat(savedCarRental.getBranches().size()).isEqualTo(3);
        assertThat(savedCarRental.getBranches()).contains(newBranch);
    }

    @Test
    void shouldNotFindCarRentalWhenOpeningNewBranch() {
        //given
        when(carRentalRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        //when
        ThrowableAssert.ThrowingCallable callable = () -> carRentalService.openNewBranch(createBranch(1L, "address"));

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(ObjectNotFoundInRepositoryException.class)
                .hasMessage("Car Rental has not been created yet");
    }

    @Test
    void shouldFindAnotherBranchOpenInCityAndThrowException() {
        Branch newBranch = createBranch(1L, "Address2");
        List<Branch> openBranches = List.of(createBranch(2L, "Address2"), createBranch(3L, "Address3"));
        CarRental carRental = createCarRental();
        carRental.setBranches(new HashSet<>(openBranches));
        when(carRentalRepositoryMock.findAll()).thenReturn(List.of(carRental));

        //when
        ThrowableAssert.ThrowingCallable callable = () -> carRentalService.openNewBranch(newBranch);

        //then
        assertThatThrownBy(callable)
                .isInstanceOf(BranchAlreadyOpenInCityException.class)
                .hasMessage("Branch null is already open in city Address2");
    }


    private CarRental createCarRental() {
        return new CarRental()
                .withCarRentalId(1L)
                .withName("Name")
                .withDomain("domain")
                .withAddress("address")
                .withLogo("logo")
                .withOwner("Owner")
                .withBranches(new HashSet<>());
    }

    private Branch createBranch(Long id, String address) {
        return new Branch()
                .withBranchId(id)
                .withAddress(address);
    }
}*/
