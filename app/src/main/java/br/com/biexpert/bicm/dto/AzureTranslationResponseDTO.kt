package br.com.biexpert.bicm.dto

data class AzureTranslationResponseDTO (
    val translations: List<Translation>
)

data class Translation (
    val text: String,
    val to: String
)
