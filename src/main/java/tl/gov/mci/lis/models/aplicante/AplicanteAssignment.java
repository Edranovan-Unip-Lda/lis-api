package tl.gov.mci.lis.models.aplicante;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.EstadoTarefa;
import tl.gov.mci.lis.enums.Role;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.user.User;

import java.time.Instant;

@Entity
@Table(name = "lis_aplicante_assignment",
        indexes = {
                @Index(name = "ix_assignment_aplicante", columnList = "aplicante_id"),
                @Index(name = "ix_assignment_assignee", columnList = "assignee_id"),
                @Index(name = "ix_assignment_active", columnList = "aplicante_id,active")
        })
@Getter
@Setter
public class AplicanteAssignment extends EntityDB {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "aplicante_id", nullable = false)
    private Aplicante aplicante;

    // Staff responsible for following the Aplicante
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", nullable = false)
    private User assignee;

    // Manager who performed the assignment action
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_id", nullable = false)
    private User assignedBy;

    @Column(nullable = false, updatable = false)
    private Instant assignedAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private EstadoTarefa status = EstadoTarefa.ATRIBUIDO;

    @Column(nullable = false)
    private boolean active = true;

    @Column(length = 500)
    private String notes;

    // Basic guard (runtime check) – ensures assignedBy is MANAGER
    @PrePersist
    @PreUpdate
    private void validateManager() {
        System.out.println("Validating assignedBy: " + assignedBy.getRole());
        if (!assignedBy.getRole().getName().equals(Role.ROLE_CHIEF.toString())) {
            throw new IllegalStateException("As atribuições devem ser realizadas por um membro da equipa com o papel de Chefe Departamento");
        }
    }
}
