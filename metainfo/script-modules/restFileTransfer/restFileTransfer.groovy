/*! UTF8 */
//Автор: mdemyanov
//Дата создания: 31.05.2020
//Дата изменения: 01.12.2020 - dpodkhvatilin
//Код: restFileTransfer
//Назначение:
/**
 * Модуль для отправки и загрузки файлов ссылками
 */
//Версия: 4.11.*
//Категория:
//Параметры------------------------------------------------------
package ru.itsm365.restFileTransfer

import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.springframework.web.multipart.*

import java.nio.charset.Charset
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.mime.content.ByteArrayBody
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.impl.client.BasicResponseHandler

import com.google.gson.Gson

import groovyx.net.http.HTTPBuilder
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClients

import javax.servlet.http.HttpServletRequest

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST

class Constants {
    static final Integer DEFAULT_TIMEOUT_IN_MILLISECONDS = 60000
    static final Gson GSON = new Gson()
}

class Client {
    HTTPBuilder http
    HashMap<String, Object> defaultQuery = [:]
    Gson gson

    Client(String address, String accessKey, Integer timeout) {
        http = new HTTPBuilder(address)
        defaultQuery.accessKey = accessKey
        http.client = HttpClients.custom().setDefaultRequestConfig(
                RequestConfig.custom()
                        .setConnectionRequestTimeout(timeout)
                        .setConnectTimeout(timeout)
                        .setSocketTimeout(timeout)
                        .build()
        ).build()
        gson = new Gson()
    }

    def post(String path, def queryData, def data) {
        def serverResponse
        queryData.putAll(defaultQuery)
        http.request(POST, JSON) { req ->
            uri.path = path
            uri.query = queryData
            headers.Accept = JSON
            body = gson.toJson(data)
            response.success = { resp, reader ->
                serverResponse = reader
            }
            response.failure = { resp ->
                throw new Exception("Ошибка переноса файлов ${uri.toString()}: ${resp.status}, ${resp.entity.content.text}")
            }
        }
        return serverResponse
    }

    def get(def path, Map queryData) {
        def serverResponse
        queryData.putAll(defaultQuery)
        http.request(GET) { req ->
            uri.path = path
            uri.query = queryData
            response.success = { resp, reader ->
                serverResponse = reader
            }
            response.failure = { resp ->
                throw new Exception("Ошибка переноса файлов ${uri.toString()}: ${resp.status}, ${resp.entity.content.text}")
            }
        }
        return serverResponse
    }
}

class Transfer {
    String objId, attrCode, fileName, mimeType, description, fileUrl

    List toAttach() {
        List data = []
        if (attrCode) {
            data.add(attrCode)
        }
        data.addAll([
                fileName,
                mimeType,
                description,
                new URL(fileUrl).bytes
        ])
        return data
    }
}
//Функции--------------------------------------------------------
String attachByUrl(def requestContent) {
    Transfer transfer = Constants.GSON.fromJson(Constants.GSON.toJson(requestContent), Transfer.class)
    Map<String, Object> resp = [:]
    try {
        api.tx.call {
            resp.result = utils.attachFile(
                    utils.get(transfer.objId),
                    *transfer.toAttach()
            ).UUID
        }
    } catch (Exception e) {
        resp.error = e.message
    }
    return new Gson().toJson(resp)
}

def sendByUrl(def file, String address, String accessKey, String remoteObjId, String attrCode = null) {
    Client client = new Client(address, accessKey, Constants.DEFAULT_TIMEOUT_IN_MILLISECONDS)
    String shareAlias = file.shareAlias
    if (shareAlias == null) {
        shareAlias = 'get' + UUID.randomUUID().toString().replaceAll('-', '')
        utils.edit(file, [shareAlias: shareAlias], true)
    }
    return client.post(
            '/sd/services/rest/exec-post',
            [
                    func  : 'modules.restFileTransfer.attachByUrl',
                    params: 'requestContent'
            ],
            new Transfer(
                    objId: remoteObjId,
                    fileName: file.title,
                    mimeType: file.mimeType,
                    description: file.description,
                    fileUrl: "${api.rest.getBaseUrl()}/share/${file.shareAlias}"
            )
    )
}

List sendByRequest(def file, String address, String accessKey, String remoteObjId, String author = '', String attrCode = '', boolean addDescr = true) {
    HttpPost post = new HttpPost(new URIBuilder(address)
            .setPath('/sd/services/rest/exec-post')
            .setParameter('accessKey', accessKey)
            .setParameter('func', 'modules.restFileTransfer.attachByRequest')
            .setParameter('params', "request")
            .setParameter('raw', 'true')
            .build())

    def entity = new MultipartEntityBuilder()
    entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
    entity.boundary = null
    entity.setCharset(Charset.forName("UTF-8"))

    entity.addPart(file.description ?: '', new ByteArrayBody(utils.readFileContent(file), file.title))
    entity.addTextBody('objectId', remoteObjId)
    if (author) entity.addTextBody('author', author)
    if (attrCode) entity.addTextBody('attrCode', attrCode)
    if (addDescr) entity.addTextBody('addDescr', 'true')

    post.setEntity(entity.build())

    def httpClient = HttpClients.createDefault()
    def response = httpClient.execute(post)
    String responseString = new BasicResponseHandler().handleResponse(response)

    return new Gson().fromJson(responseString, List.class)
}

String attachByRequest(HttpServletRequest request) {
    def fileUUID
    def object = utils.get(request.getParameter('objectId'))
    def isAttrCode = request.getParameter('attrCode')
    boolean addDescr = request.getParameter('addDescr') == 'true'

    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request

    return new Gson().toJson(multipartRequest.getFileMap().collect {
        String name, MultipartFile file ->

            def params = [
                    object,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    addDescr ? name : null,
                    file.getBytes()
            ]

            if (isAttrCode) params.add(1, isAttrCode)

            fileUUID = utils.attachFile(params).UUID
            utils.edit(fileUUID, [author: request.getParameter('author')])

            return fileUUID
    })
}
//Основной блок -------------------------------------------------