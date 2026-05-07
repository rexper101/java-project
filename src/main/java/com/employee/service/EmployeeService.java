package com.employee.service;

import com.employee.dto.EmployeeDTO;
import com.employee.exception.DuplicateResourceException;
import com.employee.exception.ResourceNotFoundException;
import com.employee.model.Employee;
import com.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service layer encapsulating all business logic for Employee operations.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // ==================== CRUD Operations ====================

    /**
     * Create a new employee.
     *
     * @throws DuplicateResourceException if the email already exists
     */
    public EmployeeDTO createEmployee(EmployeeDTO dto) {
        if (employeeRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "Employee with email '" + dto.getEmail() + "' already exists");
        }

        Employee employee = mapToEntity(dto);
        employee.setActive(true);
        Employee saved = employeeRepository.save(employee);
        return mapToDTO(saved);
    }

    /**
     * Retrieve all employees.
     */
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a single employee by ID.
     *
     * @throws ResourceNotFoundException if the employee is not found
     */
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        return mapToDTO(employee);
    }

    /**
     * Update an existing employee.
     *
     * @throws ResourceNotFoundException  if the employee is not found
     * @throws DuplicateResourceException if the new email is already used by
     *                                    another employee
     */
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO dto) {
        Employee existing = findEmployeeOrThrow(id);

        // Check email uniqueness (only if the email is changing)
        if (!existing.getEmail().equalsIgnoreCase(dto.getEmail())
                && employeeRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "Employee with email '" + dto.getEmail() + "' already exists");
        }

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setDepartment(dto.getDepartment());
        existing.setDesignation(dto.getDesignation());
        existing.setSalary(dto.getSalary());
        existing.setPhone(dto.getPhone());
        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setDateOfJoining(dto.getDateOfJoining());

        Employee updated = employeeRepository.save(existing);
        return mapToDTO(updated);
    }

    /**
     * Delete an employee by ID.
     *
     * @throws ResourceNotFoundException if the employee is not found
     */
    public void deleteEmployee(Long id) {
        findEmployeeOrThrow(id);
        employeeRepository.deleteById(Objects.requireNonNull(id));
    }

    // ==================== Additional Operations ====================

    /**
     * Search employees by name keyword.
     */
    @Transactional(readOnly = true)
    public List<EmployeeDTO> searchByName(String keyword) {
        return employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get employees filtered by department.
     */
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getByDepartment(String department) {
        return employeeRepository.findByDepartmentIgnoreCase(department)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get employees filtered by active status.
     */
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getByActiveStatus(Boolean active) {
        return employeeRepository.findByActive(active)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ==================== Helper Methods ====================

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(Objects.requireNonNull(id, "Employee ID must not be null"))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + id));
    }

    /** Map DTO → Entity */
    private Employee mapToEntity(EmployeeDTO dto) {
        return Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .designation(dto.getDesignation())
                .salary(dto.getSalary())
                .phone(dto.getPhone())
                .dateOfBirth(dto.getDateOfBirth())
                .dateOfJoining(dto.getDateOfJoining())
                .build();
    }

    /** Map Entity → DTO */
    private EmployeeDTO mapToDTO(Employee entity) {
        return EmployeeDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .department(entity.getDepartment())
                .designation(entity.getDesignation())
                .salary(entity.getSalary())
                .phone(entity.getPhone())
                .dateOfBirth(entity.getDateOfBirth())
                .dateOfJoining(entity.getDateOfJoining())
                .build();
    }
}
