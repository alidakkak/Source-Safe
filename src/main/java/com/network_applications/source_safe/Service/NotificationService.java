package com.network_applications.source_safe.Service;

import com.network_applications.source_safe.Model.Entity.File;
import com.network_applications.source_safe.Model.Entity.User;
import com.network_applications.source_safe.Model.Entity.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
@Service
public class NotificationService {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void notifyUsersAboutFileUpdate(File file, String actionMessage) {
        // جلب المستخدمين المرتبطين بالملف
        Set<User> users = file.getGroup().getUserGroups().stream()
                .map(UserGroup::getUser)
                .collect(Collectors.toSet());

        // إرسال الإشعار لكل مستخدم متصل
        String message = "File \"" + file.getName() + "\" has been " + actionMessage + ".";
        users.forEach(user -> {
            simpMessagingTemplate.convertAndSend("/topic/notifications/" + user.getId(), message);
        });
    }
}
