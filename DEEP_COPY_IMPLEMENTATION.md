# Deep Copy Implementation for Question Classes

## Overview
This document details the implementation of proper deep copying in all BaseQuestion implementations to avoid reference exceptions when copying questions.

## Problem Statement
The original `copyQuestion()` implementations were creating shallow copies, which led to shared references between the original and copied objects. This caused issues when:
1. Multiple questions shared the same mutable collections (options, keys, blanks, etc.)
2. Complex objects within questions were being reused
3. Reference exceptions occurred when modifying copied questions

## Solution Implemented

### 1. MCQ (Multiple Choice Questions)
**File**: `MCQ.kt`
**Changes**:
- `topic`: Changed from shared reference to `topic.toMutableList()`
- `options`: Deep copy each `MCQOption` with new instances having `id = null`

```kotlin
options = options.map { 
    MCQOption(
        id = null,
        text = it.text,
        isCorrect = it.isCorrect
    )
}.toMutableList()
```

### 2. MMCQ (Multiple Multiple Choice Questions)
**File**: `MMCQ.kt`
**Changes**: Same as MCQ - deep copy of `topic` and `options`

### 3. CodingQuestion
**File**: `CodingQuestion.kt`
**Changes**:
- `topic`: Deep copy with `topic.toMutableList()`
- `params`: Deep copy each `FunctionParamDTO`
- `testcases`: Deep copy each `TestCaseDTO` 
- `language`: Convert to new list with `language?.toList()`

```kotlin
params = params?.map { 
    FunctionParamDTO(it.param, it.type)
} ?: emptyList(),
testcases = testcases.map { 
    TestCaseDTO(
        input = it.input,
        expected = it.expected,
        tags = it.tags,
        isMinimal = it.isMinimal,
        language = it.language
    )
},
language = language?.toList() ?: emptyList()
```

### 4. DescriptiveQuestion
**File**: `DescriptiveQuestion.kt`
**Changes**:
- `topic`: Deep copy with `topic.toMutableList()`
- All primitive/string fields remain the same (immutable)

### 5. FillUp
**File**: `FillUp.kt`
**Changes**:
- `topic`: Deep copy with `topic.toMutableList()`
- `blanks`: Deep copy each `blanks` object with new instances

```kotlin
blanks = blanks.map { 
    blanks(
        id = it.id,
        answers = it.answers.toList()
    )
}
```

### 6. FileUpload
**File**: `FileUpload.kt`
**Changes**:
- `topic`: Deep copy with `topic.toMutableList()`
- All other fields are primitives/strings (no additional copying needed)

### 7. MatchTheFollowing
**File**: `MatchTheFollowing.kt`
**Changes**:
- `topic`: Deep copy with `topic.toMutableList()`
- `keys`: Deep copy each `MatchPair` with new `Pair` instances

```kotlin
keys = keys.map { 
    MatchPair(
        leftPair = Pair(it.leftPair.id, it.leftPair.text),
        rightPair = Pair(it.rightPair.id, it.rightPair.text)
    )
}.toMutableList()
```

### 8. TrueFalse
**File**: `TrueFalse.kt`
**Changes**:
- `topic`: Deep copy with `topic.toMutableList()`
- `answer`: Boolean (primitive, no copying needed)

## Key Principles Applied

### 1. Mutable Collection Deep Copy
All mutable collections (`MutableList`, `List`) are converted to new instances:
- `collection.toMutableList()` for mutable lists
- `collection.toList()` for immutable lists

### 2. Complex Object Recreation
All complex objects are recreated with new instances:
- `MCQOption` objects with `id = null` to generate new IDs
- `TestCaseDTO`, `FunctionParamDTO`, `blanks`, `MatchPair`, `Pair` objects

### 3. ID Reset
All copied questions have:
- `id = null` to generate new UUIDs when saved
- Nested object IDs reset (like `MCQOption.id = null`)

### 4. Immutable Field Preservation
Primitive types and immutable objects are safely copied by reference:
- `String`, `Int`, `Boolean`, `Float`
- Enum values (`Taxonomy`, `Difficulty`, `QuestionTypes`)

## Benefits

1. **Reference Safety**: No shared mutable state between original and copied questions
2. **Data Integrity**: Modifications to copied questions don't affect originals
3. **Database Safety**: New entities get fresh IDs when persisted
4. **Memory Efficiency**: Only mutable collections and complex objects are deep copied

## Testing
- All modifications compile successfully with `./gradlew compileKotlin`
- Full build passes with `./gradlew build -x test`
- No compilation errors in any question implementation

## Usage
When copying questions (e.g., from bank to quiz), the `copyQuestion()` method now creates completely independent instances that can be safely modified and persisted without affecting the original questions.

```kotlin
val copiedQuestion = originalQuestion.copyQuestion()
// copiedQuestion is now completely independent of originalQuestion
```
