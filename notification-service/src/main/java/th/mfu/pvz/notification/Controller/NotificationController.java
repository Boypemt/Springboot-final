package th.mfu.pvz.notification.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import th.mfu.pvz.notification.Service.NotificationService;
import th.mfu.pvz.notification.domain.Notification;
import th.mfu.pvz.notification.dto.NotificationDTO;
import th.mfu.pvz.notification.dto.NotificationMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper mapper;

    public NotificationController(NotificationService notificationService, NotificationMapper mapper) {
        this.notificationService = notificationService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAll() {
        List<NotificationDTO> result = notificationService.getAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getById(@PathVariable Long id) {
        Notification notification = notificationService.getById(id);
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapper.toDTO(notification));
    }
}
