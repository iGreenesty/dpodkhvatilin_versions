/*! UTF8 */
//Автор: eboronina
//Дата создания: 19.12.2019
//Код: SCRIPTSD4002176
//Назначение:
/**
 * Пересчет сумм утвержденных трудозатрат
 * @param ATTRS_WITH_LINKS - список кодов атрибутов трудозатраты,
 * в которых ссылки на активности
 * @param WORKREC_ATTR - код атрибута-ссылок на трудозатраты
 * в активностях
 * @param TIME_ATTR - код атрибута с количеством затраченных часов
 * @param SUM_ATTR_FILTR - правила фильтрации объектов для расчета
 * сумм в атрибутах
 * @param ATTR_SUM - код с атрибутом, в котором сумма
 * Сценарий получает все трудозатраты, которые были утверждены
 * за последние сутки. В результате, для каждой связанной активности
 * рассчитываем сумму утвержденных трудозатрат и обновляем в
 * активности.
 * Планировщик нужен в кейсах, когда массовым утверждением пользователь
 * утвердил несколько тзт по одной активности. В этом случае ДПС
 * отрабатывает не корректно.
 */
//Версия: 4.10.0+
//Категория: 
//Параметры------------------------------------------------------
def ATTRS_WITH_LINKS = ['serviceCall', 'task', 'changeRequest', 'problem']
def WORKREC_ATTR = 'workRecords'
def TIME_ATTR = 'time'
def SUM_ATTR_FILTR = [
  sumApprovedWR : [
    'removed' : false,
    'state' : 'closed'
  ]
]
def ATTR_SUM = 'sumApprovedWR'

//Функции--------------------------------------------------------
/**
 */
def recalcSumWorkRecords(source, attrCode, params, timeAttrCode, object) {
  def result = [:]
  def workRecords = source[(attrCode)].UUID.collect{ utils.get(it) }
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
def dateTo = new Date()
def dateFrom = use (groovy.time.TimeCategory) {dateTo - 1.day}
def wrs = utils.find('workRecord', ['closedDate' : op.between(dateFrom, dateTo)])
def scsToEdit = [:]

wrs.each {
  wr ->
  // Список атрибутов-связей, которые заполнены в объекте после изменения
  def filledLinks = ATTRS_WITH_LINKS.findAll {wr && wr[it]}
  
  // Список объектов из связей после изменения
  def filledObjects = filledLinks.collect {wr[it].UUID}
  
  // Запускаем пересчет во всех объектах, по заполненным атрибутам связи до и после изменений
  filledObjects.each {
    sourceUUID ->
    def source = utils.get(sourceUUID)
    def map = recalcSumWorkRecords(source, WORKREC_ATTR, SUM_ATTR_FILTR, TIME_ATTR, wr)
    if(scsToEdit[(sourceUUID)]) {
      def oldVal = scsToEdit[(sourceUUID)][(ATTR_SUM)]
      def newVal = map[(ATTR_SUM)]
      if(newVal > oldVal) {
        scsToEdit.put(sourceUUID, map)
      }
    } else {
      scsToEdit.put(sourceUUID, map)
    }
  }
}

scsToEdit.each {
  scUUID, map ->
  utils.edit(scUUID, map, true)
}