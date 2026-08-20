# Prism-AI

<p align="center">
  <strong>AI-powered mock interviews, personalized interview preparation, and expert mentoring.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Spring%20AI-LLM-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring AI"/>
  <img src="https://img.shields.io/badge/Groq-LLM-F55036?style=for-the-badge" alt="Groq"/>
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
</p>

---

## Long-Term Goal

**Prism-AI aims to evolve from an AI mock interview platform into a personalized interview preparation and expert mentoring platform.**

The goal is to help candidates:

**Practice → Evaluate → Identify Weaknesses → Improve → Prepare → Get Mentored**

The platform will progressively combine **performance evaluation, personalized feedback, analytics, targeted preparation, and expert mentoring** into one continuous interview preparation experience.

---

## Overview

Prism-AI simulates realistic interviews through **dynamic questioning, adaptive difficulty, follow-up questions, and intelligent topic switching**.

The current implementation focuses on the backend AI interview engine. A frontend, video-based AI interview experience, voice interaction, AI evaluation, analytics, and expert mentoring are planned as upcoming components.

---

## Features

* **AI-Powered Interviews** — dynamically generated questions based on interview context.
* **Adaptive Difficulty** — adjusts between `EASY`, `MEDIUM`, and `HARD` based on candidate performance.
* **Dynamic Question Flow** — supports `FOLLOW_UP`, `NEW_TOPIC`, and interview completion decisions.
* **Interview Lifecycle** — controlled state transitions with automatic completion for timed interviews.
* **Secure APIs** — JWT authentication, role-based authorization, and interview ownership validation.
* **Persistent Conversation History** — every question and answer is stored as an `InterviewTurn`.
* **Interview-Type Strategies** — supports Technical and HR interview behavior.

---

## AI Interview Flow

```text
Candidate Answer
       ↓
Interview Context
       ↓
     Groq LLM
       ↓
Performance + Action + Difficulty + Topic
       ↓
   Next Question
       ↓
 Save Interview Turn
```

The backend handles **security, validation, state management, and persistence**, while the AI handles **adaptive conversational decision-making**.

---

## Architecture

```text
Client
   ↓
REST API
   ↓
Spring Boot
   ├── Security / JWT
   ├── Interview Management
   ├── Interview Turns
   └── AI Orchestration
          ↓
      Spring AI
          ↓
        Groq
          ↓
     PostgreSQL
```

> The frontend, video-based AI interview experience, and voice interaction are currently planned and will be integrated with the existing backend.

---

## Tech Stack

| Layer                    | Technology                                       |
| ------------------------ | ------------------------------------------------ |
| Language                 | Java 25                                          |
| Backend                  | Spring Boot 4.1.0                                |
| Security                 | Spring Security + JWT                            |
| AI Interview             | Spring AI + Groq                                 |
| Database                 | PostgreSQL                                       |
| Persistence              | Spring Data JPA / Hibernate                      |
| API                      | REST                                             |
| Build                    | Maven                                            |
| Real-Time Communication  | WebSocket *(upcoming)*                           |
| Video Interview          | WebRTC *(upcoming)*                              |
| Voice Interaction        | Speech-to-Text / Text-to-Speech *(upcoming)*     |
| Frontend                 | React + Vite *(upcoming)*                        |
| Advanced AI              | Vector DB, RAG, Tool Calling *(upcoming)*        |


---


## Current Status

### Implemented

* Authentication & authorization
* Interview lifecycle
* Automatic interview completion
* Interview turn management
* AI question generation
* AI context construction
* Adaptive difficulty
* Follow-up questions
* Dynamic topic switching
* Groq integration through Spring AI

### Upcoming

* Video-based AI interview sessions
* Speech-to-Text / Text-to-Speech
* AI interview evaluation & feedback
* Performance analytics
* Candidate progress tracking
* Expert mentoring sessions
* Real-time candidate-professional communication
* RAG-based personalized preparation
* Advanced Spring AI capabilities
* Docker-based deployment

---

## Getting Started

### Prerequisites

* Java 25
* Maven
* PostgreSQL
* Groq API Key

### Database

```sql
CREATE DATABASE ai_mock_interview;
```

### Environment Variables

```text
DB_USERNAME=your_username
DB_PASSWORD=your_password
GROQ_API_KEY=your_api_key
```

### Run

```bash
mvn spring-boot:run
```
