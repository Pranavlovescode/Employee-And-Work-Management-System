package com.employeemanagement.manage_employee.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.employeemanagement.manage_employee.ManageEmployeeApplication;
import com.employeemanagement.manage_employee.entity.EmployeeDetails;
import com.employeemanagement.manage_employee.entity.ManagerDetails;
import com.employeemanagement.manage_employee.repository.EmployeeInfo;
import com.employeemanagement.manage_employee.repository.ManagerInfo;
import com.employeemanagement.manage_employee.response.EmployeeRegisterResponse;
import com.employeemanagement.manage_employee.services.EmailService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/add-employee")
public class EmployeeController {
    Logger logger = Logger.getLogger(ManageEmployeeApplication.class.getName());
    @Autowired
    private EmployeeInfo employeeInfo;
    @Autowired
    private ManagerInfo manager;
    // @Autowired
    // private AdminInfo admin;
    // @Autowired
    // private WorkInfo work;
    
    @Autowired
    private EmailService javaMailService;



//    Adding a new employee to the database
    @PostMapping("/register")
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeDetails employeeDetails) {
        // Date date = new Date();
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        String emp_password = bcrypt.encode(employeeDetails.getPassword());
        employeeDetails.setPassword(emp_password);
        // employeeDetails.setDate_of_joining(date);
        employeeInfo.save(employeeDetails);
        javaMailService.sendEmail(employeeDetails.getEmail(), "Verify your email", "Congratulations! You have successfully registered with us. Please verify your email to proceed further.");

        EmployeeRegisterResponse response = new EmployeeRegisterResponse(employeeDetails, "Employee added successfully and verification email sent");

        return new ResponseEntity<>(response,HttpStatus.CREATED);

    }
//    Fetching all the employees from the database
    @GetMapping
    public Page<EmployeeDetails> getEmployee_Paginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        return employeeInfo.findAll(PageRequest.of(page, size));
    }


//    Fetching a manager details using employee id (checking if manager exists for a given employee id)
    @GetMapping("/{id}")
    public EmployeeDetails getEmployeeById(@PathVariable("id") String id) {

        EmployeeDetails emp = employeeInfo.findById(id).get();
        logger.info("Employee Details fetched Successfully");
        return  emp;

    }

//    Adding a manager to an employee using manager id and employee id
    @PutMapping("/{mng_id}/{emp_id}")
    public EmployeeDetails addManagerToEmployee( @PathVariable("mng_id") String mngid,@PathVariable("emp_id") String empid) {
        ManagerDetails mng = manager.findById(mngid).get();
        EmployeeDetails employee = employeeInfo.findById(empid).get();
        employee.setManagerDetails(mng);
        employeeInfo.save(employee);
        return employee;
    }

//   Deleting an employee using employee id
    @DeleteMapping("/{id}")
    public String deleteEmployee(@PathVariable("id") String id) {
        employeeInfo.deleteById(id);

        logger.info("Employee Deleted Successfully");
        return "Employee deleted";
    }

}
