package th.mfu.pvz.notification.Repository;

import org.springframework.data.repository.CrudRepository;

import th.mfu.pvz.notification.domain.Notification;

public interface NotificationRepository extends CrudRepository<Notification, Long>{
    
}
