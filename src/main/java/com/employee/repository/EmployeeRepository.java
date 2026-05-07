package com.employee.repository;

import com.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Employee entity.
 * Provides built-in CRUD operations plus custom query methods.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /** Find an employee by email (case-insensitive). */
    Optional<Employee> findByEmailIgnoreCase(String email);

    /** Check if an email already exists. */
    boolean existsByEmailIgnoreCase(String email);

    /** Find all employees in a given department. */
    List<Employee> findByDepartmentIgnoreCase(String department);

    /** Find all active / inactive employees. */
    List<Employee> findByActive(Boolean active);

    /** Search by first or last name containing a keyword (case-insensitive). */
    List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);
}
