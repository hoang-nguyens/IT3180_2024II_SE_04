package controllers;

import models.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.InvoiceService;

import java.util.List;

@RestController
@RequestMapping("api/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;
    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices(
            @RequestParam(required = false) Long userId
    ) {
        if (userId != null) {
            return ResponseEntity.ok(invoiceService.getInvoiceByUserId(userId));
        } else {
            return ResponseEntity.ok(invoiceService.getInvoices());
        }
    }

}