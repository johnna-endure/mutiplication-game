package microservices.book.gamification.client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import microservices.book.gamification.client.dto.MultiplicationResultAttempt;

import java.io.IOException;

/**
 * Multiplication 마이크로서비스로부터 오는 답안을
 * Gamification에서 사용하는 형태로 역직렬화
 */
public class MultiplicationResultAttemptDeserializer extends JsonDeserializer<MultiplicationResultAttempt> {

    @Override
    public MultiplicationResultAttempt deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);
        return new MultiplicationResultAttempt(node.get("user").get("alias").asText(),
            node.get("multiplication").get("factorA").asInt(),
            node.get("multiplication").get("factorB").asInt(),
            node.get("resultAttempt").asInt(),
            node.get("correct").asBoolean()
        );
    }
}
