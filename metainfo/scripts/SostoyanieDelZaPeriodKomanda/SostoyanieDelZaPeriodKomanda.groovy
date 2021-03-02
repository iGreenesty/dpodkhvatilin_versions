//Назначение:
/**
 * Скрипт для вывода параметров
 * "Дата с" - дата, обязательный, по умолчанию текущая дата за вычетом 7-ти дней
 * "Дата по" - дата, обязательный, по умолчанию текущая дата
 */
//ОСНОВНОЙ БЛОК--------------------------------------------------------

def getParameters() {
  def end = new Date() + 1
  def start = end - 7
  return [
    api.parameters.getDate("dateFrom", "Дата с", start, true),
    api.parameters.getDate("dateTo", "Дата по", end, true)
  ] as List
}
table.rows.each {
  row ->
  // Для каждой строки основного отчета заполняем ссылку на заявку
  if(row.id != null) {
    row.sc_url = api.web.open('serviceCall$' + row.id.toString())
  }
}

// Текущая дата - дата формирования отчета
table.addValue('rDate', new Date())