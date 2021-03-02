//Назначение:
/**
 * Скрипт для вывода параметров 
 * "Дата решения с" - дата и время 00:00, обязательный
 * "Дата решения по" - дата и время 23:59, обязательный
 */
//ОСНОВНОЙ БЛОК--------------------------------------------------------
def getParameters() {
  return [
		api.parameters.getDateTime("dateFrom", "Дата решения с", null, 'startOfDay', true),
		api.parameters.getDateTime("dateTo", "Дата решения по", null, 'endOfDay', true)
        //api.parameters.getBoolean("bClientIsEmpty", "Учитывать заявки без контрагента", false)
	] as List
}

// Текущая дата - дата формирования отчета
table.addValue('rDate', new Date())