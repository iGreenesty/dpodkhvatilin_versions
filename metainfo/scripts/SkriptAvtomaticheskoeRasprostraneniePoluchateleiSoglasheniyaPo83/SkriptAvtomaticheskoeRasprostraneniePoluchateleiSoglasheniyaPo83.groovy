//Автор: dzevako, eboronina
//Дата создания: 02.08.2012
//Дата изменения: 01.09.2020
//Назначение:
/**
 * Автоматическое распространения получателей соглашения
 * @param AUTOCREATION - код атрибута "Распространить по оргструктуре"
 * @param AUTOCREATION_TEAM - код атрибута "Распространить по командам"
 * @param PICK_ALL - код атрибута "Выбрать всех"
 * @param RECIPIENTS - код атрибута "Получатели (сотрудники)"
 * @param RECIPIENTS_OU - код атрибута "Получатели (отделы)"
 * @param RECIPIENTS_TEAM - код атрибута "Получатели (команды)"
 */
//Версия: 4.11.0+
//Категория: Действия по событию типа скрипт
//ПАРАМЕТРЫ------------------------------------------------------------
def AUTOCREATION = 'autoCreation'
def AUTOCREATION_TEAM = 'autoCreationTe'
def PICK_ALL = 'allRecipients'
def RECIPIENTS = 'recipients'
def RECIPIENTS_OU = 'recipientsOU'
def RECIPIENTS_TEAM = 'recipientTeam'

//ФУНКЦИИ----------------------------------------------
def restrictionFilterRecipients(def uncheckedCollection) {
  def checkedCollection = []
  uncheckedCollection.each {
    object ->
    if (api.utils.checkRestrictions(object, false, false)) {
      checkedCollection << object
    }
  }
  return checkedCollection
}

//ОСНОВНОЙ БЛОК--------------------------------------------------------
def pickAll = subject[PICK_ALL]
def autoCreation = subject[AUTOCREATION]
def autoCreationTeam = subject[AUTOCREATION_TEAM]
def recipients = subject[RECIPIENTS]
def organizationUnits = subject[RECIPIENTS_OU]
def teams = subject[RECIPIENTS_TEAM]

api.tx.call {
  if(pickAll) {
    // Нужно выбрать всех
    api.ou.distributeAgreements(subject.UUID, utils.get('root', [:]).UUID)
  } else {
    if(autoCreation) {
      // Распространение по отделам
      organizationUnits.each {
        ou ->
        api.ou.distributeAgreements(subject.UUID, ou.UUID)
      }
    }

    if (autoCreationTeam) {
      // Распространение по командам
      teams.each { 
        team ->
        api.ou.distributeAgreements(subject.UUID, team.members.UUID)
      }
    } 
  }
}

def newRecipients = [] + subject[RECIPIENTS] - recipients
def newOURecipients = [] + subject[RECIPIENTS_OU] - organizationUnits
if(!newRecipients.isEmpty() || !newOURecipients.isEmpty()) {
  def msg = "Новые получатели:"
  if(!newRecipients.isEmpty()) {
    msg += "\r\nСотрудники: ${newRecipients.title.join(', ')}"
  }
  if(!newOURecipients.isEmpty()) {
    msg += "\r\nОтделы: ${newOURecipients.title.join(', ')}"
  }
  utils.event(subject, msg)
}

// Cнимаем флажки
def attrs = [:]
attrs[(AUTOCREATION)] = false
attrs[(AUTOCREATION_TEAM)] = false
attrs[(PICK_ALL)] = false

utils.edit(subject, attrs)