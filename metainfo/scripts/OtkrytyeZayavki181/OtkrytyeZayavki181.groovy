//Назначение:
/**
 * Скрипт для вывода параметров
 * "Учитывать заявки по всем услугам" - тип "Логический", по умолчанию – "нет"
 * "Или выберите конкретные услуги" - тип "Набор ссылок на объекты" класса "Услуга", по умолчанию – пусто, есть сложная форма
 * "Учитывать заявки без услуги" - тип "Логический", по умолчанию – "нет"
 * "Учитывать заявки по всем контрагентам" - тип "Логический", по умолчанию – "нет"
 * "Или выберите конкретных контрагентов" - тип "Набор ссылок на объекты" класса "Отдел", по умолчанию – пусто, есть сложная форма
 * "Учитывать заявки отделов/компаний, вложенных в выбранные" - тип "Логический", по умолчанию – "нет"
 * "Учитывать заявки без контрагента" - тип "Логический", по умолчанию – "нет"  
 */
//ОСНОВНОЙ БЛОК ---------------------------------------------------------------

def getParameters() {
  return [
    api.parameters.getBoolean("allService", "Учитывать заявки по всем услугам", false),
    api.parameters.getObjects("pService", "Или выберите конкретные услуги", "slmService", "", ['attrGroupCode' : '6ee38cb6-9554-41d2-8d55-873b51adc032']),
    api.parameters.getBoolean("bService", "Учитывать заявки без услуги", false),
    api.parameters.getBoolean("allOU", "Учитывать заявки по всем контрагентам", false),
    api.parameters.getObjects("pOu", "Или выберите конкретных контрагентов", "ou", "", ['attrGroupCode' : 'edcfcd28-3de9-4a5c-bab7-3b772a727486']),
    api.parameters.getBoolean("bOu", "Учитывать заявки отделов/компаний, вложенных в выбранные", false),
    api.parameters.getBoolean("bClientIsEmpty", "Учитывать заявки без контрагента", false)
  ] as List
}

// Обновляем таблицу - оставляем те, что будут скоро просрочены и заполняем колонку
def TESTTIME = 2*60     //  просрочены меньше чем через 2 часа (указать в минутах)
def INMINUTES = 60*1000
// Коды полей таблицы SQL запроса и отчета
def COLUMN_ID = "id"
def COLUMN_URL = "sc_url"
def COLUMN_TIME = "timeallowancetimerl"
def COLUMN3 = "number_"
def COLUMN4 = "sc_case"
def COLUMN5 = "service"
def COLUMN6 = "client"
def COLUMN7 = "responsible"

def newTableRows = []
table.rows.each {
  row ->
  if(row[COLUMN_ID] != null) {
    row[COLUMN_URL] = api.web.open('serviceCall$' + row[COLUMN_ID].toString())
    def sCall = utils.get('serviceCall$' + row[COLUMN_ID].toString())
    def timerVal = sCall?.timeAllowanceTimer
    row[COLUMN_TIME] = timerVal?.allowance
    if(row[COLUMN_TIME]/INMINUTES < TESTTIME) {
      newTableRows.add([key:row[COLUMN_TIME], row :[row[COLUMN_ID],
                                                    row[COLUMN_URL],
                                                    row[COLUMN3],
                                                    row[COLUMN4],
                                                    row[COLUMN5],
                                                    row[COLUMN6],
                                                    row[COLUMN7],
                                                    row[COLUMN_TIME]
                                                   ] as Vector])
    }
  }
}
newTableRows.sort{it.key}
table.clearData()
newTableRows.reverseEach {
  table.addRow(it.row)
}

// Текущая дата - дата формирования отчета
table.addValue('rDate', new Date());