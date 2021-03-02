/*! UTF8 */
//Автор: mdemyanov
//Дата создания: 2019-02-18
//Код: 
//Назначение:
/**
* Получить список идентификаторов файлов, прикрепленных к объекту
*/
//Версия: 4.8.*
//Категория:
//Параметры------------------------------------------------------
import groovy.json.JsonOutput
//Функции--------------------------------------------------------
/**
* Получить список идентификаторов файлов, прикрепленных к объекту
* Пример URL для вызова:
* https://your_server.itsm365.com/sd/services/rest/exec?accessKey=00000000-0000-0000-0000-000000000000&func=modules.fileUtils.getFilesUUID&params=%27serviceCall$2273901%27
* @param objectUUID идентификатор объекта, файлы которого нужно посмотреть
* @return json представление коллекции файлов с информацией по названию, идентификатору, размеру и типу контента
*/
def getFilesUUID(String objectUUID) {
  utils.files(objectUUID).collect {
    file->
    return JsonOutput.toJson([
      UUID: file.UUID,
      title: file.title,
      fileSize: file.fileSize,
      mimeType: file.mimeType
    ])
  }
}
//Основной блок -------------------------------------------------