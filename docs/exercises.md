## Exercises

### Vocabulary
- **Flashcards (SRS)**: Front: lemma (and audio). Back: translation, PoS, example.
- **Audio recognition**: Play TTS; user selects from options.

### Sentences
- **Reading + audio**: Show translation on demand; play TTS.
- **Dictation**: User types what they hear; accept minor typos via fuzzy matching.
- **Cloze**: Remove 1–3 words per sentence; offer distractors.

### Conversation mode
- Two speakers (A/B). Reveal next turn on click; auto-play TTS.
- Optionally shadowing: record user voice and compare timing (stretch goal).

### Scoring and progress
- Binary correctness for MC/cloze; partial credit where applicable.
- SRS for vocab/sentences uses SM-2-like parameters: easiness, interval, repetitions.
- Attempts store response JSON and time spent for analytics.

### Generation metadata
- Exercises reference `sentence_id` or `vocab_id` where possible to avoid duplication.
- Distractors derived from same part-of-speech and level.


