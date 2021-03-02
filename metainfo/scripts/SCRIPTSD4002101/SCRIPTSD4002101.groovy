/*! UTF8 */
//Автор: eboronina
//Дата создания: 04.04.2019
//Код: SCRIPTSD4002101
//Назначение:
/**
 * Фильтрация по лицензированным сотрудникам и командам
 * @param ATTRS_FOR_UPDATE_ON_FORMS - набор кодов атрибутов,
 * от которых зависит фильтрация
 * @param TEAM_MMBRS_ATTR - код атрибута-ссылки на участников
 * в команде
 * @param NOT_LICENSED_CODE - код лицензии "Нелицензированный"
 * Сценарий возвращает список всех команд и всех их
 * лицензированных учатников
 */
//Версия: 4.9.0+
//Категория: 
//Параметры------------------------------------------------------
def ATTRS_FOR_UPDATE_ON_FORMS = []
def TEAM_MMBRS_ATTR = 'members'
def NOT_LICENSED_CODE = 'notLicensed'
//Функции--------------------------------------------------------

//Основной блок -------------------------------------------------
if (subject == null) {
  return ATTRS_FOR_UPDATE_ON_FORMS
}

def result = []
def allTeams = utils.find('team', ['removed' : false])
allTeams.each {
  team ->
  def licMmbrs = team[(TEAM_MMBRS_ATTR)].findAll {it.license != NOT_LICENSED_CODE}.UUID
  if(!licMmbrs.isEmpty()) {
    result << team.UUID
    def map = [:]
    map[(team.UUID)] = licMmbrs
    result << map
  }
}
return result