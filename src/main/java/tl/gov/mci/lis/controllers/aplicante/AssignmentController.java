package tl.gov.mci.lis.controllers.aplicante;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.dtos.aplicante.AplicanteAssignmentDto;
import tl.gov.mci.lis.dtos.mappers.AplicanteMapper;
import tl.gov.mci.lis.services.aplicante.AssignmentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService service;
    private final AplicanteMapper mapper;

//    @PostMapping
//    public AplicanteAssignment assign(@RequestParam Long aplicanteId,
//                                      @RequestParam Long assigneeId,
//                                      @RequestParam Long managerId,
//                                      @RequestParam(required = false) String notes) {
//        return service.assign(aplicanteId, assigneeId, managerId, notes);
//    }

//    @PostMapping("/{id}/close")
//    public void close(@PathVariable Long id,
//                      @RequestParam Long managerId,
//                      @RequestParam(required = false) String notes) {
//        service.closeAssignment(id, managerId, notes);
//    }

    @GetMapping("/active")
    public List<AplicanteAssignmentDto> listActive(@RequestParam Long aplicanteId) {
        return service.listActive(aplicanteId).stream().map(mapper::toDto).toList();
    }
}
