//Назначение:
/**
 * Параметров нет
 * Отправляем текущую дату и обновляем таблицу
 */

//ОСНОВНОЙ БЛОК ---------------------------------------------------------------
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
      newTableRows.add([key : row[COLUMN_TIME], row : [row[COLUMN_ID],
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
table.addValue('rDate', new Date())