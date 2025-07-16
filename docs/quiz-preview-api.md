# Quiz Preview API Documentation

## Overview
The Quiz Preview API provides students with status-based information about their quizzes. The API supports both getting all quiz previews for a student and getting preview for a specific quiz.

## Endpoints

### 1. Get All Quiz Previews for Student
```
GET /api/student/{studentId}/quizzes/preview
```

### 2. Get Specific Quiz Preview
```
GET /api/student/{studentId}/quiz/{quizId}/preview
```

## Parameters
- `studentId` (path): The ID of the student
- `quizId` (path): The UUID of the quiz (for specific quiz preview)

## Response DTO: QuizPreviewResponseDTO

### Common Fields (Available for all quiz statuses)
- `id`: Quiz UUID
- `name`: Quiz name
- `description`: Quiz description
- `instructions`: Quiz instructions
- `startTime`: Quiz start time
- `endTime`: Quiz end time
- `duration`: Quiz duration
- `status`: Current quiz status (UPCOMING, ACTIVE, ENDED)
- `isProtected`: Whether quiz requires a password
- `courseCodes`: List of course codes
- `fullScreen`: Quiz configuration flags
- `shuffleQuestions`, `shuffleOptions`, `linearQuiz`, `calculator`, `autoSubmit`, `publishResult`
- `message`: Status-specific message

### Status-Specific Fields

#### UPCOMING Quizzes
- `timeUntilStart`: Duration until quiz starts
- `canStart`: false (quiz hasn't started yet)

#### ACTIVE Quizzes
- `canStart`: Whether student can start the quiz
- `isSubmitted`: Whether student has submitted
- `attemptsUsed`: Number of attempts used
- `maxAttempts`: Maximum allowed attempts
- `remainingTime`: Time remaining to complete quiz

#### ENDED Quizzes
- `isSubmitted`: Whether student submitted
- `attemptsUsed`: Number of attempts used
- `maxAttempts`: Maximum allowed attempts
- `score`: Student's score (if results published)
- `totalMarks`: Total possible marks (if results published)
- `rank`: Student's rank (if results published)

## Implementation Status
✅ **Basic Implementation Complete**
- Service template implemented with status-based logic
- Repository method to find quizzes by student ID
- Controller endpoints for both all quizzes and specific quiz
- DTO with status-specific fields
- Basic status determination logic

🔄 **TODO for Manual Implementation:**
1. **Enhanced Score Calculation** - Implement actual scoring logic
2. **Attempt Tracking** - Add proper attempt counting
3. **Rank Calculation** - Implement ranking among students
4. **Access Control** - Add permission checking
5. **Error Handling** - Enhanced exception handling

## Example Responses

### All Quiz Previews Response
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Data Structures Quiz",
    "status": "UPCOMING",
    "timeUntilStart": "PT2H30M",
    "canStart": false,
    "message": "Quiz will be available at 2024-01-15T10:00:00Z"
  },
  {
    "id": "456e7890-e89b-12d3-a456-426614174001",
    "name": "Algorithms Quiz",
    "status": "ACTIVE",
    "canStart": true,
    "remainingTime": "PT45M",
    "message": "Quiz is active - you can start now"
  },
  {
    "id": "789e0123-e89b-12d3-a456-426614174002",
    "name": "Database Quiz",
    "status": "ENDED",
    "isSubmitted": true,
    "score": 85.5,
    "totalMarks": 100.0,
    "message": "Quiz completed - results available"
  }
]
```

### Single Quiz Preview Response
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Data Structures Quiz",
  "description": "Quiz covering basic data structures",
  "instructions": "Answer all questions within the time limit",
  "startTime": "2024-01-15T10:00:00Z",
  "endTime": "2024-01-15T11:00:00Z",
  "duration": "PT1H",
  "status": "ACTIVE",
  "isProtected": false,
  "courseCodes": ["CS101"],
  "canStart": true,
  "isSubmitted": false,
  "remainingTime": "PT45M",
  "attemptsUsed": 0,
  "maxAttempts": 1,
  "fullScreen": true,
  "shuffleQuestions": false,
  "message": "Quiz is active - you can start now"
}
```
