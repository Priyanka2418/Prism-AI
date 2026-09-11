# Prism-AI 🚀

<p align="center">
  <strong>Adaptive AI Mock Interview Studio, Multi-Turn Behavioral & Technical Evaluation, and Real-Time Industry Mentorship Platform</strong>
</p>

## 🚀 Live Demo

<p align="center">
  <a href="https://prism-ai-xi.vercel.app/">
    <strong>🌐Prism-AI</strong>
  </a>
</p>

<p align="center">
<img src="https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 25"/>
<img src="https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 4"/>
<img src="https://img.shields.io/badge/Spring%20AI-2.0.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring AI"/>
<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
<img src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black" alt="React 19"/>
<img src="https://img.shields.io/badge/Vite-8-646CFF?style=for-the-badge&logo=vite&logoColor=white" alt="Vite 8"/>
<img src="https://img.shields.io/badge/Tailwind%20CSS-4-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white" alt="Tailwind CSS"/>
<img src="https://img.shields.io/badge/WebSocket-STOMP-010101?style=for-the-badge" alt="WebSocket STOMP"/>
</p>

---

## 📌 Overview

**Prism-AI** is a full-stack, enterprise-grade AI mock interview platform designed to bridge the gap between static interview preparation tools and authentic real-world technical and behavioral hiring evaluations.

Unlike traditional quiz bots that iterate through hardcoded question pools, **Prism-AI** employs a dynamic, stateful LLM conversation engine powered by **Groq** and **Spring AI**. It evaluates candidate answers in real time, generates adaptive follow-ups anchored directly to candidate responses, dynamically adjusts question difficulty based on demonstrated depth, and offers in-depth multi-dimensional analytics alongside 1-on-1 human mentorship.

---

## ✨ Key Features

### 🎙️ 1. Dynamic Multi-Track AI Simulation Studio
* **4 Dedicated Interview Tracks:**
  * **Technical Round:** Explores data structures, distributed systems, caching, concurrency, API design, and practical debugging trade-offs.
  * **Behavioral Round:** Structured around the **STAR method** (*Situation, Task, Action, Result*) to probe leadership, conflict resolution, ownership, and adaptability.
  * **HR & Fit Round:** Evaluates communication clarity, career motivations, teamwork ethics, and organizational culture alignment.
  * **Mixed / Comprehensive Round:** Dynamically alternates between technical architecture questions and behavioral scenarios with conversational bridging.
* **Strict Follow-Up & Zero Repetition Engine:** Intelligently limits consecutive follow-ups per topic (maximum 1) and cross-references previously asked questions to prevent repetition.
* **Adaptive Difficulty Progression:** Analyzes candidate answer depth (`STRONG`, `MODERATE`, `WEAK`) to smoothly elevate or ease question complexity in real time.
* **Speech-to-Text & Text-to-Speech (STT / TTS):** Hands-free voice interview experience with real-time speech dictation and automated voice synthesizer.
* **Client-Side Camera & Audio Recording:** In-browser media recording via `MediaRecorder API`, stored locally in `IndexedDB` for video replay.

---

### 📊 2. Evidence-Based AI Evaluation & Feedback
* **Holistic Scorecards (0–100):** Multi-dimensional scoring combining technical accuracy, communication structure, and practical depth.
* **Granular Feedback Breakdown:**
  * Overall performance justification
  * Answer quality rating & qualitative assessment
  * Key demonstrated strengths with concrete citations from the interview transcript
  * Critical development areas and missed technical trade-offs
  * Tailored 30-day recommended practice roadmap
* **Resilient Evaluation Parsing:** Structured Jackson JSON deserialization with a deterministic analytical fallback for handling unexpected AI responses.

---

### 🤝 3. Verified Industry Mentorship Marketplace
* **Expert Discovery:** Filter verified industry leaders and mentors by target company, specialization domain, experience level, and hourly rate.
* **Mentorship Request Workflow:** Requests move through `PENDING` → `ACCEPTED` / `REJECTED`.
* **Mentor Session Lifecycle:** Accepted sessions progress through `UPCOMING` → `IN_PROGRESS` → `COMPLETED`.
* **Mock Sharing:** Candidates can directly attach completed AI mock interview transcripts and scorecards when requesting mentor coaching.

---

### 💬 4. Real-Time 1-on-1 Mentorship Chat
* **Low-Latency WebSockets:** STOMP-over-WebSocket messaging broker for instantaneous communication between mentors and mentees.
* **Session-Scoped Chat:** Messages are associated with mentor sessions, with participant authorization enforced before sending messages.

---

### 🛡️ 5. Enterprise Security & Architecture
* **Stateless JWT Authentication:** Access & refresh token rotation with `HttpOnly` security cookies.
* **Database Exception Sanitization:** Intercepts low-level Hibernate and SQL errors to shield internal schemas, table names, and stack traces from client exposure.
* **Unified REST API Response Structure:** Standardized pagination, validation errors, and business exception responses.

---

## 🏗️ System Architecture

