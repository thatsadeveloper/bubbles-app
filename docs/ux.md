## UX and Flows

### Primary flows
```mermaid
sequenceDiagram
  participant U as User
  participant FE as Frontend
  participant BE as Backend
  U->>FE: Create Bubble (topic, language, level)
  FE->>BE: POST /api/bubbles
  BE-->>FE: 202 Accepted {bubbleId}
  FE-->>BE: SSE /api/bubbles/:id/events
  BE-->>FE: progress: SENTENCES->VOCAB->DIALOGUE->EXERCISES->DONE
  FE->>BE: GET bubble content
  FE-->>U: Practice modules available
```

### Screens
- **Home/Create Bubble**: Topic input, language and level selectors.
- **Bubble Overview**: Tabs for Sentences, Vocabulary, Conversation, Practice.
- **Practice**: Flashcards, Dictation, Cloze; due queue and session summary.
- **Conversation**: Stepwise turns with play/next buttons and transcript.
- **Profile**: Level, settings (voice, speed), history of bubbles.

### Accessibility
- Keyboard-first navigation and ARIA roles.
- Adjustable TTS speed, captions, reduced motion.


