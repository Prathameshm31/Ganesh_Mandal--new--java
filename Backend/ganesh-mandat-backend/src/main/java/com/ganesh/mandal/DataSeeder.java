package com.ganesh.mandal;

import com.ganesh.mandal.entity.Event;
import com.ganesh.mandal.entity.Volunteer;
import com.ganesh.mandal.repository.EventRepository;
import com.ganesh.mandal.repository.VolunteerRepository;
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

    @Override
    public void run(String... args) {
        if (eventRepository.count() > 0) return;
        seedEvents();
        seedVolunteers();
    }

    private void seedEvents() {
        String year = "2026";
        List<Event> events = List.of(
                // Before Festival
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
                // Day 1
                Event.builder().eventName("Ganesh Murti Installation").eventCategory("Day 1").festivalDay("Day 1").festivalYear(year).date(LocalDate.of(2026, 9, 7)).startTime("07:00").description("Install Ganesh murti").status("Planned").build(),
                Event.builder().eventName("Ganesh Sthapana").eventCategory("Day 1").festivalDay("Day 1").festivalYear(year).date(LocalDate.of(2026, 9, 7)).startTime("07:30").description("Ganesh Sthapana ceremony").status("Planned").build(),
                Event.builder().eventName("Maha Aarti - Day 1").eventCategory("Day 1").festivalDay("Day 1").festivalYear(year).date(LocalDate.of(2026, 9, 7)).startTime("19:00").description("Evening Maha Aarti").status("Planned").build(),
                Event.builder().eventName("Prasad Distribution - Day 1").eventCategory("Day 1").festivalDay("Day 1").festivalYear(year).date(LocalDate.of(2026, 9, 7)).startTime("20:00").description("Distribute prasad to devotees").status("Planned").build(),
                Event.builder().eventName("Welcome Ceremony").eventCategory("Day 1").festivalDay("Day 1").festivalYear(year).date(LocalDate.of(2026, 9, 7)).startTime("10:00").description("Welcome guests and dignitaries").status("Planned").build(),
                Event.builder().eventName("Cultural Program - Day 1").eventCategory("Day 1").festivalDay("Day 1").festivalYear(year).date(LocalDate.of(2026, 9, 7)).startTime("21:00").description("Cultural performances").status("Planned").build(),
                // Daily Events
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
                // Final Day
                Event.builder().eventName("Final Maha Aarti").eventCategory("Final Day").festivalDay("Final Day").festivalYear(year).date(LocalDate.of(2026, 9, 17)).startTime("19:00").description("Final day Maha Aarti").status("Planned").build(),
                Event.builder().eventName("Visarjan Procession").eventCategory("Final Day").festivalDay("Final Day").festivalYear(year).date(LocalDate.of(2026, 9, 18)).startTime("10:00").description("Visarjan procession").status("Planned").build(),
                Event.builder().eventName("Fireworks").eventCategory("Final Day").festivalDay("Final Day").festivalYear(year).date(LocalDate.of(2026, 9, 18)).startTime("22:00").description("Fireworks display").status("Planned").build(),
                Event.builder().eventName("Farewell Ceremony").eventCategory("Final Day").festivalDay("Final Day").festivalYear(year).date(LocalDate.of(2026, 9, 18)).description("Farewell to Ganesh").status("Planned").build(),
                Event.builder().eventName("Volunteer Appreciation").eventCategory("Final Day").festivalDay("Final Day").festivalYear(year).description("Thank volunteers for their service").status("Planned").build(),
                Event.builder().eventName("Closing Ceremony").eventCategory("Final Day").festivalDay("Final Day").festivalYear(year).description("Closing ceremony").status("Planned").build()
        );
        eventRepository.saveAll(events);
        System.out.println("Seeded " + events.size() + " predefined events for " + year);
    }

    private void seedVolunteers() {
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
