/*! UTF8 */
//Автор: eboronina
//Дата создания: 17.07.20
//Код: SCRIPTSD4002258
//Назначение:
/**
 * Вычисление значения по умолчанию даты эскалации по задаче
 * @param INFORM_BY - коэффициент, за сколько процентов оповещать по эскалации. 0.3 = 30%
 * @param SERVICE_TIME - код атрибута класса обслуживания в компании
 * @param TIME_ZONE - код атрибута часового пояса в компании
 * @param DEADLINE - код атрибута дедлайн в задаче
 * @param PLANDATE - код атрибута плавновая дата начала в задаче
 * Если дедлайн заполнен, сценарий определеяет, сколько отводится на решение задачи
 * от плановой даты или от даты создания до дедлайна. И значение умноженное на
 * коэффициент вычитает из дедлайна. В итоге получаем дату оповещения
 */
//Версия: 4.11.0+
//Категория: Вычисление значения атрибута по умолчанию
//Параметры------------------------------------------------------
def INFORM_BY = 0.3
def SERVICE_TIME = 'serviceTime'
def TIME_ZONE = 'timeZone'
def DEADLINE = 'deadline'
def PLANDATE = 'planDate'
//Функции--------------------------------------------------------
//Основной блок -------------------------------------------------
def result = null
if (subject[DEADLINE] != null) {
  def root = utils.get('root', [:])
  def serviceTime = root[SERVICE_TIME]
  def timeZone = root[TIME_ZONE]
  def timeToSolveIntervalSeconds = api.timing.serviceTime(serviceTime, timeZone, subject[(PLANDATE)] ?: new Date(), subject[DEADLINE])/1000
  def percentageInSeconds = (timeToSolveIntervalSeconds * INFORM_BY).toInteger()
  result = use (groovy.time.TimeCategory) { subject[DEADLINE] - percentageInSeconds.seconds }
}
return result