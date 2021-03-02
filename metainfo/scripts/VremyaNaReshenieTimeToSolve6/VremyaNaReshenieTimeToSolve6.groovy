/*! UTF8 */
//Автор: vkuznetsov
//Дата создания: 21.02.18
//Код: SCRIPTSD4001893
//Назначение:
/**
 * Вычисление значения по умолчанию для атрибута "Время на решение"
 * @param DEFAULT_INTERVAL_IN_YEARS - промежуток времени в годах, которым заполняется
 * время на решение если не указан дедлайн
 * @param SERVICE_TIME - код атрибута класса обслуживания в компании
 * @param TIME_ZONE - код атрибута часового пояса в компании
 * @param DEADLINE - код атрибута дедлайн в задаче
 * Сценарий определяет значение атрибута "Время на решение" с учетом класса обслуживания и возвращает его
 * Если дедлайн не указан, то скрипт возвращает промежуток времни указанный в параметре
 */
//Версия: 4.7.6+
//Категория: Вычисление значения атрибута по умолчанию
//Параметры------------------------------------------------------
def DEFAULT_INTERVAL_IN_YEARS = 10
def SERVICE_TIME = 'serviceTime'
def TIME_ZONE = 'timeZone'
def DEADLINE = 'deadline'
//Функции--------------------------------------------------------
//Основной блок -------------------------------------------------
def result
def timeIntervalSeconds
if (subject[DEADLINE] == null) {
  timeIntervalSeconds = DEFAULT_INTERVAL_IN_YEARS * 365 * 24 * 60 * 60
  result = api.types.newDateTimeInterval((int)timeIntervalSeconds, 'SECOND')
}
else {
  def root = utils.get('root', [:])
  def serviceTime = root[SERVICE_TIME]
  def timeZone = root[TIME_ZONE]
  timeIntervalSeconds = api.timing.serviceTime(serviceTime, timeZone, new Date(), subject[DEADLINE])/1000
  result = api.types.newDateTimeInterval((int)timeIntervalSeconds, 'SECOND')
}
return result