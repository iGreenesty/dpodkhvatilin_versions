//Автор: eboronina
//Дата создания: 23.01.2017
//Назначение:
/**
 * Скрипт фильтрации сотрудников, которых можно
 * уведомить о комментарии
 */
//Версия: 4.7.0.2

if (subject == null) {
  return ['source', 'UUID']
}

// Для суперпользователя доступны все сотрудники без ограничений
if(user == null) {
  return api.filtration.disableFiltration()
}

def allowAllEmpls = ['C1_specialist', 'C3_admin'] //Кому доступен список всех сотрудников
def rights = user.employeeSecGroups

// Если у пользователя есть права на список всех сотрудников
if(rights.find{it.code in allowAllEmpls} != null) {
  // без ограничений
  return api.filtration.disableFiltration()
}
// Иначе только сотрудники из отдела контрагента и сотрудники из отдела пользователя
return user.parent.employees