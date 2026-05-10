package com.cm_policier.effectifs.controllers;
import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.service.ControleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/controles")
@RequiredArgsConstructor
public class ControleController {

    private final ControleService service;

    /* ========================= CREATE ========================= */
    @PostMapping
    public Controle create(@RequestBody Controle controle) {
        return service.create(controle);
    }

    /* ========================= READ ALL (PAGINATION) ========================= */
    @GetMapping
    public Page<Controle> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.getAll(page, size);
    }

    /* ========================= READ ONE ========================= */
    @GetMapping("/{id}")
    public Controle getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /* ========================= UPDATE ========================= */
    @PutMapping("/{id}")
    public Controle update(@PathVariable Long id, @RequestBody Controle controle) {
        return service.update(id, controle);
    }

    /* ========================= DELETE ========================= */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}