//Назначение:
/**
 * Скрипт для вывода параметров
 * "Дата регистрации с" - дата и время, обязательный
 * "Дата регистрации по" - дата и время, обязательный
 * "Период для расчета" - тип "Элемент справочника", по умолчанию - пусто
 */
//Автор: A. Storozhuk
//Дата создания: 06.11.2013
//Код: Script481_diagram_ru
//ОСНОВНОЙ БЛОК--------------------------------------------------------

def getParameters() {
  return [
    api.parameters.getDate("dateFrom", "Дата регистрации с", true),
    api.parameters.getDate("dateTo", "Дата регистрации по", true),
    api.parameters.getCatalogItem("listPeriods", "Период для расчета", 'calcScopeType')
  ] as List
}

Calendar datetTo = new GregorianCalendar()
datetTo.setTime(dateTo)
Calendar datetFrom = new GregorianCalendar()
datetFrom.setTime(dateFrom)

def results = []

def i = 0
def intervCount = 0
while (datetFrom <= datetTo) {
  Calendar nextDt
  def resultRow = [
    interval : 0,
    cnt : 0,
    val : 0,
    avg : 0,
    type : ""
  ]
  
  switch (listPeriods_code) {
    case 'd':
    datetFrom.add(Calendar.DAY_OF_WEEK, 1)
    nextDt = datetFrom.clone(); intervCount++
    resultRow.interval = intervCount
    resultRow.type = "Дни"
    break
    
    case 'w':
    datetFrom.add(Calendar.DAY_OF_WEEK, 7)
    nextDt = datetFrom.clone()
    intervCount++
    resultRow.interval = intervCount
    resultRow.type = "Недели"
    break
    
    case 'm':
    datetFrom.add(Calendar.DAY_OF_WEEK, 30)
    nextDt = datetFrom.clone()
    intervCount++
    resultRow.interval = intervCount
    resultRow.type = "Месяцы"
    break
	
    default:
    datetFrom.add(Calendar.DAY_OF_WEEK, 30)
    nextDt = datetFrom.clone()
    intervCount++
    resultRow.interval = intervCount
    resultRow.type = "Месяцы"
  }

  boolean firstRowInInterval = true;
  while(i < table.rows.size() && (table.rows[i].registration_date.getTime() < nextDt.getTimeInMillis()) ) {
    if (firstRowInInterval) {
      firstRowInInterval=false
    } else {
      resultRow.cnt++
      resultRow.val += Math.abs(table.rows[i].registration_date.getTime() - table.rows[i-1].registration_date.getTime())
    }
    i++
  }
  if (resultRow.cnt > 0 && resultRow.val != 0) {
    resultRow.avg = resultRow.val/resultRow.cnt/1000/60/60
  }
  
  results << resultRow
  datetFrom = nextDt
}

table.clearData()


for(def item : results) {
  table.addRow([
    item.interval,
    item.avg,
    item.type
  ] as Vector);
}

// Время создания отчета
table.addValue('rDate', new Date())

return table