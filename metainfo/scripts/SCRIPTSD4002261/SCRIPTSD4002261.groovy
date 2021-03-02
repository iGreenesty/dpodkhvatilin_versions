/*! UTF8 */
//Автор: eboronina
//Дата создания: 31.08.2020
//Код: SCRIPTSD4002261
//Назначение:
/**
 * Добавить трудозатрату
 * @param FQN - метакласс создаваемой трудозатраты
 * @param ATTRS_TO_COPY - коды параметров-атрибутов для
 * копирования с формы
 * @param ERROR_DSC - сообщение об ошибке
 * Сценарий определяет атрибуты для создания трудозатраты
 * с формы и создает её. В случае ошибки, возвращает
 * сообщение об ошибке
 */
//Версия: 4.11.0+
//Категория: 
//Параметры-----------------------------------------------------
def FQN = 'workRecord$workRecord'
def ATTRS_TO_COPY = ['employee', 'timeStr', 'description', 'actualDate']
def ERROR_DSC = '''
<p>При создании трудозатраты возникла непредвиденная ошибка!</p>
<p>Пожалуйста, сообщите об этом администратору системы.</p>'''
//Функции-------------------------------------------------------

//Основной блок ------------------------------------------------
def attrsToCreate = [
  'author' : user,
  '@user' : user
]
ATTRS_TO_COPY.each {
  attr ->
  attrsToCreate[attr] = params[(attr)]
}
def cardClass = cardObject.metaClass.toString().split(/\$/)[0]
attrsToCreate.put(cardClass, cardObject)

def workRecord
try {
  workRecord = utils.create(FQN, attrsToCreate)
} catch (Exception e) {
  logger.error("Возникла ошибка при попытке создать трудозатрату в объекте ${cardObject.UUID}", e)
  utils.throwReadableException(ERROR_DSC, [] as String[], ERROR_DSC, [] as String[])
}