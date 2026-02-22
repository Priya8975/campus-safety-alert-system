package com.campus.alertprocessing.config;

import com.campus.alertprocessing.model.*;
import com.campus.alertprocessing.service.NotificationPreferenceRepository;
import com.campus.alertprocessing.service.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    public DataInitializer(UserRepository userRepository,
                           NotificationPreferenceRepository preferenceRepository) {
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Data already initialized, skipping seed");
            return;
        }

        log.info("Seeding test users and notification preferences...");

        // Create sample users
        User alice = createUser("Alice Johnson", "alice@stonybrook.edu", "+16315551001", UserRole.STUDENT);
        User bob = createUser("Bob Smith", "bob@stonybrook.edu", "+16315551002", UserRole.STUDENT);
        User carol = createUser("Carol Williams", "carol@stonybrook.edu", "+16315551003", UserRole.FACULTY);
        User dave = createUser("Dave Brown", "dave@stonybrook.edu", "+16315551004", UserRole.STAFF);
        User eve = createUser("Eve Davis", "eve@stonybrook.edu", "+16315551005", UserRole.STUDENT);

        List<User> users = userRepository.saveAll(List.of(alice, bob, carol, dave, eve));
        log.info("Created {} test users", users.size());

        // Create notification preferences
        for (User user : users) {
            // All users get push notifications for all severities
            createPreference(user, "push", SeverityLevel.LOW, null);

            // All users get email for HIGH and above
            createPreference(user, "email", SeverityLevel.HIGH, null);

            // All users get SMS for CRITICAL only
            createPreference(user, "sms", SeverityLevel.CRITICAL, null);
        }

        log.info("Created notification preferences for {} users", users.size());
    }

    private User createUser(String name, String email, String phone, UserRole role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        return user;
    }

    private void createPreference(User user, String channel,
                                   SeverityLevel threshold, String zoneFilter) {
        NotificationPreference pref = new NotificationPreference();
        pref.setUser(user);
        pref.setChannel(channel);
        pref.setSeverityThreshold(threshold);
        pref.setCampusZoneFilter(zoneFilter);
        preferenceRepository.save(pref);
    }
}
