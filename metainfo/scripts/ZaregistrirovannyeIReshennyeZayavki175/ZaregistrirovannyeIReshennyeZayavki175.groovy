//Назначение:
/**
 * Скрипт для вывода параметров
 * "Дата с" - дата и время 00:00, обязательный
 * "Дата по" - дата и время 23:59, обязательный
 */
//ОСНОВНОЙ БЛОК--------------------------------------------------------

def getParameters() {
  Calendar dateFrom = Calendar.getInstance()
  if(user != null) {
    def settings = api.employee.getPersonalSettings(user.UUID)
    String timeZoneCode = settings.getTimeZone()
    if (timeZoneCode != null) {
      dateFrom.setTimeZone(java.util.TimeZone.getTimeZone(timeZoneCode))
    }
  }
  dateFrom.set(Calendar.HOUR_OF_DAY, 0)
  dateFrom.set(Calendar.SECOND, 0)
  dateFrom.set(Calendar.MINUTE, 0)
  dateFrom.getTime()

  return [
            api.parameters.getDateTime("DateFrom", "Дата с", dateFrom.getTime(), 'startOfDay', true),
            api.parameters.getDateTime("DateTo", "Дата по", new Date(), 'endOfDay', true)

    ] as List;
}

// Текущая дата - дата формирования отчета
table.addValue('rDate', new Date())