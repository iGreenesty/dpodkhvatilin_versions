/*! UTF8 */
//Автор: vkuznetsov, eboronina
//Дата создания: 21.02.18
//Код: SCRIPTSD4001894
//Назначение:
/**
 * Вычисление значения для атрибута "Время на решение" при изменении дедлайна в задаче
 * @param DEFAULT_INTERVAL_IN_YEARS - промежуток времени в годах, которым заполняется
 * время на решение если не указан дедлайн
 * @param SERVICE_TIME - код атрибута класса обслуживания в задаче
 * @param TIME_ZONE - код атрибута часового пояса в задаче
 * @param DEADLINE - код атрибута дедлайн в задаче
 * @param TIME_SOLVE_ATTR - код атрибута время на решение
 * @param MESSAGE_IN_PAST - сообщение об ошибки в случае указания дедлайна в прошлом
 * Сценарий определяет значение атрибута "Время на решение" с учетом класса обслуживания и изменяет значение атрибута в задаче
 * Если дедлайн не указан, то скрипт задает значение равное значению параметра
 */
//Версия: 4.7.6+
//Категория: Скрипт действия по событию
//Параметры------------------------------------------------------
def DEFAULT_INTERVAL_IN_YEARS = 10
def SERVICE_TIME = 'serviceTime'
def TIME_ZONE = 'timeZone'
def DEADLINE = 'deadline'
def TIME_SOLVE_ATTR = 'timeToSolve'
def COUNTER_ATTR = 'solveTimeRes'
def MESSAGE_IN_PAST = 'Измените дедлайн: Дедлайн указан в прошлом'
//Функции--------------------------------------------------------
//Основной блок -------------------------------------------------
def timeIntervalSeconds
if (subject[DEADLINE] == null) {
  timeIntervalSeconds = DEFAULT_INTERVAL_IN_YEARS * 365 * 24 * 60 * 60
}
else {
  def date = new Date()
  if (subject[DEADLINE] < date){
    utils.throwReadableException(MESSAGE_IN_PAST, [] as String[], MESSAGE_IN_PAST, [] as String[])
  }
  
  def serviceTime = subject[SERVICE_TIME]
  def timeZone = subject[TIME_ZONE]
  def counterValue = subject[COUNTER_ATTR]
  //Время в мс сколько уже прошло
  def timeAlreadyGone = subject[TIME_SOLVE_ATTR].toMiliseconds() - counterValue.allowance + counterValue.elapsedFromOverdue
  //Время в мс от текущего момента до нового дедлайна
  def timeFromNowToDeadline = api.timing.serviceTime(serviceTime, timeZone, date, subject[DEADLINE])
  timeIntervalSeconds = Math.ceil((timeAlreadyGone + timeFromNowToDeadline)/1000)
}
def result = api.types.newDateTimeInterval((int)timeIntervalSeconds, 'SECOND')
utils.edit(subject, [(TIME_SOLVE_ATTR) : result])