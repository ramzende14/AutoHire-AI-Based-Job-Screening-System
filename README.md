# 🚀 AutoHire – AI Based Job Screening System

AutoHire is an AI-powered backend system that automates resume screening, candidate ranking, and interview scheduling using Azure AI services.

It reduces manual recruiter effort by intelligently analyzing resumes and matching them with job descriptions using similarity scoring.

---

## 🧠 Problem Statement

Manual resume screening is:
- Time-consuming
- Error-prone
- Biased
- Inefficient at scale

AutoHire solves this by:
- Extracting structured resume data
- Matching resumes with job requirements
- Scoring candidates automatically
- Scheduling interviews for high-scoring applicants

---

## 🏗 System Architecture

Frontend (HTML/CSS/JS)
        ↓
Spring Boot REST APIs
        ↓
Service Layer (Business Logic)
        ↓
Azure AI Services (Form Recognizer + OpenAI)
        ↓
MySQL Database

---

## 🔥 Key Features

### ✅ Resume Parsing
- Integrated Azure Form Recognizer (Document Intelligence)
- Extracts structured data from uploaded resumes

### ✅ JD Analyzer
- Extracts required skills from job description
- Cleans and normalizes skill keywords

### ✅ AI Matching Engine
- Uses Azure OpenAI to calculate Resume–JD similarity score
- Ranks candidates based on score

### ✅ Automated Interview Scheduling
- Automatically shortlists candidates scoring ≥ 80%
- Sends email notifications to selected candidates

### ✅ Secure Authentication
- JWT-based authentication
- Role-based access control (Admin, Recruiter, User)

---

## 🛠 Tech Stack

### Backend
- Java 8+
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA

### Database
- MySQL

### AI & Cloud
- Azure Form Recognizer
- Azure OpenAI
- Azure Cloud

### Frontend
- HTML, CSS, JavaScript

---

## 📂 Project Structure
