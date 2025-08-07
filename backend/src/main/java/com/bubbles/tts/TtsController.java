package com.bubbles.tts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

record TtsRequest(String text, String voice, String model, String format, Long bubbleId, String itemType, Long itemId) {}

@RestController
@RequestMapping("/api/tts")
public class TtsController {

    private final WebClient webClient;
    private final String apiKey;
    private final String defaultModel;
    private final String defaultVoice;
    private final AudioAssetRepository audioAssetRepository;
    private final Path audioDir;

    public TtsController(@Value("${OPENAI_API_KEY:}") String apiKey,
                         @Value("${OPENAI_TTS_MODEL:tts-1}") String defaultModel,
                         @Value("${OPENAI_TTS_VOICE:alloy}") String defaultVoice,
                         AudioAssetRepository audioAssetRepository,
                         @Value("${AUDIO_STORAGE_DIR:#{systemProperties['java.io.tmpdir'] + '/bubbles-audio'}}") String audioStorageDir) throws Exception {
        this.apiKey = apiKey;
        this.defaultModel = defaultModel;
        this.defaultVoice = defaultVoice;
        this.audioAssetRepository = audioAssetRepository;
        this.webClient = WebClient.builder()
            .baseUrl("https://api.openai.com")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .build();
        this.audioDir = Path.of(audioStorageDir);
        Files.createDirectories(this.audioDir);
    }

    @PostMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> synthesize(@RequestBody TtsRequest request) {
        String model = request.model() == null || request.model().isBlank() ? defaultModel : request.model();
        String voice = request.voice() == null || request.voice().isBlank() ? defaultVoice : request.voice();
        String format = request.format() == null || request.format().isBlank() ? "mp3" : request.format();

        byte[] audio = webClient.post()
            .uri("/v1/audio/speech")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .body(BodyInserters.fromValue(Map.of(
                "model", model,
                "voice", voice,
                "input", request.text(),
                "format", format
            )))
            .retrieve()
            .bodyToMono(byte[].class)
            .block();

        if (audio == null) {
            audio = new byte[0];
        }
        String checksum;
        try {
            checksum = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(audio));
        } catch (Exception e) {
            checksum = String.valueOf(audio.length) + System.currentTimeMillis();
        }
        boolean isMp3 = "mp3".equalsIgnoreCase(format);
        String ext = isMp3 ? "mp3" : "wav";
        Path file = audioDir.resolve(checksum + "." + ext);
        try {
            if (!Files.exists(file)) {
                Files.write(file, audio, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (Exception ignore) {}
        AudioAsset asset = new AudioAsset();
        asset.setProvider("openai");
        asset.setVoice(voice);
        asset.setUrl("/api/tts/assets/" + checksum);
        asset.setChecksum(checksum);
        if (request.bubbleId() != null) asset.setBubbleId(request.bubbleId());
        if (request.itemType() != null) asset.setItemType(request.itemType());
        if (request.itemId() != null) asset.setItemId(request.itemId());
        audioAssetRepository.save(asset);

        ByteArrayResource resource = new ByteArrayResource(audio);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=tts." + ext)
            .contentType(isMp3 ? MediaType.valueOf("audio/mpeg") : MediaType.valueOf("audio/wav"))
            .contentLength(audio.length)
            .body(resource);
    }

    @GetMapping(value = "/assets/{checksum}")
    public ResponseEntity<ByteArrayResource> getAsset(@PathVariable String checksum) throws Exception {
        var assetOpt = audioAssetRepository.findAll().stream().filter(a -> checksum.equals(a.getChecksum())).findFirst();
        if (assetOpt.isEmpty()) return ResponseEntity.notFound().build();
        boolean isMp3 = true;
        Path mp3 = audioDir.resolve(checksum + ".mp3");
        Path wav = audioDir.resolve(checksum + ".wav");
        Path file = Files.exists(mp3) ? mp3 : wav;
        isMp3 = Files.exists(mp3);
        byte[] bytes = Files.readAllBytes(file);
        return ResponseEntity.ok()
            .contentType(isMp3 ? MediaType.valueOf("audio/mpeg") : MediaType.valueOf("audio/wav"))
            .contentLength(bytes.length)
            .body(new ByteArrayResource(bytes));
    }
}


