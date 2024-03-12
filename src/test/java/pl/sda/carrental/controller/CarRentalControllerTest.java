/*
package pl.sda.carrental.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.CarRental;
import pl.sda.carrental.service.CarRentalService;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CarRentalControllerTest {

    @Autowired
    private WebTestClient testClient;
    @MockBean
    private CarRentalService service;

    @Test
    void shouldGetCarRental() {
          CarRental carRental = new CarRental(
                1L,
                "Car Rental",
                "www.cars.pl",
                "Warszawa",
                "Janusz",
                "Logo",
                new HashSet<>()
        );

        Mockito.when(service.getCarRental()).thenReturn(carRental);

        Flux<CarRental> responseBody = testClient.get().uri("/carRental")
                .exchange()
                .expectStatus().isOk()
                .returnResult(CarRental.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(new CarRental(
                        1L,
                        "Car Rental",
                        "www.cars.pl",
                        "Warszawa",
                        "Janusz",
                        "Logo",
                        new HashSet<>()
                ))
                .verifyComplete();
    }

    @Test
    void shouldSaveCarRental() {
        Set<Branch> branches = new HashSet<>();
        branches.add(new Branch(
                1L,
                "Radom",
                " inne",
                null,
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                null,
                null)
        );

        CarRental carRental = new CarRental(
                1L,
                "Car Rental",
                "www.cars.pl",
                "Warszawa",
                "Janusz",
                "Logo",
                branches
        );
        testClient
                //given
                .post()
                .uri("/carRental")
                .bodyValue(carRental)

                //when
                .exchange()
                //then
                .expectStatus()
                .isOk();

    }

}*/
