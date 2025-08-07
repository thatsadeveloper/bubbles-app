## Overview

### What is Bubbles?
**Bubbles** is a language-learning web app. A user requests a topic (a "bubble"), selects their target language and proficiency (e.g., A1–C2), and the app generates:
- **20–30 sentences** tailored to the topic and level
- **Complete topic vocabulary** with part-of-speech, translations, and examples
- **Practice modes**: vocabulary flashcards (spaced repetition), sentence learning, cloze completion, listening, and a stepwise dialogue with voiceover

### Goals
- **Personalized**: Level-appropriate content and difficulty.
- **Practical**: Topic-centric content for real situations.
- **Persistent**: Save bubbles, progress, and attempts for later review.
- **Accessible**: Audio support, keyboard navigation, multi-device.

### Terminology
- **Bubble**: A generated topic package (sentences, vocab, exercises, conversation).
- **Turn**: One message in a two-person dialogue.
- **Attempt**: A learner’s answer to an exercise item.

### High-level flow
1. User defines topic, target language, and level.
2. Backend creates a bubble record and triggers generation via OpenAI.
3. Generation produces sentences, vocabulary, conversation, and exercises.
4. Assets (e.g., audio) are generated on-demand and cached.
5. User practices across modes and tracks progress over time.


