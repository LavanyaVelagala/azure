import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AzureOpenAIFromJson {

    public static void main(String[] args) throws IOException {

        String jsonInput = "{\n" +
                "  \"messages\": [\n" +
                "    { \"role\": \"system\", \"content\": \"You are a helpful assistant.\" },\n" +
                "    { \"role\": \"user\", \"content\": \"Explain cloud computing.\" },\n" +
                "    { \"role\": \"assistant\", \"content\": \"Cloud computing uses remote servers instead of local machines.\" },\n" +
                "    { \"role\": \"user\", \"content\": \"Give real-world examples.\" }\n" +
                "  ]\n" +
                "}";

        // 1️⃣ Parse JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonInput);
        JsonNode messagesNode = root.get("messages");

        List<ChatRequestMessage> messages = new ArrayList<>();
        for (JsonNode msg : messagesNode) {
            String role = msg.get("role").asText();
            String content = msg.get("content").asText();

            switch (role) {
                case "system":
                    messages.add(new com.azure.ai.openai.models.ChatRequestSystemMessage(content));
                    break;
                case "user":
                    messages.add(new com.azure.ai.openai.models.ChatRequestUserMessage(content));
                    break;
                case "assistant":
                    messages.add(new com.azure.ai.openai.models.ChatRequestAssistantMessage(content));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown role: " + role);
            }
        }

        // 2️⃣ Create Azure OpenAI client
        OpenAIClient client = new OpenAIClientBuilder()
                .endpoint("https://YOUR-RESOURCE-NAME.openai.azure.com/")
                .credential(new com.azure.core.credential.AzureKeyCredential("YOUR_API_KEY"))
                .buildClient();

        // 3️⃣ Call chat completion API
        ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
        ChatCompletions completions = client.getChatCompletions("YOUR_DEPLOYMENT_NAME", options);

        // 4️⃣ Print the assistant's reply
        System.out.println(completions.getChoices().get(0).getMessage().getContent());
    }
}
