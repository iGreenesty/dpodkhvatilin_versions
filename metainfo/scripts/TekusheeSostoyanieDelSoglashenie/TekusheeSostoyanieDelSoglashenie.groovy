//Назначение:
/**
 * Скрипт для вывода параметров
 * "Показывать заявки, которые будут просрочены через часов:" - целое число, обязательный, по умолчанию - 2
 * Обновляем таблицу
 */
//ОСНОВНОЙ БЛОК--------------------------------------------------------

def getParameters() {
  return [
    api.parameters.getInteger("hoursPar", "Показывать заявки, которые будут просрочены через часов:", 2, true)
  ] as List
}

// Обновляем таблицу - оставляем те, что будут скоро просрочены и заполняем колонку
def INMINUTES = 60*1000
// Коды полей таблицы SQL запроса и отчета
def COLUMN_ID = "id"
def COLUMN_URL = "sc_url"
def COLUMN_TIME = "timeallowancetimerl"
def COLUMN3 = "number_"
def COLUMN4 = "sc_case"
def COLUMN5 = "deadline"
def COLUMN6 = "client"
def COLUMN7 = "responsible"
def COLUMN8 = "ovrd"
def COLUMN9 = "hours" // значение из параметра

def newTableRows = []
def TESTTIME = table.rows[0] ? table.rows[0].hours * 60 : 0
table.rows.each {
  row ->
  if(row[COLUMN_ID] != null) {
    row[COLUMN_URL] = api.web.open('serviceCall$' + row[COLUMN_ID].toString())
    def sCall = utils.get('serviceCall$' + row[COLUMN_ID].toString())
    def timerVal = sCall?.timeAllowanceTimer
    row[COLUMN_TIME] = timerVal?.allowance
    if(row[COLUMN_TIME]/INMINUTES < TESTTIME && timerVal?.status?.code in ['a', 'e']) {
      newTableRows.add([key : row[COLUMN5], row : [row[COLUMN_ID],
                                                   row[COLUMN_URL],
                                                   row[COLUMN3],
                                                   row[COLUMN4],
                                                   row[COLUMN6],
                                                   row[COLUMN7],
                                                   row[COLUMN5],
                                                   row[COLUMN_TIME],
                                                   row[COLUMN8],
                                                   row[COLUMN9]
                                                  ] as Vector])
    }
  }
}
newTableRows.sort{it.key}
table.clearData()
newTableRows.each{
  table.addRow(it.row)
}

// Текущая дата - дата формирования отчета
table.addValue('rDate', new Date())