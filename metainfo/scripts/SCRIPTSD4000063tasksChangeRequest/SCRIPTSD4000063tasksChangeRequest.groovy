/*! UTF8 */
//Автор: skucher
//Дата создания: 12.09.2012
//Код: SCRIPTSD4000063
//Назначение:
/**
 * Проверка, что связанные объекты находятся в определенных статусах
 * @param LINKED_OBJECTS - код атрибута связи. Если нужны вложенные
 * объекты, может быть пустая строка
 * @param CHILD_METACLASS - код класса вложенных объектов. Если
 * нужны связанные объекты, может быть пустая строка
 * значение в прошлом
 * @param CASES - cписок типов объектов. Если пусто, то будут
 * проверены объекты всех типов.
 * @param STATES - cписок статусов через запятую, пробелы допустимы.
 * @param STATUS_CONDITION - условие статуса. true - для успеха объекты
 * должны находится в одном из статусов, false - не должны находиться.
 * @param COUNT_CONDITION - условие количеств. true - для всех объектов,
 * false - хотя бы для одного
 * @param ERROR_MSG - сообщение для пользователя, если условие не
 * выполнилось
 * Сценарий получает список объектов - связанных или вложенных. И
 * проверяет их статусы в зависимости от условий
 */
//Версия: 4.0+
//Категория: условие входа/выхода из статуса
//Параметры-----------------------------------------------------
def LINKED_OBJECTS = 'tasks'
def CHILD_METACLASS = ''
def CASES = []
def STATES = ['closed', 'resolved']
def STATUS_CONDITION = true
def COUNT_CONDITION = true
def ERROR_MSG = 'В рамках текущего запроса на изменение есть незакрытые задачи'
  
//Функции-------------------------------------------------------
def getResult(def isSuccess, def faultMessage = "Причина не определена") {
  return (isSuccess) ? '' : faultMessage
}

def checkStatus = {
  objects ->
  objects = (objects instanceof Collection) ? objects : [objects]
  if(CASES) {
    objects = objects.findAll {
      CASES.contains(it.getMetainfo().getCase())
    }
  }
  
  if(objects.isEmpty()) {
    // Если объектов нет
    return true
  }
  
  def check = {
    object ->
    STATES.contains(object.state) == STATUS_CONDITION
  }
  return (COUNT_CONDITION) ? objects.every(check) : objects.find(check)
}

//Основной блок-------------------------------------------------
// Проверки заполненности параметров
if(!STATES) {
  // Проверка, что STATES заполнен
  return getResult(false, "Параметр скрипта 'STATES' не может быть пустым.")
}
if (LINKED_OBJECTS && CHILD_METACLASS || !(LINKED_OBJECTS || CHILD_METACLASS)) {
  // Проверка, что заполнен LINKED_OBJECTS или CHILD_METACLASS
  return getResult(false, "В параметрах скрипта должен быть задан ровно один из двух аргументов: 'LINKED_OBJECTS' или 'CHILD_METACLASS'")
}


if(LINKED_OBJECTS) {
  def isSuccess = checkStatus(subject[LINKED_OBJECTS])
  return getResult(isSuccess, ERROR_MSG)
}

if(CHILD_METACLASS) {
  def isSuccess = checkStatus(utils.find(CHILD_METACLASS, [parent : subject]))
  return getResult(isSuccess, ERROR_MSG)
}
return getResult(false)