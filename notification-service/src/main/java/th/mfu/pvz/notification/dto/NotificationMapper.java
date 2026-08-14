package th.mfu.pvz.notification.dto;

import th.mfu.pvz.notification.domain.Notification;

public class NotificationMapper {

    public NotificationDTO toDTO(Notification entity) {
        if (entity == null) {
            return null;
        }
        return new NotificationDTO(
                entity.getId(),
                entity.getOrderId(),
                entity.getCustomerId(),
                entity.getMessage(),
                entity.getCreatedAt()
        );
    }
}
