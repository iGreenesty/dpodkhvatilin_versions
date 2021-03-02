/*! UTF8 */
//Автор: eboronina
//Дата создания: 14.08.18
//Код: SCRIPTSD4002119
//Назначение:
/**
* Пересчет суммарных трудозатрат по заявке при добавлении
* изменении и удалении трудозатраты
* @param SC_ATTR - код атрибута ссылки на заявку в трудозатратах
* @param WORKREC_ATTR - код атрибута обратной ссылки на
* трудозатраты в заявке
* @param TIME_ATTR - код вещественного атрибута "Списанное время"
* @param SUM_ATTR_FILTR - соответствие между кодом атрибута суммы
* и условиями фильтрации
* @param SUM_ATTR_APPR - код атрибута сумма подтвержденных трудозатрат
* @param STATE_APPR - код статуса "Утверждена"
* Сценарий пересчитывает суммарные трудозатраты и суммарные
* утвержденные трудозатраты в заявках, связанных с объектом до изменения и после
*/
//Версия: 4.6.0+
//Категория: Действие по событию типа скрипт
//Параметры------------------------------------------------------
def ATTRS_WITH_LINKS = ['serviceCall', 'task', 'changeRequest', 'problem']
def WORKREC_ATTR = 'workRecords'
def TIME_ATTR = 'time'
def SUM_ATTR_FILTR = [
  sumWorkRecords : [
    'removed' : false
  ],
  sumApprovedWR : [
    'removed' : false,
    'state' : 'closed'
  ]
]

//Функции--------------------------------------------------------
/**
*/
def recalcSumWorkRecords(source, attrCode, params, timeAttrCode, plusOrMinus = null) {
  def result = [:]
  def object = subject ?: oldSubject
  def workRecordUUIDs = source[(attrCode)].UUID
  switch(plusOrMinus) {
    case 'plus':
    	workRecordUUIDs = workRecordUUIDs.plus(object.UUID)
   		break;
    case 'minus':
    	workRecordUUIDs = workRecordUUIDs.minus(object.UUID)
    	break;
  }
  def workRecords = workRecordUUIDs.collect{utils.get(it)}
  params.each {
    attr, filters ->
    def accepted = workRecords.findAll {
      workRecord ->
      // проходимся по всем трудозатратам в списке
      filters.find {
        k, v ->
        // проверяем условия - ищем первое, которое не выполняется
        workRecord[(k)] != v
      } == null // если все условия прошли, будет null и нам подойдет такая трудозатрата
    }
    result.put((attr), accepted.sum{it[(timeAttrCode)]})
  }
  return result
}
//Основной блок -------------------------------------------------
// Список атрибутов-связей, которые заполнены в объекте до изменения
def oldFilledLinks = ATTRS_WITH_LINKS.findAll {oldSubject && oldSubject[it]} 
// Список атрибутов-связей, которые заполнены в объекте после изменения
def filledLinks = ATTRS_WITH_LINKS.findAll {subject && subject[it]}

// Список объектов из связей до изменения
def oldFilledObjects = oldFilledLinks.collect {oldSubject[it].UUID} 
// Список объектов из связей после изменения
def filledObjects = filledLinks.collect {subject[it].UUID} 

// Отердактировали сам объект, атрибуты связи не трогали
if (oldSubject && subject) {
  // Запускаем пересчет во всех объектах, по заполненным атрибутам связи до и после изменений
  filledObjects.intersect(oldFilledObjects).each {
    sourceUUID ->
    def source = utils.get(sourceUUID)
    def map = recalcSumWorkRecords(source, WORKREC_ATTR, SUM_ATTR_FILTR, TIME_ATTR)
    utils.edit(source, map)
  }
}
// Отдельно для oldSubject
if(oldSubject) {
  // Запускаем пересчет во всех объектах, по заполненным ранее атрибутам связи
  oldFilledObjects.findAll{!(it in filledObjects)}.each {
    oldSourceUUID ->
    def source = utils.get(oldSourceUUID)
    def map = recalcSumWorkRecords(source, WORKREC_ATTR, SUM_ATTR_FILTR, TIME_ATTR, 'minus')
    utils.edit(source, map)
  }
}
// Отдельно для subject
if(subject) {
  // Запускаем пересчет во всех объектах, по заполненным атрибутам связи
  filledObjects.findAll{!(it in oldFilledObjects)}.each {
    curSourceUUID ->
    def source = utils.get(curSourceUUID)
    def map = recalcSumWorkRecords(source, WORKREC_ATTR, SUM_ATTR_FILTR, TIME_ATTR, 'plus')
    utils.edit(source, map)
  }
}