package cl.tempo.controllers;


import cl.tempo.dto.NumerosRequest;
import cl.tempo.model.entities.InvokeHistoryEntity;
import cl.tempo.services.InvokeHistoryService;
import cl.tempo.services.TempoService;
import cl.tempo.services.RedisService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class TempoController {

    private static final Logger logger = LoggerFactory.getLogger(TempoController.class);

    @Autowired
    private TempoService tempoService;
    
    @Autowired
    private InvokeHistoryService invokeHistoryService;

    @RequestMapping(value = "/calculate", method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody ResponseEntity<?> calculate(@Valid @RequestBody NumerosRequest requestObj)
    {
        return tempoService.calcular(requestObj);
    }

    @RequestMapping(value = "/getHistoryCalls", method = RequestMethod.GET,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAllWithPagination(Pageable pageable) {
        return  invokeHistoryService.getAllHistoryCallWithPagination(pageable);

    }

}

