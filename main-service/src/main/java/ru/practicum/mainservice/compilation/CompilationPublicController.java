package ru.practicum.mainservice.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.compilation.model.CompilationDto;

import java.util.List;

@RestController
@Log4j2
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    List<CompilationDto> getCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting compilations: pinned={}, from={}, size={}", pinned, from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return compilationService.getCompilations(pinned, pageRequest);
    }

    @GetMapping("/{compId}")
    CompilationDto getOneCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("Getting compilation={}", compId);
        return compilationService.getOneCompilation(compId);
    }
}
