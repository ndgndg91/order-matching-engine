package com.ndgndg91.ordermatchingengine.test;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final KafkaTemplate<String, Test> template;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        template.send("test", new Test("test"));
        return ResponseEntity.ok("Success");
    }
}
