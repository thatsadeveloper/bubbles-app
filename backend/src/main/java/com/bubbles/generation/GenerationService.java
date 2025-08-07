package com.bubbles.generation;

import com.bubbles.bubble.Bubble;
import com.bubbles.bubble.BubbleRepository;
import com.bubbles.content.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenerationService {

    private final ChatClient chatClient;
    private final BubbleRepository bubbleRepository;
    private final SentenceRepository sentenceRepository;
    private final VocabularyRepository vocabularyRepository;
    private final ConversationTurnRepository conversationTurnRepository;
    private final ExerciseRepository exerciseRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public GenerationService(ChatClient.Builder chatClientBuilder,
                             BubbleRepository bubbleRepository,
                             SentenceRepository sentenceRepository,
                             VocabularyRepository vocabularyRepository,
                             ConversationTurnRepository conversationTurnRepository,
                             ExerciseRepository exerciseRepository) {
        this.chatClient = chatClientBuilder.build();
        this.bubbleRepository = bubbleRepository;
        this.sentenceRepository = sentenceRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.conversationTurnRepository = conversationTurnRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional
    public void generateForBubble(Bubble bubble) {
        bubble.setStatus("RUNNING");
        bubbleRepository.save(bubble);

        try {
            // Clear previous content on retry
            sentenceRepository.findByBubbleIdOrderByOrderIndexAsc(bubble.getId()).forEach(sentenceRepository::delete);
            vocabularyRepository.findByBubbleId(bubble.getId()).forEach(vocabularyRepository::delete);
            conversationTurnRepository.findByBubbleIdOrderByOrderIndexAsc(bubble.getId()).forEach(conversationTurnRepository::delete);
            exerciseRepository.findByBubbleId(bubble.getId()).forEach(exerciseRepository::delete);

            // Phase 1: sentences as JSON
            String sPrompt = "Return JSON with key 'sentences' only: an array of 20 short sentences in " + bubble.getTargetLanguage() +
                ", about '" + bubble.getTopic() + "', level " + bubble.getLevel() + ". Each item: {text, translation}.";
            String sJson = chatClient.prompt(sPrompt).call().content();
            JsonNode sRoot = objectMapper.readTree(sJson);
            JsonNode sArr = sRoot.path("sentences");
            int order = 1;
            if (sArr.isArray()) {
                for (JsonNode node : sArr) {
                    Sentence s = new Sentence();
                    s.setBubbleId(bubble.getId());
                    s.setOrderIndex(order++);
                    s.setText(node.path("text").asText(""));
                    s.setTranslation(node.path("translation").asText(null));
                    s.setDifficulty(1);
                    sentenceRepository.save(s);
                }
            }

            // Phase 2: vocabulary from sentences
            String vPrompt = "Extract a comprehensive vocabulary list as JSON key 'vocabulary' only: array of {lemma, translation, pos}. " +
                "Consider the following sentences: " + collectSentencesForPrompt(bubble.getId());
            String vJson = chatClient.prompt(vPrompt).call().content();
            JsonNode vRoot = objectMapper.readTree(vJson);
            JsonNode vArr = vRoot.path("vocabulary");
            if (vArr.isArray()) {
                for (JsonNode node : vArr) {
                    Vocabulary v = new Vocabulary();
                    v.setBubbleId(bubble.getId());
                    v.setLemma(node.path("lemma").asText(""));
                    v.setTranslation(node.path("translation").asText(null));
                    v.setPos(node.path("pos").asText(null));
                    vocabularyRepository.save(v);
                }
            }

            // Phase 3: conversation with two speakers
            String cPrompt = "Create a two-person conversation as JSON key 'conversation' only: array of 12 turns {speaker:'A'|'B', text, translation}. " +
                "Topic: '" + bubble.getTopic() + "', level " + bubble.getLevel() + ", language " + bubble.getTargetLanguage() + ".";
            String cJson = chatClient.prompt(cPrompt).call().content();
            JsonNode cRoot = objectMapper.readTree(cJson);
            JsonNode cArr = cRoot.path("conversation");
            int cOrder = 1;
            if (cArr.isArray()) {
                for (JsonNode node : cArr) {
                    ConversationTurn t = new ConversationTurn();
                    t.setBubbleId(bubble.getId());
                    t.setOrderIndex(cOrder++);
                    t.setSpeaker(node.path("speaker").asText("A"));
                    t.setText(node.path("text").asText(""));
                    t.setTranslation(node.path("translation").asText(null));
                    conversationTurnRepository.save(t);
                }
            }

            // Phase 4: simple cloze exercises derived from sentences
            createClozeExercisesFromSentences(bubble.getId());
            // Phase 4b: basic dictation exercises
            createDictationExercisesFromSentences(bubble.getId());

            bubble.setStatus("COMPLETED");
            bubbleRepository.save(bubble);
        } catch (Exception ex) {
            bubble.setStatus("FAILED");
            bubbleRepository.save(bubble);
            // log and swallow to keep worker simple; controller can inspect FAILED state
        }
    }

    private static List<String> splitLines(String text) {
        String[] parts = text.split("\r?\n");
        List<String> result = new ArrayList<>();
        for (String p : parts) {
            if (!p.isBlank()) result.add(p.trim());
        }
        return result;
    }

    private String collectSentencesForPrompt(Long bubbleId) {
        StringBuilder sb = new StringBuilder();
        for (Sentence s : sentenceRepository.findByBubbleIdOrderByOrderIndexAsc(bubbleId)) {
            sb.append("[").append(s.getText()).append("] ");
        }
        return sb.toString();
    }

    private void createClozeExercisesFromSentences(Long bubbleId) throws Exception {
        List<Sentence> sentences = sentenceRepository.findByBubbleIdOrderByOrderIndexAsc(bubbleId);
        int created = 0;
        for (Sentence s : sentences) {
            if (created >= 8) break;
            String[] words = s.getText().split(" ");
            if (words.length < 3) continue;
            int idx = Math.min( Math.max(1, words.length / 3), words.length - 2 );
            String answer = words[idx].replaceAll("[.,!?]$", "");
            words[idx] = "_____";
            String cloze = String.join(" ", words);

            com.bubbles.content.Exercise ex = new com.bubbles.content.Exercise();
            ex.setBubbleId(bubbleId);
            ex.setType("CLOZE");
            JsonNode prompt = objectMapper.createObjectNode()
                .put("sentence", cloze)
                .put("translation", s.getTranslation() == null ? "" : s.getTranslation());
            JsonNode solution = objectMapper.createObjectNode().put("answer", answer);
            ex.setPrompt(objectMapper.writeValueAsString(prompt));
            ex.setSolution(objectMapper.writeValueAsString(solution));
            exerciseRepository.save(ex);
            created++;
        }
    }

    private void createDictationExercisesFromSentences(Long bubbleId) throws Exception {
        List<Sentence> sentences = sentenceRepository.findByBubbleIdOrderByOrderIndexAsc(bubbleId);
        int created = 0;
        for (Sentence s : sentences) {
            if (created >= 5) break;
            com.bubbles.content.Exercise ex = new com.bubbles.content.Exercise();
            ex.setBubbleId(bubbleId);
            ex.setType("DICTATION");
            JsonNode prompt = objectMapper.createObjectNode()
                .put("instruction", "Type what you hear")
                .put("translation", s.getTranslation() == null ? "" : s.getTranslation());
            JsonNode solution = objectMapper.createObjectNode().put("answer", s.getText());
            ex.setPrompt(objectMapper.writeValueAsString(prompt));
            ex.setSolution(objectMapper.writeValueAsString(solution));
            exerciseRepository.save(ex);
            created++;
        }
    }
}


