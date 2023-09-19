package com.vazquez.rest.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FrontEndController {


    @PostMapping("/submit")
    public String submit(@RequestParam("userInput") String userInput, Model model){


        System.out.println(userInput);
        model.addAttribute("message", "You entered: " + userInput);

        return "template";

    }

    @GetMapping("/")
    public String index(Model model)
    {
        model.addAttribute("message", "Zoo wee mama");
        return "index";

    }




}
