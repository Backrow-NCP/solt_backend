package org.backrow.solt.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loadbalancer")
@Log4j2
public class LoadBalancerController {

    @GetMapping
    public void loadbalancer(){
        try{
            log.info("loadbalancer ok");
        }catch(RuntimeException e){
            log.error(e);
        }
    }
}
