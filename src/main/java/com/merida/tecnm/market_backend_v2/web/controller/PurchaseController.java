package com.merida.tecnm.market_backend_v2.web.controller;

import com.merida.tecnm.market_backend_v2.domain.Purchase;
import com.merida.tecnm.market_backend_v2.domain.PurchaseRequest;
import com.merida.tecnm.market_backend_v2.domain.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
@Tag(name = "Purchase", description = "Manage purchases and their items")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("")
    @Operation(summary = "Get all purchases")
    @ApiResponse(responseCode = "200", description = "Purchase list")
    public ResponseEntity<List<Purchase>> getAll() {
        return new ResponseEntity<>(purchaseService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Purchase by ID")
    @ApiResponse(responseCode = "200", description = "Purchase found")
    @ApiResponse(responseCode = "404", description = "Purchase not found")
    public ResponseEntity<Purchase> getPurchase(
            @Parameter(description = "ID of the purchase", example = "1")
            @PathVariable("id") int purchaseId) {
        return purchaseService.getPurchase(purchaseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get purchases by client")
    @ApiResponse(responseCode = "200", description = "Client purchase list")
    public ResponseEntity<List<Purchase>> getByClient(
            @Parameter(description = "Exact client ID from the catalog", example = "CLI-001")
            @PathVariable("clientId") String clientId) {
        return purchaseService.getByClient(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("")
    @Operation(summary = "Create a purchase",
            description = "Save the purchase and all its items in one transaction",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseRequest.class),
                            examples = @ExampleObject(name = "Example Purchase", value = """
                                    {
                                      "idCliente": "CLI-001",
                                      "medioPago": "TARJETA",
                                      "comentario": "Entregar por la tarde",
                                      "productosAdquiridos": [
                                        { "idProducto": 15, "cantidad": 2, "total": 500.0 },
                                        { "idProducto": 8, "cantidad": 1, "total": 150.0 }
                                      ]
                                    }
                                    """))))
    @ApiResponse(responseCode = "201", description = "Purchase and items created")
    @ApiResponse(responseCode = "400", description = "Invalid purchase data")
    @ApiResponse(responseCode = "404", description = "Client or product not found")
    @ApiResponse(responseCode = "409", description = "Purchase conflicts with a database constraint")
    public ResponseEntity<Purchase> save(@RequestBody PurchaseRequest request) {
        return new ResponseEntity<>(purchaseService.save(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a purchase", description = "Replace the editable header and item list")
    @ApiResponse(responseCode = "200", description = "Purchase updated")
    @ApiResponse(responseCode = "400", description = "Invalid purchase data")
    @ApiResponse(responseCode = "404", description = "Purchase, client or product not found")
    @ApiResponse(responseCode = "409", description = "Purchase conflicts with a database constraint")
    public ResponseEntity<Purchase> update(@PathVariable("id") int purchaseId,
                                           @RequestBody PurchaseRequest request) {
        return purchaseService.update(purchaseId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a purchase and its items")
    @ApiResponse(responseCode = "204", description = "Purchase deleted")
    @ApiResponse(responseCode = "404", description = "Purchase not found")
    public ResponseEntity<Void> delete(@PathVariable("id") int purchaseId) {
        if (purchaseService.delete(purchaseId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
