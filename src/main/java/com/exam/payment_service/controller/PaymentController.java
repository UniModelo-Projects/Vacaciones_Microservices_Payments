package com.exam.payment_service.controller;

import com.exam.payment_service.model.Payment;
import com.exam.payment_service.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagos")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentRepository paymentRepository;
    private final org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentController(PaymentRepository paymentRepository, org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/procesar")
    public Payment processPayment(@RequestBody Payment payment) {
        try {
            log.info("Processing payment for order: {}", payment.getOrdenId());
            // Simulate random failure for testing retries
            if (Math.random() < 0.3) {
                throw new RuntimeException("Simulated failure during payment processing");
            }
            payment.setEstado("COMPLETADO");
            return paymentRepository.save(payment);
        } catch (Exception e) {
            log.error("Error processing payment, sending to retry topic: {}", e.getMessage());
            
            java.util.Map<String, Object> wrappedPayload = new java.util.HashMap<>();
            wrappedPayload.put("data", payment);
            wrappedPayload.put("sendEmail", java.util.Map.of("status", "PENDING", "message", ""));
            wrappedPayload.put("updateRetryJobs", java.util.Map.of("status", "PENDING", "message", ""));
            
            kafkaTemplate.send("payments_retry_jobs", wrappedPayload);
            throw e;
        }
    }

    @PostMapping("/retry")
    public Payment retry(@RequestBody Payment payment) {
        log.info("Retrying payment for order: {}", payment.getOrdenId());
        return paymentRepository.save(payment);
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable String id) {
        log.info("Fetching payment with id: {}", id);
        return paymentRepository.findById(id).orElse(null);
    }

    @GetMapping("/orden/{ordenId}")
    public Payment getPaymentByOrder(@PathVariable String ordenId) {
        log.info("Fetching payment for order: {}", ordenId);
        return paymentRepository.findByOrdenId(ordenId).orElse(null);
    }

    @PutMapping("/{id}/reembolso")
    public Payment refundPayment(@PathVariable String id) {
        log.info("Refunding payment with id: {}", id);
        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment != null) {
            payment.setEstado("REEMBOLSADO");
            return paymentRepository.save(payment);
        }
        return null;
    }
}
