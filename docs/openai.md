## OpenAI Integration

### Phases
- **Phase 1: Sentences**: Generate 20–30 level-appropriate sentences for topic.
- **Phase 2: Vocabulary**: Extract a comprehensive vocab set from sentences and domain knowledge.
- **Phase 3: Conversation**: Create a two-person dialogue using topic and level.
- **Phase 4: Exercises**: Produce cloze prompts and distractors; flag audio-friendly items.

### Models and outputs
- Use GPT models supporting structured JSON output. Request `json_object` style responses.
- Constrain schemas to keep token costs low; reference sentence IDs where possible.

### Prompting strategy
- Provide system prompt with role and constraints (level, language policy, JSON-only).
- Provide examples for each phase.
- Use deterministic temperature for vocab extraction, slightly higher for conversation.

### Example JSON schema (sentences)
```json
{
  "topic": "string",
  "targetLanguage": "fr",
  "level": "A2",
  "sentences": [
    { "id": "s1", "text": "string", "translation": "string", "difficulty": 1 }
  ]
}
```

### Safety and validation
- Validate JSON against schemas; reject on missing fields.
- Enforce maximum tokens per phase; fallback to multi-call batching if needed.
- Moderation checks on outputs if required.

### Cost controls
- Batch sizes: 20–30 sentences, vocab capped (e.g., <= 120 entries).
- Cache by `(topic, level, targetLanguage)` for anonymous demos.
- Store token counts and cost estimates in `openai_generations`.

### Retries and idempotency
- Idempotency keys: `bubbleId:phase`.
- Exponential backoff with jitter.
- Persist partial results only when JSON validates.


