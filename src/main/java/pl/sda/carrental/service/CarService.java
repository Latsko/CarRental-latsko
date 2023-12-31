package pl.sda.carrental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.model.Car;
import pl.sda.carrental.repository.CarRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;

    public Car getCarById(Long id) {
        return carRepository.findById(id).
                orElseThrow(() -> new ObjectNotFoundInRepositoryException("There is no car with selected id"));
    }

    public List<Car> getCars() {
        return carRepository.findAll();
    }

    public void addCar(Car car) {
        carRepository.save(car);
    }

    public void editCar(Long id, Car car) {
        Car editedCar = carRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No car under ID #" + id));
        editedCar.setCar_id(id);
        editedCar.setMake(car.getMake());
        editedCar.setModel(car.getModel());
        editedCar.setBodyStyle(car.getBodyStyle());
        editedCar.setYear(car.getYear());
        editedCar.setColour(car.getColour());
        editedCar.setMileage(car.getMileage());
        editedCar.setStatus(car.getStatus());
        editedCar.setPrice(car.getPrice());

        carRepository.deleteById(id);
        carRepository.save(editedCar);
    }

    public void deleteCarById(Long id) {
        carRepository.findById(id).orElseThrow(() -> new ObjectNotFoundInRepositoryException(""));
        carRepository.deleteById(id);
    }


}
