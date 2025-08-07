## Voice and Audio

### TTS
- Use a TTS provider (OpenAI TTS or compatible) for target language.
- Cache audio per `(text, voice, language)`; store in `audio_assets` with checksum.
- Expose signed URLs from object storage.

### Playback
- Frontend uses Web Audio API; prefetch next turn; adjustable speed.
- Provide captions/subtitles synced to text.

### Generation timing
- On-demand per sentence/turn; pre-generate popular items in background.


