//Назначение:
/**
 * Скрипт для вывода параметров
 * "Дата с" - дата и время 00:00, обязательный
 * "Дата по" - дата и время 23:59, обязательный
* "Учитывать заявки по всем сотрудникам" - тип "Логический", по умолчанию – "нет"
 * "Или выберите конкретных сотрудников" - тип "Набор ссылок на объекты" класса "Сотрудник", обязательный, по умолчанию – пусто, есть сложная форма
 * "Учитывать архивные задачи" - тип "Логический", по умолчанию – "да"
 */
//ОСНОВНОЙ БЛОК--------------------------------------------------------

def getParameters()
{
  return [ 
    api.parameters.getDateTime("dateFrom", "Дата с", null, 'startOfDay', true),
    api.parameters.getDateTime("dateTo", "Дата по", null, 'endOfDay', true),
    api.parameters.getBoolean("allEmployees", "Учитывать задачи по всем сотрудникам", true),
    api.parameters.getObjects("employeeList", "Или выберите конкретных сотрудников", "employee", '', ['attrGroupCode' : 'fd153a0c-33bb-433e-903c-b2968592b9a2'], false),
    api.parameters.getBoolean("isArchived", "Учитывать архивные задачи", true)
  ] as List
}

// Текущая дата - дата формирования отчета
table.addValue('creationDate', new Date())