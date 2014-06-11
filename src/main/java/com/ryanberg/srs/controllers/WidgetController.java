package com.ryanberg.srs.controllers;

import com.ryanberg.srs.domain.Widget;
import com.ryanberg.srs.services.repositories.WidgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/widgets")
public class WidgetController
{
    @Autowired
    WidgetRepository widgetRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<Widget> list()
    {
        return (List<Widget>) widgetRepository.findAll();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Widget get(@PathVariable("id") Long id)
    {
        return widgetRepository.findOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Widget create(@RequestBody Widget widget)
    {
        return widgetRepository.save(widget);
    }

}
