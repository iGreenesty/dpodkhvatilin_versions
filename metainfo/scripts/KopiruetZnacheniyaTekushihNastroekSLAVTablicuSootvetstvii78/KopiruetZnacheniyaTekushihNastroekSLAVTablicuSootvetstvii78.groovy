//Автор: apershukov, eboronina
//Дата создания: 18.06.2014
//Код: SCRIPTSD4000521
//Назначение:
/**
* Копирует значения текущийх настроек SLA в таблицу соответствий. Устанавливается 
* как скрипт действия по событию "Изменение объекта" и "Удаление объекта"
*/
//Версия: 4.3.2.x
//Категория: Скрипт действия по событию
//Последнее изменение: 26.02.2018
//Цель изменения: соответствие между таблицей и атрибутами от типа правила

//ПАРАМЕТРЫ------------------------------------------------------------
// Соответствие между fqn объекта, кодом таблицы соответствий и коды атрибутов
def FQN_TABLE_MAP = [
  'agreementRule$agreementRule' : [
    'table' : 'resolutionTimeNew',
    'attrs' : [
      'resolutionTime'	: 'resolutionTime',
      'service'			: 'services',
      'priority'		: 'priority',
      'agreement'		: 'parent',
      'metaClass'		: 'typesSCs'
    ]
  ],
  'agreementRule$reactRule' : [
    'table' : 'reactTime',
    'attrs' : [
      'reactTime'	: 'resolutionTime',
      'service'		: 'services',
      'priority'	: 'priority',
      'agreement'	: 'parent',
      'metaClass'	: 'typesSCs'
    ]
  ]
]
//Сообщениеи при ошибке
def ERR_MESSAGE = 'Возникла ошибка. Обратитесь к администратору системы или в техническую поддержку'
//ФУНКЦИИ--------------------------------------------------------------

//ОСНОВНОЙ БЛОК--------------------------------------------------------
//Получаем элемент справочника "Таблицы соответствий" с кодом TABLE_CODE
def fqn = api.metainfo.getMetaClass(subject).code
if(FQN_TABLE_MAP[fqn]) {
  def tableCode = FQN_TABLE_MAP[fqn].table
  def attrs = FQN_TABLE_MAP[fqn].attrs
  def systemTable = utils.get('rulesSettings', ['code': tableCode])
  //Получаем все строки таблицы соответствий
  def rowSet = systemTable['rowSet']
  
  //Получаем все объекты настроек SLA
  def allRules = utils.find(fqn, ['removed' : false])
  //Заполняем из объектов настроек SLA новое содержимое таблицы соответствий
  def rules = [].withDefault { [:] }
  allRules.eachWithIndex{
    rule, i ->
    attrs.each {
      attr -> 
      rules[i].put(attr.key, rule[attr.value])
    }
  }
  
  //Добавляем в таблицу соответствий строку, соответствующую новому объекту настроек SLA
  def lastIndx = rules.size();
  attrs.each {
    attr -> 
    rules[lastIndx].put(attr.key, subject[attr.value])
  }
  
  //Устанавливаем новое содержимое таблицы соответствий
  try {
    utils.edit(systemTable, ['rowSet': rules])
  }
  catch (Exception e) {
	logger.error("Ошибка при создании правила SLA", e)
    utils.throwReadableException(ERR_MESSAGE, [] as String[], ERR_MESSAGE, [] as String[])
  }
}