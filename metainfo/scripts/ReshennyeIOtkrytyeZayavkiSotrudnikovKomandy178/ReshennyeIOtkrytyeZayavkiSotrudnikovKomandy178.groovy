//Назначение:
/**
 * Скрипт для вывода параметров
 * "Дата решения с" - дата и время 00:00, обязательный
 * "Дата решения по" - дата и время 23:59, обязательный
 * "Команда" - тип "Ссылка на БО" класса "Команда", по умолчанию – пусто, обязательный, есть сложная форма
 */
//ОСНОВНОЙ БЛОК--------------------------------------------------------
def getParameters() {
	return [
		api.parameters.getDateTime("dateFrom", "Дата решения с", null, 'startOfDay', true),
		api.parameters.getDateTime("dateTo", "Дата решения по", null, 'endOfDay', true),
        api.parameters.getObject("pTeam", "Команда", "team", "", ['attrGroupCode' : 'c561db88-b310-46cd-8e6c-d72d9920fa30'], true),
	] as List
}

// Текущая дата - дата формирования отчета
table.addValue('rDate', new Date())