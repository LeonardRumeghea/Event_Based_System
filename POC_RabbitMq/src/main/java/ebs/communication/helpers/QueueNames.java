package ebs.communication.helpers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static ebs.communication.entities.Constants.*;

public class QueueNames {

    private ArrayList<String> queues;

    public QueueNames() {
        queues = new ArrayList<>();
    }

    public void fetchQueues()  {
        HttpClient client = HttpClient.newHttpClient();
        String auth = USERNAME + ":" + PASSWORD;
        String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RABBITMQ_SERVER + "/api/queues"))
                .header("Authorization", "Basic " + encodedAuth)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode queuesNode = new ObjectMapper().readTree(response.body());
                queues = (ArrayList<String>) queuesNode.findValuesAsText("name");

            } else {
                throw new RuntimeException("Failed to fetch queues: " + response.statusCode() + " " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getBrokers() {
        return queues.stream()
                .filter(name -> name.startsWith("broker"))
                .collect(Collectors.toList());
    }

    public List<String> getSubs() {
        return queues.stream()
                .filter(name -> name.startsWith("sub"))
                .collect(Collectors.toList());
    }
}
