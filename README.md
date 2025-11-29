# SmartRail Railway Booking System

SmartRail is a railway reservation platform built using **Spring Boot** and **PostgreSQL**.  
It supports train search, seat availability tracking, class-based fare calculation, ticket booking, cancellation, and PDF ticket download.

---

## 🚆 Features
- Train search by station & date  
- Live seat availability (auto-refresh every 5 seconds)  
- Multi-class booking  
  - SL, 3A, 2A, 1A, CC, EC  
- Dynamic fare calculation (based on class multipliers)  
- Passenger details & booking history  
- PNR generation  
- Ticket cancellation  
- **PDF Ticket Download**  
- REST API backend for full-stack integration

---

## 🛠️ Tech Stack
### Backend
- Java 17  
- Spring Boot  
- Spring Data JPA  
- PostgreSQL  
- Lombok  
- iText PDF Generator  

### Frontend (separate repository)
- React.js  
- Axios  
- React Router  
- Custom UI (SmartRail Theme)

---

## 📁 Project Structure (Backend)
src/main/java/com/smartrail/railway_booking
├── controller
├── model
├── repository
├── service
└── dto
