/*
 * Copyright (c) 2024 Mariusz Bernacki <consulting@didalgo.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package com.didalgo.intellij.chatgpt.chat.models;

import com.didalgo.intellij.chatgpt.settings.GeneralSettings;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.common.OpenAiApiConstants;
import org.springframework.web.client.RestClient;

import java.util.List;

public class OpenAiModelFamily implements ModelFamily {

    @Override
    public OpenAiChatModel createChatModel(GeneralSettings.AssistantOptions config) {
        var baseUrl = config.isEnableCustomApiEndpointUrl()? config.getApiEndpointUrl(): getDefaultApiEndpointUrl();
        var apiKey = config.getApiKey();
        var options = OpenAiChatOptions.builder()
                .model(config.getModelName())
                .temperature(config.getTemperature())
                .streamUsage(config.isEnableStreamOptions())
                .topP(config.getTopP())
                .N(1)
                .reasoningEffort(config.isReasoningEffortEnabled() ? config.getReasoningEffort() : null)
                .build();

        return OpenAiChatModel.builder()
                .defaultOptions(options)
                .openAiApi(OpenAiApi.builder()
                        .baseUrl(baseUrl)
                        .apiKey(apiKey)
                        .restClientBuilder(RestClient.builder().apply(ModelFamily.defaultTimeout()))
                        .build())
                .build();
    }

    @Override
    public String getDefaultApiEndpointUrl() {
        return OpenAiApiConstants.DEFAULT_BASE_URL;
    }

    @Override
    public List<String> getCompatibleApiEndpointUrls() {
        return List.of(
                "https://api.groq.com/openai",
                "https://api.mistral.ai",
                "https://openrouter.ai/api"
        );
    }

    @Override
    public String getApiKeysHomepage() {
        return "https://platform.openai.com/api-keys";
    }
}
