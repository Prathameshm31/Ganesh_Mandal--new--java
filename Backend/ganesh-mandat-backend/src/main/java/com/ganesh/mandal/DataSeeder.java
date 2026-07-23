package com.ganesh.mandal;

import com.ganesh.mandal.entity.*;
import com.ganesh.mandal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() > 0) return;

        seedPermissions();
        seedRoles();
        seedUsers();
        seedEvents();
        seedVolunteers();
    }

    private void seedPermissions() {
        List<Permission> permissions = List.of(
            Permission.builder().moduleName("Dashboard").permissionName("View Dashboard").permissionCode("DASHBOARD:VIEW").build(),

            Permission.builder().moduleName("User Management").permissionName("View Users").permissionCode("USERS:VIEW").build(),
            Permission.builder().moduleName("User Management").permissionName("Add User").permissionCode("USERS:ADD").build(),
            Permission.builder().moduleName("User Management").permissionName("Edit User").permissionCode("USERS:EDIT").build(),
            Permission.builder().moduleName("User Management").permissionName("Delete User").permissionCode("USERS:DELETE").build(),
            Permission.builder().moduleName("User Management").permissionName("Reset Password").permissionCode("USERS:RESET_PASSWORD").build(),

            Permission.builder().moduleName("Role Management").permissionName("View Roles").permissionCode("ROLES:VIEW").build(),
            Permission.builder().moduleName("Role Management").permissionName("Create Roles").permissionCode("ROLES:CREATE").build(),
            Permission.builder().moduleName("Role Management").permissionName("Edit Roles").permissionCode("ROLES:EDIT").build(),
            Permission.builder().moduleName("Role Management").permissionName("Delete Roles").permissionCode("ROLES:DELETE").build(),
            Permission.builder().moduleName("Role Management").permissionName("Assign Roles").permissionCode("ROLES:ASSIGN").build(),

            Permission.builder().moduleName("Permission Management").permissionName("View Permissions").permissionCode("PERMISSIONS:VIEW").build(),
            Permission.builder().moduleName("Permission Management").permissionName("Assign Permissions").permissionCode("PERMISSIONS:ASSIGN").build(),
            Permission.builder().moduleName("Permission Management").permissionName("Remove Permissions").permissionCode("PERMISSIONS:REMOVE").build(),

            Permission.builder().moduleName("Ganesh Murti Management").permissionName("View").permissionCode("MURTI:VIEW").build(),
            Permission.builder().moduleName("Ganesh Murti Management").permissionName("Add").permissionCode("MURTI:ADD").build(),
            Permission.builder().moduleName("Ganesh Murti Management").permissionName("Edit").permissionCode("MURTI:EDIT").build(),
            Permission.builder().moduleName("Ganesh Murti Management").permissionName("Delete").permissionCode("MURTI:DELETE").build(),

            Permission.builder().moduleName("Sponsors").permissionName("View").permissionCode("SPONSORS:VIEW").build(),
            Permission.builder().moduleName("Sponsors").permissionName("Add").permissionCode("SPONSORS:ADD").build(),
            Permission.builder().moduleName("Sponsors").permissionName("Edit").permissionCode("SPONSORS:EDIT").build(),
            Permission.builder().moduleName("Sponsors").permissionName("Delete").permissionCode("SPONSORS:DELETE").build(),

            Permission.builder().moduleName("Donations").permissionName("View").permissionCode("DONATIONS:VIEW").build(),
            Permission.builder().moduleName("Donations").permissionName("Add").permissionCode("DONATIONS:ADD").build(),
            Permission.builder().moduleName("Donations").permissionName("Edit").permissionCode("DONATIONS:EDIT").build(),
            Permission.builder().moduleName("Donations").permissionName("Delete").permissionCode("DONATIONS:DELETE").build(),
            Permission.builder().moduleName("Donations").permissionName("Approve").permissionCode("DONATIONS:APPROVE").build(),

            Permission.builder().moduleName("Prasad Management").permissionName("View").permissionCode("PRASAD:VIEW").build(),
            Permission.builder().moduleName("Prasad Management").permissionName("Add").permissionCode("PRASAD:ADD").build(),
            Permission.builder().moduleName("Prasad Management").permissionName("Edit").permissionCode("PRASAD:EDIT").build(),
            Permission.builder().moduleName("Prasad Management").permissionName("Delete").permissionCode("PRASAD:DELETE").build(),

            Permission.builder().moduleName("Volunteer & Committee").permissionName("View").permissionCode("VOLUNTEERS:VIEW").build(),
            Permission.builder().moduleName("Volunteer & Committee").permissionName("Add").permissionCode("VOLUNTEERS:ADD").build(),
            Permission.builder().moduleName("Volunteer & Committee").permissionName("Edit").permissionCode("VOLUNTEERS:EDIT").build(),
            Permission.builder().moduleName("Volunteer & Committee").permissionName("Delete").permissionCode("VOLUNTEERS:DELETE").build(),
            Permission.builder().moduleName("Volunteer & Committee").permissionName("Assign Duties").permissionCode("VOLUNTEERS:ASSIGN_DUTIES").build(),

            Permission.builder().moduleName("Event Management").permissionName("View").permissionCode("EVENTS:VIEW").build(),
            Permission.builder().moduleName("Event Management").permissionName("Add").permissionCode("EVENTS:ADD").build(),
            Permission.builder().moduleName("Event Management").permissionName("Edit").permissionCode("EVENTS:EDIT").build(),
            Permission.builder().moduleName("Event Management").permissionName("Delete").permissionCode("EVENTS:DELETE").build(),
            Permission.builder().moduleName("Event Management").permissionName("Publish Events").permissionCode("EVENTS:PUBLISH").build(),

            Permission.builder().moduleName("Reports").permissionName("View Reports").permissionCode("REPORTS:VIEW").build(),
            Permission.builder().moduleName("Reports").permissionName("Export PDF").permissionCode("REPORTS:EXPORT_PDF").build(),
            Permission.builder().moduleName("Reports").permissionName("Export Excel").permissionCode("REPORTS:EXPORT_EXCEL").build(),
            Permission.builder().moduleName("Reports").permissionName("Print Reports").permissionCode("REPORTS:PRINT").build(),

            Permission.builder().moduleName("Notification Management").permissionName("View").permissionCode("NOTIFICATIONS:VIEW").build(),
            Permission.builder().moduleName("Notification Management").permissionName("Send Notifications").permissionCode("NOTIFICATIONS:SEND").build(),
            Permission.builder().moduleName("Notification Management").permissionName("Resend Notifications").permissionCode("NOTIFICATIONS:RESEND").build(),
            Permission.builder().moduleName("Notification Management").permissionName("Manage Templates").permissionCode("NOTIFICATIONS:MANAGE_TEMPLATES").build(),

            Permission.builder().moduleName("Settings").permissionName("View").permissionCode("SETTINGS:VIEW").build(),
            Permission.builder().moduleName("Settings").permissionName("Update").permissionCode("SETTINGS:UPDATE").build()
        );

        permissionRepository.saveAll(permissions);
        System.out.println("Seeded " + permissions.size() + " permissions");
    }

    private void seedRoles() {
        Role adminRole = Role.builder().roleName("ADMIN").description("Full system access").status("ACTIVE").build();
        Role userRole = Role.builder().roleName("USER").description("Basic user access").status("ACTIVE").build();
        Role managerRole = Role.builder().roleName("MANAGER").description("Managerial access").status("ACTIVE").build();
        roleRepository.saveAll(List.of(adminRole, userRole, managerRole));

        List<Permission> allPerms = permissionRepository.findAll();
        for (Permission p : allPerms) {
            rolePermissionRepository.save(RolePermission.builder().role(adminRole).permission(p).build());
        }

        List<String> userPermCodes = List.of(
            "DASHBOARD:VIEW",
            "USERS:VIEW",
            "MURTI:VIEW",
            "SPONSORS:VIEW",
            "DONATIONS:VIEW",
            "DONATIONS:ADD",
            "PRASAD:VIEW",
            "VOLUNTEERS:VIEW",
            "EVENTS:VIEW",
            "REPORTS:VIEW",
            "NOTIFICATIONS:VIEW",
            "SETTINGS:VIEW"
        );
        for (Permission p : allPerms) {
            if (userPermCodes.contains(p.getPermissionCode())) {
                rolePermissionRepository.save(RolePermission.builder().role(userRole).permission(p).build());
            }
        }

        List<String> managerPermCodes = List.of(
            "DASHBOARD:VIEW",
            "USERS:VIEW",
            "USERS:ADD",
            "USERS:EDIT",
            "ROLES:VIEW",
            "PERMISSIONS:VIEW",
            "MURTI:VIEW", "MURTI:ADD", "MURTI:EDIT",
            "SPONSORS:VIEW", "SPONSORS:ADD", "SPONSORS:EDIT",
            "DONATIONS:VIEW", "DONATIONS:ADD", "DONATIONS:EDIT", "DONATIONS:APPROVE",
            "PRASAD:VIEW", "PRASAD:ADD", "PRASAD:EDIT",
            "VOLUNTEERS:VIEW", "VOLUNTEERS:ADD", "VOLUNTEERS:EDIT", "VOLUNTEERS:ASSIGN_DUTIES",
            "EVENTS:VIEW", "EVENTS:ADD", "EVENTS:EDIT", "EVENTS:PUBLISH",
            "REPORTS:VIEW", "REPORTS:EXPORT_PDF", "REPORTS:EXPORT_EXCEL", "REPORTS:PRINT",
            "NOTIFICATIONS:VIEW", "NOTIFICATIONS:SEND",
            "SETTINGS:VIEW", "SETTINGS:UPDATE"
        );
        for (Permission p : allPerms) {
            if (managerPermCodes.contains(p.getPermissionCode())) {
                rolePermissionRepository.save(RolePermission.builder().role(managerRole).permission(p).build());
            }
        }

        System.out.println("Seeded 3 roles with permissions");
    }

    private void seedUsers() {
        User admin = User.builder().username("admin").password("admin123").name("Admin User").email("admin@example.com").status("ACTIVE").build();
        User user = User.builder().username("user").password("user123").name("Regular User").email("user@example.com").status("ACTIVE").build();
        User manager = User.builder().username("manager").password("manager123").name("Manager User").email("manager@example.com").status("ACTIVE").build();
        userRepository.saveAll(List.of(admin, user, manager));

        Role adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();
        Role userRole = roleRepository.findByRoleName("USER").orElseThrow();
        Role managerRole = roleRepository.findByRoleName("MANAGER").orElseThrow();

        userRoleRepository.saveAll(List.of(
            UserRole.builder().user(admin).role(adminRole).build(),
            UserRole.builder().user(user).role(userRole).build(),
            UserRole.builder().user(manager).role(managerRole).build()
        ));

        System.out.println("Seeded 3 users");
    }

    private void seedEvents() {
        if (eventRepository.count() > 0) return;
        String year = "2026";
        List<Event> events = List.of(
                Event.builder().eventName("Ganesh Murti Booking").eventCategory("Before Festival").festivalDay("Pre-Festival").festivalYear(year).date(LocalDate.of(2026, 9, 1)).description("Book Ganesh murti from artist").status("Planned").build(),
                Event.builder().eventName("Murti Arrival").eventCategory("Before Festival").festivalDay("Pre-Festival").festivalYear(year).date(LocalDate.of(2026, 9, 5)).description("Arrival of Ganesh murti").status("Planned").build(),
                Event.builder().eventName("Mandap Setup").eventCategory("Before Festival").festivalDay("Pre-Festival").festivalYear(year).date(LocalDate.of(2026, 9, 3)).description("Setup the main pandal").status("Planned").build(),
                Event.builder().eventName("Decoration Work").eventCategory("Before Festival").festivalDay("Pre-Festival").festivalYear(year).date(LocalDate.of(2026, 9, 4)).description("Decorate the mandap").status("Planned").build(),
                Event.builder().eventName("Electrical Setup").eventCategory("Before Festival").festivalDay("Pre-Festival").festivalYear(year).date(LocalDate.of(2026, 9, 3)).description("Setup electrical connections and lighting").status("Planned").build(),
                Event.builder().eventName("Sound System Setup").eventCategory("Before Festival").festivalDay("Pre-Festival").festivalYear(year).date(LocalDate.of(2026, 9, 4)).description("Install sound system").status("Planned").build(),
                Event.builder().eventName("Cleaning Drive").eventCategory("Before Festival").festivalDay("Pre-Festival").festivalYear(year).date(LocalDate.of(2026, 9, 2)).description("Clean the premises").status("Planned").build(),
                Event.builder().eventName("Volunteer Meeting").eventCategory("Before Festival").festivalDay("Pre-Festival").festivalYear(year).date(LocalDate.of(2026, 8, 30)).description("Coordinate with all volunteers").status("Planned").build(),
                Event.builder().eventName("Sponsor Meeting").eventCategory("Before Festival").festivalDay("Pre-Festival").festivalYear(year).date(LocalDate.of(2026, 8, 28)).description("Meet with sponsors").status("Planned").build(),
                Event.builder().eventName("Banner Installation").eventCategory("Before Festival").festivalDay("Pre-Festival").festivalYear(year).date(LocalDate.of(2026, 9, 5)).description("Install banners around the area").status("Planned").build(),
                Event.builder().eventName("Ganesh Murti Installation").eventCategory("Day 1").festivalDay("Day 1").festivalYear(year).date(LocalDate.of(2026, 9, 7)).startTime("07:00").description("Install Ganesh murti").status("Planned").build(),
                Event.builder().eventName("Ganesh Sthapana").eventCategory("Day 1").festivalDay("Day 1").festivalYear(year).date(LocalDate.of(2026, 9, 7)).startTime("07:30").description("Ganesh Sthapana ceremony").status("Planned").build(),
                Event.builder().eventName("Maha Aarti - Day 1").eventCategory("Day 1").festivalDay("Day 1").festivalYear(year).date(LocalDate.of(2026, 9, 7)).startTime("19:00").description("Evening Maha Aarti").status("Planned").build(),
                Event.builder().eventName("Prasad Distribution - Day 1").eventCategory("Day 1").festivalDay("Day 1").festivalYear(year).date(LocalDate.of(2026, 9, 7)).startTime("20:00").description("Distribute prasad to devotees").status("Planned").build(),
                Event.builder().eventName("Welcome Ceremony").eventCategory("Day 1").festivalDay("Day 1").festivalYear(year).date(LocalDate.of(2026, 9, 7)).startTime("10:00").description("Welcome guests and dignitaries").status("Planned").build(),
                Event.builder().eventName("Cultural Program - Day 1").eventCategory("Day 1").festivalDay("Day 1").festivalYear(year).date(LocalDate.of(2026, 9, 7)).startTime("21:00").description("Cultural performances").status("Planned").build(),
                Event.builder().eventName("Morning Aarti").eventCategory("Daily").festivalDay("Daily").festivalYear(year).description("Morning prayer and aarti").status("Planned").build(),
                Event.builder().eventName("Afternoon Aarti").eventCategory("Daily").festivalDay("Daily").festivalYear(year).description("Afternoon prayer").status("Planned").build(),
                Event.builder().eventName("Evening Aarti").eventCategory("Daily").festivalDay("Daily").festivalYear(year).description("Evening aarti").status("Planned").build(),
                Event.builder().eventName("Bhajan").eventCategory("Daily").festivalDay("Daily").festivalYear(year).description("Bhajan singing session").status("Planned").build(),
                Event.builder().eventName("Kirtan").eventCategory("Daily").festivalDay("Daily").festivalYear(year).description("Kirtan session").status("Planned").build(),
                Event.builder().eventName("Kids Activities").eventCategory("Daily").festivalDay("Daily").festivalYear(year).description("Activities for children").status("Planned").build(),
                Event.builder().eventName("Maha Prasad").eventCategory("Daily").festivalDay("Daily").festivalYear(year).description("Main prasad distribution").status("Planned").build(),
                Event.builder().eventName("Drawing Competition").eventCategory("Daily").festivalDay("Daily").festivalYear(year).description("Drawing competition for kids").status("Planned").build(),
                Event.builder().eventName("Rangoli Competition").eventCategory("Daily").festivalDay("Daily").festivalYear(year).description("Rangoli competition").status("Planned").build(),
                Event.builder().eventName("Quiz Competition").eventCategory("Daily").festivalDay("Daily").festivalYear(year).description("Quiz competition").status("Planned").build(),
                Event.builder().eventName("Dance Competition").eventCategory("Daily").festivalDay("Daily").festivalYear(year).description("Dance competition").status("Planned").build(),
                Event.builder().eventName("Singing Competition").eventCategory("Daily").festivalDay("Daily").festivalYear(year).description("Singing competition").status("Planned").build(),
                Event.builder().eventName("Final Maha Aarti").eventCategory("Final Day").festivalDay("Final Day").festivalYear(year).date(LocalDate.of(2026, 9, 17)).startTime("19:00").description("Final day Maha Aarti").status("Planned").build(),
                Event.builder().eventName("Visarjan Procession").eventCategory("Final Day").festivalDay("Final Day").festivalYear(year).date(LocalDate.of(2026, 9, 18)).startTime("10:00").description("Visarjan procession").status("Planned").build(),
                Event.builder().eventName("Fireworks").eventCategory("Final Day").festivalDay("Final Day").festivalYear(year).date(LocalDate.of(2026, 9, 18)).startTime("22:00").description("Fireworks display").status("Planned").build(),
                Event.builder().eventName("Farewell Ceremony").eventCategory("Final Day").festivalDay("Final Day").festivalYear(year).description("Farewell to Ganesh").status("Planned").build(),
                Event.builder().eventName("Volunteer Appreciation").eventCategory("Final Day").festivalDay("Final Day").festivalYear(year).description("Thank volunteers for their service").status("Planned").build(),
                Event.builder().eventName("Closing Ceremony").eventCategory("Final Day").festivalDay("Final Day").festivalYear(year).description("Closing ceremony").status("Planned").build()
        );
        eventRepository.saveAll(events);
        System.out.println("Seeded " + events.size() + " predefined events for " + year);
    }

    private void seedVolunteers() {
        if (volunteerRepository.count() > 0) return;
        String year = "2026";
        List<Volunteer> volunteers = List.of(
                Volunteer.builder().name("Rajesh Sharma").mobile("9876543210").email("rajesh@example.com").address("123 Main St, Pune").category("Core Committee").role("President").festivalYear(year).status("Active").joiningDate(LocalDate.of(2026, 1, 15)).skills("Leadership, Management").experience("10 years").availability("Full-time").build(),
                Volunteer.builder().name("Amit Deshmukh").mobile("9876543211").email("amit@example.com").address("456 Oak Ave, Pune").category("Core Committee").role("Vice President").festivalYear(year).status("Active").joiningDate(LocalDate.of(2026, 1, 20)).skills("Management, Coordination").experience("8 years").availability("Full-time").build(),
                Volunteer.builder().name("Priya Patil").mobile("9876543212").email("priya@example.com").address("789 Pine Rd, Pune").category("Core Committee").role("Secretary").festivalYear(year).status("Active").joiningDate(LocalDate.of(2026, 2, 1)).skills("Documentation, Communication").experience("6 years").availability("Part-time").build(),
                Volunteer.builder().name("Sandeep Joshi").mobile("9876543213").email("sandeep@example.com").category("Finance").role("Treasurer").festivalYear(year).status("Active").joiningDate(LocalDate.of(2026, 2, 5)).skills("Accounting, Finance").experience("12 years").availability("Full-time").build(),
                Volunteer.builder().name("Neha Kulkarni").mobile("9876543214").email("neha@example.com").category("Social Media & Marketing").role("Social Media Manager").festivalYear(year).status("Active").joiningDate(LocalDate.of(2026, 2, 10)).skills("Social Media, Content Creation").experience("5 years").availability("Part-time").build(),
                Volunteer.builder().name("Vijay More").mobile("9876543215").email("vijay@example.com").category("Decoration & Murti").role("Decoration Head").festivalYear(year).status("Active").joiningDate(LocalDate.of(2026, 2, 15)).skills("Decoration, Design").experience("7 years").availability("Full-time").build(),
                Volunteer.builder().name("Anita Gaikwad").mobile("9876543216").email("anita@example.com").category("Prasad & Food").role("Prasad Coordinator").festivalYear(year).status("Active").joiningDate(LocalDate.of(2026, 3, 1)).skills("Catering, Management").experience("9 years").availability("Full-time").build(),
                Volunteer.builder().name("Rahul Pawar").mobile("9876543217").email("rahul@example.com").category("Security & Safety").role("Security Head").festivalYear(year).status("Active").joiningDate(LocalDate.of(2026, 3, 5)).skills("Security, Crowd Management").experience("15 years").availability("Full-time").build(),
                Volunteer.builder().name("Sneha Desai").mobile("9876543218").email("sneha@example.com").category("Event Management").role("Event Organizer").festivalYear(year).status("Active").joiningDate(LocalDate.of(2026, 3, 10)).skills("Event Planning, Coordination").experience("6 years").availability("Part-time").build(),
                Volunteer.builder().name("Manoj Jadhav").mobile("9876543219").email("manoj@example.com").category("Logistics").role("Sound System Coordinator").festivalYear(year).status("Inactive").joiningDate(LocalDate.of(2026, 3, 15)).skills("Audio, Technical").experience("4 years").availability("Weekends").build(),
                Volunteer.builder().name("Kavita Shinde").mobile("9876543220").email("kavita@example.com").category("Public Relations").role("VIP Guest Coordinator").festivalYear(year).status("Active").joiningDate(LocalDate.of(2026, 4, 1)).skills("Hospitality, Communication").experience("8 years").availability("Part-time").build(),
                Volunteer.builder().name("Deepak Chavan").mobile("9876543221").email("deepak@example.com").category("Event Management").role("Anchor/Host").festivalYear(year).status("Active").joiningDate(LocalDate.of(2026, 4, 5)).skills("Anchoring, Communication").experience("3 years").availability("Full-time").build()
        );
        volunteerRepository.saveAll(volunteers);
        System.out.println("Seeded " + volunteers.size() + " volunteers for " + year);
    }
}
