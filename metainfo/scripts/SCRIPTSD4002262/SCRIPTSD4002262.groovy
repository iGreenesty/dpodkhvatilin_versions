/*! UTF8 */
//Автор: eboronina
//Дата создания: 01.09.2020
//Код: SCRIPTSD4002262
//Назначение:
/**
 * Распространить соглашение по всем или выбранным отделам
 * @param RECIPIENTS - код атрибута "Получатели (сотрудники)"
 * @param RECIPIENTS_OU - код атрибута "Получатели (отделы)"
 * Сценарий проверяет, если в списке были выбраны отделы, то
 * распространяет соглашение только по выбранным отделам. Иначе
 * распространяет по всем отделам-получателям. При распространении
 * соглашение начинают получать все вложенные сотрудники и отделы
 * на всех уровнях вложенности. По завершению фиксируется в истории
 * соглашения, какие новые получатели появились
 */
//Версия: 4.10.0+
//Категория: 
//Параметры-----------------------------------------------------
def RECIPIENTS = 'recipients'
def RECIPIENTS_OU = 'recipientsOU'
//Функции-------------------------------------------------------

//Основной блок ------------------------------------------------
def agreement = cardObject
def recipientsEmpl = [] + agreement[RECIPIENTS]
def recipientsOus = [] + agreement[RECIPIENTS_OU]
def ousToDistrib = (subjects.isEmpty()) ? recipientsOus : subjects

api.tx.call {
  ousToDistrib.each {
    ou ->
      api.ou.distributeAgreements(agreement.UUID, ou.UUID)
  }
}

def newRecipients = [] + agreement[RECIPIENTS] - recipientsEmpl
def newOURecipients = [] + agreement[RECIPIENTS_OU] - recipientsOus

if(!newRecipients.isEmpty() || !newOURecipients.isEmpty()) {
  def msg = "Новые получатели по кнопке:"
  if(!newRecipients.isEmpty()) {
    msg += "\r\nСотрудники: ${newRecipients.title.join(', ')}"
  }
  if(!newOURecipients.isEmpty()) {
    msg += "\r\nОтделы: ${newOURecipients.title.join(', ')}"
  }
  utils.event(agreement, msg)
}