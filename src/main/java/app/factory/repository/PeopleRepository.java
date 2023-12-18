package app.factory.repository;

import app.factory.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeopleRepository extends JpaRepository<Person, Integer> {
}