```text
┌──────────────────────────────────────────────────────────────────────────┐
│                          REACT + VITE FRONTEND                           │
│  ┌────────────────────────┐  ┌───────────────────┐  ┌─────────────────┐  │
│  │ Interview Simulation   │  │ Mentorship Hub    │  │ Real-Time STOMP │  │
│  │ Studio (STT / Video)   │  │ & Profile Engine  │  │ Chat Workspace  │  │
│  └───────────┬────────────┘  └─────────┬─────────┘  └────────┬────────┘  │
└──────────────┼─────────────────────────┼─────────────────────┼───────────┘
               │ HTTPS (REST / JSON)     │                     │ WSS (STOMP)
               ▼                         ▼                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                      SPRING BOOT 4.1.0 BACKEND CORE                      │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │ Security Layer (JWT Authentication, RBAC, HttpOnly Cookies)        │  │
│  └───────────────────────────────────┬────────────────────────────────┘  │
│                                      ▼                                   │
│  ┌─────────────────────┐   ┌─────────────────────┐   ┌────────────────┐  │
│  │ Interview Lifecycle │   │ Mentorship Service  │   │ WebSocket STOMP│  │
│  │ & Turn State Machine│   │ & Request Dispatch  │   │ Message Broker │  │
│  └──────────┬──────────┘   └──────────┬──────────┘   └────────┬───────┘  │
│             │                         │                       │          │
│             ▼                         ▼                       │          │
│  ┌─────────────────────┐   ┌─────────────────────┐            │          │
│  │ Spring AI / Groq    │   │ Global Exception    │            │          │
│  │ LLM Client Engine   │   │ Sanitizer & Advice  │            │          │
│  └──────────┬──────────┘   └─────────────────────┘            │          │
└─────────────┼─────────────────────────────────────────────────┼──────────┘
              │                                                 │
              ▼                                                 ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                      POSTGRESQL RELATIONAL DATABASE                      │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐   ┌───────────┐  │
│  │ interviews   │   │ interview_   │   │ mentor_      │   │ chat_     │  │
│  │ & feedback   │   │ turns        │   │ requests     │   │ messages  │  │
│  └──────────────┘   └──────────────┘   └──────────────┘   └───────────┘  │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 💻 Tech Stack

| Domain                     | Technologies                                                                      |
| -------------------------- | --------------------------------------------------------------------------------- |
| **Backend Core**           | Java 25, Spring Boot 4.1.0, Spring MVC, Spring Data JPA                           |
| **AI & Inference**         | Spring AI 2.0.0, Groq Cloud API (`openai/gpt-oss-20b`), Jackson JSON              |
| **Security & Auth**        | Spring Security, JWT, BCrypt, Role-Based Access Control                           |
| **Database & Persistence** | PostgreSQL, Hibernate ORM, JPA                                                    |
| **Real-Time Layer**        | Spring WebSocket, STOMP Protocol                                                  |
| **Frontend Platform**      | React 19, Vite 8, Tailwind CSS 4, React Router DOM 7                              |
| **Media & Audio**          | Browser MediaRecorder API, Web Speech Recognition API, SpeechSynthesis, IndexedDB |
| **Tooling & Build**        | Maven, npm, Docker, Postman                                                       |
| **Deployment**             | Vercel, Render, Render PostgreSQL                                                 |

---

## 🚀 Getting Started

### 📋 Prerequisites

Ensure you have the following installed on your machine:

* **Java Development Kit (JDK):** Version `25`
* **Node.js:** Version `20+`
* **PostgreSQL:** PostgreSQL running on port `5432`
* **Git:** Latest stable version
* **Groq API Key:** Obtainable from [Groq Console](https://console.groq.com/)

---

### ⚙️ 1. Clone the Repository

```bash
git clone https://github.com/Priyanka2418/Prism-AI.git
cd Prism-AI
```

---

### 🗄️ 2. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE ai_mock_interview;
```

Make sure PostgreSQL is running on port `5432`.

---

### 🔧 3. Backend Configuration

The backend uses Spring Boot configuration with environment variables for sensitive values.

Local configuration can be found at:

```text
src/main/resources/application-local.yaml
```

Example:

```yaml
spring:
  ai:
    openai:
      api-key: ${GROQ_API_KEY}
      base-url: https://api.groq.com/openai/v1
      chat:
        model: openai/gpt-oss-20b

  datasource:
    url: jdbc:postgresql://localhost:5432/ai_mock_interview
    username: postgres
    password: your_postgres_password

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

jwt:
  secret: ${JWT_SECRET}
  access-token-expiration: 3600000
  refresh-token-expiration: 604800000
```

Set the required environment variables:

```text
GROQ_API_KEY=your_groq_api_key
JWT_SECRET=your_secure_jwt_secret
```

---

### 🏃 4. Run the Backend

From the project root:

```bash
./mvnw spring-boot:run
```

#### Windows PowerShell

```powershell
.\mvnw.cmd spring-boot:run
```

The backend runs at:

```text
http://localhost:8080
```

If Swagger/OpenAPI is enabled, API documentation is available at:

```text
http://localhost:8080/swagger-ui.html
```

---

### 🌐 5. Run the Frontend

Open a separate terminal:

```bash
cd frontend
npm install
npm run dev
```

The frontend runs at:

```text
http://localhost:5173
```

During local development, Vite proxies `/api` requests to:

```text
http://localhost:8080
```

---

For local development, PostgreSQL runs on:

```text
localhost:5432
```

with the database:

```text
ai_mock_interview
```

---

## ☁️ Deployment

Prism-AI is deployed using **Vercel for the frontend** and **Render for the backend and PostgreSQL database**.

```text
                         GitHub
                           │
                  ┌────────┴────────┐
                  │                 │
                  ▼                 ▼
              Frontend           Backend
                  │                 │
                  ▼                 ▼
               Vercel             Render
                                    │
                                    ▼
                            Render PostgreSQL
```



## 🤖 AI Interview Flow

Prism-AI uses Spring AI with Groq for AI-powered interview conversations.

```text
Candidate
    │
    ▼
Interview Room
    │
    ▼
Spring Boot Backend
    │
    ▼
Spring AI
    │
    ▼
Groq Cloud API
    │
    ▼
AI Interview Response
    │
    ├──────────────► SpeechSynthesis
    │
    ▼
Interview Evaluation
    │
    ▼
Interview Feedback
```
---

<p align="center">
  <strong>Prism-AI</strong><br/>
  AI-powered mock interviews, evaluation, and mentorship.
</p>
