package kz.iitu.medicineplatform.search;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmbeddingServiceTest {

    @Test
    void requestsQueryAndPassageVectorsFromTheConfiguredModel() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        StringBuilder requests = new StringBuilder();
        server.createContext("/embed", exchange -> {
            requests.append(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)).append('\n');
            float[] vector = new float[EmbeddingService.DIMENSIONS];
            vector[0] = 2;
            byte[] body = mapper.writeValueAsBytes(new float[][]{vector});
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();

        try {
            EmbeddingService service = new EmbeddingService(
                    "http://127.0.0.1:" + server.getAddress().getPort() + "/embed",
                    "intfloat/multilingual-e5-small",
                    mapper);

            float[] query = service.embedQuery("cough");
            float[] document = service.embedDocument("expectorant");

            assertEquals(EmbeddingService.DIMENSIONS, query.length);
            assertEquals(EmbeddingService.DIMENSIONS, document.length);
            assertEquals(1f, query[0]);
            assertEquals(1f, document[0]);
            assertTrue(requests.toString().contains("query: cough"));
            assertTrue(requests.toString().contains("passage: expectorant"));
            assertEquals("intfloat/multilingual-e5-small", service.modelId());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void rejectsVectorsWithAnUnexpectedDimension() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/embed", exchange -> {
            byte[] body = "[[0.1,0.2]]".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();

        try {
            EmbeddingService service = new EmbeddingService(
                    "http://127.0.0.1:" + server.getAddress().getPort() + "/embed",
                    "intfloat/multilingual-e5-small",
                    mapper);
            assertThrows(IllegalStateException.class, () -> service.embedQuery("cough"));
        } finally {
            server.stop(0);
        }
    }
}
