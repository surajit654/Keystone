package com.KEYSTONE.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleTestController {

    @GetMapping("/api/dispatcher/test")
    public String dispatcherTest() {
        return "Dispatcher access granted!";
    }

    @GetMapping("/api/technician/test")
    public String technicianTest() {
        return "Technician access granted!";
    }

    @GetMapping("/api/manager/test")
    public String managerTest() {
        return "Manager access granted!";
    }

    @GetMapping("/api/customer/test")
    public String customerTest() {
        return "Customer access granted!";
    }
}