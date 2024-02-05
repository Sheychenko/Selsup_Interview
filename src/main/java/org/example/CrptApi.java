package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private final HttpClient httpClient;
    private final Semaphore semaphore;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.httpClient = HttpClient.newHttpClient();
        //Обработка
        this.semaphore = new Semaphore(requestLimit);
    }

    public void createDocument(String apiUrl, String jsonStructure, String signature) {
        try {
            semaphore.acquire();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonStructure))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Обработка успешного ответа
                System.out.println("Document created successfully");
            } else {
                // Обработка ошибок
                System.out.println("Error creating document. Status code: " + response.statusCode());
                System.out.println("Response body: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    public static void main(String[] args) {
        // Пример использования
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 3);
        String url_API = "https://ismp.crpt.ru/api/v3/lk/documents/create";
        String documentJson = "{\"description\": {\"participantInn\": \"string\"}, " +
                "\"doc_id\": \"string\", \"doc_status\": \"string\", \"doc_type\": \"LP_INTRODUCE_GOODS\", " +
                "\"importRequest\": true, \"owner_inn\": \"string\", \"participant_inn\": \"string\", " +
                "\"producer_inn\": \"string\", \"production_date\": \"2020-01-23\", \"production_type\": \"string\", " +
                "\"products\": [{\"certificate_document\": \"string\", \"certificate_document_date\": \"2020-01-23\", " +
                "\"certificate_document_number\": \"string\", \"owner_inn\": \"string\", \"producer_inn\": \"string\", " +
                "\"production_date\": \"2020-01-23\", \"tnved_code\": \"string\", \"uit_code\": \"string\", " +
                "\"uitu_code\": \"string\" }], \"reg_date\": \"2020-01-23\", \"reg_number\": \"string\"}";
        String signature = "exampleSignature";

        crptApi.createDocument(url_API, documentJson, signature);
    }
}