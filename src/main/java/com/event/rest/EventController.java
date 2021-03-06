package com.event.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.error.domain.ServiceError;
import com.error.service.ErrorService;
import com.event.domain.Event;
import com.event.domain.EventRest;
import com.event.service.EventService;
import com.user.service.UserService;


@RestController
@RequestMapping("/events")
public class EventController
{
    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private ErrorService errorService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity getByUsername(@RequestParam String username, Principal principal) {
        String loginUserName = principal.getName();
        try {
            Iterable<Event> list = eventService.getEventsByUsername(username, loginUserName);
            List<EventRest> eventList = new ArrayList<>();
            list.forEach(event -> eventList.add(mapToRest(event)));
            return new ResponseEntity(eventList, HttpStatus.OK);
        }
        catch (ServiceError serviceError) {
            return new ResponseEntity(errorService.mapToRest(serviceError), errorService.getHTTPStatus(serviceError));
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity getById(@PathVariable Integer id, Principal principal) {
        String loginUserName = principal.getName();
        try {
            Event event = eventService.getOne(id, loginUserName);
            return new ResponseEntity(mapToRest(event), HttpStatus.OK);
        }
        catch (ServiceError serviceError) {
            return new ResponseEntity(errorService.mapToRest(serviceError), errorService.getHTTPStatus(serviceError));
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity create(@RequestBody EventRest eventRest, Principal principal) {
        String loginUserName = principal.getName();
        try {
            Event request = mapFromRest(eventRest);
            request.setUser(this.userService.getByName(eventRest.getUsername(), loginUserName));
            Event event = eventService.create(request, loginUserName);
            return new ResponseEntity(mapToRest(event), HttpStatus.OK);
        }
        catch (ServiceError serviceError) {
            return new ResponseEntity(errorService.mapToRest(serviceError), errorService.getHTTPStatus(serviceError));
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity update(@PathVariable Integer id, @RequestBody EventRest eventRest, Principal principal) {
        String loginUserName = principal.getName();
        try {
            Event request = mapFromRest(eventRest);
            request.setUser(this.userService.getByName(eventRest.getUsername(), loginUserName));
            Event event = eventService.update(id, request, loginUserName);
            return new ResponseEntity(mapToRest(event), HttpStatus.OK);
        }
        catch (ServiceError serviceError) {
            return new ResponseEntity(errorService.mapToRest(serviceError), errorService.getHTTPStatus(serviceError));
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity delete(@PathVariable Integer id, Principal principal) {
        String loginUserName = principal.getName();
        try {
            eventService.delete(id, loginUserName);
            return new ResponseEntity("EventRest deleted successfully", HttpStatus.OK);
        }
        catch (ServiceError serviceError) {
            return new ResponseEntity(errorService.mapToRest(serviceError), errorService.getHTTPStatus(serviceError));
        }
    }

    private static Event mapFromRest (EventRest eventRest) {
        Event event = new Event();
        event.setId(eventRest.getId());
        event.setCreatedOn(eventRest.getCreatedOn());
        event.setTitle(eventRest.getTitle());
        event.setDescription(eventRest.getDescription());

        return event;
    }

    private static EventRest mapToRest (Event event) {
        return new EventRest(event.getId(), event.getUser().getName(), event.getTitle(), event.getDescription(), event.getCreatedOn());
    }
}
