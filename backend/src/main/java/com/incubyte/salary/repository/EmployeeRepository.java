package com.incubyte.salary.repository;

import com.incubyte.salary.dto.CountryInsight;
import com.incubyte.salary.dto.DepartmentInsight;
import com.incubyte.salary.dto.JobTitleInsight;
import com.incubyte.salary.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    @Query("SELECT e FROM Employee e WHERE (:country IS NULL OR e.country = :country) AND (:department IS NULL OR e.department = :department) AND (:name IS NULL OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Employee> findByFilters(@Param("country") String country,
                                 @Param("department") String department,
                                 @Param("name") String name,
                                 Pageable pageable);

    @Query("SELECT new com.incubyte.salary.dto.DepartmentInsight(e.department, COUNT(e), AVG(e.salary), MIN(e.salary), MAX(e.salary)) " +
           "FROM Employee e GROUP BY e.department ORDER BY e.department")
    List<DepartmentInsight> findSalaryStatsByDepartment();

    @Query("SELECT new com.incubyte.salary.dto.CountryInsight(e.country, COUNT(e), AVG(e.salary), MIN(e.salary), MAX(e.salary)) " +
           "FROM Employee e GROUP BY e.country ORDER BY e.country")
    List<CountryInsight> findSalaryStatsByCountry();

    @Query("SELECT new com.incubyte.salary.dto.JobTitleInsight(e.jobTitle, COUNT(e), AVG(e.salary), MIN(e.salary), MAX(e.salary)) " +
           "FROM Employee e WHERE (:country IS NULL OR e.country = :country) GROUP BY e.jobTitle ORDER BY AVG(e.salary) DESC")
    List<JobTitleInsight> findSalaryStatsByJobTitle(@Param("country") String country);
}
