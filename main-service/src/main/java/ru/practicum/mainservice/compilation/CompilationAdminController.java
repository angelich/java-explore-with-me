package ru.practicum.mainservice.compilation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.compilation.model.CompilationDto;
import ru.practicum.mainservice.compilation.model.NewCompilationDto;


@Log4j2
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/admin/compilations")
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto dto) {
        log.info("Create new compilation={}", dto);
        return compilationService.createCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    void deleteCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("Deleting compilation={}", compId);
        compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    void deleteEventFromCompilation(@PathVariable(name = "compId") Long compId,
                                    @PathVariable(name = "eventId") Long eventId) {
        log.info("Deleting event from compilation: compilation={}, event={}", compId, eventId);
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    void addEventToCompilation(@PathVariable(name = "compId") Long compId,
                               @PathVariable(name = "eventId") Long eventId) {
        log.info("Add event to compilation: compilation={}, event={}", compId, eventId);
        compilationService.addEventToCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    @ResponseStatus(HttpStatus.OK)
    void unpinCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("Unpin compilation={}", compId);
        compilationService.unpinCompilation(compId);
    }

    @PatchMapping("/{compId}/pin")
    @ResponseStatus(HttpStatus.OK)
    void pinCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("Pin compilation={}", compId);
        compilationService.pinCompilation(compId);
    }
}
