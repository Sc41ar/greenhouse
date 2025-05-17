package com.elecom.greenhouse.service;

import com.elecom.greenhouse.entities.CultureData;
import com.elecom.greenhouse.model.dto.ChatCompletionResponse;
import com.elecom.greenhouse.model.dto.Message;
import com.elecom.greenhouse.model.dto.ModelRequest;
import com.elecom.greenhouse.model.dto.ModelResponse;
import com.elecom.greenhouse.repositories.CultureDataRepository;
import com.elecom.greenhouse.util.ResponseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@AllArgsConstructor
public class PromptService {

    private final String SYSTEM_PROMPT = """
             The user will provide question about gardening specific cultures in vertical greenhouse for commercial purposes and maximum output.\s
             Write Article in Russian about the plant and provide information about the following:
             Such as recommended humidity percentage, temperature in degrees Celsius, watering frequency per day and watering schedule in 24 hours format, soil type, light exposure in seconds,\s
             light exposure pause in seconds, and fertilization schedule.\s\s
             And output them in JSON format.\s
                        \s
             EXAMPLE INPUT:\s
             How to grow basil?\s
                        \s
             EXAMPLE JSON OUTPUT:
             {
                 "plantName": "Basil",
                 "article":"Growing basil in a small home farm requires well-draining soil (pH 6.0–7.5), 6–8 hours of sunlight daily (or grow lights), consistent watering without waterlogging, regular pruning to encourage bushy growth, and warm temperatures above 70°F (21°C) to mimic its native tropical conditions.",
                 "humidityShare": 0.65,
                 "temperature": 21,
                 "wateringFrequency": 1,
                 "lightExposure": 60000,
                 "lightExposurePause": 10000,
             }
            \s""";
    private final CultureDataRepository cultureDataRepository;
    public RestTemplate restTemplate = new RestTemplate();
    @Value("${api.url}")
    private String apiUrl;
    @Value("${deepseek.api.key}")
    private String apiKey;

    @org.springframework.beans.factory.annotation.Autowired
    public PromptService(CultureDataRepository cultureDataRepository) {
        this.cultureDataRepository = cultureDataRepository;
    }

    public ModelResponse sendPrompt(String userPrompt) {
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        ModelRequest request = new ModelRequest();
        request.setModel("deepseek-chat");
        request.setMessages(List.of(
                new Message("system", SYSTEM_PROMPT),
                new Message("user", userPrompt)
        ));
        request.setStream(false);

        HttpEntity<ModelRequest> requestEntity = new HttpEntity<>(request, headers);

        String response = restTemplate.postForObject(apiUrl + "/chat/completions", requestEntity,
                String.class);

        try {
            ModelResponse modelResponse = processResponse(response);
            saveCultureData(modelResponse);
            return modelResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ModelResponse processResponse(String prompt) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        var a = mapper.readValue(prompt, ChatCompletionResponse.class);
        ModelResponse modelResponse = ResponseMapper.mapToModelResponse(a);

        return modelResponse;
    }

    public void saveCultureData(ModelResponse modelResponse) {
        CultureData data = new CultureData();
        data.setHumidityShare(modelResponse.getHumidityShare());
        data.setTemperature(modelResponse.getTemperature());
        data.setLightExposurePauseSeconds(modelResponse.getLightExposurePause());
        data.setLightExposureSeconds(modelResponse.getLightExposure());
        data.setPlantName(modelResponse.getPlantName());
        data.setFertilizationSchedule(modelResponse.getFertilizationSchedule());
        data.setSoilType(modelResponse.getSoilType());

        cultureDataRepository.save(data);
    }


}
