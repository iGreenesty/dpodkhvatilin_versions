def getActualParameters(def parameters) {
  if(parameters.scCases.isEmpty()) {
    parameters.noScCases = '[без ограничений]'
  }
  return parameters
}

def getParameters() {
	return [
		api.parameters.getDateTime("dateFrom", "Дата решения с", null, 'startOfDay', true),
		api.parameters.getDateTime("dateTo", "Дата решения по", null, 'endOfDay', true),
        api.parameters.getObject("pTeam", "Команда", "team", "", ['attrGroupCode' : 'c561db88-b310-46cd-8e6c-d72d9920fa30'], true),
      	api.parameters.getCaseList('scCases', 'Ограничить по типам заявок', 'serviceCall'),
      	api.parameters.getString('noScCases', '')
        //api.parameters.getBoolean("bClientIsEmpty", "Учитывать заявки без контрагента", false)
	] as List;
};
table.addValue('rDate', new Date());