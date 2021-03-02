//Назначение:
/**
 * Скрипт для вывода параметров
 * "Дата решения с" - дата и время 00:00, обязательный
 * "Дата решения по" - дата и время 23:59, обязательный
 * "Учитывать заявки по всем услугам" - тип "Логический", по умолчанию – "нет"
 * "Или выберите конкретные услуги" - тип "Набор ссылок на объекты" класса "Услуга", по умолчанию – пусто, есть сложная форма
 * "Учитывать заявки без услуги" - тип "Логический", по умолчанию – "нет"
 * "Учитывать заявки по всем ответственным" - тип "Логический", по умолчанию – "нет"
 * "Или выберите конкретных ответственных" - тип "Набор ссылок на объекты" класса "Отдел", по умолчанию – пусто, есть сложная форма
 */
//ОСНОВНОЙ БЛОК--------------------------------------------------------

def getParameters() {
  return [
    api.parameters.getDateTime("dateFrom","Дата решения с", null, 'startOfDay', true),
	api.parameters.getDateTime("dateTo", "Дата решения по", null, 'endOfDay', true),
    api.parameters.getBoolean("allService", "Учитывать заявки по всем услугам", false),
    api.parameters.getObjects("pService", "Или выберите конкретные услуги", "slmService", "", ['attrGroupCode' : '6ee38cb6-9554-41d2-8d55-873b51adc032']),
    api.parameters.getBoolean("bService", "Учитывать заявки без услуги", false),
    api.parameters.getBoolean("allEmployees", "Учитывать заявки по всем ответственным", false),
    api.parameters.getObjects("pEmployee", "Или выберите конкретных ответственных", "employee", "", ['attrGroupCode' : 'fd153a0c-33bb-433e-903c-b2968592b9a2'], false)
  ] as List
}

table.rows.each {
  row ->
  // Для каждой строки основного отчета заполняем ссылку на заявку
  if(row.id != null){
    row.sc_url = api.web.open('serviceCall$' + row.id.toString())
  }
}

// Текущая дата - дата формирования отчета
table.addValue('rDate', new Date())