package com.ryanberg.srs.controllers;

import com.ryanberg.srs.domain.Greeting;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicLong;

@Controller
public class GreetingController
{
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/secure/greeting")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody Greeting secureGreeting(@RequestParam(value="name", required=false, defaultValue="Secure") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @RequestMapping("/greeting")
    @PreAuthorize("hasRole('ROLE_USER')")
    public @ResponseBody Greeting greeting(@RequestParam(value="name", required=false, defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
}
