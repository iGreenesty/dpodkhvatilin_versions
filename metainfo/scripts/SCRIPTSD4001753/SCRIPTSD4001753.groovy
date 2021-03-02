/*! UTF8 */
//Автор: eboronina
//Дата создания: 14.11.17
//Код: SCRIPTSD4001753
//Назначение: 
/**
 * Копирование файлов из комментария в объект и заполнение автора файла
 * @param FILE_ATTR_CODE - Код атрибута типа Файл в Комментарии
 */
//Версия: 4.7.0+
//Категория: Действие по событию типа скрипт
//Параметры-----------------------------------------------------
def FILE_ATTR_CODE = 'files'
//Функции-------------------------------------------------------
/**
  * Функция attrFileCopy() принимает на вход:
  * @source - объект, из которого необходимо копировать
  * @attrCode - код атрибута, в котором лежат файлы
  * @target - объект, в который необходимо копировать
  * @author - сотрудник, который будет определен автором файлов
  * Функция возвращает:
  * @result - размер списка прикреплённых файлов
 */
def attrFileCopy(source, attrCode, target, author) {
  def sourceFiles = source[attrCode]
  def files = []
  sourceFiles.each {
    file ->
    byte[] data
    def description = 'Файл из комментария' + ((file.description) ? ". Описание: ${file.description}" : '')
    try {
      data = utils.readFileContent(file)
    } catch(Exception e) {
      logger.error("Копирование файлов из комментария в объект. Ошибка чтения содержимого файла", e)
      throw e
    }
    try {
      def attachedFile = utils.attachFile(target, file.title, file.mimeType, description, data)
     utils.edit(attachedFile, ['author' : author])
     files += attachedFile
    } catch(Exception e) {
      logger.error("Копирование файлов из комментария в объект. Не удалось добавить файл", e)
      throw e
    }
  }
  return files.size()
}
//Основной блок-------------------------------------------------
def comment = sourceObject
def author = comment.author
def source = subject
try {
  attrFileCopy(comment, FILE_ATTR_CODE, source, author)
} catch (Exception e) {
  def msg = "Возникла ошибка при копировании файлов в объект. Обратитесь к администратору."
  utils.throwReadableException(msg, [] as String[], msg, [] as String[])
}