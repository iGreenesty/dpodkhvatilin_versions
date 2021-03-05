/*! UTF8 */
//Автор: dpodkhvatilin
//Дата создания: 01.02.2021
//Назначение:
/**
 * Модуль для отправки данных в Elasticsearch
 */
//Версия: 4.11.*

//Параметры------------------------------------------------------
package itsm365.elkModule

import java.lang.reflect.Type
import javax.servlet.http.HttpServletRequest

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.FieldNamingPolicy
import com.google.gson.reflect.TypeToken

import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClients
import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT
import static groovyx.net.http.Method.DELETE

import org.apache.commons.codec.binary.Base64

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Constants {
    static final String TOKEN = 'ZWxhc3RpYzpzdFk5QXlGQVk3SHM4MTk0NTdQc1c2Nk0='
    static final String HOST = 'https://elastic.svc.itsm365.com'
    static final int TIMEOUT = 30000

    static final String HOT_KEYS_INDEX = '/hot_keys_analytics/_doc'
    static final String BROWSER_STATS_INDEX = '/browser_analytics/_doc'

    static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
}

class RequestContent {
    String path
    Map data
}

class ElkClient {
    HTTPBuilder http
    Gson gson
    Logger logger

    ElkClient(String host, String token, Integer timeout) {
        http = new HTTPBuilder(host)

        http.client = HttpClients.custom().setDefaultRequestConfig(
                RequestConfig.custom()
                        .setConnectionRequestTimeout(timeout)
                        .setConnectTimeout(timeout)
                        .setSocketTimeout(timeout)
                        .build()
        ).build()

        http.setHeaders(['Authorization': 'Basic ' + token])

        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
    }

    //GET Method
    def <T> T get(String path, Type type) {
        T serverResponse

        http.request(GET, JSON) { req ->
            uri.path = path

            response.success = { resp, reader ->
                serverResponse = gson.fromJson(gson.toJson(reader), type)
            }

            response.failure = { resp, reader ->
                throw new Exception(resp.statusLine as String)
            }
        }

        return serverResponse
    }

    //POST Method
    def <T> T post(String path, Map query, Type type) {
        T serverResponse

        http.request(POST, JSON) { req ->
            uri.path = path
            body = query

            response.success = { resp, reader ->
                serverResponse = gson.fromJson(gson.toJson(reader), type)
            }

            response.failure = { resp, reader ->
                throw new Exception(resp.statusLine as String)
            }
        }

        return serverResponse
    }

    //PUT Method
    def <T> T put(String path, Map query, Type type) {
        T serverResponse

        http.request(PUT, JSON) { req ->
            uri.path = path
            body = query

            response.success = { resp, reader ->
                serverResponse = gson.fromJson(gson.toJson(reader), type)
            }

            response.failure = { resp, reader ->
                throw new Exception(resp.statusLine as String)
            }
        }

        return serverResponse
    }

    //DELETE Method
    def <T> T delete(String path, Type type) {
        T serverResponse

        http.request(DELETE, JSON) { req ->
            uri.path = path

            response.success = { resp, reader ->
                serverResponse = gson.fromJson(gson.toJson(reader), type)
            }

            response.failure = { resp, reader ->
                throw new Exception(resp.statusLine as String)
            }
        }

        return serverResponse
    }
}

ElkClient getClient() {
    return new ElkClient(Constants.HOST, Constants.TOKEN, Constants.TIMEOUT)
}

//Функции--------------------------------------------------------
/**
 * @param request - содержит JSON-объект отправляемый в эластик
 */
def createDocument(HttpServletRequest request) {
    RequestContent requestContent = Constants.GSON.fromJson(
            request.reader.text, RequestContent.class
    )

    return client.<Map> post(requestContent.path, requestContent.data, Map.class)
}

/**
 * @param requestContent - мапа с параметрами отправляемого в эластик JSON-объекта
 * @param path - адрес индекса вида - "/index_name/_doc"
 */
def createDocument(Map requestContent, String path) {
    return client.<Map> post(path, requestContent, Map.class)
}

/**
 * @param requestContent - мапа с параметрами отправляемого в эластик JSON-объекта
 * @param pathInBase64 - адрес индекса закодированный в Base64 - "L2luZGV4X25hbWUvX2RvYw"
 */
def createDocumentBase64(Map requestContent, String pathInBase64) {
    return client.<Map> post(new String(pathInBase64.decodeBase64()), requestContent, Map.class)
}