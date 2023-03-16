package br.com.biexpert.bicm

import br.com.biexpert.bicm.dto.AzureTranslationResponseDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class AzureTranslator {


    private lateinit var chaveAzure: String
    private lateinit var endpointAzure: String
    private lateinit var regiaoAzure: String
    private var gson = Gson()

    init {
        chaveAzure = "**********"
        endpointAzure =
            "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from=pt&to=en"
        regiaoAzure = "eastus"
    }


    fun translate(text: String): AzureTranslationResponseDTO {
        val encodedText = URLEncoder.encode(text, "UTF-8")
        val url = URL(endpointAzure)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", chaveAzure)
        connection.setRequestProperty("Ocp-Apim-Subscription-Region", regiaoAzure)

        connection.doOutput = true
        val requestBody = "[{'Text': '$encodedText'}]"
        connection.outputStream.write(requestBody.toByteArray(charset("UTF-8")))

        val response = connection.inputStream.bufferedReader().readText()

        val azureTranslatorResponseObject = gson.fromJson(
            response, object : TypeToken<List<AzureTranslationResponseDTO>>() {})

        println(response)

        connection.disconnect()
        return azureTranslatorResponseObject.first()
    }


}