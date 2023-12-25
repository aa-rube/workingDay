package app.factory.service;

import app.factory.model.Person;
import app.factory.repository.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PeopleService {
    @Autowired
    private PeopleRepository peopleRepository;

    private List<Person> sortPeopleBySecondName() {
        List<Person> people = peopleRepository.findAll();
        return people.stream()
                .sorted(Comparator.comparing(Person::getSecondName))
                .collect(Collectors.toList());
    }

    public List<Person> filterPeopleBySecondNameRange(char startChar, char endChar) {
        return sortPeopleBySecondName().stream()
                .filter(person -> {
                    String secondName = person.getSecondName().toUpperCase();

                    return !secondName.isEmpty() &&
                            startChar <= secondName.charAt(0) &&
                            secondName.charAt(0) <= endChar;
                })
                .collect(Collectors.toList());
    }

    public boolean deleteById(int id) {
        Optional<Person> personOptional = peopleRepository.findById(id);
        if (personOptional.isPresent()) {
            peopleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void save(Person person) {
        peopleRepository.save(person);
    }

    public Person findPersonById(int id) {
        Optional<Person> personOptional = peopleRepository.findById(id);
        return personOptional.orElse(null);
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }
}