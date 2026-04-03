package com.example.pariba.services.impl;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.dtos.requests.RequestExportRequest;
import com.example.pariba.dtos.responses.ExportJobResponse;
import com.example.pariba.enums.ExportStatus;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.ExportJob;
import com.example.pariba.models.Person;
import com.example.pariba.repositories.ExportJobRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.IExportService;
import com.example.pariba.services.ISubscriptionService;
import com.example.pariba.exceptions.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExportServiceImpl implements IExportService {

    private final ExportJobRepository exportJobRepository;
    private final PersonRepository personRepository;
    private final IAuditService auditService;
    private final ISubscriptionService subscriptionService;

    public ExportServiceImpl(ExportJobRepository exportJobRepository,
                            PersonRepository personRepository,
                            IAuditService auditService,
                            ISubscriptionService subscriptionService) {
        this.exportJobRepository = exportJobRepository;
        this.personRepository = personRepository;
        this.auditService = auditService;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public ExportJobResponse requestExport(String personId, RequestExportRequest request) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        // Vérifier les droits d'export selon le plan d'abonnement
        String format = request.getFormat().toUpperCase();
        if ("PDF".equals(format)) {
            if (!subscriptionService.canExportPdf(personId)) {
                log.warn("⚠️ Export PDF refusé pour {} - Plan ne permet pas l'export PDF", personId);
                throw new ForbiddenException("L'export PDF n'est pas disponible avec votre plan. Passez à un plan supérieur.");
            }
        } else if ("EXCEL".equals(format) || "XLSX".equals(format) || "XLS".equals(format)) {
            if (!subscriptionService.canExportExcel(personId)) {
                log.warn("⚠️ Export Excel refusé pour {} - Plan ne permet pas l'export Excel", personId);
                throw new ForbiddenException("L'export Excel n'est pas disponible avec votre plan. Passez à un plan supérieur.");
            }
        }
        
        log.info("✅ Export {} autorisé pour {}", format, personId);

        ExportJob job = new ExportJob();
        job.setRequestedBy(person);
        job.setExportType(request.getExportType());
        job.setFormat(request.getFormat());
        job.setStatus(ExportStatus.PENDING);
        job.setFilters(request.getFilters());

        job = exportJobRepository.save(job);

        // Audit log
        auditService.log(personId, AppConstants.AUDIT_EXPORT_REQUEST, "ExportJob", job.getId(), null);

        // TODO: Déclencher le traitement asynchrone de l'export
        // processExportJobAsync(job.getId());

        return new ExportJobResponse(job);
    }

    @Override
    public ExportJobResponse getExportJobById(String jobId) {
        ExportJob job = exportJobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("ExportJob", "id", jobId));
        return new ExportJobResponse(job);
    }

    @Override
    public List<ExportJobResponse> getExportJobsByPerson(String personId) {
        return exportJobRepository.findByRequestedByIdOrderByCreatedAtDesc(personId)
                .stream()
                .map(ExportJobResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public void processExportJob(String jobId) {
        ExportJob job = exportJobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("ExportJob", "id", jobId));

        try {
            job.setStatus(ExportStatus.PROCESSING);
            exportJobRepository.save(job);

            // TODO: Générer le fichier d'export selon le type et le format
            String filePath = generateExportFile(job);

            job.setStatus(ExportStatus.COMPLETED);
            job.setDownloadUrl(filePath);
            exportJobRepository.save(job);

        } catch (Exception e) {
            job.setStatus(ExportStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            exportJobRepository.save(job);
        }
    }

    @Override
    public void cleanupOldExports() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        List<ExportJob> oldJobs = exportJobRepository.findOldCompletedJobs(cutoffDate);

        for (ExportJob job : oldJobs) {
            // TODO: Supprimer le fichier physique
            if (job.getDownloadUrl() != null) {
                // deleteFile(job.getDownloadUrl());
            }
            exportJobRepository.delete(job);
        }
    }

    private String generateExportFile(ExportJob job) {
        // TODO: Implémenter la génération du fichier selon le type et le format
        String fileName = String.format("export_%s_%s.%s", 
            job.getExportType(), 
            job.getId(), 
            job.getFormat().toLowerCase());

        // Simuler la génération
        return "/exports/" + fileName;
    }
}
