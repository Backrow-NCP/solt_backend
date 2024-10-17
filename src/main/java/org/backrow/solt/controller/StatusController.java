package org.backrow.solt.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loadbalancer")
@Log4j2
public class StatusController {
    @GetMapping
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Spring Server is running");
    }
}
