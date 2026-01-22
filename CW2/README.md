# Restaurant Management Mobile Application

**MAL2017 – Software Engineering II**  
**Author:** OOI WEI CHYEH (BSCS2509254)

---

## What This Is

This repository documents the design work for a mobile app meant to help restaurant staff manage operations while giving customers an easier way to browse menus and book tables. It's built around two very different user groups with different needs, which made the design process interesting and occasionally frustrating.

The project follows the expected workflow for MAL2017: starting with user research, moving through rough sketches, testing those ideas with actual people, and then refining everything based on what didn't work. The end goal is a functional Android app, but right now this is mostly about getting the design right before diving into code.

---

## What's Here

The repo contains:

- **Context analysis** – who's using this, where they're using it, and what problems they're trying to solve
- **Low-fidelity sketches** – early interface ideas drawn out on paper (or Canva, close enough)
- **Storyboards** – visual narratives showing how staff and guests move through key tasks
- **Usability testing materials** – consent forms, test plans, participant notes, and a bunch of observations about what went wrong
- **Revised designs** – updated interfaces that address the issues people actually had
- **Change documentation** – a running log of what changed and why

These pieces form the foundation for building the high-fidelity UI in Android Studio later on.

---

## Repository Structure

```
design-exercises-OoiWeiChyeh/
  ├── CW1/
      ├── Documentation/
      │   ├── Diagram & Forms/
      │   └── MAL2017_PresentationSlide/
      ├── Source Code/
      └── MAL2017_SoftwareEngineering2_CW1_OoiWeiChyeh/
  └── README.md
```

---

## Key Design Decisions

### Understanding the Users

The context analysis identifies two primary user types: restaurant staff and customers who operate in completely different environments. Staff are dealing with noise, multitasking, and peak-hour chaos. Customers might be browsing at home on a tablet or making last-minute bookings on their phone while walking. These constraints shaped a lot of the interface choices, like larger buttons for staff and clearer confirmation feedback for guests.

There's also the question of technical literacy. Some staff are comfortable with mobile apps, others less so. The design had to work for both without feeling dumbed down.

### Early Sketches

The low-fidelity prototypes were deliberately rough just enough to test ideas without getting attached to any particular layout. Keeping things at sketch level made it easier to throw out bad ideas quickly. The focus was on interaction flow and information hierarchy rather than making things look polished.

Looking back, some of the early navigation patterns were overly complex. Users pointed that out pretty quickly during testing.

### Usability Testing

Two participants took part: a staff member from a campus café and a university student who fits the guest profile. Both signed consent forms (included in the appendices), and I walked them through paper-based prototypes while they thought aloud.

**What came out of it:**

- The staff member worried the reservation list would "get messy during the lunch rush" and found the "Add Menu Item" button too easy to miss
- The student wanted clearer wording (like "Edit Booking" instead of "Modify Reservation") and better feedback when bookings were confirmed
- Both struggled with inconsistent spacing and expected icons for main navigation

Honestly, watching people get confused by things that seemed obvious to me was humbling. The test made it clear which labels didn't match user expectations and where the interface needed more breathing room.

### Design Revisions

After testing, several changes were made:

- Replaced vague terms like "Modify" with more direct language
- Increased button sizes for key actions (Fitts' Law in practice)
- Added colour-coding for reservation status so staff could scan quickly
- Improved spacing and alignment across screens
- Introduced a floating action button (FAB) for adding menu items, making it harder to overlook

The revised storyboards show these updates in context. The core user journeys haven't changed, but interactions should feel smoother now—or at least, that's the hope.

---

## Future Enhancement

This stage is about design, not implementation, but a few things are already on my mind for later development:

- **Responsive layouts** – the interface needs to work across different Android devices and screen sizes
- **Role-based access** – keeping staff and guest features properly separated without making login annoying
- **Environmental factors** – ensuring readability in bright restaurant lighting or at night
- **Performance** – the app needs to handle reservation lists during peak hours without lagging

Some of these concerns influenced design decisions (like using larger touch targets and high-contrast colours), even though the actual coding happens later.

---

## Notes on Process

This project follows the MAL2017 coursework structure which emphasises user-centred design and iterative improvement. The work demonstrates systematic analysis, structured evaluation and reflective redesign basically, showing the messy process of how design actually happens rather than pretending everything was perfect from the start.

Any AI-assisted work is disclosed in the final submission as required by the module guidelines.

---

## About Me

**OOI WEI CHYEH**  
BSc (Hons) Computer Science (Cyber Security)  
University of Plymouth
